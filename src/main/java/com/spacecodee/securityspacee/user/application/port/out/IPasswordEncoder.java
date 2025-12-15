package com.spacecodee.securityspacee.user.application.port.out;

public interface IPasswordEncoder {

    String encode(String plainPassword);

    boolean matches(String plainPassword, String hashedPassword);
}
