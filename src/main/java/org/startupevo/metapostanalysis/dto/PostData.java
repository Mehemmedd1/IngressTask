package org.startupevo.metapostanalysis.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single post object returned by the Meta Graph API
 * {@code /{page-id}/posts} endpoint when requested with the fields
 * {@code message, created_time, likes{summary(true)}, comments{summary(true)}}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostData {

    /**
     * Unique identifier of the post.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Text content of the post. May be {@code null} for media-only posts.
     */
    @JsonProperty("message")
    private String message;

    /**
     * ISO-8601 timestamp indicating when the post was published.
     */
    @JsonProperty("created_time")
    private String createdTime;

    /**
     * Likes connection. The {@code summary} field contains the total count.
     */
    @JsonProperty("likes")
    private LikesConnection likes;

    /**
     * Comments connection. The {@code summary} field contains the total count.
     */
    @JsonProperty("comments")
    private CommentsConnection comments;

    // ----- Nested connection wrappers -----

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LikesConnection {

        @JsonProperty("summary")
        private LikesSummary summary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommentsConnection {

        @JsonProperty("summary")
        private CommentsSummary summary;
    }

    // ----- Convenience helpers -----

    /**
     * Returns the total number of likes, or {@code 0} when the summary is missing.
     */
    public long getLikeCount() {
        return likes != null && likes.getSummary() != null
                ? likes.getSummary().getTotalCount()
                : 0L;
    }

    /**
     * Returns the total number of comments, or {@code 0} when the summary is missing.
     */
    public long getCommentCount() {
        return comments != null && comments.getSummary() != null
                ? comments.getSummary().getTotalCount()
                : 0L;
    }

    /**
     * Simple engagement metric: likes + comments.
     */
    public long getEngagement() {
        return getLikeCount() + getCommentCount();
    }
}
