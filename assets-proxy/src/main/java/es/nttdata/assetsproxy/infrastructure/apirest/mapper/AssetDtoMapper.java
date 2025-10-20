package es.nttdata.assetsproxy.infrastructure.apirest.mapper;

import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.infrastructure.apirest.dto.AssetFileUploadRequest;
import es.nttdata.assetsproxy.infrastructure.apirest.dto.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.Base64;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = AssetStatus.class)
public interface AssetDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "uploadDate", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "status", expression = "java(es.nttdata.assetsproxy.domain.model.AssetStatus.PENDING)")
    @Mapping(target = "fileBytes", source = "encodedFile", qualifiedByName = "decodeBase64")
    @Mapping(
            target = "size",
            expression = "java((int) mapEncodedFileToBytes(dto.encodedFile()).length)"
    )
    AssetDomain toDomain(AssetFileUploadRequest dto);

    @Mapping(
            target = "id",
            expression = "java(asset.getId() != null ? asset.getId().toString() : null)"
    )
    @Mapping(target = "url", source = "url")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "uploadDate", source = "uploadDate")
    Asset toResponseDto(AssetDomain asset);

    List<Asset> toResponseDtoList(List<AssetDomain> assets);

    @Named("decodeBase64")
    default byte[] mapEncodedFileToBytes(String encodedFile) {
        try {
            return Base64.getDecoder().decode(encodedFile);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("encodedFile base64 is not valid", ex);
        }
    }
}
