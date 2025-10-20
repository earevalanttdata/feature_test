package es.nttdata.assetsproxy.domain.port.repository;

import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;

import java.util.List;
import java.util.Optional;

public interface AssetRepositoryPort {

    Optional<AssetDomain> findById(Long id);

    AssetDomain save(AssetDomain asset);

    List<AssetDomain> search(SearchCriteria criteria);

    void updateStatus(Long id, AssetStatus status);

    void updateStorageUrl(Long id, String storageUrl);
}
