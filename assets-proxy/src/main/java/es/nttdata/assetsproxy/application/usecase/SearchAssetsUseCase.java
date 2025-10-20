package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.exception.AssetNotFoundException;
import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import es.nttdata.assetsproxy.domain.port.usecase.SearchAssetsPort;
import es.nttdata.assetsproxy.domain.validation.SearchCriteriaValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchAssetsUseCase implements SearchAssetsPort {

    private final AssetRepositoryPort repository;

    @Override
    public List<AssetDomain> search(SearchCriteria criteria) {
        SearchCriteriaValidator.validate(criteria);
        List<AssetDomain> result = repository.search(criteria);
        if(result == null){
            throw new AssetNotFoundException("No assets found with the provided criteria");
        }
        log.info("Items found {}", result.size());
        return result;
    }
}
