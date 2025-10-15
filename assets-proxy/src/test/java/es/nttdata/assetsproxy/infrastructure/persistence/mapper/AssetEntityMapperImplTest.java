package es.nttdata.assetsproxy.infrastructure.persistence.mapper;

import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.model.AssetStatus;
import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AssetEntityMapperImplTest {

    private final AssetEntityMapper mapper = new AssetEntityMapperImpl();

    @Test
    void toEntity_and_back() {
        OffsetDateTime now = OffsetDateTime.now();
        Asset domain = new Asset(1L, "banner.jpg", "image/jpeg", 123L,
                "images/banner.jpg", now, AssetStatus.COMPLETED);

        AssetEntity entity = mapper.toEntity(domain);
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getFilename()).isEqualTo("banner.jpg");
        assertThat(entity.getContentType()).isEqualTo("image/jpeg");
        assertThat(entity.getStatus()).isEqualTo("COMPLETED");

        Asset roundtrip = mapper.toDomain(entity);
        assertThat(roundtrip.getId()).isEqualTo(1L);
        assertThat(roundtrip.getStatus()).isEqualTo(AssetStatus.COMPLETED);
    }
}
