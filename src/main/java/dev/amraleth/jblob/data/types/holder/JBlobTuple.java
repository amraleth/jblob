package dev.amraleth.jblob.data.types.holder;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import dev.amraleth.jblob.data.types.JBlobResult;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Represents a tuple of an arbitrary size, backed by a fixed-length array.
 *
 * @author amraleth
 * @see JBlobPair
 * @see JBlobTriple
 * @since 1.1
 */
@JBlobThreadSafe
@JBlobImmutable
public final class JBlobTuple {
    private final @NotNull Object[] data;

    /**
     * Private constructor for constructing a new tuple.
     *
     * @param size     The size of the tuple.
     * @param elements The elements to populate the tuple with.
     */
    private JBlobTuple(int size, @NotNull Object... elements) {
        this.data = Arrays.copyOf(elements, elements.length);
    }

    /**
     * Constructs a new tuple with elements.
     *
     * @param elements The elements to populate the tuple with.
     * @param <T>      The type of the tuple.
     * @return A new tuple.
     */
    public static <T> @NotNull JBlobTuple t(@NotNull Object @NotNull ... elements) {
        return new JBlobTuple(elements.length, elements);
    }


    /**
     * Gets the object at a given index.
     *
     * @param index The index.
     * @return A result of the object.
     */
    public @NotNull JBlobResult<Object> get(int index) {
        if (this.data.length - 1 < index) {
            return JBlobResult.failure("Index out of bounds");
        }
        return JBlobResult.success(this.data[index]);
    }

    /**
     * Gets the object at a given index and casts it to a specified class.
     *
     * @param index The index.
     * @param clazz The class to cast too.
     * @param <T>   The type of the cast.
     * @return The result.
     * @throws ClassCastException When the cast is invalid.
     */
    public <T> @NotNull JBlobResult<T> get(int index, @NotNull Class<T> clazz) {
        if (this.data.length - 1 < index) {
            return JBlobResult.failure("Index out of bounds");
        }
        return JBlobResult.success(clazz.cast(this.data[index]));
    }
}
