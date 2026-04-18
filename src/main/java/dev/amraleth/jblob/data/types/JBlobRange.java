package dev.amraleth.jblob.data.types;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
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
    private final T start;
    private final T end;
    private final boolean startInclusive;
    private final boolean endInclusive;

    /**
     * Private constructor for creating a new range.
     *
     * @param start          The start of the range.
     * @param end            The end of the range.
     * @param startInclusive Weather the start of the range is inclusive.
     * @param endInclusive   Weather the end of the range is inclusive.
     */
    private JBlobRange(@NotNull T start, @NotNull T end, boolean startInclusive, boolean endInclusive) {
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
    public static <T extends Comparable<T>> @NotNull JBlobRange<T> closed(@NotNull T start, @NotNull T end) {
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
    public static <T extends Comparable<T>> @NotNull JBlobRange<T> open(@NotNull T start, @NotNull T end) {
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
    public static <T extends Comparable<T>> @NotNull JBlobRange<T> closedOpen(@NotNull T start, @NotNull T end) {
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
    public static <T extends Comparable<T>> @NotNull JBlobRange<T> openClosed(@NotNull T start, @NotNull T end) {
        return new JBlobRange<>(start, end, false, true);
    }

    /**
     * Checks if a given value is in the range.
     *
     * @param value The value to check.
     * @return True if the value is in range, false otherwise.
     */
    public boolean contains(@NotNull T value) {
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
    public boolean overlaps(@NotNull JBlobRange<T> other) {
        return this.contains(other.start) || this.contains(other.end)
                || other.contains(this.start) || other.contains(this.end);
    }

    /**
     * Checks if another range is adjacent to this range.
     *
     * @param other The other range.
     * @return True if the ranges are adjacent, false otherwise.
     */
    public boolean isAdjacentTo(@NotNull JBlobRange<T> other) {
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
    public @NotNull Optional<JBlobRange<T>> intersection(@NotNull JBlobRange<T> other) {
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
    public @NotNull JBlobRange<T> span(@NotNull JBlobRange<T> other) {
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

    /**
     * Gets the start of the range.
     *
     * @return The start.
     */
    public @NotNull T getStart() {
        return this.start;
    }

    /**
     * Gets the end of the range.
     *
     * @return The end.
     */
    public @NotNull T getEnd() {
        return this.end;
    }

    /**
     * Indicates if the start is inclusive.
     *
     * @return True if the start is inclusive, otherwise false.
     */
    public boolean isStartInclusive() {
        return this.startInclusive;
    }

    /**
     * Indicates if the end is inclusive.
     *
     * @return True if the end is inclusive, otherwise false.
     */
    public boolean isEndInclusive() {
        return this.endInclusive;
    }

    @Override
    public String toString() {
        return String.format("Range [%s%s, %s%s", this.startInclusive ? "[" : "(", this.start, this.end, this.endInclusive ? "]" : ")" + "]");
    }
}