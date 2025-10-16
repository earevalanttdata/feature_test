package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.exception.AssetNotFoundException;
import es.nttdata.assetsproxy.domain.exception.BusinessException;
import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchAssetsUseCase {

    private final AssetRepositoryPort repository;

    public List<Asset> search(SearchCriteria criteria){
        if (criteria == null) {
            throw new BusinessException("Search criteria must not be null");
        }
        List<Asset> result = repository.search(criteria);
        if(result == null){
            throw new AssetNotFoundException("No assets found with the provided criteria");
        }
        log.info("Items found {}", result.size());
        return result;
    }
}
