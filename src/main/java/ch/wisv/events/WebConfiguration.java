package ch.wisv.events;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.UrlHandlerFilter;

@Configuration
public class WebConfiguration {

    @Bean
    public UrlHandlerFilter urlHandlerFilter() {
        return UrlHandlerFilter.trailingSlashHandler("/**").wrapRequest().build();
    }
}
