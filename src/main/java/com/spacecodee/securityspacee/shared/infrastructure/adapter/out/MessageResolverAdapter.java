package com.spacecodee.securityspacee.shared.infrastructure.adapter.out;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;

public final class MessageResolverAdapter implements IMessageResolverPort {

    private final MessageSource messageSource;

    public MessageResolverAdapter(@NonNull MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public @NonNull String getMessage(@NonNull String code) {
        return this.getMessage(code, (Object[]) null);
    }

    @Override
    public @NonNull String getMessage(@NonNull String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = this.messageSource.getMessage(code, args, code, locale);
        return message != null ? message : code;
    }
}
