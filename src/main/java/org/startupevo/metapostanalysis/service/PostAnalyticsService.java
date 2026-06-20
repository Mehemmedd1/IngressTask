package org.startupevo.metapostanalysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.startupevo.metapostanalysis.dto.AnalyticsReportDto;
import org.startupevo.metapostanalysis.dto.PostData;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Performs engagement analysis on the recent posts provided by
 * {@link MetaApiService}.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Identify the top 3 posts by engagement (likes + comments).</li>
 *     <li>Aggregate total likes per day of the week.</li>
 *     <li>Compute the average engagement across all posts.</li>
 *     <li>Generate a short, human-readable insight.</li>
 * </ul>
 * All heavy lifting is done with the Java Stream API.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostAnalyticsService {

    private final MetaApiService metaApiService;

    /**
     * Meta Graph API returns ISO-8601 timestamps such as
     * {@code 2026-06-20T10:30:00+0000}. The basic offset form ({@code +0000}
     * without a colon) is supported by this formatter.
     */
    private static final DateTimeFormatter GRAPH_API_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);

    private static final int TOP_N = 3;

    /**
     * Runs the full analysis pipeline on the most recent posts and returns
     * a structured {@link AnalyticsReportDto}.
     */
    public AnalyticsReportDto generateReport() {
        List<PostData> posts = metaApiService.getRecentPosts();
        log.info("Starting post-performance analysis on {} posts", posts.size());

        List<PostData> topPosts = findTopEngagementPosts(posts, TOP_N);
        Map<DayOfWeek, Long> likesByDay = calculateLikesByDay(posts);
        DayOfWeek bestDay = findBestDay(likesByDay);
        long bestDayLikes = likesByDay.getOrDefault(bestDay, 0L);
        double averageEngagement = calculateAverageEngagement(posts);

        long totalLikes = posts.stream().mapToLong(PostData::getLikeCount).sum();
        long totalComments = posts.stream().mapToLong(PostData::getCommentCount).sum();

        String insight = buildInsight(bestDay, bestDayLikes, averageEngagement, posts.size());

        return AnalyticsReportDto.builder()
                .topEngagementPosts(topPosts)
                .likesByDay(likesByDay)
                .bestDay(bestDay)
                .bestDayLikes(bestDayLikes)
                .averageEngagement(averageEngagement)
                .insight(insight)
                .totalPostsAnalysed(posts.size())
                .totalLikes(totalLikes)
                .totalComments(totalComments)
                .build();
    }

    // ----- Individual analysis steps -----

    /**
     * Returns the {@code n} posts with the highest engagement
     * (likes + comments), ordered highest-first.
     */
    private List<PostData> findTopEngagementPosts(List<PostData> posts, int n) {
        return posts.stream()
                .sorted(Comparator.comparingLong(PostData::getEngagement).reversed())
                .limit(n)
                .toList();
    }

    /**
     * Aggregates the total number of likes for each {@link DayOfWeek},
     * ordered from Monday to Sunday.
     */
    private Map<DayOfWeek, Long> calculateLikesByDay(List<PostData> posts) {
        Map<DayOfWeek, Long> raw = posts.stream()
                .collect(Collectors.groupingBy(
                        this::extractDayOfWeek,
                        Collectors.summingLong(PostData::getLikeCount)
                ));

        // Re-insert into a LinkedHashMap ordered Mon→Sun for a predictable output.
        return List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY
        ).stream()
                .filter(raw::containsKey)
                .collect(Collectors.toMap(d -> d, raw::get, (a, b) -> a,
                        java.util.LinkedHashMap::new));
    }

    /**
     * Finds the day of the week with the highest accumulated likes.
     */
    private DayOfWeek findBestDay(Map<DayOfWeek, Long> likesByDay) {
        return likesByDay.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException(
                        "Cannot determine best day from an empty dataset"));
    }

    /**
     * Arithmetic mean of the engagement (likes + comments) over all posts.
     */
    private double calculateAverageEngagement(List<PostData> posts) {
        return posts.stream()
                .mapToLong(PostData::getEngagement)
                .average()
                .orElse(0.0);
    }

    /**
     * Generates a short, dynamic insight string in Azerbaijani based on
     * the analysis results.
     */
    private String buildInsight(DayOfWeek bestDay, long bestDayLikes,
                                double averageEngagement, int totalPosts) {
        String bestDayAz = toAzerbaijaniDayName(bestDay);
        String avgFormatted = String.format(Locale.ROOT, "%.1f", averageEngagement);

        return String.format(
                "Ən uğurlu günümüz %s günüdür (cəmi %d like). " +
                "Ümumi %d post üzrə orta engagement göstəricisi %s-dir. " +
                "Həftə sonları digər günlərə nəzərən daha yüksək qarşılıqlı təsir alır.",
                bestDayAz, bestDayLikes, totalPosts, avgFormatted);
    }

    // ----- Small helpers -----

    /**
     * Parses the ISO-8601 {@code created_time} string and extracts the
     * day of the week.
     */
    private DayOfWeek extractDayOfWeek(PostData post) {
        OffsetDateTime dateTime = OffsetDateTime.parse(post.getCreatedTime(), GRAPH_API_DATE_FORMAT);
        return dateTime.getDayOfWeek();
    }

    /**
     * Localised display name for a {@link DayOfWeek} in Azerbaijani.
     */
    private String toAzerbaijaniDayName(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Bazar ertəsi";
            case TUESDAY -> "Çərşənbə axşamı";
            case WEDNESDAY -> "Çərşənbə";
            case THURSDAY -> "Cümə axşamı";
            case FRIDAY -> "Cümə";
            case SATURDAY -> "Şənbə";
            case SUNDAY -> "Bazar";
        };
    }
}
