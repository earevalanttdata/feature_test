package es.nttdata.assetsproxy.domain.model;

import java.time.OffsetDateTime;

public record SearchCriteria(
        OffsetDateTime uploadDateStart,
        OffsetDateTime uploadDateEnd,
        String filenamePattern,
        String filetype,
        SortDirection sortDirection
) { }
