package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    public static final String TEST_USER_NAME = "test-user";
    public static final String TEST_USER_PASS = "test-pass";
    public static final String TEST_USER_ROLE = "test-role";

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername(TEST_USER_NAME)
                .password(passwordEncoder.encode(TEST_USER_PASS))
                .roles(TEST_USER_ROLE)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    //@Bean
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(
                        RateConditionRoleTest.Resource.Endpoints.ROLE_MATCH,
                        RateConditionRoleTest.Resource.Endpoints.ROLE_NO_MATCH)
                .hasRole(TEST_USER_ROLE)
                .antMatchers("/**")
                .permitAll()
                .and()
                .httpBasic();
        //return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}