package dev.amraleth.jblob.data.types;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a data type that can have either a success or a failure. Conceptionally it is used for operations, where
 * both an error and a success are equally expected. The result is immutable and can only have one state, where
 * {@link JBlobResult#isSuccess()} indicates if the result is of type success.
 *
 * @param <T> The type of value this result holds.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe(notes = "If T is an immutable type")
@JBlobImmutable
public final class JBlobResult<T> {
    private final @Nullable T value;
    @Getter
    private final boolean success;
    private final @Nullable String errorMessage;
    private final @Nullable Exception exception;

    /**
     * Private constructor for creating a new result.
     *
     * @param value        The nullable value of the result.
     * @param success      Weather the result is of type success or failure.
     * @param errorMessage A nullable error message.
     */
    private JBlobResult(@Nullable T value, boolean success, @Nullable String errorMessage, @Nullable Exception exception) {
        this.value = value;
        this.success = success;
        this.errorMessage = errorMessage;
        this.exception = exception;
    }

    /**
     * Constructs a success result with a specific return value.
     *
     * @param value The value of the result.
     * @param <T>   The type of data this result holds.
     * @return A new result.
     */
    public static <T> @NotNull JBlobResult<T> success(@Nullable T value) {
        return new JBlobResult<>(value, true, null, null);
    }

    /**
     * Constructs a failure result with a specific return value.
     *
     * @param errorMessage The error message.
     * @param <T>          The type of data this result holds.
     * @param exception    An optional exception thrown on failure.
     * @return A new result.
     */
    public static <T> @NotNull JBlobResult<T> failure(@Nullable String errorMessage, @Nullable Exception exception) {
        return new JBlobResult<>(null, false, errorMessage, exception);
    }

    /**
     * Indicates if the result is a failure.
     *
     * @return True if failure, false otherwise
     */
    public boolean isFailure() {
        return !this.success;
    }

    /**
     * Gets the value of the result.
     *
     * @return The value of the result.
     */
    public @Nullable T getValue() {
        return this.value;
    }

    /**
     * Returns the value of the result or a fallback.
     *
     * @param fallback The fallback to return when the result is not a success.
     * @return The value or the fallback.
     */
    public @Nullable T orElse(@NonNull T fallback) {
        return this.success ? this.value : fallback;
    }

    /**
     * Throws an exception if the result is not a success.
     *
     * @return The value if the result was a success.
     * @throws IllegalStateException If the result is not a success.
     */
    public @Nullable T orElseThrow() {
        if (!this.success) {
            throw new IllegalStateException(this.errorMessage);
        }
        return this.value;
    }

    /**
     * Gets the error message.
     *
     * @return The error message.
     */
    public @Nullable String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Constructs a new result from the current result by applying a mapping on it.
     *
     * @param mapper The mapping to apply.
     * @param <U>    U generic type.
     * @return The constructed result.
     */
    public <U> @NotNull JBlobResult<U> map(@NonNull Function<T, U> mapper) {
        if (this.success) {
            return JBlobResult.success(mapper.apply(this.value));
        }
        return JBlobResult.failure(this.errorMessage, null);
    }

    /**
     * Performs a flat mapping.
     *
     * @param mapper The mapper to apply.
     * @param <U>    The type of the result.
     * @return The result of the operation.
     */
    public <U> @NotNull JBlobResult<U> flatMap(@NonNull Function<T, JBlobResult<U>> mapper) {
        if (this.success) {
            return mapper.apply(this.value);
        }
        return JBlobResult.failure(this.errorMessage, null);
    }

    /**
     * Runs an action if the result was a success.
     *
     * @param then The action to run.
     * @return The result.
     */
    public @NotNull JBlobResult<T> ifSuccess(@NonNull Consumer<T> then) {
        if (this.success) then.accept(this.value);
        return this;
    }

    /**
     * Runs an action if the result was a failure.
     *
     * @param then The action to run.
     * @return The result.
     */
    public @NotNull JBlobResult<T> ifFailure(@NonNull BiConsumer<String, Exception> then) {
        if (!this.success) then.accept(this.errorMessage, this.exception);
        return this;
    }
}
