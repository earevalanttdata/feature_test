package es.nttdata.assetsproxy.infrastructure.apirest.controller;

import es.nttdata.assetsproxy.application.usecase.UploadAssetUseCase;
import es.nttdata.assetsproxy.infrastructure.apirest.dto.AssetFileUploadRequest;
import es.nttdata.assetsproxy.infrastructure.apirest.dto.AssetFileUploadResponse;
import es.nttdata.assetsproxy.infrastructure.apirest.mapper.AssetDtoMapper;
import es.nttdata.assetsproxy.domain.model.AssetDomain;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mgmt/1/assets")
public class AssetUploadController {

    private final UploadAssetUseCase uploadAssetUseCase;
    private final AssetDtoMapper mapper;

    @PostMapping(path = "/actions/upload", consumes = "application/json", produces = "application/json")
    public ResponseEntity<AssetFileUploadResponse> upload(@Valid @RequestBody AssetFileUploadRequest request) {
        AssetDomain asset = mapper.toDomain(request);
        Long id = uploadAssetUseCase.accept(asset);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new AssetFileUploadResponse(id.toString()));
    }
}
