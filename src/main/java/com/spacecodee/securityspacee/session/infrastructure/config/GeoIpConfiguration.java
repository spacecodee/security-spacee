package com.spacecodee.securityspacee.session.infrastructure.config;

import java.io.File;
import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maxmind.geoip2.DatabaseReader;

@Configuration
@ConditionalOnProperty(name = "session.concurrent.geo-ip.enabled", havingValue = "true")
public class GeoIpConfiguration {

    @Bean
    public @NonNull DatabaseReader geoIpDatabaseReader(
            @Value("${session.concurrent.geo-ip.database-path}") String databasePath) throws IOException {
        File database = new File(databasePath);
        return new DatabaseReader.Builder(database).build();
    }
}
