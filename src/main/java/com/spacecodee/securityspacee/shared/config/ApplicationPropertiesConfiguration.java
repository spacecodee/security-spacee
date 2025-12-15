package com.spacecodee.securityspacee.shared.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.spacecodee.securityspacee.shared.config.properties.CacheProperties;
import com.spacecodee.securityspacee.shared.config.properties.JwtProperties;
import com.spacecodee.securityspacee.shared.config.properties.SecurityProperties;

@Configuration
@EnableAsync
@EnableConfigurationProperties({
        SecurityProperties.class,
        CacheProperties.class,
        JwtProperties.class
})
public class ApplicationPropertiesConfiguration {
}
