package ru.ntwz.javaspringbootauthexample.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("jwt")
public class JwtConfig {
    public String secret;
    public Long expiration;
}
