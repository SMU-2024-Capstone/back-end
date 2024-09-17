package capstone.courseweb.user;


import capstone.courseweb.jwt.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

private final JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/callback/kakao").permitAll()
                        .requestMatchers("/keyword/**").permitAll()
                        .requestMatchers("/user/nickname").permitAll()
                        .requestMatchers("/search/category").permitAll()
                        .requestMatchers("/test-result").permitAll()
                        .requestMatchers("/refresh-token").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()  // H2-Console 허용 (중복 허용)
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/home/ai").permitAll()
                        .requestMatchers("/places").permitAll()
                        .requestMatchers("/rating").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
