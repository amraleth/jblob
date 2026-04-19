package dev.amraleth.jblob.data.types;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A timezone-agnostic database-independent timestamp backed by a java {@link Instant}.
 *
 * @author amraleth
 * @see JBlobResult
 * @since 1.4
 */

@JBlobImmutable
@JBlobThreadSafe
public final class JBlobTimestamp implements Comparable<JBlobTimestamp> {

    /**
     * UTC epoch reference point.
     */
    public static final JBlobTimestamp EPOCH = new JBlobTimestamp(Instant.EPOCH);

    private final @NotNull
    @NonNull Instant instant;

    /**
     * Private constructor.
     *
     * @param instant The backing UTC instant.
     */
    private JBlobTimestamp(@NotNull @NonNull Instant instant) {
        this.instant = instant;
    }

    /**
     * Returns a timestamp representing the current moment in UTC.
     *
     * @return The current timestamp.
     */
    public static @NotNull @NonNull JBlobTimestamp now() {
        return new JBlobTimestamp(Instant.now());
    }

    /**
     * Constructs a timestamp from a UTC {@link Instant}.
     *
     * @param instant The instant.
     * @return A new timestamp.
     */
    public static @NotNull @NonNull JBlobTimestamp of(@NotNull @NonNull Instant instant) {
        return new JBlobTimestamp(instant);
    }

    /**
     * Constructs a timestamp from milliseconds since the Unix epoch (UTC).
     *
     * @param epochMilli Milliseconds since {@code 1970-01-01T00:00:00Z}.
     * @return A new timestamp.
     */
    public static @NotNull @NonNull JBlobTimestamp ofEpochMilli(long epochMilli) {
        return new JBlobTimestamp(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * Constructs a timestamp from seconds since the Unix epoch (UTC).
     *
     * @param epochSecond Seconds since {@code 1970-01-01T00:00:00Z}.
     * @return A new timestamp.
     */
    public static @NotNull @NonNull JBlobTimestamp ofEpochSecond(long epochSecond) {
        return new JBlobTimestamp(Instant.ofEpochSecond(epochSecond));
    }

    /**
     * Constructs a timestamp from a {@link LocalDateTime}, interpreting it in the given zone.
     *
     * @param dateTime The local date-time.
     * @param zone     The zone to resolve ambiguity.
     * @return A new timestamp.
     */
    public static @NotNull @NonNull JBlobTimestamp of(@NotNull LocalDateTime dateTime, @NotNull @NonNull ZoneId zone) {
        return new JBlobTimestamp(dateTime.atZone(zone).toInstant());
    }

    /**
     * Constructs a timestamp from a JDBC {@link Timestamp}.
     *
     * @param sqlTimestamp The SQL timestamp.
     * @return A new timestamp.
     */
    public static @NotNull @NonNull JBlobTimestamp of(@NotNull @NonNull Timestamp sqlTimestamp) {
        return new JBlobTimestamp(sqlTimestamp.toInstant());
    }

    /**
     * Parses an ISO-8601 string (e.g. {@code "2024-06-01T12:00:00Z"}) into a timestamp.
     *
     * @param text The ISO-8601 string.
     * @return A result containing the parsed timestamp, or a failure if the format is invalid.
     */
    public static @NotNull @NonNull JBlobResult<JBlobTimestamp> parse(@NotNull @NonNull String text) {
        try {
            return JBlobResult.success(new JBlobTimestamp(Instant.parse(text)));
        } catch (DateTimeParseException e) {
            return JBlobResult.failure("Invalid ISO-8601 timestamp: %s".formatted(text), e);
        }
    }

    /**
     * Returns a new timestamp with the given duration added.
     *
     * @param duration The duration to add.
     * @return A new timestamp.
     */
    public @NotNull @NonNull JBlobTimestamp plus(@NotNull @NonNull Duration duration) {
        return new JBlobTimestamp(this.instant.plus(duration));
    }


    /**
     * Returns a new timestamp with the given duration subtracted.
     *
     * @param duration The duration to subtract.
     * @return A new timestamp.
     */
    public @NotNull @NonNull JBlobTimestamp minus(@NotNull @NonNull Duration duration) {
        return new JBlobTimestamp(this.instant.minus(duration));
    }

    /**
     * Returns the {@link Duration} between this timestamp and another.
     * The result is negative if {@code other} is before this timestamp.
     *
     * @param other The other timestamp.
     * @return The duration between the two timestamps.
     */
    public @NotNull @NonNull Duration durationUntil(@NotNull @NonNull JBlobTimestamp other) {
        return Duration.between(this.instant, other.instant);
    }


    /**
     * Returns whether this timestamp is before the given timestamp.
     *
     * @param other The other timestamp.
     * @return {@code true} if this is before {@code other}.
     */
    public boolean isBefore(@NotNull @NonNull JBlobTimestamp other) {
        return this.instant.isBefore(other.instant);
    }

    /**
     * Returns whether this timestamp is after the given timestamp.
     *
     * @param other The other timestamp.
     * @return {@code true} if this is after {@code other}.
     */
    public boolean isAfter(@NotNull @NonNull JBlobTimestamp other) {
        return this.instant.isAfter(other.instant);
    }

    /**
     * Returns whether this timestamp is between {@code from} (inclusive) and {@code to} (exclusive).
     *
     * @param from The start of the range (inclusive).
     * @param to   The end of the range (exclusive).
     * @return {@code true} if this timestamp falls within the range.
     */
    public boolean isBetween(@NotNull @NonNull JBlobTimestamp from, @NotNull @NonNull JBlobTimestamp to) {
        return !this.isBefore(from) && this.isBefore(to);
    }

    @Override
    public int compareTo(@NotNull @NonNull JBlobTimestamp other) {
        return this.instant.compareTo(other.instant);
    }

    /**
     * Returns the underlying UTC {@link Instant}.
     *
     * @return The instant.
     */
    public @NotNull @NonNull Instant toInstant() {
        return this.instant;
    }

    /**
     * Returns the number of milliseconds since the Unix epoch.
     *
     * @return Epoch milliseconds.
     */
    public long toEpochMilli() {
        return this.instant.toEpochMilli();
    }

    /**
     * Returns the number of seconds since the Unix epoch.
     *
     * @return Epoch seconds.
     */
    public long toEpochSecond() {
        return this.instant.getEpochSecond();
    }

    /**
     * Converts this timestamp to a {@link LocalDateTime} in the given zone.
     * Use for display only - never persist a zoned value.
     *
     * @param zone The target timezone.
     * @return A local date-time in the given zone.
     */
    public @NotNull @NonNull LocalDateTime toLocalDateTime(@NotNull @NonNull ZoneId zone) {
        return LocalDateTime.ofInstant(this.instant, zone);
    }

    /**
     * Converts this timestamp to a JDBC {@link Timestamp} for use with JDBC / JPA.
     *
     * @return A SQL timestamp.
     */
    public @NotNull @NonNull Timestamp toSqlTimestamp() {
        return Timestamp.from(this.instant);
    }

    /**
     * Formats this timestamp using a custom {@link DateTimeFormatter}.
     *
     * @param formatter The formatter to use.
     * @return The formatted string.
     */
    public @NotNull @NonNull String format(@NotNull @NonNull DateTimeFormatter formatter) {
        return formatter.format(this.instant);
    }

    @Override
    public String toString() {
        return this.instant.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof JBlobTimestamp other)) return false;
        return this.instant.equals(other.instant);
    }

    @Override
    public int hashCode() {
        return this.instant.hashCode();
    }
}
