package dev.amraleth.jblob.func;

import dev.amraleth.jblob.data.types.JBlobResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Functional interface for allowing checked exceptions inside a function block.
 *
 * @param <T> The type of the function.
 * @param <R> The type of the result.
 * @author amraleth
 * @see JBlobResult
 * @since 1.0
 */
@FunctionalInterface
public interface JBlobThrowingFunction<T, R> {

    /**
     * Application method for the function interface.
     *
     * @param value The value to apply to the function.
     * @return The return value of the function.
     * @throws Exception If an error during application happened.
     */
    R apply(T value) throws Exception;

    /**
     * Converts the throwing function to a java function.
     *
     * @return The function.
     * @see Function
     */
    default @NotNull Function<T, R> toFunction() {
        return value -> {
            try {
                return this.apply(value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Converts the throwing function to a java function that returns the result.
     *
     * @return The function.
     * @see JBlobResult
     * @see Function
     */
    default @NotNull Function<T, JBlobResult<R>> toResult() {
        return value -> {
            try {
                return JBlobResult.success(this.apply(value));
            } catch (Exception e) {
                return JBlobResult.failure(e.getMessage(), e);
            }
        };
    }

    /**
     * Chains a throwing function onto this function.
     *
     * @param after The function to run after this function.
     * @param <V>   The new return value.
     * @return The function.
     */
    default <V> @NotNull JBlobThrowingFunction<T, V> andThen(@NotNull JBlobThrowingFunction<R, V> after) {
        return value -> after.apply(this.apply(value));
    }

    /**
     * Runs a throwing function before this function.
     *
     * @param before The function to run before this function.
     * @param <V>    The after return type.
     * @return The function.
     */
    default <V> @NotNull JBlobThrowingFunction<V, R> compose(@NotNull JBlobThrowingFunction<V, T> before) {
        return value -> this.apply(before.apply(value));
    }

    /**
     * Wraps a throwing function into a java function.
     *
     * @param function The throwing function to wrap.
     * @param <T>      The function type.
     * @param <R>      The return type.
     * @return The java function.
     * @see Function
     */
    static <T, R> @NotNull Function<T, R> wrap(@NotNull JBlobThrowingFunction<T, R> function) {
        return function.toFunction();
    }

    /**
     * Wraps a throwing function to a result.
     *
     * @param function The throwing function to wrap.
     * @param <T>      The function type.
     * @param <R>      The return type.
     * @return The result.
     * @see JBlobResult
     */
    static <T, R> @NotNull Function<T, JBlobResult<R>> wrapToResult(@NotNull JBlobThrowingFunction<T, R> function) {
        return function.toResult();
    }
}
