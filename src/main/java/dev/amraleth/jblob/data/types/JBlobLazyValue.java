package dev.amraleth.jblob.data.types;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import lombok.NonNull;

/**
 * Represents a value that is initialized later and thread-safe. It is used like
 * <pre>{@code
 * JBlobLazyValue<String> val = JBlobLazyValue.of(() -> "Lazy test");
 * val.getIfInitialized(); // empty optional
 * val.get(); // string
 * }</pre>
 *
 * @param <T> The type of the lazy value.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe(notes = "If T is an immutable type")
@JBlobImmutable
public final class JBlobLazyValue<T> {
    private final @NotNull Supplier<T> supplier;
    private volatile @Nullable T value;
    private volatile boolean initialized;

    /**
     * Private constructor for creating a new lazy value.
     *
     * @param supplier The supplier that supplies the value to this lazy value.
     */
    private JBlobLazyValue(@NonNull Supplier<T> supplier) {
        this.supplier = supplier;
        this.value = null;
        this.initialized = false;
    }

    /**
     * Constructs a new lazy value from a supplier.
     *
     * @param supplier The supplier that supplies the value.
     * @param <T>      The type of the value.
     * @return A new lazy value.
     */
    public static <T> @NotNull JBlobLazyValue<T> of(@NonNull Supplier<T> supplier) {
        return new JBlobLazyValue<>(supplier);
    }

    /**
     * Constructs a new lazy value from a value.
     *
     * @param value The value.
     * @param <T>   The type of the value.
     * @return A new lazy value.
     */
    public static <T> @NotNull JBlobLazyValue<T> ofValue(@NonNull T value) {
        JBlobLazyValue<T> lazy = new JBlobLazyValue<>(() -> value);
        lazy.value = value;
        lazy.initialized = true;
        return lazy;
    }

    /**
     * Gets the lazy value. Performs a synchronization first and then gets the value from the supplier. If the value
     * has already been retrieved, it will just return it without performing any additional operations.
     *
     * @return The value.
     */
    public @Nullable T get() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    this.value = this.supplier.get();
                    this.initialized = true;
                }
            }
        }
        return this.value;
    }

    /**
     * Gets the lazy value if it is initialized.
     *
     * @return The optional value.
     */
    public @NotNull Optional<T> getIfInitialized() {
        synchronized (this) {
            return this.initialized ? Optional.ofNullable(this.value) : Optional.empty();
        }
    }

    /**
     * Gets the lazy value or a fallback if the value is not yet initialized.
     *
     * @param fallback The fallback value.
     * @return The value or fallback.
     */
    public @Nullable T orElse(@NonNull T fallback) {
        synchronized (this) {
            return this.initialized ? this.value : fallback;
        }
    }

    /**
     * Gets the lazy value or a fallback value from a fallback supplier if not yet initialized.
     *
     * @param fallback The fallback supplier supplying the fallback value.
     * @return The value or fallback.
     */
    public @Nullable T orElseGet(@NonNull Supplier<T> fallback) {
        synchronized (this) {
            return this.initialized ? this.value : fallback.get();
        }
    }

    /**
     * Checks if the value of the lazy value is initialized.
     *
     * @return True if the value is initialized, otherwise false.
     */
    public boolean isInitialized() {
        synchronized (this) {
            return this.initialized;
        }
    }

    /**
     * Performs a mapping on the lazy value.
     *
     * @param mapper The mapper.
     * @param <U>    Generic U.
     * @return The lazy value for chaining.
     */
    public <U> @NotNull JBlobLazyValue<U> map(@NonNull Function<T, U> mapper) {
        return JBlobLazyValue.of(() -> mapper.apply(this.get()));
    }

    /**
     * Performs a flat mapping on the lazy value.
     *
     * @param mapper The mapper.
     * @param <U>    Generic U.
     * @return The lazy value for chaining.
     */
    public <U> @NotNull JBlobLazyValue<U> flatMap(@NonNull Function<T, JBlobLazyValue<U>> mapper) {
        return JBlobLazyValue.of(() -> mapper.apply(this.get()).get());
    }

    /**
     * Invalidates the lazy value.
     */
    public synchronized void invalidate() {
        this.value = null;
        this.initialized = false;
    }
}

