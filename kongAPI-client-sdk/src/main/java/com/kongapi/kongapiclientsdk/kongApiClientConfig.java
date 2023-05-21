package com.kongapi.kongapiclientsdk;

import com.kongapi.kongapiclientsdk.client.kongApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ComponentScan
@ConfigurationProperties(prefix = "kongapi.client")
public class kongApiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public kongApiClient kongApiClient(){
        return new kongApiClient(accessKey,secretKey);
    }
}
