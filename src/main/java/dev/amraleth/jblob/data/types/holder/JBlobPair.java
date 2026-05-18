package dev.amraleth.jblob.data.types.holder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.amraleth.jblob.annotation.JBlobThreadSafe;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;

/**
 * Represents a pair of data.
 *
 * @param <F>    The type of the first entry.
 * @param <S>    The type of the second entry.
 * @param first  The first element of the pair.
 * @param second The second element of the pair.
 * @author amraleth
 * @since 1.0
 */
@JBlobThreadSafe(notes = "If F and S are immutable types")
@JBlobImmutable
public record JBlobPair<F, S>(@Nullable F first, @Nullable S second) {

    /**
     * Converts this pair into a triple.
     *
     * @param third The third value to insert into the triple.
     * @param <U>   The new type of the value to add.
     * @return A new triple with the third value set to the argument.
     */
    public <U> @NotNull JBlobTriple<F, S, U> toTriple(@Nullable U third) {
        return new JBlobTriple<>(this.first, this.second, third);
    }

    @Override
    public @NotNull String toString() {
        return "Pair [first=" + this.first + " second=" + this.second + "]";
    }
}
