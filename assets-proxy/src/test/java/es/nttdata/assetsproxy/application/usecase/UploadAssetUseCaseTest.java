package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.exception.BusinessException;
import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.port.async.AssetPublisherPort;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadAssetUseCaseTest {

    @Mock
    private AssetRepositoryPort repository;
    @Mock
    private AssetPublisherPort publisher;

    private UploadAssetUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UploadAssetUseCase(repository, publisher);
    }

    @Test
    void reject_when_filename_blank() {
        AssetDomain asset = new AssetDomain(
                null, "   ", "image/png", 10, null, null, AssetStatus.PENDING
        );
        BusinessException ex = assertThrows(BusinessException.class, () -> useCase.accept(asset));
        assertTrue(ex.getMessage().toLowerCase().contains("name is required"));
        verifyNoInteractions(publisher);
        verify(repository, never()).save(any());
    }

    @Test
    void save_and_publish_and_return_id() {
        AssetDomain toSave = new AssetDomain(
                null, "photo.png", "image/png", 10, null, null, AssetStatus.PENDING
        );
        AssetDomain saved = new AssetDomain(
                42L, "photo.png", "image/png", 10, null, toSave.getUploadDate(), AssetStatus.PENDING
        );

        when(repository.save(any(AssetDomain.class))).thenReturn(saved);

        Long id = useCase.accept(toSave);

        assertEquals(42L, id);

        ArgumentCaptor<AssetDomain> captor = ArgumentCaptor.forClass(AssetDomain.class);
        verify(repository).save(any(AssetDomain.class));
        verify(publisher).publishAsync(captor.capture());

        AssetDomain published = captor.getValue();
        assertEquals(42L, published.getId()); // el id del asset en memoria se actualiza tras save
        assertEquals("photo.png", published.getFilename());
    }

    @Test
    void wrap_publisher_exception_as_business_exception() {
        AssetDomain toSave = new AssetDomain(
                null, "video.mp4", "video/mp4", 20, null, null, AssetStatus.PENDING
        );
        AssetDomain saved = new AssetDomain(
                99L, "video.mp4", "video/mp4", 20, null, toSave.getUploadDate(), AssetStatus.PENDING
        );

        when(repository.save(any(AssetDomain.class))).thenReturn(saved);
        doThrow(new RuntimeException("downstream unavailable"))
                .when(publisher).publishAsync(any(AssetDomain.class));

        BusinessException ex = assertThrows(BusinessException.class, () -> useCase.accept(toSave));
        assertTrue(ex.getMessage().toLowerCase().contains("publisher rejected the asset downstream"));
        verify(repository).save(any(AssetDomain.class));
    }
}
