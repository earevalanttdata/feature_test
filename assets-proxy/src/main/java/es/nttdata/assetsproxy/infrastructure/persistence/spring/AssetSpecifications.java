package es.nttdata.assetsproxy.infrastructure.persistence.spring;

import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public final class AssetSpecifications {

    private AssetSpecifications() {}

    public static Specification<AssetEntity> uploadedAtFrom(OffsetDateTime start) {
        return (root, q, cb) -> start == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("uploadDate"), start);
    }

    public static Specification<AssetEntity> uploadedAtTo(OffsetDateTime end) {
        return (root, q, cb) -> end == null ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("uploadDate"), end);
    }

    public static Specification<AssetEntity> filenameLike(String pattern) {
        return (root, q, cb) -> (pattern == null || pattern.isBlank()) ? cb.conjunction()
                : cb.like(cb.lower(root.get("filename")), "%" + pattern.toLowerCase() + "%");
    }

    public static Specification<AssetEntity> contentTypeEquals(String mime) {
        return (root, q, cb) -> (mime == null || mime.isBlank()) ? cb.conjunction()
                : cb.equal(root.get("contentType"), mime);
    }
}
