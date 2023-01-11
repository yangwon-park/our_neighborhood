package ywphsm.ourneighbor.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import ywphsm.ourneighbor.config.ScriptUtils;
import ywphsm.ourneighbor.service.member.login.security.MemberDetailsService;
import ywphsm.ourneighbor.service.member.login.CustomOAuthUserService;

import javax.servlet.http.HttpSession;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MemberDetailsService memberDetailService;
    private final CustomOAuthUserService customOAuthUserService;
    private final CustomAuthSuccessHandler customAuthSuccessHandler;
    private final CustomAuthFailHandler customAuthFailHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberDetailService).passwordEncoder(passwordEncoder());
    }

    /*
        날씨 API 값 쿠키 저장 시, 방화벽 설정에서 잡혀서 defaultHttpFireWall로 변경
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.httpFirewall(defaultHttpFireWall());
    }

    @Bean
    public HttpFirewall defaultHttpFireWall() {
        return new DefaultHttpFirewall();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable();

        http
                .authorizeRequests()
                .antMatchers("/user/**").hasAnyRole("USER", "SELLER", "ADMIN")
                .antMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()      //권한 인증 실패시 핸들러
                .exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler());

        http
                .formLogin()
                .usernameParameter("userId")            //default값 username
                .passwordParameter("password")
                .loginPage("/login")
                .loginProcessingUrl("/loginSecurity")
                .successHandler(customAuthSuccessHandler)
                .failureHandler(customAuthFailHandler)

                /*
                    logout
                 */
                .and()
                .logout()
                .logoutUrl("/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    HttpSession session = request.getSession();
                    session.invalidate();
                })
                .logoutSuccessHandler((request, response, authentication) -> ScriptUtils.alertAndMovePage(response, "로그아웃되었습니다.", "/"))
                .deleteCookies("remember-me")

                /*
                    OAuth 로그인
                 */
                .and()
                .oauth2Login()
                .loginPage("/login")
                .defaultSuccessUrl("/api-check")
                .userInfoEndpoint()
                .userService(customOAuthUserService);

        //중복 로그인
        http.sessionManagement()
                .maximumSessions(1)                 // 세션 최대 허용 수
                .maxSessionsPreventsLogin(false);   // false이면 중복 로그인하면 이전 로그인이 풀린다.
    }
}