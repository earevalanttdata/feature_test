package es.nttdata.assetsproxy.infrastructure.adapter.async;

import es.nttdata.assetsproxy.domain.exception.BusinessException;
import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.port.async.AssetPublisherPort;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncAssetPublisherAdapter implements AssetPublisherPort {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".png", ".jpg", ".jpeg", ".webp");

    private static final Set<String> VIDEO_EXTENSIONS = Set.of(".mp4", ".mov", ".mpeg", ".webm");

    private final AssetRepositoryPort repository;

    @Async
    @Override
    public void publishAsync(AssetDomain asset) {
        try {
            byte[] content = asset.getFileBytes();
            if (content == null || content.length == 0) {
                log.error("ERROR: The content cannot be empty");
                repository.updateStatus(asset.getId(), AssetStatus.FAILED);
                return;
            }
            repository.updateStatus(asset.getId(), AssetStatus.UPLOADING);
            log.info("Simulating upload: '{}' ({} bytes).", asset.getFilename(), content.length);
            String url = buildStorageUrl(asset.getFilename(), asset.getContentType());
            repository.updateStorageUrl(asset.getId(), url);

            repository.updateStatus(asset.getId(), AssetStatus.COMPLETED);
            log.info("Simulation of upload completed.");
        } catch (Exception ex) {
            log.error("Error simulating file upload.: "+ex.getMessage());
            repository.updateStatus(asset.getId(), AssetStatus.FAILED);
        }
    }

    private static String buildStorageUrl(String filename, String contentType) {
        String safeName = sanitizeFilename(filename);
        String folder = resolveFolder(contentType, safeName); // "images" | "videos"
        String uniqueName = UUID.randomUUID() + "-" + safeName;
        return folder + "/" + uniqueName;
    }

    private static String resolveFolder(String contentType, String filename) {
        if (contentType != null && !contentType.isBlank()) {
            String lower = contentType.toLowerCase(Locale.ROOT);
            if (lower.startsWith("image/")) return "images";
            if (lower.startsWith("video/")) return "videos";
        }
        String lowerName = filename.toLowerCase(Locale.ROOT);

        if (IMAGE_EXTENSIONS.stream().anyMatch(lowerName::endsWith)) {
            return "images";
        }
        if (VIDEO_EXTENSIONS.stream().anyMatch(lowerName::endsWith)) {
            return "videos";
        }
        throw new BusinessException("Only images and videos are allowed.");
    }

    private static String sanitizeFilename(String raw) {
        String justName = raw.replace("\\", "/");
        int idx = justName.lastIndexOf('/');
        if (idx >= 0) justName = justName.substring(idx + 1);
        justName = justName.replaceAll("[\\r\\n\\t]", "");
        return justName;
    }
}
