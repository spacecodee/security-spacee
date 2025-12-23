package com.spacecodee.securityspacee.session.infrastructure.service;

import java.net.InetAddress;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.spacecodee.securityspacee.session.application.port.out.IGeoIpService;
import com.spacecodee.securityspacee.session.domain.valueobject.Location;

@Service
@ConditionalOnProperty(name = "session.concurrent.geo-ip.enabled", havingValue = "true")
public final class MaxMindGeoIpService implements IGeoIpService {

    private static final Logger log = LoggerFactory.getLogger(MaxMindGeoIpService.class);
    private static final String UNKNOWN_VALUE = "Unknown";
    private static final String UNKNOWN_COUNTRY_CODE = "XX";

    private final DatabaseReader geoIpReader;

    public MaxMindGeoIpService(@NonNull DatabaseReader geoIpReader) {
        this.geoIpReader = geoIpReader;
    }

    @Override
    public @NonNull Optional<Location> lookup(@NonNull String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            CityResponse response = this.geoIpReader.city(inetAddress);

            City cityRecord = response.city();
            Country countryRecord = response.country();
            com.maxmind.geoip2.record.Location locationRecord = response.location();

            String city = cityRecord.names().get("en") != null ? cityRecord.names().get("en") : UNKNOWN_VALUE;
            String country = countryRecord.names().get("en") != null ? countryRecord.names().get("en")
                    : UNKNOWN_VALUE;
            String countryCode = countryRecord.isoCode() != null ? countryRecord.isoCode() : UNKNOWN_COUNTRY_CODE;
            Double latitude = locationRecord.latitude();
            Double longitude = locationRecord.longitude();

            return Optional.of(Location.builder()
                    .city(city)
                    .country(country)
                    .countryCode(countryCode)
                    .latitude(latitude)
                    .longitude(longitude)
                    .build());
        } catch (GeoIp2Exception e) {
            log.warn("GeoIP lookup failed for IP {}: {}", ipAddress, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error during GeoIP lookup for IP {}: {}", ipAddress, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public boolean isSuspiciousLocation(@NonNull Integer userId, @NonNull Location newLocation) {
        return false;
    }
}
