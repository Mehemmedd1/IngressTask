package org.startupevo.metapostanalysis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wraps the {@code cursors} and {@code next}/{@code previous} URLs returned
 * as part of a paginated Graph API response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Paging {

    @JsonProperty("cursors")
    private Cursors cursors;

    @JsonProperty("next")
    private String next;

    @JsonProperty("previous")
    private String previous;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cursors {

        @JsonProperty("before")
        private String before;

        @JsonProperty("after")
        private String after;
    }
}
