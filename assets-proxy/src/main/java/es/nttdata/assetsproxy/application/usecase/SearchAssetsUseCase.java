package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchAssetsUseCase {

    private final AssetRepositoryPort repository;

    public List<Asset> search(SearchCriteria criteria) {
        return repository.search(criteria);
    }
}
