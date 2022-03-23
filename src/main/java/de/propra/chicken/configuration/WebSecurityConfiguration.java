package de.propra.chicken.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${Chicken.rollen.orga}")
    private Set<String> orga;

    @Value("${Chicken.rollen.tutor}")
    private Set<String> tutoren;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(a -> a
                        .antMatchers("/*", "/error", "/css/**", "/img/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout().logoutSuccessUrl("/login").permitAll()
                .deleteCookies("JSESSIONID").clearAuthentication(true).invalidateHttpSession(true)
                .and()
                .oauth2Login()
                .and()
                .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));



    }

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> createUserService() {
        DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();
        return userRequest -> {
            OAuth2User oauth2User = defaultService.loadUser(userRequest);

            var attributes = oauth2User.getAttributes(); //keep existing attributes

            var authorities = new HashSet<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));


            String login = attributes.get("login").toString();
            System.out.printf("USER LOGIN: %s%n", login);

            if (orga.contains(login)) {
                System.out.printf("Orga User %s%n", login);
                authorities.add(new SimpleGrantedAuthority("ROLE_ORGA"));
            } else if(tutoren.contains(login)){
                System.out.printf("Tutor User %s%n", login);
                authorities.add(new SimpleGrantedAuthority("ROLE_TUTOR"));
            } else {
                System.out.printf("Nicht Team User %s%n", login);
            }

            return new DefaultOAuth2User(authorities, attributes, "login");
        };
    }
}