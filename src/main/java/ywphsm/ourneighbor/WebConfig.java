package ywphsm.ourneighbor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ywphsm.ourneighbor.interceptor.LogInterceptor;
import ywphsm.ourneighbor.interceptor.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*ico", "/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/", "/login", "/sign_up", "/sign_up/confirm-email", "/sign_in",
                        "/logout", "/css/**", "/*ico", "/error", "/images/**", "/sign_in/kakao"
                );
    }


}
