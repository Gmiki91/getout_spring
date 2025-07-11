package com.blue.getout;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200","http://localhost:4000","https://localhost:4200","https://localhost:4000",
                        "https://getout-ng.azurewebsites.net","https://signsign.azurewebsites.net")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS");
    }
}
