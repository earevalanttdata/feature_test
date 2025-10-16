package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.exception.BusinessException;
import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.port.async.AssetPublisherPort;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UploadAssetUseCase {

    private final AssetRepositoryPort repository;
    private final AssetPublisherPort publisher;

    public Long accept(Asset asset) {
        if (asset == null) {
            throw new BusinessException("Asset must not be null");
        }
        if (asset.getFilename() == null || asset.getFilename().isBlank()) {
            throw new BusinessException("Asset name is required");
        }
        log.info("Saving data from file {}", asset.getFilename());
        Asset saved = repository.save(asset);
        asset.setId(saved.getId());
        try{
            publisher.publishAsync(asset);
        }catch (Exception e) {
            throw new BusinessException("Upload publisher rejected the asset "+e.getMessage());
        }
        log.info("The data has been saved successfully {}", asset.getFilename());
        return saved.getId();
    }
}
