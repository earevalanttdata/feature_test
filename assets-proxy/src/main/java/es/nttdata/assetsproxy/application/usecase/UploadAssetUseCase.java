package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.port.async.AssetPublisherPort;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UploadAssetUseCase {

    private final AssetRepositoryPort repository;
    private final AssetPublisherPort publisher;

    public Long accept(Asset asset) {
        Asset saved = repository.save(asset);
        asset.setId(saved.getId());
        publisher.publishAsync(asset);
        return saved.getId();
    }
}
