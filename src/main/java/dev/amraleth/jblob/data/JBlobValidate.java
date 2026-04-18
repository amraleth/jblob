package dev.amraleth.jblob.data;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import dev.amraleth.jblob.data.types.JBlobResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Validator that allows for multiple validations to be applied onto a single value. Furthermore, it allows for chaining
 * of multiple validators together. It can be constructed like this:
 *
 * <pre>{@code
 * JBlobValidate<String> v = JBlobValidate.of(username)
 *      .notNull("must not be null")
 *      .validate(s -> !s.isBlank(), "must not be blank")
 *      .validate(s -> s.length() >= 3, "must be at least 3 characters")
 *      .validate(s -> s.length() <= 20, "must be at most 20 characters")
 *      .validate(s -> s.matches("[\\w]+"), "must contain only word characters");
 * }</pre>
 * and can then be used like this:
 * <pre>{@code
 * if (v.isValid()) {
 *    System.out.println(v.getValue());
 * } else {
 *    v.getErrors().forEach(System.out::println);
 * }
 * }</pre>
 * Other uses include:
 * <pre>{@code
 * // Nested field validation on an object
 * JBlobValidate<UserDto> userValidation = JBlobValidate.of(dto)
 *   .notNull("user must not be null")
 *    .field("username", UserDto::getUsername, f -> f
 *    .notNull("required")
 *    .validate(s -> s.length() >= 3, "too short"))
 *    .field("email", UserDto::getEmail, f -> f
 *    .notNull("required")
 *    .validate(s -> s.contains("@"), "invalid format"));
 *
 * // Merging two independent validations
 * JBlobValidate<Config> combined = validateHost(config)
 *    .merge(validatePort(config));
 *
 * // Bridge to {@link JBlobResult}
 * JBlobResult<String> result = v.toResult();
 * }</pre>
 *
 * @param <T> The type of the validator.
 * @author amraleth
 * @see JBlobResult
 * @since 1.1
 */
@JBlobThreadSafe(notes = "If T is an immutable type")
@JBlobImmutable
public final class JBlobValidate<T> {

    private final @Nullable T value;
    private final @NotNull List<String> errors;

    /**
     * Private constructor for creating a new validator.
     *
     * @param value  The value.
     * @param errors The list of errors;
     */
    private JBlobValidate(@Nullable T value, @NotNull List<String> errors) {
        this.value = value;
        this.errors = Collections.unmodifiableList(errors);
    }

    /**
     * Starts a validation chain for the given value.
     *
     * @param value The value to start the chain for.
     * @param <T> The type of the validator.
     * @return New validator.
     */
    public static <T> @NotNull JBlobValidate<T> of(@Nullable T value) {
        return new JBlobValidate<>(value, Collections.emptyList());
    }

    /**
     * Fails with the given message if the value is null.
     *
     * @param message The message to send if the assertion fails.
     * @return The validator for chaining.
     */
    public @NotNull JBlobValidate<T> notNull(@NotNull String message) {
        if (this.value != null) return this;
        return withError(message);
    }

    /**
     * Fails with the given message if the predicate returns false.
     * Skipped entirely if the value is already null.
     *
     * @param predicate The predicate to run.
     * @param message   The message to send if the predicate fails.
     * @return The validator for chaining.
     */
    public @NotNull JBlobValidate<T> validate(
            @NotNull Predicate<T> predicate,
            @NotNull String message
    ) {
        if (this.value == null || predicate.test(this.value)) return this;
        return withError(message);
    }

    /**
     * Runs a nested validation on a field extracted from the value.
     * Any errors from the nested validation are merged in, prefixed with the field name.
     *
     * @param fieldName The name of the field.
     * @param extractor The extractor to run on the field.
     * @param rules     The rules to apply on the value.
     * @param <F> The return type of the extractor.
     * @return The new validator.
     *
     * <pre>{@code
     * JBlobValidate.of(user)
     *     .notNull("user must not be null")
     *     .field("email", User::getEmail, v -> v
     *         .notNull("must not be null")
     *         .validate(s -> s.contains("@"), "must be a valid email"))
     * }</pre>
     */
    public <F> @NotNull JBlobValidate<T> field(
            @NotNull String fieldName,
            @NotNull Function<T, F> extractor,
            @NotNull Function<JBlobValidate<F>, JBlobValidate<F>> rules
    ) {
        if (this.value == null) return this;
        JBlobValidate<F> fieldValidation = rules.apply(JBlobValidate.of(extractor.apply(value)));
        if (fieldValidation.isValid()) return this;

        List<String> merged = new ArrayList<>(this.errors);
        for (String e : fieldValidation.errors) {
            merged.add(fieldName + ": " + e);
        }
        return new JBlobValidate<>(this.value, merged);
    }

    /**
     * Maps the value if the validation is currently passing.
     * If there are already errors, the mapper is skipped and errors are preserved.
     *
     * @param mapper The mapping to apply.
     * @return A new validator.
     * @param <U> The Type of the validator.
     */
    public <U> @NotNull JBlobValidate<U> map(@NotNull Function<T, U> mapper) {
        if (!this.errors.isEmpty() || this.value == null) {
            return new JBlobValidate<>(null, this.errors);
        }
        return new JBlobValidate<>(mapper.apply(this.value), this.errors);
    }

    /**
     * Merges the errors of another validation into this one.
     * Useful for combining independent validations that don't share a value.
     *
     * @param other The other validator to merge into this.
     * @return A new validator that is a merge of this and the other validator.
     *
     * <pre>{@code
     * JBlobValidate.of(a).validate(...)
     *     .merge(JBlobValidate.of(b).validate(...));
     * }</pre>
     */
    public @NotNull JBlobValidate<T> merge(@NotNull JBlobValidate<?> other) {
        if (other.errors.isEmpty()) return this;
        List<String> merged = new ArrayList<>(this.errors);
        merged.addAll(other.errors);
        return new JBlobValidate<>(this.value, merged);
    }

    /**
     * Checks if the validator is valid.
     * @return If the list of errors is empty.
     */
    public boolean isValid() {
        return this.errors.isEmpty();
    }

    /**
     * Returns the validated value.
     *
     * @return The value.
     * @throws IllegalStateException If there are validation errors.
     */
    public @NotNull T getValue() {
        if (!isValid() || this.value == null) {
            throw new IllegalStateException(
                    "Validation failed with errors: " + this.errors
            );
        }
        return this.value;
    }

    /**
     * Gets all errors that happened during validation.
     *
     * @return An unmodifiable list of errors.
     */
    public @NotNull @Unmodifiable List<String> getErrors() {
        return this.errors;
    }

    /**
     * Converts to a result. Success with the value or failure with joined error messages.
     *
     * @return The result.
     */
    public @NotNull JBlobResult<T> toResult() {
        if (isValid()) {
            return JBlobResult.success(getValue());
        }
        return JBlobResult.failure(String.join("; ", this.errors), null);
    }

    private @NotNull JBlobValidate<T> withError(@NotNull String message) {
        List<String> next = new ArrayList<>(this.errors);
        next.add(message);
        return new JBlobValidate<>(this.value, next);
    }
}
