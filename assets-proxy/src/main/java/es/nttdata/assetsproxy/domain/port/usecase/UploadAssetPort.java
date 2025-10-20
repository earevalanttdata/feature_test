package es.nttdata.assetsproxy.domain.port.usecase;

import es.nttdata.assetsproxy.domain.model.AssetDomain;

public interface UploadAssetPort {

    Long accept(AssetDomain asset);

}
