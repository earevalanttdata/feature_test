package es.nttdata.assetsproxy.domain.port.async;

import es.nttdata.assetsproxy.domain.model.Asset;

public interface AssetPublisherPort {

    /**
     * La llamada NO debe bloquear: el POST responderá 202 inmediatamente.
     */
    void publishAsync(Asset asset);
}
