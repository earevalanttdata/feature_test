package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.exception.BusinessException;
import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.port.async.AssetPublisherPort;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadAssetUseCaseTest {

    private AssetRepositoryPort repository;
    private AssetPublisherPort publisher;
    private UploadAssetUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(AssetRepositoryPort.class);
        publisher = mock(AssetPublisherPort.class);
        useCase = new UploadAssetUseCase(repository, publisher);
    }

    @Test
    void reject_when_filename_blank() {
        Asset asset = new Asset(
                null, "   ", "image/png", 10L, null, null, AssetStatus.PENDING
        );
        BusinessException ex = assertThrows(BusinessException.class, () -> useCase.accept(asset));
        assertTrue(ex.getMessage().toLowerCase().contains("name is required"));
        verifyNoInteractions(publisher);
        verify(repository, never()).save(any());
    }

    @Test
    void save_and_publish_and_return_id() {
        Asset toSave = new Asset(
                null, "photo.png", "image/png", 10L, null, null, AssetStatus.PENDING
        );
        Asset saved = new Asset(
                42L, "photo.png", "image/png", 10L, null, toSave.getUploadDate(), AssetStatus.PENDING
        );

        when(repository.save(any(Asset.class))).thenReturn(saved);

        Long id = useCase.accept(toSave);

        assertEquals(42L, id);

        ArgumentCaptor<Asset> captor = ArgumentCaptor.forClass(Asset.class);
        verify(repository).save(any(Asset.class));
        verify(publisher).publishAsync(captor.capture());

        Asset published = captor.getValue();
        assertEquals(42L, published.getId()); // el id del asset en memoria se actualiza tras save
        assertEquals("photo.png", published.getFilename());
    }

    @Test
    void wrap_publisher_exception_as_business_exception() {
        Asset toSave = new Asset(
                null, "video.mp4", "video/mp4", 20L, null, null, AssetStatus.PENDING
        );
        Asset saved = new Asset(
                99L, "video.mp4", "video/mp4", 20L, null, toSave.getUploadDate(), AssetStatus.PENDING
        );

        when(repository.save(any(Asset.class))).thenReturn(saved);
        doThrow(new RuntimeException("downstream unavailable"))
                .when(publisher).publishAsync(any(Asset.class));

        BusinessException ex = assertThrows(BusinessException.class, () -> useCase.accept(toSave));
        assertTrue(ex.getMessage().toLowerCase().contains("publisher rejected the asset downstream"));
        verify(repository).save(any(Asset.class));
    }
}
