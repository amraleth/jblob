package dev.amraleth.jblob;

import dev.amraleth.jblob.annotation.JBlobStaticClass;
import dev.amraleth.jblob.exception.JBlobAssertException;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Contains chainable assertions for easier assertions
 *
 * @author amraleth
 * @since 1.0
 */
@JBlobStaticClass
public final class JBlobAssert {

    /**
     * Private constructor.
     */
    private JBlobAssert() {

    }

    /**
     * Base assertion with a predicate. The format values will be applied to the format string.
     *
     * @param value     The value to assert on.
     * @param predicate The predicate to run and test the value on.
     * @param format    The format of the exception string.
     * @param formats   The format values to apply on the format.
     * @param <T>       The type of value this assertion is on.
     * @return The input value's type.
     * @throws IllegalArgumentException If the predicate fails.
     */
    public static <T> @Nullable T assertion(@Nullable T value, @NonNull Predicate<T> predicate,
                                            @NonNull String format, @NonNull Object... formats) {
        if (!predicate.test(value)) {
            throw new JBlobAssertException(String.format(format, formats));
        }
        return value;
    }

    /**
     * Asserts that a given value is not null.
     *
     * @param value The value to check.
     * @param <T>   The input value's type.
     * @return The value.
     */
    public static <T> @Nullable T assertNotNull(@Nullable T value) {
        return assertion(value, Objects::nonNull, "Non null value expected", "");
    }

    /**
     * Asserts that a given value is null.
     *
     * @param value The value to check.
     * @param <T>   The input value's type.
     * @return The value.
     */
    public static <T> @Nullable T assertNull(@Nullable T value) {
        return assertion(value, Objects::isNull, "Null value expected", "");
    }

    /**
     * Asserts that a string is not blank.
     *
     * @param value The string to check.
     * @param <T>   The input value's type.
     * @return The value.
     */
    public static <T extends String> T assertNotBlank(@Nullable T value) {
        return assertion(value, v -> !v.isBlank(), "Non blank value expected", "");
    }

    /**
     * Asserts that a number is positive. Zero is considered a positive number.
     *
     * @param value The number to check.
     * @param <T>   The input value's type.
     * @return The value.
     */
    public static <T extends Number> T assertPositive(@Nullable T value) {
        return assertion(value, v -> v.doubleValue() >= 0, "Positive value expected", "");
    }

    /**
     * Asserts that a number is negative
     *
     * @param value The number to check.
     * @param <T>   The input value's type.
     * @return The value.
     */
    public static <T extends Number> T assertNegative(@Nullable T value) {
        return assertion(value, v -> v.doubleValue() < 0, "Negative value expected", "");
    }

    /**
     * Base two hand assertion, that takes in two values and performs a predicate on them.
     *
     * @param first     The first value.
     * @param second    The second value.
     * @param predicate The predicate to run the first and second value on.
     * @param format    The format of the exception.
     * @param formats   The format values to apply on the format.
     * @param <T>       The type of the first value.
     * @param <K>       The type of the second value.
     * @return The first value.
     */
    public static <T, K> @Nullable T biAssertion(@Nullable T first, @Nullable K second,
                                                 @NonNull BiPredicate<T, K> predicate,
                                                 @NonNull String format, @NonNull Object... formats) {
        if (!predicate.test(first, second)) {
            throw new JBlobAssertException(String.format(format, formats));
        }
        return first;
    }

    /**
     * Asserts that two values are equal.
     *
     * @param first  The first value.
     * @param second The second value.
     * @param <T>    The type of the first value.
     * @param <K>    The type of the second value.
     * @return The first value.
     */
    public static <T, K> @Nullable T assertEquals(@Nullable T first, @Nullable K second) {
        return biAssertion(first, second, Objects::equals, "Expected first and second to match", "");
    }

    /**
     * Asserts that two values are not equal.
     *
     * @param first  The first value.
     * @param second The second value.
     * @param <T>    The type of the first value.
     * @param <K>    The type of the second value.
     * @return The first value.
     */
    public static <T, K> @Nullable T assertNotEquals(@Nullable T first, @Nullable K second) {
        return biAssertion(first, second, (f, s) -> !Objects.equals(f, s), "Expected first and second to not match", "");
    }

}
