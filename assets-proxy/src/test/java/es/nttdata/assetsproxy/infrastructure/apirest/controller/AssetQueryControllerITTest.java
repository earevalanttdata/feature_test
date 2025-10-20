package es.nttdata.assetsproxy.infrastructure.apirest.controller;

import es.nttdata.assetsproxy.application.usecase.SearchAssetsUseCase;
import es.nttdata.assetsproxy.boot.TestBootConfig;
import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.infrastructure.apirest.dto.Asset;
import es.nttdata.assetsproxy.infrastructure.apirest.mapper.AssetDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = AssetQueryController.class)
@ContextConfiguration(classes = {
        TestBootConfig.class,
        AssetQueryController.class
})
class AssetQueryControllerITTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private SearchAssetsUseCase useCase;
    @MockitoBean
    private AssetDtoMapper mapper;

    @Test
    void search_maps_query_params_and_returns_list() throws Exception {
        List<AssetDomain> domain = List.of(
                new AssetDomain(7L, "logo.png", "image/png", 10, "s3://logo.png",
                        OffsetDateTime.now(), AssetStatus.COMPLETED)
        );
        List<Asset> dto = List.of(
                new Asset("7", "logo.png", "image/png", "s3://logo.png", 10, domain.get(0).getUploadDate())
        );

        when(useCase.search(any(SearchCriteria.class))).thenReturn(domain);
        when(mapper.toResponseDtoList(domain)).thenReturn(dto);

        mvc.perform(get("/api/mgmt/1/assets")
                        .param("filenamePattern", "logo")
                        .param("filetype", "image/png")
                        .param("sortDirection", "ASC")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("7"))
                .andExpect(jsonPath("$[0].filename").value("logo.png"));
    }

    @Test
    void search_returns_empty_list_ok() throws Exception {
        when(useCase.search(any(SearchCriteria.class))).thenReturn(List.of());
        when(mapper.toResponseDtoList(List.of())).thenReturn(List.of());

        mvc.perform(get("/api/mgmt/1/assets")
                        .param("filenamePattern", "nope")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
