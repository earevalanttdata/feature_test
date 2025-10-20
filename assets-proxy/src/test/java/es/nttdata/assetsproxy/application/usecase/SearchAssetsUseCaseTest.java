package es.nttdata.assetsproxy.application.usecase;

import es.nttdata.assetsproxy.domain.exception.AssetNotFoundException;
import es.nttdata.assetsproxy.domain.exception.BusinessException;
import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.model.SortDirection;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchAssetsUseCaseTest {

    @Mock
    private AssetRepositoryPort repository;

    private SearchAssetsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SearchAssetsUseCase(repository);
    }

    @Test
    void reject_null_criteria() {
        assertThrows(BusinessException.class, () -> useCase.search(null));
        verifyNoInteractions(repository);
    }

    @Test
    void repository_returns_null_throws_not_found() {
        SearchCriteria criteria = new SearchCriteria(OffsetDateTime.now(), OffsetDateTime.now().plusDays(1), null, null, SortDirection.ASC);
        when(repository.search(criteria)).thenReturn(null);

        assertThrows(AssetNotFoundException.class, () -> useCase.search(criteria));
    }

    @Test
    void empty_list_is_ok() {
        SearchCriteria criteria = new SearchCriteria(OffsetDateTime.now(), OffsetDateTime.now().plusDays(1), "banner", "image/png", SortDirection.DESC);
        when(repository.search(criteria)).thenReturn(Collections.emptyList());

        List<AssetDomain> result = useCase.search(criteria);
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
        List<AssetDomain> stub = List.of(
                new AssetDomain(1L, "logo.png", "image/png", 100, "s3://bucket/logo.png", OffsetDateTime.now(), AssetStatus.COMPLETED)
        );
        when(repository.search(criteria)).thenReturn(stub);

        List<AssetDomain> result = useCase.search(criteria);
        assertEquals(1, result.size());
        assertEquals("logo.png", result.getFirst().getFilename());
    }
}
