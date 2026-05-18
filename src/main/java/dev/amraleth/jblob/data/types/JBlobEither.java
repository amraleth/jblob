package dev.amraleth.jblob.data.types;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import lombok.Getter;
import lombok.NonNull;

/**
 * Represents a data holder who can have a value on either the right or left side.
 * It is used like
 * <pre>{@code
 * JBlobEither<String, Integer> val = JBlobEither.left("Test");
 * val.ifLeft(e -> System.out.println("Left value"))
 *      .ifRight(e -> System.out.println("Right value"));
 * }</pre>
 *
 * @param <L> The type of the left side.
 * @param <R> The type of the right side.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe(notes = "If L and R are immutable types")
@JBlobImmutable
public final class JBlobEither<L, R> {
    @Getter
    private final @Nullable L left;
    @Getter
    private final @Nullable R right;
    @Getter
    private final boolean isLeft;

    /**
     * Private constructor for creating a new either.
     *
     * @param left   The left value.
     * @param right  The right value.
     * @param isLeft If the value is on the left or right side.
     */
    private JBlobEither(@Nullable L left, @Nullable R right, boolean isLeft) {
        this.left = left;
        this.right = right;
        this.isLeft = isLeft;
    }

    /**
     * Constructs a new either with data on the left side.
     *
     * @param value The value.
     * @param <L>   The type of the left value.
     * @param <R>   The type of the right value.
     * @return The either instance.
     */
    public static <L, R> @NotNull JBlobEither<L, R> left(@Nullable L value) {
        return new JBlobEither<>(value, null, true);
    }

    /**
     * Constructs a new either with data on the right side.
     *
     * @param value The value.
     * @param <L>   The type of the left value.
     * @param <R>   The type of the right value.
     * @return The either instance.
     */
    public static <L, R> @NotNull JBlobEither<L, R> right(@Nullable R value) {
        return new JBlobEither<>(null, value, false);
    }

    /**
     * Constructs an either instance from a given result.
     *
     * @param result The result.
     * @param <T>    The type of value the result holds.
     * @return An either instance constructed from a result.
     */
    public static <T> @NotNull JBlobEither<String, T> fromResult(@NonNull JBlobResult<T> result) {
        return result.isSuccess()
        ? JBlobEither.right(result.getValue())
        : JBlobEither.left(result.getErrorMessage());
    }

    /**
     * Gets the left value or a fallback if the value is not present.
     *
     * @param fallback The fallback.
     * @return The value or the fallback.
     */
    public @Nullable L leftOrElse(@NonNull L fallback) {
        return this.isLeft ? this.left : fallback;
    }

    /**
     * Gets the right value or a fallback if the value is not present.
     *
     * @param fallback The fallback.
     * @return The value or the fallback.
     */
    public @Nullable R rightOrElse(@NonNull R fallback) {
        return !this.isLeft ? this.right : fallback;
    }

    /**
     * Gets the left element or throws if the value is on the right side.
     *
     * @return The value.
     * @throws NoSuchElementException If the element is not on the left side.
     */
    public @Nullable L leftOrElseThrow() {
        if (!this.isLeft) throw new NoSuchElementException("Either holds a right value");
        return this.left;
    }

    /**
     * Gets the right element or throws if the value is on the left side.
     *
     * @return The value.
     * @throws NoSuchElementException If the element is not on the right side.
     */
    public @Nullable R rightOrElseThrow() {
        if (this.isLeft) throw new NoSuchElementException("Either holds a left value");
        return this.right;
    }

    /**
     * Performs a mapping on the left side.
     *
     * @param mapper The mapping to perform.
     * @param <U>    Generic U.
     * @return A new either instance.
     */
    public <U> @NotNull JBlobEither<U, R> mapLeft(@NonNull Function<L, U> mapper) {
        if (this.isLeft) return JBlobEither.left(mapper.apply(this.left));
        return JBlobEither.right(this.right);
    }

    /**
     * Performs a mapping on the right side.
     *
     * @param mapper The mapping to perform.
     * @param <U>    Generic U.
     * @return A new either instance.
     */
    public <U> @NotNull JBlobEither<L, U> mapRight(@NonNull Function<R, U> mapper) {
        if (!this.isLeft) return JBlobEither.right(mapper.apply(this.right));
        return JBlobEither.left(this.left);
    }

    /**
     * Performs a flat mapping on the left value.
     *
     * @param mapper The mapper to apply.
     * @param <U>    The first type of the either.
     * @return The applied mapper if the value is left, otherwise a new either of the right value.
     */
    public <U> @NotNull JBlobEither<U, R> flatMapLeft(@NonNull Function<L, JBlobEither<U, R>> mapper) {
        if (this.isLeft) return mapper.apply(this.left);
        return JBlobEither.right(this.right);
    }

    /**
     * Performs a flat mapping on the right value.
     *
     * @param mapper The mapper to apply.
     * @param <U>    The first type of the either.
     * @return The applied mapper if the value is right, otherwise a new either of the left value.
     */
    public <U> @NotNull JBlobEither<L, U> flatMapRight(@NonNull Function<R, JBlobEither<L, U>> mapper) {
        if (!this.isLeft) return mapper.apply(this.right);
        return JBlobEither.left(this.left);
    }

    /**
     * Folds the either.
     *
     * @param onLeft  Function to apply on the left side, if present.
     * @param onRight Function to apply on the right side, if present.
     * @param <U>     Generic U.
     * @return U
     */
    public <U> @NotNull U fold(@NonNull Function<L, U> onLeft, @NonNull Function<R, U> onRight) {
        return this.isLeft ? onLeft.apply(this.left) : onRight.apply(this.right);
    }

    /**
     * Performs an action if the value is on the left side.
     *
     * @param then The consumer to perform.
     * @return this.
     */
    public @NotNull JBlobEither<L, R> ifLeft(@NonNull Consumer<L> then) {
        if (this.isLeft) then.accept(this.left);
        return this;
    }

    /**
     * Performs an action if the value is on the right side.
     *
     * @param then The consumer to perform.
     * @return this.
     */
    public @NotNull JBlobEither<L, R> ifRight(@NonNull Consumer<R> then) {
        if (!this.isLeft) then.accept(this.right);
        return this;
    }
}
