package es.nttdata.assetsproxy.domain.port.async;

import es.nttdata.assetsproxy.domain.model.AssetDomain;

public interface AssetPublisherPort {

    void publishAsync(AssetDomain asset);
}
