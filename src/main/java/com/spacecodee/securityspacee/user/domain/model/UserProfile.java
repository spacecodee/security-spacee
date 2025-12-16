package com.spacecodee.securityspacee.user.domain.model;

import java.time.LocalDate;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.user.domain.exception.InvalidUserDataException;

import lombok.Getter;

@Getter
public final class UserProfile {

    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final boolean phoneVerified;
    private final String languageCode;
    private final String avatarUrl;
    private final String bio;
    private final String timezone;
    private final LocalDate dateOfBirth;

    private UserProfile(UserProfileSnapshot snapshot) {
        this.firstName = validateFirstName(snapshot.firstName());
        this.lastName = validateLastName(snapshot.lastName());
        this.phoneNumber = snapshot.phoneNumber();
        this.phoneVerified = snapshot.phoneVerified();
        this.languageCode = validateLanguageCode(snapshot.languageCode());
        this.avatarUrl = snapshot.avatarUrl();
        this.bio = snapshot.bio();
        this.timezone = validateTimezone(snapshot.timezone());
        this.dateOfBirth = snapshot.dateOfBirth();
    }

    @Contract("null -> fail")
    private @NonNull String validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidUserDataException("user.validation.profile.first_name_required");
        }
        if (firstName.length() > 100) {
            throw new InvalidUserDataException("user.validation.profile.first_name_max_length");
        }
        return firstName.trim();
    }

    @Contract("null -> fail")
    private @NonNull String validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidUserDataException("user.validation.profile.last_name_required");
        }
        if (lastName.length() > 100) {
            throw new InvalidUserDataException("user.validation.profile.last_name_max_length");
        }
        return lastName.trim();
    }

    @Contract(pure = true)
    private @NonNull String validateLanguageCode(String languageCode) {
        return (languageCode == null || languageCode.isBlank()) ? "en" : languageCode;
    }

    @Contract(pure = true)
    private @NonNull String validateTimezone(String timezone) {
        return (timezone == null || timezone.isBlank()) ? "UTC" : timezone;
    }

    @Contract("_ -> new")
    public static @NonNull UserProfile of(UserProfileSnapshot snapshot) {
        return new UserProfile(snapshot);
    }

    @Contract(pure = true)
    public @NonNull String getFullName() {
        return firstName + " " + lastName;
    }

    @Contract("_, _ -> new")
    public @NonNull UserProfile withName(String firstName, String lastName) {
        UserProfileSnapshot snapshot = new UserProfileSnapshot(
                firstName != null ? firstName : this.firstName,
                lastName != null ? lastName : this.lastName,
                this.phoneNumber,
                this.phoneVerified,
                this.languageCode,
                this.avatarUrl,
                this.bio,
                this.timezone,
                this.dateOfBirth);
        return new UserProfile(snapshot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(dateOfBirth, that.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, phoneNumber, dateOfBirth);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "fullName='" + getFullName() + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", timezone='" + timezone + '\'' +
                '}';
    }
}
