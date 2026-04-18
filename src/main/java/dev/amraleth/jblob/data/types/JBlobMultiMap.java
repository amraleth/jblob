package dev.amraleth.jblob.data.types;

import dev.amraleth.jblob.annotation.mutability.JBlobMutable;
import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * A map that associates each key with multiple values.
 * Unlike a standard {@link Map}, a {@code JBlobMultiMap} allows multiple values
 * to be stored under the same key. The values for each key are stored as a
 * {@link List}, preserving insertion order.
 *
 * <pre>{@code
 * JBlobMultiMap<String, String> map = JBlobMultiMap.create();
 * map.put("fruits", "apple");
 * map.put("fruits", "banana");
 * map.put("veggies", "carrot");
 *
 * map.get("fruits"); // JBlobResult.success(["apple", "banana"])
 * }</pre>
 *
 * @param <K> The type of keys.
 * @param <V> The type of values.
 * @author amraleth
 * @since 1.1
 */
@JBlobMutable
@JBlobThreadSafe(notes = "If K and V are immutable types.")
public final class JBlobMultiMap<K, V> {
    private final @NotNull Map<K, List<V>> store;

    /**
     * Private constructor for creating a new multi map.
     */
    private JBlobMultiMap() {
        this.store = new HashMap<>();
    }

    /**
     * Constructs a new empty multi map.
     *
     * @param <K> The type of keys.
     * @param <V> The type of values.
     * @return A new empty multi map.
     */
    public static <K, V> @NotNull JBlobMultiMap<K, V> create() {
        return new JBlobMultiMap<>();
    }

    /**
     * Constructs a new multi map from an existing standard map, treating each
     * entry as a single-value mapping.
     *
     * @param map The map to construct from.
     * @param <K> The type of keys.
     * @param <V> The type of values.
     * @return A new multi map.
     */
    public static <K, V> @NotNull JBlobMultiMap<K, V> fromMap(@NotNull Map<K, V> map) {
        JBlobMultiMap<K, V> multiMap = new JBlobMultiMap<>();
        map.forEach(multiMap::put);
        return multiMap;
    }

    /**
     * Puts a key-value pair into the multi map. If the key already exists,
     * the value is appended to its existing list of values.
     *
     * @param key   The key.
     * @param value The value.
     */
    public synchronized void put(@NotNull K key, @NotNull V value) {
        this.store.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * Puts all values from the given collection under the specified key.
     *
     * @param key    The key.
     * @param values The values to associate with the key.
     */
    public synchronized void putAll(@NotNull K key, @NotNull Collection<V> values) {
        this.store.computeIfAbsent(key, k -> new ArrayList<>()).addAll(values);
    }

    /**
     * Gets all values associated with the given key.
     *
     * @param key The key.
     * @return A result containing an unmodifiable list of values, or a failure if the key is not present.
     */
    public synchronized @NotNull JBlobResult<List<V>> get(@NotNull K key) {
        List<V> values = this.store.get(key);
        if (values == null || values.isEmpty()) {
            return JBlobResult.failure("No values found for key %s".formatted(key), null);
        }
        return JBlobResult.success(Collections.unmodifiableList(values));
    }

    /**
     * Gets the first value associated with the given key.
     *
     * @param key The key.
     * @return A result containing the first value, or a failure if the key is not present.
     */
    public synchronized @NotNull JBlobResult<V> getFirst(@NotNull K key) {
        List<V> values = this.store.get(key);
        if (values == null || values.isEmpty()) {
            return JBlobResult.failure("No values found for key %s".formatted(key), null);
        }
        return JBlobResult.success(values.getFirst());
    }

    /**
     * Gets the last value associated with the given key.
     *
     * @param key The key.
     * @return A result containing the last value, or a failure if the key is not present.
     */
    public synchronized @NotNull JBlobResult<V> getLast(@NotNull K key) {
        List<V> values = this.store.get(key);
        if (values == null || values.isEmpty()) {
            return JBlobResult.failure("No values found for key %s".formatted(key), null);
        }
        return JBlobResult.success(values.getLast());
    }

    /**
     * Removes all values associated with the given key.
     *
     * @param key The key to remove.
     * @return True if the key was present and removed, false otherwise.
     */
    public synchronized boolean removeKey(@NotNull K key) {
        return this.store.remove(key) != null;
    }

    /**
     * Removes a specific value from the given key's value list.
     *
     * @param key   The key.
     * @param value The specific value to remove.
     * @return True if the value was present and removed, false otherwise.
     */
    public synchronized boolean removeValue(@NotNull K key, @NotNull V value) {
        List<V> values = this.store.get(key);
        if (values == null) return false;
        boolean removed = values.remove(value);
        if (values.isEmpty()) this.store.remove(key);
        return removed;
    }

    /**
     * Checks if the given key exists in the multi map.
     *
     * @param key The key to check.
     * @return True if the key is present, false otherwise.
     */
    public synchronized boolean containsKey(@NotNull K key) {
        return this.store.containsKey(key);
    }

    /**
     * Checks if the given value exists under the given key.
     *
     * @param key   The key.
     * @param value The value.
     * @return True if the key-value pair is present, false otherwise.
     */
    public synchronized boolean containsEntry(@NotNull K key, @NotNull V value) {
        List<V> values = this.store.get(key);
        return values != null && values.contains(value);
    }

    /**
     * Returns the total number of key-value entries across all keys.
     *
     * @return The total entry count.
     */
    public synchronized int totalSize() {
        return this.store.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Returns the number of distinct keys.
     *
     * @return The key count.
     */
    public synchronized int keySize() {
        return this.store.size();
    }

    /**
     * Returns the number of values associated with the given key.
     *
     * @param key The key.
     * @return The value count for the key, or 0 if the key is not present.
     */
    public synchronized int valueSize(@NotNull K key) {
        List<V> values = this.store.get(key);
        return values == null ? 0 : values.size();
    }

    /**
     * Checks if the multi map contains no entries.
     *
     * @return True if empty, false otherwise.
     */
    public synchronized boolean isEmpty() {
        return this.store.isEmpty();
    }

    /**
     * Removes all entries from the multi map.
     */
    public synchronized void clear() {
        this.store.clear();
    }

    /**
     * Returns an unmodifiable view of all keys in this multi map.
     *
     * @return The key set.
     */
    public synchronized @NotNull @Unmodifiable Set<K> keySet() {
        return Collections.unmodifiableSet(this.store.keySet());
    }

    /**
     * Performs the given action for each key and its associated list of values.
     *
     * @param action The action to perform.
     */
    public synchronized void forEach(@NotNull BiConsumer<K, List<V>> action) {
        this.store.forEach(action);
    }

    /**
     * Returns an immutable snapshot of this multi map as a standard map.
     * Each key maps to an unmodifiable list of its values.
     *
     * @return An unmodifiable snapshot.
     */
    public synchronized @NotNull @Unmodifiable Map<K, List<V>> snapshot() {
        Map<K, List<V>> copy = new HashMap<>();
        this.store.forEach((k, v) -> copy.put(k, List.copyOf(v)));
        return Collections.unmodifiableMap(copy);
    }
}