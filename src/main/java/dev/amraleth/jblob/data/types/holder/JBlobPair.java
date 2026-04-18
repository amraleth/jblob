package dev.amraleth.jblob.data.types.holder;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a pair of data.
 *
 * @param <F> The type of the first entry.
 * @param <S> The type of the second entry.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe(notes = "If F and S are immutable types")
@JBlobImmutable
public record JBlobPair<F, S>(@Nullable F first, @Nullable S second) {
    /**
     * Constructs a new pair with both the first and second entry set to null.
     */
    public JBlobPair() {
        this(null, null);
    }

    /**
     * Constructs a new pair with both the first and second entry set.
     *
     * @param first  The first entry.
     * @param second The second entry.
     */
    public JBlobPair {
    }


    @Override
    public String toString() {
        return "Pair [first=" + this.first + " second=" + this.second + "]";
    }
}
