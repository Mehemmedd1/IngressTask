package org.test.metapostanalysis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.test.metapostanalysis.config.MetaApiConfig;
import org.test.metapostanalysis.dto.CommentsSummary;
import org.test.metapostanalysis.dto.LikesSummary;
import org.test.metapostanalysis.dto.PostData;

import java.util.List;

/**
 * Service responsible for providing recent post data from the Meta Graph API.
 * <p>
 * <b>Current mode:</b> MOCK — due to permission restrictions on the
 * Meta Developer dashboard, this service returns a hardcoded list of 20
 * realistic test posts that mirror the real Graph API JSON structure
 * (fields: {@code message}, {@code created_time}, {@code likes.summary.total_count},
 * {@code comments.summary.total_count}).
 * <p>
 * Replace {@link #getRecentPosts()} with a real HTTP call once the access
 * token permissions are approved.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetaApiService {

    private final MetaApiConfig metaApiConfig;

    /**
     * Returns the 20 most recent posts.
     * <p>
     * In this mock implementation the data is generated in-memory with
     * varied dates (last 14 days), diverse engagement numbers (likes 10–500,
     * comments 2–95), and different content themes to produce interesting
     * analysis results in the next layer.
     *
     * @return list of 20 {@link PostData} objects ordered newest-first.
     */
    public List<PostData> getRecentPosts() {
        log.info("Mock mode enabled (token='{}') — returning 20 hardcoded posts",
                metaApiConfig.getAccessToken());

        return List.of(
                // --- 20 June 2026 (Saturday) ---
                buildPost("100001",
                        "Bu gün komandamız ilə birlikdə startup ekosistemi haqqında maraqlı bir müzakirə etdik. Gələcək planlar çox ümidvericidir!",
                        "2026-06-20T10:30:00+0000", 487, 95),

                // --- 19 June 2026 (Friday) ---
                buildPost("100002",
                        "Cümə axşamı kiçik bir məhsul yeniləməsi buraxdıq. İstifadəçi rəyləri çox müsbət oldu.",
                        "2026-06-19T18:45:00+0000", 215, 38),

                // --- 18 June 2026 (Thursday) ---
                buildPost("100003",
                        "Yeni blog yazımız dərc olundu: 'Data analitika niyə hər bir startup üçün vacibdir?' Linki şərhlərdə tapa bilərsiniz.",
                        "2026-06-18T14:00:00+0000", 142, 27),

                // --- 17 June 2026 (Wednesday) ---
                buildPost("100004",
                        "Bu həftə sonuncu sprint planlamasını tamamladıq. Komanda çox məhsuldar işləyir.",
                        "2026-06-17T09:15:00+0000", 56, 8),

                // --- 16 June 2026 (Tuesday) ---
                buildPost("100005",
                        "Maraqlı bir müsahibə verdik. Tezliklə podcast platformalarında dinləyə bilərsiniz.",
                        "2026-06-16T20:00:00+0000", 89, 12),

                // --- 15 June 2026 (Monday) ---
                buildPost("100006",
                        "Yeni həftə, yeni hədəflər. Bu həftənin əsas prioriteti istifadəçi təcrübəsini yaxşılaşdırmaqdır.",
                        "2026-06-15T08:00:00+0000", 34, 5),

                // --- 14 June 2026 (Sunday) ---
                buildPost("100007",
                        "Bazar günü istirahət edə-edə növbəti böyük buraxılışın strategiyasını düşünürük. İdeyalar çox güclüdür.",
                        "2026-06-14T12:20:00+0000", 421, 76),

                // --- 13 June 2026 (Saturday) ---
                buildPost("100008",
                        "Bu gün Bakıda keçirilən Tech Meetup-da iştirak etdik. Yeni tərəfdaşlıqlar qurmaq üçün əla fürsət oldu!",
                        "2026-06-13T16:00:00+0000", 356, 64),

                // --- 12 June 2026 (Friday) ---
                buildPost("100009",
                        "Məhsulumuzun beta versiyası 1000 istifadəçiyə çatdı. Dəstəyiniz üçün təşəkkürlər!",
                        "2026-06-12T11:30:00+0000", 298, 52),

                // --- 11 June 2026 (Thursday) ---
                buildPost("100010",
                        "AI və machine learning sahəsindəki son trendlər haqqında kiçik bir araşdırma paylaşdıq.",
                        "2026-06-11T19:15:00+0000", 175, 31),

                // --- 10 June 2026 (Wednesday) ---
                buildPost("100011",
                        "Bu gün daxili training sessiyamız oldu. Mövzu: effektiv kod review praktikaları.",
                        "2026-06-10T15:40:00+0000", 67, 9),

                // --- 09 June 2026 (Tuesday) ---
                buildPost("100012",
                        "Yeni dizayn konseptimizi sizinlə bölüşürük. Rəy bildirməyi unutmayın!",
                        "2026-06-09T13:00:00+0000", 203, 41),

                // --- 08 June 2026 (Monday) ---
                buildPost("100013",
                        "Həftəyə güclü başladıq. Yeni xüsusiyyətlər üzərində iş davam edir.",
                        "2026-06-08T07:30:00+0000", 45, 6),

                // --- 07 June 2026 (Sunday) ---
                buildPost("100014",
                        "Bazar günü komanda ilə kiçik bir hackathon təşkil etdik. 12 saat, 4 prototip — çox əyləncəli idi!",
                        "2026-06-07T22:00:00+0000", 389, 71),

                // --- 06 June 2026 (Saturday) ---
                buildPost("100015",
                        "Müştərilərimizdən aldığımız ən yaxşı rəyləri sizinlə bölüşürük. Hər biri bizim üçün dəyərlidir.",
                        "2026-06-06T17:30:00+0000", 455, 88),

                // --- 05 June 2026 (Friday) ---
                buildPost("100016",
                        "Cümə günü kiçik bir bug fix buraxdıq. Performans 20% yaxşılaşdı.",
                        "2026-06-05T10:00:00+0000", 112, 18),

                // --- 04 June 2026 (Thursday) ---
                buildPost("100017",
                        "Bu gün investor görüşümüz çox uğurlu keçdi. Növbəti mərhələ üçün planlar hazırdır.",
                        "2026-06-04T14:45:00+0000", 167, 29),

                // --- 03 June 2026 (Wednesday) ---
                buildPost("100018",
                        "Texniki borc ilə bağlı yeni yanaşmamızı paylaşırıq. Uzun vədadi çox səmərəli olacaq.",
                        "2026-06-03T11:20:00+0000", 78, 14),

                // --- 02 June 2026 (Tuesday) ---
                buildPost("100019",
                        "Yeni komanda üzvümüzü salamlayırıq! Frontend sahəsində çox təcrübəli bir mütəxəssis.",
                        "2026-06-02T09:00:00+0000", 23, 3),

                // --- 01 June 2026 (Monday) ---
                buildPost("100020",
                        "İyun ayına başlayırıq! Bu ay ərzində bir neçə böyük yenilik elan edəcəyik. Bizi izləməyə davam edin.",
                        "2026-06-01T08:30:00+0000", 134, 22)
        );
    }

    // ----- Helper factory methods -----

    private PostData buildPost(String id, String message, String createdTime,
                               long likeCount, long commentCount) {
        return PostData.builder()
                .id(id)
                .message(message)
                .createdTime(createdTime)
                .likes(PostData.LikesConnection.builder()
                        .summary(LikesSummary.builder()
                                .totalCount(likeCount)
                                .canLike(true)
                                .hasLiked(false)
                                .build())
                        .build())
                .comments(PostData.CommentsConnection.builder()
                        .summary(CommentsSummary.builder()
                                .totalCount(commentCount)
                                .order("ranked")
                                .canComment(true)
                                .build())
                        .build())
                .build();
    }
}
