package com.cmc.suppin.global.security.config;

import com.cmc.suppin.global.security.reslover.CurrentAccountArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CurrentAccountArgumentResolver currentAccountArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentAccountArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // TODO: 2024-08-07 개발용으로 모든 도메인 허용, 운영 시 아래 주석 해제
//                .allowedOrigins(getAllowOrigins())
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowCredentials(true);
    }

    private String[] getAllowOrigins() {
        return Arrays.asList(
                "http://localhost:3000",
                "https://localhost:3000",
                "https://dev.suppin.store",
                "https://api.suppin.store",
                "https://suppin.store",
                "http://192.168.200.120:3000",
                "https://coherent-midge-probably.ngrok-free.app",
                "https://suppin-web.vercel.app/",
                "https://suppin-survey.vercel.app"
        ).toArray(String[]::new);
    }
}
