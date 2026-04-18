package dev.amraleth.jblob.data;

import dev.amraleth.jblob.annotation.JBlobExperimental;
import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobMutable;
import dev.amraleth.jblob.data.types.JBlobResult;
import dev.amraleth.jblob.data.types.holder.JBlobPair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A thread-safe, fixed-capacity cache that evicts the least-recently-used entry
 * when full. Used for caching expensive computations, database lookups, or any
 * resource where repeated access patterns are expected and memory must be bounded.
 *
 * <p>Backed by a {@link LinkedHashMap} in access-order mode, which handles LRU
 * promotion internally with no manual bookkeeping required.
 *
 * <p>Supports an optional eviction listener (fired on capacity eviction only, not
 * on manual {@link #invalidate} or {@link #clear} calls) and built-in hit/miss/eviction
 * statistics via {@link #stats()}.
 *
 * <pre>{@code
 * JBlobLruCache<String, User> cache = JBlobLruCache.<String, User>builder(500)
 *     .onEviction((key, user) -> log.debug("Evicted user: {}", key))
 *     .build();
 *
 * User user = cache.getOrCompute("u:123", id -> db.loadUser(id));
 * }</pre>
 *
 * @param <K> the type of keys.
 * @param <V> the type of cached values.
 * @author amraleth
 * @see LinkedHashMap
 * @since 1.1
 */
@JBlobThreadSafe(notes = "If K and V are immutable types")
@JBlobMutable
@JBlobExperimental
public final class JBlobLruCache<K, V> {
    private final int maxSize;
    private final @NotNull Map<K, V> store;
    private final @Nullable BiConsumer<K, V> evictionListener;

    private long hits;
    private long misses;
    private long evictions;

    /**
     * Builder for building a new lru cache.
     *
     * @param <K> The type of key.
     * @param <V> The type of cached value.
     */
    public static final class Builder<K, V> {

        private final int maxSize;
        private @Nullable BiConsumer<K, V> evictionListener = null;

        /**
         * Private builder constructor.
         *
         * @param maxSize The maximum size.
         */
        private Builder(int maxSize) {
            if (maxSize < 1) throw new IllegalArgumentException("maxSize must be >= 1");
            this.maxSize = maxSize;
        }

        /**
         * Called whenever an entry is evicted due to capacity. Not called on manual invalidation.
         *
         * @param listener The consumer to consume the data when an entry is evicted.
         * @return This.
         */
        public @NotNull Builder<K, V> onEviction(@NotNull BiConsumer<K, V> listener) {
            this.evictionListener = listener;
            return this;
        }

        /**
         * Builds the actual lru cache instance.
         *
         * @return The instance.
         */
        public @NotNull JBlobLruCache<K, V> build() {
            return new JBlobLruCache<>(this);
        }
    }

    /**
     * Constructs a new cache with a maximum size.
     *
     * @param maxSize The maximum size of the cache.
     * @param <K>     The type of the keys of the builder.
     * @param <V>     The type of the values of the builder.
     * @return A new builder.
     */
    public static <K, V> @NotNull Builder<K, V> builder(int maxSize) {
        return new Builder<>(maxSize);
    }

    /**
     * Constructs a plain cache without any addition configuration.
     *
     * @param <K>     The type of key.
     * @param <V>     The type of cached value.
     * @param maxSize The maximum size.
     * @return A new instance of the lru cache.
     */
    public static <K, V> @NotNull JBlobLruCache<K, V> of(int maxSize) {
        return JBlobLruCache.<K, V>builder(maxSize).build();
    }

    /**
     * Private constructor for constructing a new actual lru cache.
     *
     * @param builder The builder to apply at build step.
     */
    private JBlobLruCache(@NotNull Builder<K, V> builder) {
        this.maxSize = builder.maxSize;
        this.evictionListener = builder.evictionListener;
        this.store = new LinkedHashMap<>(16, 0.75f, /* accessOrder= */ true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                if (size() > maxSize) {
                    evictions++;
                    if (evictionListener != null) {
                        evictionListener.accept(eldest.getKey(), eldest.getValue());
                    }
                    return true;
                }
                return false;
            }
        };
    }

    /**
     * Puts a key-value pair into the lru cache.
     *
     * @param key   The key to put.
     * @param value The value to put in.
     */
    public synchronized void put(@NotNull K key, @NotNull V value) {
        this.store.put(key, value);
    }

    /**
     * Puts a {@link JBlobPair} into the lru cache.
     *
     * @param pair The pair to put in.
     */
    public synchronized void put(@NotNull JBlobPair<K, V> pair) {
        this.store.put(pair.first(), pair.second());
    }

    /**
     * Gets a value from the cache.
     *
     * @param key The key to get the value for.
     * @return The result.
     */
    public synchronized @NotNull JBlobResult<V> get(@NotNull K key) {
        V value = this.store.get(key);
        if (value != null) {
            hits++;
            return JBlobResult.success(value);

        }
        misses++;
        return JBlobResult.failure("No value found for key %s".formatted(key), null);
    }

    /**
     * Returns the cached value, or computes it, caches it, and returns it.
     *
     * <pre>{@code
     * User user = cache.getOrCompute(userId, id -> userRepository.load(id));
     * }</pre>
     *
     * @param key    The key to get the value for.
     * @param loader The loader to compute this for.
     * @return The value.
     */
    public synchronized @NotNull V getOrCompute(
            @NotNull K key,
            @NotNull Function<K, V> loader
    ) {
        V existing = this.store.get(key);
        if (existing != null) {
            hits++;
            return existing;
        }
        misses++;
        V computed = loader.apply(key);
        store.put(key, computed);
        return computed;
    }

    /**
     * Updates an existing entry, only if it is already cached.
     *
     * @param key     The key.
     * @param updater The function applied to update the value.
     * @return True if the entry was present and updated, false if it was a no-op.
     */
    public synchronized boolean updateIfPresent(@NotNull K key, @NotNull Function<V, V> updater) {
        V existing = this.store.get(key);
        if (existing == null) return false;
        this.store.put(key, updater.apply(existing));
        return true;
    }

    /**
     * Removes a single entry.
     *
     * @param key The key to get the value for.
     */
    public synchronized void invalidate(@NotNull K key) {
        this.store.remove(key);
    }

    /**
     * Removes all entries. Does not trigger the eviction listener.
     */
    public synchronized void clear() {
        this.store.clear();
    }

    /**
     * Checks if a given key is in the cache.
     *
     * @param key The key to check for.
     * @return True if present, otherwise false.
     */
    public synchronized boolean containsKey(@NotNull K key) {
        return this.store.containsKey(key);
    }

    /**
     * Gets the size of the cache.
     *
     * @return The size.
     */
    public synchronized int size() {
        return this.store.size();
    }

    /**
     * Checks if the cache is full.
     *
     * @return True if it is full, otherwise false.
     */
    public boolean isFull() {
        return size() >= this.maxSize;
    }

    /**
     * Gets the maximum size of the cache.
     *
     * @return The maximum size.
     */
    public int maxSize() {
        return this.maxSize;
    }

    /**
     * Returns an immutable snapshot of the cache contents.
     * Iteration order is from least-recently-used to most-recently-used.
     *
     * @return An unmodifiable map of the cache.
     */
    public synchronized @NotNull @Unmodifiable Map<K, V> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(this.store));
    }

    /**
     * Gets the stats of the cache.
     *
     * @return The stats.
     */
    public synchronized @NotNull Stats stats() {
        return new Stats(this.hits, this.misses, this.evictions);
    }

    /**
     * Resets the stats.
     */
    public synchronized void resetStats() {
        hits = 0;
        misses = 0;
        evictions = 0;
    }

    /**
     * The stats of the cache.
     */
    public static final class Stats {

        private final long hits;
        private final long misses;
        private final long evictions;

        /**
         * Constructs a new stats instance.
         *
         * @param hits      The hits of the cache.
         * @param misses    The misses of the cache.
         * @param evictions The evictions of the cache.
         */
        private Stats(long hits, long misses, long evictions) {
            this.hits = hits;
            this.misses = misses;
            this.evictions = evictions;
        }

        /**
         * Gets the amount of hits.
         *
         * @return The amount of hits.
         */
        public long hits() {
            return hits;
        }

        /**
         * Gets the amount of misses.
         *
         * @return The amount of misses.
         */
        public long misses() {
            return misses;
        }

        /**
         * Gets the amount of evictions.
         *
         * @return The amount of evictions.
         */
        public long evictions() {
            return evictions;
        }

        /**
         * Gets the amount of requests as a combination of hits and misses.
         *
         * @return The amount of requests.
         */
        public long requests() {
            return hits + misses;
        }

        /**
         * Gets the hit rate as hits divided by the total requests.
         *
         * @return The hit rate.
         */
        public double hitRate() {
            long total = requests();
            return total == 0 ? 0.0 : (double) hits / total;
        }

        @Override
        public @NotNull String toString() {
            return String.format(
                    "Stats{hits=%d, misses=%d, evictions=%d, hitRate=%.1f%%}",
                    hits, misses, evictions, hitRate() * 100
            );
        }
    }
}
