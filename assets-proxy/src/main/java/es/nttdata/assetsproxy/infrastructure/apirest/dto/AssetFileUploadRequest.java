package es.nttdata.assetsproxy.infrastructure.apirest.dto;

import jakarta.validation.constraints.NotBlank;

public record AssetFileUploadRequestDto(
        @NotBlank String filename,
        @NotBlank String encodedFile,
        @NotBlank String contentType
) { }
