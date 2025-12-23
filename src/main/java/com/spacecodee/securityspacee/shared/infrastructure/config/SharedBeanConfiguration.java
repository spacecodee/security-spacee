package com.spacecodee.securityspacee.shared.infrastructure.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spacecodee.securityspacee.shared.application.port.out.IClientIpExtractorPort;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;
import com.spacecodee.securityspacee.shared.infrastructure.adapter.out.ClientIpExtractorAdapter;
import com.spacecodee.securityspacee.shared.infrastructure.adapter.out.MessageResolverAdapter;

@Configuration
public class SharedBeanConfiguration {

    @Bean
    public @NonNull IClientIpExtractorPort clientIpExtractorPort() {
        return new ClientIpExtractorAdapter();
    }

    @Bean
    public @NonNull IMessageResolverPort messageResolverPort(@NonNull MessageSource messageSource) {
        return new MessageResolverAdapter(messageSource);
    }
}
