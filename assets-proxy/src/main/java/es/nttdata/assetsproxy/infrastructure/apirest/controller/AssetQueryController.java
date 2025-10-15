package es.nttdata.assetsproxy.infrastructure.apirest.controller;

import es.nttdata.assetsproxy.application.usecase.SearchAssetsUseCase;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.model.SortDirection;
import es.nttdata.assetsproxy.infrastructure.apirest.dto.AssetResponseDto;
import es.nttdata.assetsproxy.infrastructure.apirest.mapper.AssetDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/mgmt/1/assets")
public class AssetQueryController {

    private final SearchAssetsUseCase useCase;
    private final AssetDtoMapper mapper;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<AssetResponseDto>> search(
            @RequestParam(name = "uploadDateStart", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime uploadDateStart,

            @RequestParam(name = "uploadDateEnd", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime uploadDateEnd,

            @RequestParam(name = "filename", required = false)
            String filenamePattern,

            @RequestParam(name = "filetype", required = false)
            String filetype,

            @RequestParam(name = "sortDirection", required = false, defaultValue = "DESC")
            String sortDirection
    ) {
        SortDirection direction = "ASC".equalsIgnoreCase(sortDirection)?SortDirection.ASC:SortDirection.DESC;

        SearchCriteria criteria = new SearchCriteria(uploadDateStart, uploadDateEnd, filenamePattern, filetype, direction);

        List<AssetResponseDto> response = mapper.toResponseDtoList(useCase.search(criteria));

        if (response.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(response);
    }
}
