package dev.amraleth.jblob.data.types;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents a range of data that can be inclusive or exclusive of the endings.
 *
 * <pre>{@code
 * Range<Integer> nums = Range.closed(1, 10);
 * Range<LocalDate> dates = Range.closedOpen(LocalDate.now(), LocalDate.now().plusDays(7));
 *
 * nums.contains(5);                    // true
 * nums.contains(10);                   // true
 * dates.contains(today);               // true
 * dates.contains(today.plusDays(7));   // false
 * }</pre>
 *
 * @param <T> The type of range.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe(notes = "If T is an immutable type")
@JBlobImmutable
public final class JBlobRange<T extends Comparable<T>> {
    @Getter
    private final T start;
    @Getter
    private final T end;
    @Getter
    private final boolean startInclusive;
    @Getter
    private final boolean endInclusive;

    /**
     * Private constructor for creating a new range.
     *
     * @param start          The start of the range.
     * @param end            The end of the range.
     * @param startInclusive Weather the start of the range is inclusive.
     * @param endInclusive   Weather the end of the range is inclusive.
     */
    private JBlobRange(@NotNull @NonNull T start, @NotNull @NonNull T end, boolean startInclusive, boolean endInclusive) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(String.format("Start %s must not be greater than end %s", start, end));
        }
        this.start = start;
        this.end = end;
        this.startInclusive = startInclusive;
        this.endInclusive = endInclusive;
    }

    /**
     * Constructs a closed range with both the start and the end being inclusive.
     *
     * @param start The start of the range.
     * @param end   The end of the range.
     * @param <T>   The type of the range.
     * @return A new range.
     */
    public static <T extends Comparable<T>> @NotNull @NonNull JBlobRange<T> closed(@NotNull @NonNull T start, @NotNull @NonNull T end) {
        return new JBlobRange<>(start, end, true, true);
    }

    /**
     * Constructs an open range with both the start and the end being exclusive.
     *
     * @param start The start of the range.
     * @param end   The end of the range.
     * @param <T>   The type of the range.
     * @return A new range.
     */
    public static <T extends Comparable<T>> @NotNull @NonNull JBlobRange<T> open(@NotNull @NonNull T start, @NotNull @NonNull T end) {
        return new JBlobRange<>(start, end, false, false);
    }

    /**
     * Constructs a range with the start being inclusive and the end being exclusive.
     *
     * @param start The start of the range.
     * @param end   The end of the range.
     * @param <T>   The type of the range.
     * @return A new range.
     */
    public static <T extends Comparable<T>> @NotNull @NonNull JBlobRange<T> closedOpen(@NotNull @NonNull T start, @NotNull @NonNull T end) {
        return new JBlobRange<>(start, end, true, false);
    }

    /**
     * Constructs a range with the start being exclusive and the end being inclusive.
     *
     * @param start The start of the range.
     * @param end   The end of the range.
     * @param <T>   The type of the range.
     * @return A new range.
     */
    public static <T extends Comparable<T>> @NotNull @NonNull JBlobRange<T> openClosed(@NotNull @NonNull T start, @NotNull @NonNull T end) {
        return new JBlobRange<>(start, end, false, true);
    }

    /**
     * Checks if a given value is in the range.
     *
     * @param value The value to check.
     * @return True if the value is in range, false otherwise.
     */
    public boolean contains(@NotNull @NonNull T value) {
        int startCmp = value.compareTo(this.start);
        int endCmp = value.compareTo(this.end);

        boolean afterStart = this.startInclusive ? startCmp >= 0 : startCmp > 0;
        boolean beforeEnd = this.endInclusive ? endCmp <= 0 : endCmp < 0;

        return afterStart && beforeEnd;
    }

    /**
     * Checks if another range overlaps with this range.
     *
     * @param other The other range.
     * @return True if the ranges overlap, false otherwise.
     */
    public boolean overlaps(@NotNull @NonNull JBlobRange<T> other) {
        return this.contains(other.start) || this.contains(other.end)
                || other.contains(this.start) || other.contains(this.end);
    }

    /**
     * Checks if another range is adjacent to this range.
     *
     * @param other The other range.
     * @return True if the ranges are adjacent, false otherwise.
     */
    public boolean isAdjacentTo(@NotNull @NonNull JBlobRange<T> other) {
        return this.end.compareTo(other.start) == 0 || other.end.compareTo(this.start) == 0;
    }

    /**
     * Indicates if this range is empty.
     *
     * @return True if empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.start.compareTo(this.end) == 0 && (!this.startInclusive || !this.endInclusive);
    }

    /**
     * Gets the intersection of this range and another range.
     *
     * @param other The other range.
     * @return An optional range that is the intersection of this range and the other range.
     */
    public @NotNull @NonNull Optional<JBlobRange<T>> intersection(@NotNull @NonNull JBlobRange<T> other) {
        if (!this.overlaps(other)) return Optional.empty();

        T newStart = this.start.compareTo(other.start) >= 0 ? this.start : other.start;
        T newEnd = this.end.compareTo(other.end) <= 0 ? this.end : other.end;
        boolean newStartInclusive = this.start.compareTo(other.start) == 0
                ? this.startInclusive && other.startInclusive
                : (this.start.compareTo(other.start) >= 0 ? this.startInclusive : other.startInclusive);
        boolean newEndInclusive = this.end.compareTo(other.end) == 0
                ? this.endInclusive && other.endInclusive
                : (this.end.compareTo(other.end) <= 0 ? this.endInclusive : other.endInclusive);

        return Optional.of(new JBlobRange<>(newStart, newEnd, newStartInclusive, newEndInclusive));
    }

    /**
     * Gets the smallest range that includes both this range and another range.
     *
     * @param other The other range.
     * @return The range.
     */
    public @NotNull @NonNull JBlobRange<T> span(@NotNull @NonNull JBlobRange<T> other) {
        T newStart = this.start.compareTo(other.start) <= 0 ? this.start : other.start;
        T newEnd = this.end.compareTo(other.end) >= 0 ? this.end : other.end;
        boolean newStartInclusive = this.start.compareTo(other.start) == 0
                ? this.startInclusive || other.startInclusive
                : (this.start.compareTo(other.start) <= 0 ? this.startInclusive : other.startInclusive);
        boolean newEndInclusive = this.end.compareTo(other.end) == 0
                ? this.endInclusive || other.endInclusive
                : (this.end.compareTo(other.end) >= 0 ? this.endInclusive : other.endInclusive);

        return new JBlobRange<>(newStart, newEnd, newStartInclusive, newEndInclusive);
    }

    @Override
    public String toString() {
        return String.format("Range [%s%s, %s%s", this.startInclusive ? "[" : "(", this.start, this.end, this.endInclusive ? "]" : ")" + "]");
    }
}