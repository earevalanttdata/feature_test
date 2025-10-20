package es.nttdata.assetsproxy.infrastructure.adapter.repository;

import es.nttdata.assetsproxy.domain.model.AssetDomain;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.model.SortDirection;
import es.nttdata.assetsproxy.domain.port.repository.AssetRepositoryPort;
import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
import es.nttdata.assetsproxy.infrastructure.persistence.mapper.AssetEntityMapper;
import es.nttdata.assetsproxy.infrastructure.persistence.spring.AssetJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static es.nttdata.assetsproxy.infrastructure.persistence.spring.AssetSpecifications.*;

@Slf4j
@RequiredArgsConstructor
@Repository
@Transactional
public class AssetRepositoryAdapter implements AssetRepositoryPort {

    private final AssetJpaRepository repository;
    private final AssetEntityMapper mapper;

    @Override
    public AssetDomain save(AssetDomain asset) {
        AssetEntity saved = repository.save(mapper.toEntity(asset));
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AssetDomain> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<AssetDomain> search(SearchCriteria c) {
        Specification<AssetEntity> spec = uploadedAtFrom(c.uploadDateStart())
                .and(uploadedAtTo(c.uploadDateEnd()))
                .and(filenameLike(c.filenamePattern()))
                .and(contentTypeEquals(c.filetype()));
        log.info("Specification {}", spec);
        Sort sort = (c.sortDirection() == SortDirection.ASC)
                ? Sort.by("uploadDate").ascending()
                : Sort.by("uploadDate").descending();

        return repository.findAll(spec, sort).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void updateStatus(Long id, AssetStatus status) {
        repository.findById(id).ifPresent(e -> {
            e.setStatus(status.name());
            repository.save(e);
        });
    }

    @Override
    public void updateStorageUrl(Long id, String storageUrl) {
        repository.findById(id).ifPresent(e -> {
            e.setUrl(storageUrl);
            repository.save(e);
        });
    }
}
