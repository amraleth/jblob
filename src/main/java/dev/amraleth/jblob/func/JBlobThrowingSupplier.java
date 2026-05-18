package dev.amraleth.jblob.func;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.amraleth.jblob.data.types.JBlobLazyValue;
import dev.amraleth.jblob.data.types.JBlobResult;
import lombok.NonNull;

/**
 * Represents a supplier that allows for checked exceptions.
 *
 * @param <T> The type of value this supplier supplies.
 * @author amraleth
 * @see JBlobResult
 * @see JBlobLazyValue
 * since 1.0
 */
@FunctionalInterface
public interface JBlobThrowingSupplier<T> {

    /**
     * Getter method for the functional interface.
     *
     * @return The data this supplier holds.
     * @throws Exception If an error during the supplication happened.
     */
    @Nullable T get() throws Exception;

    /**
     * Converts this to a normal supplier.
     *
     * @return The supplier.
     */
    default @NotNull Supplier<T> toSupplier() {
        return () -> {
            try {
                return this.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Converts this to a {@link JBlobResult}.
     *
     * @return The result.
     */
    default @NotNull Supplier<JBlobResult<T>> toResult() {
        return () -> {
            try {
                return JBlobResult.success(this.get());
            } catch (Exception e) {
                return JBlobResult.failure(e.getMessage(), e);
            }
        };
    }

    /**
     * Converts this to a {@link JBlobLazyValue}.
     *
     * @return The lazy value.
     */
    default @NotNull JBlobLazyValue<T> toLazy() {
        return JBlobLazyValue.of(this.toSupplier());
    }

    /**
     * Gets a value or a fallback if the value extraction fails.
     *
     * @param supplier The supplier.
     * @param fallback The fallback.
     * @param <T>      The type of value.
     * @return The value or the fallback.
     */
    static <T> @Nullable T getOrElse(@NonNull JBlobThrowingSupplier<T> supplier, T fallback) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return fallback;
        }
    }

    /**
     * Gets a value or throws an exception if the value extraction fails.
     *
     * @param supplier The supplier.
     * @param <T>      The type of value.
     * @return The value.
     */
    static <T> @Nullable T getOrThrow(@NotNull JBlobThrowingSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
