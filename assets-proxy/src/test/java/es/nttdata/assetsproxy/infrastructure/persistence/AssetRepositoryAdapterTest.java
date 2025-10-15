package es.nttdata.assetsproxy.infrastructure.persistence;

import es.nttdata.assetsproxy.domain.model.Asset;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.model.SortDirection;
import es.nttdata.assetsproxy.infrastructure.adapter.repository.AssetRepositoryAdapter;
import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
import es.nttdata.assetsproxy.infrastructure.persistence.mapper.AssetEntityMapperImpl;
import es.nttdata.assetsproxy.infrastructure.persistence.spring.AssetJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@Import({AssetRepositoryAdapter.class, AssetEntityMapperImpl.class})
class AssetRepositoryAdapterIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("assetsdb")
            .withUsername("assets")
            .withPassword("assets");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired AssetJpaRepository repository;
    @Autowired AssetRepositoryAdapter adapter;

    @BeforeEach
    void seed() {

        AssetEntity a1 = new AssetEntity();
        a1.setFilename("banner_portada.jpg");
        a1.setContentType("image/jpeg");
        a1.setSize(845322L);
        a1.setUrl("images/banner_portada.jpg");
        a1.setUploadDate(OffsetDateTime.parse("2025-10-10T06:52:49.585495Z"));
        a1.setStatus("COMPLETED");

        AssetEntity a2 = new AssetEntity();
        a2.setFilename("video_tutorial.mp4");
        a2.setContentType("video/mp4");
        a2.setSize(157286400L);
        a2.setUrl("videos/video_tutorial.mp4");
        a2.setUploadDate(OffsetDateTime.parse("2025-10-10T11:22:49.585495Z"));
        a2.setStatus("UPLOADING");

        repository.saveAll(List.of(a1, a2));
    }

    @Test
    void search_byDateRange_andFiletype_desc() {
        SearchCriteria criteria = new SearchCriteria(
                OffsetDateTime.parse("2025-10-10T00:00:00Z"),
                OffsetDateTime.parse("2025-10-10T23:59:59Z"),
                null,
                "image/jpeg",
                SortDirection.DESC
        );

        List<Asset> result = adapter.search(criteria);

        assertThat(result).extracting(Asset::getFilename).containsExactly("banner_portada.jpg");

        assertThat(result.get(0).getContentType()).isEqualTo("image/jpeg");
    }

    @Test
    void search_filenameLike_returnsMatches_sortedAsc() {
        SearchCriteria criteria = new SearchCriteria(null, null, "video", null, SortDirection.ASC);

        List<Asset> result = adapter.search(criteria);

        assertThat(result).extracting(Asset::getFilename).containsExactly("video_tutorial.mp4");
    }
}
