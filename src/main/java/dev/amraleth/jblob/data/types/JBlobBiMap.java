package dev.amraleth.jblob.data.types;

import dev.amraleth.jblob.annotation.mutability.JBlobMutable;
import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * A bidirectional map that maintains a one-to-one relationship between keys and values.
 * Unlike a standard {@link Map}, a {@code JBlobBiMap} enforces uniqueness on both keys
 * and values, allowing lookups in both directions. Attempting to insert a duplicate
 * value under a different key will result in a failure result.
 *
 * <pre>{@code
 * JBlobBiMap<String, Integer> map = JBlobBiMap.create();
 * map.put("one", 1);
 * map.put("two", 2);
 *
 * map.getByKey("one");   // JBlobResult.success(1)
 * map.getByValue(2);     // JBlobResult.success("two")
 *
 * map.inverse().getByKey(1); // JBlobResult.success("one")
 * }</pre>
 *
 * @param <K> The type of keys.
 * @param <V> The type of values.
 * @author amraleth
 * @since 1.1
 */
@JBlobMutable
@JBlobThreadSafe(notes = "If K and V are immutable types.")
public final class JBlobBiMap<K, V> {
    private final @NotNull Map<K, V> forward;
    private final @NotNull Map<V, K> inverse;

    /**
     * Private constructor for creating a new bi map.
     */
    private JBlobBiMap() {
        this.forward = new HashMap<>();
        this.inverse = new HashMap<>();
    }

    /**
     * Private constructor for creating an inverse view of an existing bi map.
     *
     * @param forward The forward map.
     * @param inverse The inverse map.
     */
    private JBlobBiMap(@NotNull Map<K, V> forward, @NotNull Map<V, K> inverse) {
        this.forward = forward;
        this.inverse = inverse;
    }

    /**
     * Constructs a new empty bi map.
     *
     * @param <K> The type of keys.
     * @param <V> The type of values.
     * @return A new empty bi map.
     */
    public static <K, V> @NotNull JBlobBiMap<K, V> create() {
        return new JBlobBiMap<>();
    }

    /**
     * Constructs a new bi map from an existing standard map.
     * Fails if the given map contains duplicate values.
     *
     * @param map The map to construct from.
     * @param <K> The type of keys.
     * @param <V> The type of values.
     * @return A result containing the new bi map, or a failure if duplicate values exist.
     */
    public static <K, V> @NotNull JBlobResult<JBlobBiMap<K, V>> fromMap(@NotNull Map<K, V> map) {
        JBlobBiMap<K, V> biMap = new JBlobBiMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            JBlobResult<Void> result = biMap.put(entry.getKey(), entry.getValue());
            if (result.isFailure()) {
                return JBlobResult.failure(result.getErrorMessage(), null);
            }
        }
        return JBlobResult.success(biMap);
    }

    /**
     * Puts a key-value pair into the bi map.
     * <p>
     * Fails if the value is already associated with a different key, as this
     * would violate the one-to-one constraint of the bi map.
     *
     * @param key   The key.
     * @param value The value.
     * @return A success result, or a failure if the value is already present.
     */
    public synchronized @NotNull JBlobResult<Void> put(@NotNull K key, @NotNull V value) {
        if (this.inverse.containsKey(value) && !this.inverse.get(value).equals(key)) {
            return JBlobResult.failure("Value " + value + " is already associated with a different key.", null);
        }
        K existingKey = this.inverse.get(value);
        if (existingKey != null) this.forward.remove(existingKey);
        this.forward.put(key, value);
        this.inverse.put(value, key);
        return JBlobResult.success(null);
    }

    /**
     * Gets the value associated with the given key.
     *
     * @param key The key.
     * @return A result containing the value, or a failure if the key is not present.
     */
    public synchronized @NotNull JBlobResult<V> getByKey(@NotNull K key) {
        V value = this.forward.get(key);
        if (value == null) return JBlobResult.failure("No value found for key %s".formatted(key), null);
        return JBlobResult.success(value);
    }

    /**
     * Gets the key associated with the given value.
     *
     * @param value The value.
     * @return A result containing the key, or a failure if the value is not present.
     */
    public synchronized @NotNull JBlobResult<K> getByValue(@NotNull V value) {
        K key = this.inverse.get(value);
        if (key == null) return JBlobResult.failure("No key found for value %s".formatted(value), null);
        return JBlobResult.success(key);
    }

    /**
     * Removes the entry associated with the given key.
     *
     * @param key The key to remove.
     * @return True if the key was present and removed, false otherwise.
     */
    public synchronized boolean removeByKey(@NotNull K key) {
        V value = this.forward.remove(key);
        if (value == null) return false;
        this.inverse.remove(value);
        return true;
    }

    /**
     * Removes the entry associated with the given value.
     *
     * @param value The value to remove.
     * @return True if the value was present and removed, false otherwise.
     */
    public synchronized boolean removeByValue(@NotNull V value) {
        K key = this.inverse.remove(value);
        if (key == null) return false;
        this.forward.remove(key);
        return true;
    }

    /**
     * Checks if the given key is present in the bi map.
     *
     * @param key The key to check.
     * @return True if the key is present, false otherwise.
     */
    public synchronized boolean containsKey(@NotNull K key) {
        return this.forward.containsKey(key);
    }

    /**
     * Checks if the given value is present in the bi map.
     *
     * @param value The value to check.
     * @return True if the value is present, false otherwise.
     */
    public synchronized boolean containsValue(@NotNull V value) {
        return this.inverse.containsKey(value);
    }

    /**
     * Returns the number of entries in the bi map.
     *
     * @return The size.
     */
    public synchronized int size() {
        return this.forward.size();
    }

    /**
     * Checks if the bi map contains no entries.
     *
     * @return True if empty, false otherwise.
     */
    public synchronized boolean isEmpty() {
        return this.forward.isEmpty();
    }

    /**
     * Removes all entries from the bi map.
     */
    public synchronized void clear() {
        this.forward.clear();
        this.inverse.clear();
    }

    /**
     * Returns an inverse view of this bi map where keys become values and
     * values become keys.
     *
     * @return The inverse bi map.
     */
    public synchronized @NotNull JBlobBiMap<V, K> inverse() {
        return new JBlobBiMap<>(new HashMap<>(this.inverse), new HashMap<>(this.forward));
    }

    /**
     * Returns an immutable snapshot of the forward key-to-value mapping.
     *
     * @return An unmodifiable map snapshot.
     */
    public synchronized @NotNull @Unmodifiable Map<K, V> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(this.forward));
    }
}
