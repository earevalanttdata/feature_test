package es.nttdata.assetsproxy.domain.validation;

import es.nttdata.assetsproxy.domain.exception.BusinessException;

import es.nttdata.assetsproxy.domain.model.SearchCriteria;

public final class SearchCriteriaValidator {

    private SearchCriteriaValidator() {}

    public static void validate(SearchCriteria criteria) {
        if (criteria == null) {
            throw new BusinessException("Search criteria cannot be null");
        }

        if (isBlank(criteria.filenamePattern())) {
            throw new BusinessException(("Filename must not be empty or blank"));
        }

        if (isBlank(criteria.filetype())) {
            throw new BusinessException(("Filetype must not be empty or blank"));
        }

        if (criteria.uploadDateStart() != null &&  criteria.uploadDateEnd() != null
                && !criteria.uploadDateStart().isBefore(criteria.uploadDateEnd())) {
            throw new BusinessException(("uploadDateStart must be strictly before uploadDateEnd"));
        }
    }

    private static boolean isBlank(String value) {
        return value != null && value.isBlank();
    }
}
