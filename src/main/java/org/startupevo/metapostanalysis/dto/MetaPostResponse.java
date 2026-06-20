package org.startupevo.metapostanalysis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Top-level response wrapper returned by the Meta Graph API
 * {@code /{page-id}/posts} endpoint.
 * <p>
 * Example shape:
 * <pre>
 * {
 *   "data": [ ... ],
 *   "paging": { ... }
 * }
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaPostResponse {

    /**
     * List of post objects returned in the current page.
     */
    @JsonProperty("data")
    private List<PostData> data;

    /**
     * Pagination metadata supplied by the Graph API.
     */
    @JsonProperty("paging")
    private Paging paging;
}
