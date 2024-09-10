package capstone.courseweb.jwt.config;

import capstone.courseweb.jwt.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtFilterConfig {

    private final JwtAuthProvider jwtAuthProvider;

    public JwtFilterConfig(JwtAuthProvider jwtAuthProvider) {
        this.jwtAuthProvider = jwtAuthProvider;
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtAuthProvider);
    }
}
