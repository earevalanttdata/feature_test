package es.nttdata.assetsproxy.infrastructure.adapter.async;

import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncAssetPublisherAdapterTest {

    @Mock
    private AssetRepositoryPort repository;

    private AsyncAssetPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsyncAssetPublisherAdapter(repository);
    }

    @Test
    void publishAsync_whenContentIsNull_marksAsFailed() {
        AssetDomain asset = mock(AssetDomain.class);
        when(asset.getId()).thenReturn(123L);
        when(asset.getFileBytes()).thenReturn(null);

        adapter.publishAsync(asset);

        verify(repository).updateStatus(123L, AssetStatus.FAILED);
        verify(repository, never()).updateStorageUrl(anyLong(), anyString());
    }

    @Test
    void publishAsync_whenContentIsEmpty_marksAsFailed() {
        AssetDomain asset = mock(AssetDomain.class);
        when(asset.getId()).thenReturn(456L);
        when(asset.getFileBytes()).thenReturn(new byte[0]);

        adapter.publishAsync(asset);

        verify(repository).updateStatus(456L, AssetStatus.FAILED);
        verify(repository, never()).updateStorageUrl(anyLong(), anyString());
    }

    @Test
    void publishAsync_whenImageFile_updatesUrlAndCompletes() {
        AssetDomain asset = mock(AssetDomain.class);
        when(asset.getId()).thenReturn(1L);

        when(asset.getFilename()).thenReturn("..\\sub/../My Image.JPG");
        when(asset.getFileBytes()).thenReturn(new byte[]{1,2,3});

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        adapter.publishAsync(asset);

        verify(repository).updateStorageUrl(eq(1L), urlCaptor.capture());
        verify(repository).updateStatus(1L, AssetStatus.COMPLETED);

        String url = urlCaptor.getValue();

        assertTrue(url.toLowerCase().contains("images"));
        assertTrue(url.toLowerCase().contains("my image.jpg"));
    }

    @Test
    void publishAsync_whenVideoFile_updatesUrlAndCompletes() {
        AssetDomain asset = mock(AssetDomain.class);
        when(asset.getId()).thenReturn(2L);
        when(asset.getFilename()).thenReturn("clip.MP4");
        when(asset.getFileBytes()).thenReturn(new byte[]{9,9,9});

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        adapter.publishAsync(asset);

        verify(repository).updateStorageUrl(eq(2L), urlCaptor.capture());
        verify(repository).updateStatus(2L, AssetStatus.COMPLETED);

        String url = urlCaptor.getValue();
        assertTrue(url.toLowerCase().contains("videos"));
        assertTrue(url.toLowerCase().contains("clip.mp4"));
    }

    @Test
    void publishAsync_whenUnsupportedExtension_marksAsFailed() {
        // given
        AssetDomain asset = mock(AssetDomain.class);
        when(asset.getId()).thenReturn(3L);
        when(asset.getFilename()).thenReturn("readme.pdf");
        when(asset.getFileBytes()).thenReturn(new byte[]{1});

        adapter.publishAsync(asset);

        verify(repository).updateStatus(3L, AssetStatus.FAILED);
        verify(repository, never()).updateStorageUrl(anyLong(), anyString());
    }

    @Test
    void publishAsync_whenContentTypeIsImage_usesContentTypeFolder() {
        AssetDomain asset = mock(AssetDomain.class);
        when(asset.getId()).thenReturn(10L);
        when(asset.getContentType()).thenReturn("image/png");
        when(asset.getFilename()).thenReturn("whatever.dat");
        when(asset.getFileBytes()).thenReturn(new byte[]{1});

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        adapter.publishAsync(asset);

        verify(repository).updateStorageUrl(eq(10L), urlCaptor.capture());
        verify(repository).updateStatus(10L, AssetStatus.COMPLETED);

        String url = urlCaptor.getValue();
        assertTrue(url.toLowerCase().contains("images"));
    }

    @Test
    void publishAsync_whenContentTypeIsVideo_usesContentTypeFolder() {

        AssetDomain asset = mock(AssetDomain.class);
        when(asset.getId()).thenReturn(11L);
        when(asset.getContentType()).thenReturn("video/mp4");
        when(asset.getFilename()).thenReturn("something.unknown");
        when(asset.getFileBytes()).thenReturn(new byte[]{2});

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        adapter.publishAsync(asset);

        verify(repository).updateStorageUrl(eq(11L), urlCaptor.capture());
        verify(repository).updateStatus(11L, AssetStatus.COMPLETED);

        String url = urlCaptor.getValue();
        assertTrue(url.toLowerCase().contains("videos"));
    }
}
