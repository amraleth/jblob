package dev.amraleth.jblob.data.repository;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobMutable;
import dev.amraleth.jblob.data.types.JBlobResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a thread-safe implementation of the {@link JBlobBiRepository} by utilizing a {@link ConcurrentHashMap} as
 * the underlying data structure.
 *
 * @param <K> The type of key.
 * @param <V> The type of value.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe
@JBlobMutable
public final class ConcurrentHashMapJBlobBiRepository<K, V> implements JBlobBiRepository<K, V> {
    private final ConcurrentHashMap<K, V> map;

    /**
     * Constructs a new repository.
     */
    public ConcurrentHashMapJBlobBiRepository() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public @NotNull JBlobResult<V> findBy(@NotNull K key) {
        if (!this.map.containsKey(key)) {
            return JBlobResult.failure("Value for key %s not found".formatted(key));
        }
        return JBlobResult.success(this.map.get(key));
    }

    @Override
    public @NotNull @Unmodifiable List<K> getKeys() {
        return this.map.keySet().stream().toList();
    }

    @Override
    public @NotNull @Unmodifiable List<V> getValues() {
        return this.map.values().stream().toList();
    }

    @Override
    public void insert(@NotNull K key, @NotNull V value) {
        this.map.put(key, value);
    }

    @Override
    public void delete(@NotNull K key) {
        this.map.remove(key);
    }

    @Override
    public int getCount() {
        return this.map.size();
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}
