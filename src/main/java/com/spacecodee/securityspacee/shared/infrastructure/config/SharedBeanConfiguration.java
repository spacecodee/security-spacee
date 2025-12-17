package com.spacecodee.securityspacee.shared.infrastructure.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spacecodee.securityspacee.shared.application.port.out.IClientIpExtractorPort;
import com.spacecodee.securityspacee.shared.infrastructure.adapter.out.ClientIpExtractorAdapter;

@Configuration
public class SharedBeanConfiguration {

    @Bean
    public @NonNull IClientIpExtractorPort clientIpExtractorPort() {
        return new ClientIpExtractorAdapter();
    }
}
