package dev.amraleth.jblob.data.repository;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import dev.amraleth.jblob.data.types.JBlobResult;
import lombok.NonNull;

/**
 * Represents a basic repository of data that can have any backend data structure associated with it. It specifies
 * the common actions used frequently on the underlying data structure. It is specified to always have two generic
 * arguments.
 *
 * @param <K> The type of key this repository will hold.
 * @param <V> The type of value this repository will hold.
 * @author amraleth
 * @since 1.0
 */
public sealed interface JBlobBiRepository<K, V> permits ConcurrentHashMapJBlobBiRepository {

    /**
     * Finds a single nullable value by a given key.
     *
     * @param key The key to search for.
     * @return The value if no match was found.
     */
    @NotNull
    JBlobResult<V> findBy(@NonNull K key);

    /**
     * Gets a list of all keys.
     *
     * @return An unmodifiable list of all keys.
     * @apiNote It is not specified, how an implementation handles this. Refer to the specific implementation to get
     * information about weather this is a cloned list or a reference.
     */
    @NotNull
    @Unmodifiable
    List<K> getKeys();

    /**
     * Gets a list of all values.
     *
     * @return An unmodifiable list of all values.
     * @apiNote It is not specified, how an implementation handles this. Refer to the specific implementation to get
     * information about weather this is a cloned list or a reference.
     */
    @NotNull
    @Unmodifiable
    List<V> getValues();

    /**
     * Inserts a new key-value pair into the repository.
     *
     * @param key   The key to insert.
     * @param value The value to insert.
     */
    void insert(@NonNull K key, @NonNull V value);

    /**
     * Removes an entry from the repository.
     *
     * @param key The key to remove the associated pair of.
     */
    void delete(@NonNull K key);

    /**
     * Gets the total count of entries in the repository.
     *
     * @return The count.
     */
    int getCount();

    /**
     * Clears the repository.
     */
    void clear();
}
