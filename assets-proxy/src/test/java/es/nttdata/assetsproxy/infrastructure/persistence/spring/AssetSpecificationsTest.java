//package es.nttdata.assetsproxy.infrastructure.persistence.spring;
//
//import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
//import org.junit.jupiter.api.Test;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.time.OffsetDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class AssetSpecificationsTest {
//
//    @Test
//    void uploadedAtFrom_null_returnsConjunction() {
//        Specification<AssetEntity> spec = AssetSpecifications.uploadedAtFrom(null);
//        assertThat(spec).isNotNull();
//    }
//
//    @Test
//    void uploadedAtFrom_withValue_buildsPredicate() {
//        var start = OffsetDateTime.parse("2025-10-01T00:00:00Z");
//        Specification<AssetEntity> spec = AssetSpecifications.uploadedAtFrom(start);
//        assertThat(spec).isNotNull();
//    }
//
//    @Test
//    void filenameLike_null_or_blank_isConjunction() {
//        assertThat(AssetSpecifications.filenameLike(null)).isNotNull();
//        assertThat(AssetSpecifications.filenameLike("")).isNotNull();
//    }
//
//    @Test
//    void contentTypeEquals_buildsPredicate() {
//        assertThat(AssetSpecifications.contentTypeEquals("image/png")).isNotNull();
//    }
//}
package es.nttdata.assetsproxy.infrastructure.persistence.spring;

import es.nttdata.assetsproxy.infrastructure.persistence.entity.AssetEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class AssetSpecificationsBranchesTest {

    @Mock
    private Root<AssetEntity> root;
    @Mock
    private CriteriaQuery<?> query;
    @Mock
    private CriteriaBuilder cb;

    @Test
    void uploadedAtFrom_whenStartNotNull_usesGreaterOrEqual() {
        OffsetDateTime start = OffsetDateTime.now().minusDays(1);

        Path<OffsetDateTime> uploadDatePath = (Path<OffsetDateTime>) mock(Path.class);
        Predicate expected = mock(Predicate.class);

        when(root.get("uploadDate")).thenReturn((Path)uploadDatePath);
        when(cb.greaterThanOrEqualTo(uploadDatePath, start)).thenReturn(expected);

        Predicate result = AssetSpecifications.uploadedAtFrom(start).toPredicate(root, query, cb);

        assertSame(expected, result);
        verify(cb).greaterThanOrEqualTo(uploadDatePath, start);
        verify(cb, never()).conjunction();
    }

    @Test
    void uploadedAtTo_whenEndNotNull_usesLessOrEqual() {
        OffsetDateTime end = OffsetDateTime.now();

        Path<OffsetDateTime> uploadDatePath = (Path<OffsetDateTime>) mock(Path.class);
        Predicate expected = mock(Predicate.class);

        when(root.get("uploadDate")).thenReturn((Path)uploadDatePath);
        when(cb.lessThanOrEqualTo(uploadDatePath, end)).thenReturn(expected);

        Predicate result = AssetSpecifications.uploadedAtTo(end).toPredicate(root, query, cb);

        // then
        assertSame(expected, result);
        verify(cb).lessThanOrEqualTo(uploadDatePath, end);
        verify(cb, never()).conjunction();
    }

    @Test
    void filenameLike_whenPatternNonBlank_usesLowerLikeContaining() {
        String pattern = "initFileName";

        Path<String> filenamePath = (Path<String>) mock(Path.class);
        Expression<String> lowerExpr = (Expression<String>) mock(Expression.class);
        Predicate expected = mock(Predicate.class);

        when(root.get("filename")).thenReturn((Path)filenamePath);
        when(cb.lower(filenamePath)).thenReturn(lowerExpr);

        ArgumentCaptor<String> likeValue = ArgumentCaptor.forClass(String.class);
        when(cb.like(eq(lowerExpr), likeValue.capture())).thenReturn(expected);

        Predicate result = AssetSpecifications.filenameLike(pattern).toPredicate(root, query, cb);

        assertSame(expected, result);
        verify(cb).lower(filenamePath);
        verify(cb).like(eq(lowerExpr), anyString());
        String valueUsed = likeValue.getValue();
        assertTrue(valueUsed.startsWith("%") && valueUsed.endsWith("%"));
        assertTrue(valueUsed.contains(pattern.toLowerCase()));
        verify(cb, never()).conjunction();
    }

    @Test
    void contentTypeEquals_whenMimeNonBlank_usesEqual() {
        String mime = "image/png";

        Path<String> contentTypePath = (Path<String>) mock(Path.class);
        Predicate expected = mock(Predicate.class);

        when(root.get("contentType")).thenReturn((Path)contentTypePath);
        when(cb.equal(contentTypePath, mime)).thenReturn(expected);

        Predicate result = AssetSpecifications.contentTypeEquals(mime).toPredicate(root, query, cb);

        assertSame(expected, result);
        verify(cb).equal(contentTypePath, mime);
        verify(cb, never()).conjunction();
    }
}
