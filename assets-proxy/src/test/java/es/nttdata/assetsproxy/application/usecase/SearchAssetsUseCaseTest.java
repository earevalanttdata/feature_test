package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.exception.AssetNotFoundException;
import es.nttdata.assetsproxy.domain.exception.BusinessException;
import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.model.SortDirection;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SearchAssetsUseCaseTest {

    private AssetRepositoryPort repository;

    private SearchAssetsUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(AssetRepositoryPort.class);
        useCase = new SearchAssetsUseCase(repository);
    }

    @Test
    void reject_null_criteria() {
        assertThrows(BusinessException.class, () -> useCase.search(null));
        verifyNoInteractions(repository);
    }

    @Test
    void repository_returns_null_throws_not_found() {
        SearchCriteria criteria = new SearchCriteria(null, null, null, null, SortDirection.ASC);
        when(repository.search(criteria)).thenReturn(null);

        assertThrows(AssetNotFoundException.class, () -> useCase.search(criteria));
    }

    @Test
    void empty_list_is_ok() {
        SearchCriteria criteria = new SearchCriteria(null, null, "banner", "image/png", SortDirection.DESC);
        when(repository.search(criteria)).thenReturn(Collections.emptyList());

        List<Asset> result = useCase.search(criteria);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void returns_found_assets() {
        SearchCriteria criteria = new SearchCriteria(
                OffsetDateTime.now().minusDays(1),
                OffsetDateTime.now(),
                "logo",
                "image/png",
                SortDirection.ASC
        );
        List<Asset> stub = List.of(
                new Asset(1L, "logo.png", "image/png", 100L, "s3://bucket/logo.png", OffsetDateTime.now(), AssetStatus.COMPLETED)
        );
        when(repository.search(criteria)).thenReturn(stub);

        List<Asset> result = useCase.search(criteria);
        assertEquals(1, result.size());
        assertEquals("logo.png", result.getFirst().getFilename());
    }
}
