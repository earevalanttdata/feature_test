package es.nttdata.assetsproxy.infrastructure.apirest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.nttdata.assetsproxy.application.usecase.UploadAssetUseCase;
import es.nttdata.assetsproxy.boot.TestBootConfig;
import es.nttdata.assetsproxy.infrastructure.apirest.dto.AssetFileUploadRequestDto;
import es.nttdata.assetsproxy.infrastructure.apirest.mapper.AssetDtoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AssetUploadController.class)
@ContextConfiguration(classes = {
        TestBootConfig.class,
        AssetUploadController.class
})
class AssetUploadControllerITTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private UploadAssetUseCase uploadAssetUseCase;
    @MockitoBean
    private AssetDtoMapper assetDtoMapper;

    @Test
    void upload_validates_body() throws Exception {
        AssetFileUploadRequestDto invalid = new AssetFileUploadRequestDto(
                "", "ZHVtbXk=", "image/png"
        );
        mvc.perform(post("/api/mgmt/1/assets/actions/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void upload_returns_202_and_id() throws Exception {
        when(uploadAssetUseCase.accept(any())).thenReturn(123L);

        AssetFileUploadRequestDto req = new AssetFileUploadRequestDto(
                "foto.png",
                "ZHVtbXk=",  "image/png"
        );

        mvc.perform(post("/api/mgmt/1/assets/actions/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("123"));
    }
}
