package com.spacecodee.securityspacee.shared.exception;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(

        @JsonProperty("type") String type,

        @JsonProperty("title") String title,

        @JsonProperty("status") int status,

        @JsonProperty("detail") String detail,

        @JsonProperty("instance") String instance,

        @JsonProperty("timestamp") Instant timestamp,

        @JsonProperty("errors") Map<String, String> errors) {

    @Contract(" -> new")
    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String type;
        private String title;
        private int status;
        private String detail;
        private String instance;
        private Instant timestamp = Instant.now();
        private Map<String, String> errors;

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder errors(Map<String, String> errors) {
            this.errors = errors;
            return this;
        }

        @Contract(" -> new")
        public @NonNull ProblemDetail build() {
            return new ProblemDetail(type, title, status, detail, instance, timestamp, errors);
        }
    }
}
