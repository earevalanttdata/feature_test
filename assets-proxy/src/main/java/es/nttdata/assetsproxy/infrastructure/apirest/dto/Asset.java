package es.nttdata.assetsproxy.infrastructure.apirest.dto;

import java.time.OffsetDateTime;

public record AssetResponseDto(
        String id,
        String filename,
        String contentType,
        String url,
        Integer size,
        OffsetDateTime uploadDate
) { }
