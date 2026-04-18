package dev.amraleth.jblob.data.types.holder;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a triple of data.
 *
 * @param <F>    The type of the first entry.
 * @param <S>    The type of the second entry.
 * @param <T>    The type of the third entry.
 * @param first  The first element of the triple.
 * @param second The second element of the triple.
 * @param third  The third element of the triple.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe(notes = "If F, S and T are immutable types")
@JBlobImmutable
public record JBlobTriple<F, S, T>(@Nullable F first, @Nullable S second, @Nullable T third) {

    /**
     * Converts this triple into a pair by dropping the last element of it.
     *
     * @return A new pair with the third value dropped.
     */
    public @NotNull JBlobPair<F, S> toPair() {
        return new JBlobPair<>(this.first, this.second);
    }

    @Override
    public String toString() {
        return "Triple [first=" + this.first + "second=" + this.second + "third=" + this.third + "]";
    }
}

