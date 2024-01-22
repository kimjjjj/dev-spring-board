package hello.dev.config;

import hello.dev.interceptor.LoginCheckInterceptor;
import hello.dev.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${resource.handler}")
    private String resourceHandler;

    @Value("${resource.location}")
    private String resourceLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String defaultPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources";
//        String path = defaultPath + File.separator + "static" + File.separator + "images" + File.separator;
        registry.addResourceHandler(resourceHandler)
//                .addResourceLocations(resourceLocation);
                .addResourceLocations("file:///" + System.getProperty("user.dir") + File.separator + "images" + File.separator);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/add", "/mypage", "/mypage/*")
//                .excludePathPatterns("/css/**", "/*.ico", "/error", "/images/*",
//                        "/", "/login", "/logout")
        ;
    }
}
