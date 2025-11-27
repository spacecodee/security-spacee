package com.spacecodee.securityspacee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecuritySpaceeApplication {

    private SecuritySpaceeApplication() {
        // Private constructor to prevent instantiation
    }

    static void main(String[] args) {
        SpringApplication.run(SecuritySpaceeApplication.class, args);
    }

}
