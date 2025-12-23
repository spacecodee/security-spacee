package com.spacecodee.securityspacee.auth.application.port.out;

public interface IPasswordValidator {

    boolean matches(String rawPassword, String hashedPassword);
}
