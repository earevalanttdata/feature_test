package es.nttdata.assetsproxy.domain.port.usecase;

import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;

import java.util.List;

public interface SearchAssetsPort {

    List<AssetDomain> search(SearchCriteria criteria);
}
