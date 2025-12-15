package com.spacecodee.securityspacee.user.adapter.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(

        @NotNull(message = "{user.validation.username.required}")
        @NotBlank(message = "{user.validation.username.blank}")
        @Size(min = 3, max = 50, message = "{user.validation.username.min_length}")
        @Pattern(regexp = "^\\w+$", message = "{user.validation.username.invalid_format}")
        @JsonProperty("username") String username,

        @NotNull(message = "{user.validation.email.required}")
        @NotBlank(message = "{user.validation.email.blank}")
        @Email(message = "{user.validation.email.invalid_format}")
        @Size(max = 255, message = "{user.validation.email.max_length}")
        @JsonProperty("email") String email,

        @NotNull(message = "{user.validation.password.required}")
        @NotBlank(message = "{user.validation.password.blank}")
        @Size(min = 8, max = 128, message = "{user.validation.password.min_length}")
        @JsonProperty("password") String password,

        @JsonProperty("user_type") UserType userType,

        @JsonProperty("first_name") String firstName,

        @JsonProperty("last_name") String lastName,

        @JsonProperty("phone_number")
        @Size(max = 20, message = "{user.validation.profile.phone_max_length}") String phoneNumber,

        @JsonProperty("language_code")
        @Size(max = 10) String languageCode,

        @JsonProperty("avatar_url") String avatarUrl,

        @JsonProperty("bio")
        @Size(max = 500, message = "{user.validation.profile.bio_max_length}") String bio,

        @JsonProperty("timezone")
        @Size(max = 50) String timezone,

        @JsonProperty("date_of_birth")
        @Past(message = "{user.validation.profile.date_of_birth_past}") LocalDate dateOfBirth) {
}
