package org.test.metapostanalysis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the {@code summary} block returned inside a likes connection.
 * <p>
 * Example fragment:
 * <pre>
 * "summary": {
 *     "total_count": 42,
 *     "can_like": true,
 *     "has_liked": false
 * }
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LikesSummary {

    @JsonProperty("total_count")
    private long totalCount;

    @JsonProperty("can_like")
    private boolean canLike;

    @JsonProperty("has_liked")
    private boolean hasLiked;
}
