package org.test.metapostanalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

/**
 * Structured report that aggregates the results of the post-performance
 * analysis performed on the last 20 Meta posts.
 * <p>
 * Contains:
 * <ul>
 *     <li>The top 3 posts by engagement (likes + comments).</li>
 *     <li>Total likes accumulated per day of the week.</li>
 *     <li>The best-performing day of the week.</li>
 *     <li>The average engagement across all analysed posts.</li>
 *     <li>A dynamically generated human-readable insight.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReportDto {

    /**
     * Top 3 posts ranked by engagement (likes + comments), highest first.
     */
    private List<PostData> topEngagementPosts;

    /**
     * Total number of likes collected for each day of the week.
     * Only days that actually have posts are present in the map.
     */
    private Map<DayOfWeek, Long> likesByDay;

    /**
     * Day of the week with the highest accumulated likes.
     */
    private DayOfWeek bestDay;

    /**
     * Total likes accumulated on the best day.
     */
    private long bestDayLikes;

    /**
     * Arithmetic mean of the engagement score (likes + comments)
     * across all analysed posts.
     */
    private double averageEngagement;

    /**
     * Dynamically generated, human-readable summary of the analysis.
     */
    private String insight;

    /**
     * Total number of posts that were analysed.
     */
    private int totalPostsAnalysed;

    /**
     * Sum of all likes across all analysed posts.
     */
    private long totalLikes;

    /**
     * Sum of all comments across all analysed posts.
     */
    private long totalComments;
}
