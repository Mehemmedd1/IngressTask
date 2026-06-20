package org.startupevo.metapostanalysis.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.startupevo.metapostanalysis.dto.AnalyticsReportDto;
import org.startupevo.metapostanalysis.dto.PostData;
import org.startupevo.metapostanalysis.service.PostAnalyticsService;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Runs once at application startup and prints the full post-performance
 * analysis to the terminal in a neatly formatted table.
 * <p>
 * This guarantees a visible, structured output even when no browser or
 * API client is connected — useful for Railway's log viewer and for the
 * task reviewers who want to see terminal output.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsTerminalRunner implements CommandLineRunner {

    private final PostAnalyticsService postAnalyticsService;

    private static final DateTimeFormatter SHORT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", new Locale("az", "AZ"));

    private static final DateTimeFormatter GRAPH_API_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);

    private static final int MESSAGE_PREVIEW_LENGTH = 45;

    @Override
    public void run(String... args) {
        AnalyticsReportDto report = postAnalyticsService.generateReport();

        printHeader();
        printTopPosts(report.getTopEngagementPosts());
        printLikesByDay(report.getLikesByDay(), report.getBestDay(), report.getBestDayLikes());
        printSummaryStats(report);
        printInsight(report.getInsight());
        printFooter();
    }

    // ----- Output blocks -----

    private void printHeader() {
        log.info("==========================================================");
        log.info("   META POST PERFORMANCE ANALYSIS REPORT");
        log.info("==========================================================");
    }

    private void printTopPosts(List<PostData> topPosts) {
        log.info("");
        log.info("--- TOP 3 POSTS BY ENGAGEMENT ----------------------------");
        log.info(String.format("%-5s | %-45s | %-18s | %6s | %6s | %7s",
                "Rank", "Message", "Date", "Likes", "Cmnts", "Engage"));
        log.info("------+---------------------------------------------+--------------------+--------+--------+--------");

        for (int i = 0; i < topPosts.size(); i++) {
            PostData post = topPosts.get(i);
            String rank = "#" + (i + 1);
            String messagePreview = truncate(post.getMessage(), MESSAGE_PREVIEW_LENGTH);
            String date = formatShortDate(post.getCreatedTime());

            log.info(String.format("%-5s | %-45s | %-18s | %6d | %6d | %7d",
                    rank, messagePreview, date,
                    post.getLikeCount(), post.getCommentCount(), post.getEngagement()));
        }
    }

    private void printLikesByDay(Map<DayOfWeek, Long> likesByDay,
                                 DayOfWeek bestDay, long bestDayLikes) {
        log.info("");
        log.info("--- LIKES BY DAY OF WEEK --------------------------------");

        for (Map.Entry<DayOfWeek, Long> entry : likesByDay.entrySet()) {
            DayOfWeek day = entry.getKey();
            long likes = entry.getValue();
            String dayName = padRight(toAzerbaijaniDayName(day), 18);
            String marker = day == bestDay ? "  <-- BEST" : "";
            log.info("  {}  |  {} likes{}", dayName, formatNumber(likes), marker);
        }
    }

    private void printSummaryStats(AnalyticsReportDto report) {
        log.info("");
        log.info("--- SUMMARY ----------------------------------------------");
        log.info("  Total posts analysed : {}", report.getTotalPostsAnalysed());
        log.info("  Total likes          : {}", report.getTotalLikes());
        log.info("  Total comments       : {}", report.getTotalComments());
        log.info("  Average engagement   : {}", String.format(Locale.ROOT, "%.2f", report.getAverageEngagement()));
        log.info("  Best day             : {} ({} likes)",
                toAzerbaijaniDayName(report.getBestDay()), report.getBestDayLikes());
    }

    private void printInsight(String insight) {
        log.info("");
        log.info("--- INSIGHT ----------------------------------------------");
        log.info("  {}", insight);
    }

    private void printFooter() {
        log.info("");
        log.info("==========================================================");
        log.info("  Report available at: http://localhost:8080/");
        log.info("  JSON API available at: http://localhost:8080/api/analytics");
        log.info("==========================================================");
    }

    // ----- Small helpers -----

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "(Media post)";
        }
        // Collapse multiple lines into a single line for table display.
        String oneLine = text.replace('\n', ' ').replace('\r', ' ');
        if (oneLine.length() <= maxLength) {
            return oneLine;
        }
        return oneLine.substring(0, maxLength - 3) + "...";
    }

    private String formatShortDate(String isoCreatedTime) {
        try {
            OffsetDateTime dt = OffsetDateTime.parse(isoCreatedTime, GRAPH_API_DATE_FORMAT);
            return dt.format(SHORT_DATE_FORMAT);
        } catch (Exception e) {
            return isoCreatedTime;
        }
    }

    private String padRight(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        return text + " ".repeat(width - text.length());
    }

    /**
     * Formats a long number with grouping separators (e.g. 1298 -> "1,298").
     */
    private String formatNumber(long value) {
        return String.format("%,d", value);
    }

    private String toAzerbaijaniDayName(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> "Bazar ertesi";
            case TUESDAY -> "Chershenbe axshami";
            case WEDNESDAY -> "Chershenbe";
            case THURSDAY -> "Cume axshami";
            case FRIDAY -> "Cume";
            case SATURDAY -> "Shenbe";
            case SUNDAY -> "Bazar";
        };
    }
}
