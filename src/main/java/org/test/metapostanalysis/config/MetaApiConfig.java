package org.test.metapostanalysis.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "meta.api")
public class MetaApiConfig {

    /**
     * Base URL of the Meta Graph API (e.g. https://graph.facebook.com).
     */
    private String baseUrl;

    /**
     * API version to use (e.g. v21.0).
     */
    private String version;

    /**
     * Long-lived access token obtained from the Meta for Developers portal.
     */
    private String accessToken;

    /**
     * The Page / User ID whose posts will be analysed. Defaults to "me".
     */
    private String pageId;

    /**
     * Convenience helper that returns the fully-qualified versioned base URL,
     * e.g. {@code https://graph.facebook.com/v21.0}.
     */
    public String getVersionedBaseUrl() {
        return baseUrl + "/" + version;
    }
}
