package es.nttdata.assetsproxy.infrastructure.persistence.mapper;

import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
                         uses = AssetStatus.class)
public interface AssetEntityMapper {

    @Mapping(
            target = "status",
            expression = "java(asset.getStatus() != null " +
                    "? asset.getStatus().name() " +
                    ": AssetStatus.PENDING.name())"
    )
    AssetEntity toEntity(AssetDomain asset);

    AssetDomain toDomain(AssetEntity entity);
}
