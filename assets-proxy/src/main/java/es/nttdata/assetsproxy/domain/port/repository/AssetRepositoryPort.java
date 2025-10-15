package es.nttdata.assetsproxy.domain.port.repository;

import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;

import java.util.List;
import java.util.Optional;

public interface AssetRepositoryPort {

    Optional<Asset> findById(Long id);

    Asset save(Asset asset);

    List<Asset> search(SearchCriteria criteria);

    void updateStatus(Long id, AssetStatus status);

    void updateStorageUrl(Long id, String storageUrl);
}
