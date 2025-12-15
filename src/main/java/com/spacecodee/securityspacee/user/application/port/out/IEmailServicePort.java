package com.spacecodee.securityspacee.user.application.port.out;

import org.jspecify.annotations.NonNull;

public interface IEmailServicePort {

    void sendWelcomeEmail(@NonNull String toEmail, @NonNull String username);

    void sendPasswordResetEmail(@NonNull String toEmail, @NonNull String resetToken);

    void sendEmailVerificationEmail(@NonNull String toEmail, @NonNull String verificationToken);
}
