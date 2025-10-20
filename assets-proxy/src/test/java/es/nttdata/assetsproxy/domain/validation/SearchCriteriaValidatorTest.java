package es.nttdata.assetsproxy.domain.validation;

import es.nttdata.assetsproxy.domain.exception.BusinessException;
import es.nttdata.assetsproxy.domain.model.SearchCriteria;
import es.nttdata.assetsproxy.domain.model.SortDirection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchCriteriaValidatorTest {

    private static final OffsetDateTime DATE_BEFORE = OffsetDateTime.of(2025, 10, 20, 10, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime DATE_AFTER = OffsetDateTime.of(2025, 10, 21, 10, 0, 0, 0, ZoneOffset.UTC);

    @Mock
    private SearchCriteria criteria;

    private void setupValidMock() {
        when(criteria.filenamePattern()).thenReturn("file.txt");
        when(criteria.filetype()).thenReturn("text/plain");
        when(criteria.uploadDateStart()).thenReturn(DATE_BEFORE);
        when(criteria.uploadDateEnd()).thenReturn(DATE_AFTER);
    }

    @Test
    void shouldPassValidation_whenAllFieldsAreValid() {
        when(criteria.filenamePattern()).thenReturn("file.txt");
        when(criteria.filetype()).thenReturn("text/plain");
        when(criteria.uploadDateStart()).thenReturn(DATE_BEFORE);
        when(criteria.uploadDateEnd()).thenReturn(DATE_AFTER);

        assertDoesNotThrow(() -> SearchCriteriaValidator.validate(criteria));
    }

    @Test
    void shouldFail_whenCriteriaIsNull() {
        BusinessException exception = assertThrows(BusinessException.class,
                () -> SearchCriteriaValidator.validate(null));
        assertEquals("Search criteria cannot be null", exception.getMessage());
    }

    @Test
    void shouldFail_whenFilenamePatternIsBlank() {
        when(criteria.filenamePattern()).thenReturn("   ");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> SearchCriteriaValidator.validate(criteria));

        assertEquals("Filename must not be empty or blank", exception.getMessage());
    }

    @Test
    void shouldFail_whenFiletypeIsBlank() {
        when(criteria.filetype()).thenReturn("");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> SearchCriteriaValidator.validate(criteria));

        assertEquals("Filetype must not be empty or blank", exception.getMessage());
    }

    @Test
    @DisplayName("should fail when uploadDateStart is NOT strictly before uploadDateEnd (Equal)")
    void shouldFail_whenStartDateEqualsEndDate() {
        when(criteria.uploadDateStart()).thenReturn(DATE_AFTER);
        when(criteria.uploadDateEnd()).thenReturn(DATE_BEFORE);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> SearchCriteriaValidator.validate(criteria));

        assertEquals("uploadDateStart must be strictly before uploadDateEnd", exception.getMessage());
    }
}