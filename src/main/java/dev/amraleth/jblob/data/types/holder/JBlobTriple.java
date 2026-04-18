package dev.amraleth.jblob.data.types.holder;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a triple of data.
 *
 * @param <F> The type of the first entry.
 * @param <S> The type of the second entry.
 * @param <T> The type of the third entry.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe(notes = "If F, S and T are immutable types")
@JBlobImmutable
public record JBlobTriple<F, S, T>(@Nullable F first, @Nullable S second, @Nullable T third) {
    /**
     * Constructs an empty triple with all values set to null.
     */
    public JBlobTriple() {
        this(null, null, null);
    }

    /**
     * Constructs a new triple with specific data.
     *
     * @param first  The first entry.
     * @param second The second entry.
     * @param third  The third entry.
     */
    public JBlobTriple {
    }

    @Override
    public String toString() {
        return "Triple [first=" + this.first + "second=" + this.second + "third=" + this.third + "]";
    }
}

