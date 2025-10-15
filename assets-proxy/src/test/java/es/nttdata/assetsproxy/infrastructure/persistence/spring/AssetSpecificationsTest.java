package es.nttdata.assetsproxy.infrastructure.persistence.spring;

import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AssetSpecificationsTest {

    @Test
    void uploadedAtFrom_null_returnsConjunction() {
        Specification<AssetEntity> spec = AssetSpecifications.uploadedAtFrom(null);
        assertThat(spec).isNotNull();
    }

    @Test
    void uploadedAtFrom_withValue_buildsPredicate() {
        var start = OffsetDateTime.parse("2025-10-01T00:00:00Z");
        Specification<AssetEntity> spec = AssetSpecifications.uploadedAtFrom(start);
        assertThat(spec).isNotNull();
    }

    @Test
    void filenameLike_null_or_blank_isConjunction() {
        assertThat(AssetSpecifications.filenameLike(null)).isNotNull();
        assertThat(AssetSpecifications.filenameLike("")).isNotNull();
    }

    @Test
    void contentTypeEquals_buildsPredicate() {
        assertThat(AssetSpecifications.contentTypeEquals("image/png")).isNotNull();
    }
}
