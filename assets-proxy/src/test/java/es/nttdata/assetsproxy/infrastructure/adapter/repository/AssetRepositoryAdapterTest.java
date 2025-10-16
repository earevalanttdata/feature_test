package es.nttdata.assetsproxy.infrastructure.adapter.repository;

import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.model.SortDirection;
import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
import es.nttdata.assetsproxy.infrastructure.persistence.mapper.AssetEntityMapper;
import es.nttdata.assetsproxy.infrastructure.persistence.spring.AssetJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetRepositoryAdapterTest {

    @Mock
    private AssetJpaRepository repository;
    @Mock
    private AssetEntityMapper mapper;

    private AssetRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AssetRepositoryAdapter(repository, mapper);
    }

    @Test
    void save_mapsDomainToEntity_persists_andMapsBack() {
        OffsetDateTime now = OffsetDateTime.now();
        Asset domainIn = new Asset(null, "banner.jpg", "image/jpeg", 123L,null, now, AssetStatus.PENDING
        );

        AssetEntity entityIn = new AssetEntity();
        entityIn.setId(null);
        entityIn.setFilename("banner.jpg");
        entityIn.setContentType("image/jpeg");
        entityIn.setSize(123L);
        entityIn.setUploadDate(now);
        entityIn.setStatus("PENDING");

        AssetEntity entitySaved = new AssetEntity();
        entitySaved.setId(101L);
        entitySaved.setFilename("banner.jpg");
        entitySaved.setContentType("image/jpeg");
        entitySaved.setSize(123L);
        entitySaved.setUploadDate(now);
        entitySaved.setStatus("PENDING");
        entitySaved.setUrl("images/banner.jpg");

        Asset domainOut =
                new Asset(101L, "banner.jpg", "image/jpeg", 123L, "images/banner.jpg", now, AssetStatus.PENDING);

        when(mapper.toEntity(domainIn)).thenReturn(entityIn);
        when(repository.save(entityIn)).thenReturn(entitySaved);
        when(mapper.toDomain(entitySaved)).thenReturn(domainOut);

        Asset result = adapter.save(domainIn);

        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("images/banner.jpg", result.getUrl());
        verify(mapper).toEntity(domainIn);
        verify(repository).save(entityIn);
        verify(mapper).toDomain(entitySaved);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void search_buildsSpecification_andDelegatesToFindAll_withSortByUploadDateAsc() {
        SearchCriteria criteria =
                new SearchCriteria(OffsetDateTime.now().minusDays(7), OffsetDateTime.now(), "invoice", "image/png", SortDirection.ASC);

        AssetEntity e1 = new AssetEntity(); e1.setId(1L); e1.setFilename("invoice-1.png");
        AssetEntity e2 = new AssetEntity(); e2.setId(2L); e2.setFilename("invoice-2.png");

        Asset a1 = new Asset(1L, "invoice-1.png", "image/png", null, null, null, AssetStatus.PENDING);
        Asset a2 = new Asset(2L, "invoice-2.png", "image/png", null, null, null, AssetStatus.PENDING);

        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(a1);
        when(mapper.toDomain(e2)).thenReturn(a2);

        List<Asset> result = adapter.search(criteria);

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repository).findAll(any(Specification.class), sortCaptor.capture());

        Sort sort = sortCaptor.getValue();
        assertNotNull(sort);
        Sort.Order order = sort.stream().findFirst().orElseThrow();
        assertEquals("uploadDate", order.getProperty());
        assertTrue(order.isAscending());

        assertEquals(List.of(a1, a2), result);
        verify(mapper).toDomain(e1);
        verify(mapper).toDomain(e2);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void search_whenDirectionIsNull_defaultsToDescendingUploadDate() {
        SearchCriteria criteria = new SearchCriteria(
                null, null, null, null, null
        );

        when(repository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

        List<Asset> result = adapter.search(criteria);

        assertNotNull(result);

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repository).findAll(any(Specification.class), sortCaptor.capture());
        Sort.Order order = sortCaptor.getValue().stream().findFirst().orElseThrow();

        assertEquals("uploadDate", order.getProperty());
        assertTrue(order.isDescending());
    }

    @Test
    void updateStatus_findsById_updatesAndSaves() {
        AssetEntity entity = new AssetEntity();
        entity.setId(10L);
        entity.setStatus("PENDING");

        when(repository.findById(10L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        adapter.updateStatus(10L, AssetStatus.COMPLETED);

        assertEquals("COMPLETED", entity.getStatus());
        verify(repository).findById(10L);
        verify(repository).save(entity);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void updateStatus_whenIdNotFound_doesNothing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        adapter.updateStatus(999L, AssetStatus.FAILED);

        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void updateStorageUrl_findsById_updatesAndSaves() {
        AssetEntity entity = new AssetEntity();
        entity.setId(20L);
        entity.setUrl(null);

        when(repository.findById(20L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        adapter.updateStorageUrl(20L, "https://cdn/x/y.jpg");

        assertEquals("https://cdn/x/y.jpg", entity.getUrl());
        verify(repository).findById(20L);
        verify(repository).save(entity);
    }

    @Test
    void updateStorageUrl_whenIdNotFound_doesNothing() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        adapter.updateStorageUrl(404L, "ignored");

        verify(repository).findById(404L);
        verify(repository, never()).save(any());
    }
}
