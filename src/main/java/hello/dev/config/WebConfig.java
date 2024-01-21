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
                .addPathPatterns("/add")
//                .excludePathPatterns("/css/**", "/*.ico", "/error", "/images/*",
//                        "/", "/login", "/logout")
        ;
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry){
//        registry.addMapping("/**")
//                .allowedOrigins(
//                        "http://ec2-54-211-11-22.compute-1.amazonaws.com:8080",
//                        "http://localhost:8080",
//                        "http://localhost:3000",
//                        "https://localhost:3000",
//                        "https://127.0.0.1:3000"
//                )
//                .allowedMethods("GET", "POST", "PUT", "DELETE");
//    }

//    public void addCorsMappings(CorsRegistry registry) {
//        registry
//                .addMapping("/**") // 프로그램에서 제공하는 URL
//                .allowedOrigins("*") // 청을 허용할 출처를 명시, 전체 허용 (가능하다면 목록을 작성한다.
//                .allowedHeaders("*") // 어떤 헤더들을 허용할 것인지
//                .allowedMethods("*") // 어떤 메서드를 허용할 것인지 (GET, POST...)
//                .allowCredentials(false); // 쿠키 요청을 허용한다(다른 도메인 서버에 인증하는 경우에만 사용해야하며, true 설정시 보안상 이슈가 발생할 수 있다)
//    }

//    @Bean
//    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(false);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.setMaxAge(6000L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        FilterRegistrationBean<CorsFilter> filterBean = new FilterRegistrationBean<>(new CorsFilter(source));
//        filterBean.setOrder(0);
//        return filterBean;
//    }

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        // config.addAllowedOrigin("*");
        config.addAllowedOriginPattern("*"); // addAllowedOriginPattern("*") 대신 사용
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
