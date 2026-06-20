package org.startupevo.metapostanalysis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the {@code summary} block returned inside a comments connection.
 * <p>
 * Example fragment:
 * <pre>
 * "summary": {
 *     "order": "ranked",
 *     "total_count": 7,
 *     "can_comment": true
 * }
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentsSummary {

    @JsonProperty("order")
    private String order;

    @JsonProperty("total_count")
    private long totalCount;

    @JsonProperty("can_comment")
    private boolean canComment;
}
