package com.spacecodee.securityspacee.session.infrastructure.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.port.out.IUserAgentParser;
import com.spacecodee.securityspacee.session.domain.valueobject.DeviceInfo;

public final class SimpleUserAgentParser implements IUserAgentParser {

    private static final String DEVICE_IPHONE = "iPhone";
    private static final String DEVICE_IPAD = "iPad";
    private static final String DEVICE_ANDROID = "Android Device";
    private static final String DEVICE_MAC = "Mac";
    private static final String DEVICE_WINDOWS = "Windows PC";
    private static final String DEVICE_LINUX = "Linux Device";
    private static final String DEVICE_UNKNOWN = "Unknown Device";

    private static final String BROWSER_EDGE = "Edge";
    private static final String BROWSER_CHROME = "Chrome";
    private static final String BROWSER_FIREFOX = "Firefox";
    private static final String BROWSER_SAFARI = "Safari";
    private static final String BROWSER_OPERA = "Opera";
    private static final String BROWSER_UNKNOWN = "Unknown Browser";

    private static final String OS_WINDOWS_10 = "Windows 10";
    private static final String OS_WINDOWS_11 = "Windows 11";
    private static final String OS_MACOS = "macOS";
    private static final String OS_IOS = "iOS";
    private static final String OS_ANDROID = "Android";
    private static final String OS_LINUX = "Linux";
    private static final String OS_UNKNOWN = "Unknown OS";

    private static final String DEVICE_TYPE_MOBILE = "mobile";
    private static final String DEVICE_TYPE_TABLET = "tablet";
    private static final String DEVICE_TYPE_DESKTOP = "desktop";

    private static final String USER_AGENT_IPHONE = "iPhone";
    private static final String USER_AGENT_IPAD = "iPad";
    private static final String USER_AGENT_ANDROID = "Android";
    private static final String USER_AGENT_MACINTOSH = "Macintosh";
    private static final String USER_AGENT_WINDOWS = "Windows";
    private static final String USER_AGENT_LINUX = "Linux";
    private static final String USER_AGENT_EDGE = "Edg/";
    private static final String USER_AGENT_CHROME = "Chrome/";
    private static final String USER_AGENT_FIREFOX = "Firefox/";
    private static final String USER_AGENT_SAFARI = "Safari/";
    private static final String USER_AGENT_OPERA = "Opera";
    private static final String USER_AGENT_OPR = "OPR/";
    private static final String USER_AGENT_MOBILE = "Mobile";
    private static final String USER_AGENT_TABLET = "Tablet";
    private static final String USER_AGENT_WINDOWS_NT_10 = "Windows NT 10.0";
    private static final String USER_AGENT_WINDOWS_NT_11 = "Windows NT 11.0";
    private static final String USER_AGENT_MAC_OS_X = "Mac OS X";
    private static final String USER_AGENT_IPHONE_OS = "iPhone OS";

    private static final Map<String, String> DEVICE_NAME_MAPPING = new LinkedHashMap<>();
    private static final Map<String, String> BROWSER_MAPPING = new LinkedHashMap<>();
    private static final Map<String, String> OS_MAPPING = new LinkedHashMap<>();
    private static final Map<String, String> DEVICE_TYPE_MAPPING = new LinkedHashMap<>();

    static {
        DEVICE_NAME_MAPPING.put(USER_AGENT_IPHONE, DEVICE_IPHONE);
        DEVICE_NAME_MAPPING.put(USER_AGENT_IPAD, DEVICE_IPAD);
        DEVICE_NAME_MAPPING.put(USER_AGENT_ANDROID, DEVICE_ANDROID);
        DEVICE_NAME_MAPPING.put(USER_AGENT_MACINTOSH, DEVICE_MAC);
        DEVICE_NAME_MAPPING.put(USER_AGENT_WINDOWS, DEVICE_WINDOWS);
        DEVICE_NAME_MAPPING.put(USER_AGENT_LINUX, DEVICE_LINUX);

        BROWSER_MAPPING.put(USER_AGENT_EDGE, BROWSER_EDGE);
        BROWSER_MAPPING.put(USER_AGENT_CHROME, BROWSER_CHROME);
        BROWSER_MAPPING.put(USER_AGENT_FIREFOX, BROWSER_FIREFOX);
        BROWSER_MAPPING.put(USER_AGENT_OPERA, BROWSER_OPERA);
        BROWSER_MAPPING.put(USER_AGENT_OPR, BROWSER_OPERA);
        BROWSER_MAPPING.put(USER_AGENT_SAFARI, BROWSER_SAFARI);

        OS_MAPPING.put(USER_AGENT_WINDOWS_NT_10, OS_WINDOWS_10);
        OS_MAPPING.put(USER_AGENT_WINDOWS_NT_11, OS_WINDOWS_11);
        OS_MAPPING.put(USER_AGENT_MAC_OS_X, OS_MACOS);
        OS_MAPPING.put(USER_AGENT_IPHONE_OS, OS_IOS);
        OS_MAPPING.put(USER_AGENT_ANDROID, OS_ANDROID);
        OS_MAPPING.put(USER_AGENT_LINUX, OS_LINUX);

        DEVICE_TYPE_MAPPING.put(USER_AGENT_MOBILE, DEVICE_TYPE_MOBILE);
        DEVICE_TYPE_MAPPING.put(USER_AGENT_IPHONE, DEVICE_TYPE_MOBILE);
        DEVICE_TYPE_MAPPING.put(USER_AGENT_ANDROID, DEVICE_TYPE_MOBILE);
        DEVICE_TYPE_MAPPING.put(USER_AGENT_TABLET, DEVICE_TYPE_TABLET);
        DEVICE_TYPE_MAPPING.put(USER_AGENT_IPAD, DEVICE_TYPE_TABLET);
    }

    @Override
    public @NonNull DeviceInfo parse(@NonNull String userAgent) {
        final String deviceName = this.extractDeviceName(userAgent);
        final String browser = this.extractBrowser(userAgent);
        final String os = this.extractOs(userAgent);
        final String deviceType = this.determineDeviceType(userAgent);

        return DeviceInfo.builder()
                .deviceName(deviceName)
                .browser(browser)
                .os(os)
                .deviceType(deviceType)
                .build();
    }

    private @NonNull String extractDeviceName(@NonNull String userAgent) {
        return this.findMatch(userAgent, DEVICE_NAME_MAPPING, DEVICE_UNKNOWN);
    }

    private @NonNull String extractBrowser(@NonNull String userAgent) {
        final String browser = this.findMatch(userAgent, BROWSER_MAPPING, BROWSER_UNKNOWN);
        if (BROWSER_SAFARI.equals(browser) && userAgent.contains(USER_AGENT_CHROME)) {
            return BROWSER_UNKNOWN;
        }
        return browser;
    }

    private @NonNull String extractOs(@NonNull String userAgent) {
        return this.findMatch(userAgent, OS_MAPPING, OS_UNKNOWN);
    }

    private @NonNull String determineDeviceType(@NonNull String userAgent) {
        for (Map.Entry<String, String> entry : DEVICE_TYPE_MAPPING.entrySet()) {
            if (userAgent.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return DEVICE_TYPE_DESKTOP;
    }

    private @NonNull String findMatch(@NonNull String userAgent, @NonNull Map<String, String> mapping,
                                      @NonNull String defaultValue) {
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            if (userAgent.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return defaultValue;
    }
}
