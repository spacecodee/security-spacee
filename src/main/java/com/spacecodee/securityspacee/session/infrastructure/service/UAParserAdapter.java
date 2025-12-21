package com.spacecodee.securityspacee.session.infrastructure.service;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import com.spacecodee.securityspacee.session.application.port.out.IUserAgentParser;
import com.spacecodee.securityspacee.session.domain.valueobject.DeviceInfo;

import ua_parser.Client;
import ua_parser.Parser;

@Service
public final class UAParserAdapter implements IUserAgentParser {

    private final Parser uaParser;

    public UAParserAdapter() {
        this.uaParser = new Parser();
    }

    @Override
    public @NonNull DeviceInfo parse(@NonNull String userAgent) {
        Client client = this.uaParser.parse(userAgent);

        String deviceName = this.buildDeviceName(client);
        String browser = this.buildBrowserName(client);
        String os = this.buildOsName(client);
        String deviceType = this.determineDeviceType(client);

        return DeviceInfo.builder()
                .deviceName(deviceName)
                .browser(browser)
                .os(os)
                .deviceType(deviceType)
                .build();
    }

    private @NonNull String buildDeviceName(@NonNull Client client) {
        String family = client.device.family;
        if ("Other".equals(family) || family == null) {
            return "Unknown Device";
        }
        return family;
    }

    private @NonNull String buildBrowserName(@NonNull Client client) {
        String browserFamily = client.userAgent.family;
        String browserMajor = client.userAgent.major;

        if (browserMajor != null) {
            return browserFamily + " " + browserMajor;
        }
        return browserFamily;
    }

    private @NonNull String buildOsName(@NonNull Client client) {
        String osFamily = client.os.family;
        String osMajor = client.os.major;

        if (osMajor != null) {
            return osFamily + " " + osMajor;
        }
        return osFamily;
    }

    private @NonNull String determineDeviceType(@NonNull Client client) {
        String family = client.device.family.toLowerCase();

        if (family.contains("iphone") || family.contains("android")) {
            return "mobile";
        } else if (family.contains("ipad") || family.contains("tablet")) {
            return "tablet";
        } else {
            return "desktop";
        }
    }
}
