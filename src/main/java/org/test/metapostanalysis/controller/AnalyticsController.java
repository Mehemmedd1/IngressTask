package org.test.metapostanalysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.test.metapostanalysis.dto.AnalyticsReportDto;
import org.test.metapostanalysis.service.PostAnalyticsService;

import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Web layer that exposes the post-performance analysis report both as a
 * JSON payload (for API consumers) and as a rendered HTML page (for browsers).
 * <p>
 * Endpoints:
 * <ul>
 *     <li>{@code GET /} — Thymeleaf HTML report page.</li>
 *     <li>{@code GET /api/analytics} — raw JSON of {@link AnalyticsReportDto}.</li>
 *     <li>{@code GET /api/health} — lightweight liveness check for Railway.</li>
 * </ul>
 */
@Controller
@RequiredArgsConstructor
public class AnalyticsController {

    private final PostAnalyticsService postAnalyticsService;

    /**
     * Renders the Bootstrap-styled HTML report page at the application root.
     */
    @GetMapping("/")
    public String reportPage(Model model) {
        AnalyticsReportDto report = postAnalyticsService.generateReport();
        model.addAttribute("report", report);
        model.addAttribute("dayNames", buildDayNamesMap());
        return "analytics";
    }

    /**
     * Returns the same report as a structured JSON object, useful for
     * programmatic clients or quick debugging via curl/Postman.
     */
    @GetMapping(value = "/api/analytics", produces = "application/json")
    @ResponseBody
    public AnalyticsReportDto analyticsJson() {
        return postAnalyticsService.generateReport();
    }

    /**
     * Simple health endpoint that Railway (or any orchestrator) can probe
     * to verify the instance is alive.
     */
    @GetMapping(value = "/api/health", produces = "application/json")
    @ResponseBody
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "meta-post-analysis");
    }

    /**
     * Builds an ordered {@code Map<DayOfWeek, String>} with Azerbaijani
     * display names so the Thymeleaf template does not render raw enum
     * values like {@code SATURDAY}.
     */
    private Map<DayOfWeek, String> buildDayNamesMap() {
        Map<DayOfWeek, String> names = new LinkedHashMap<>();
        names.put(DayOfWeek.MONDAY, "Bazar ertəsi");
        names.put(DayOfWeek.TUESDAY, "Çərşənbə axşamı");
        names.put(DayOfWeek.WEDNESDAY, "Çərşənbə");
        names.put(DayOfWeek.THURSDAY, "Cümə axşamı");
        names.put(DayOfWeek.FRIDAY, "Cümə");
        names.put(DayOfWeek.SATURDAY, "Şənbə");
        names.put(DayOfWeek.SUNDAY, "Bazar");
        return names;
    }
}
