package dev.amraleth.jblob.data;

import dev.amraleth.jblob.annotation.JBlobStaticClass;

/**
 * Contains methods for working with single bit flags. A bit flag is a bit in an integer that indicates a boolean
 * value. Single bits can be defined as
 * <pre>{@code
 * private final int READABLE   = 1 << 0;
 * private final int WRITABLE   = 1 << 1;
 * private final int EXECUTABLE = 1 << 2;
 * }</pre>
 * <p>
 * and then accessed like
 * <pre>{@code
 * int perms = READABLE | WRITABLE;
 *
 * JBlobBitFlag.hasFlags(perms, READABLE);              // true
 * JBlobBitFlag.hasFlags(perms, WRITABLE);              // true
 * JBlobBitFlag.hasFlags(perms, READABLE | WRITABLE);   // true
 * JBlobBitFlag.hasFlags(perms, EXECUTABLE);            // false
 * }</pre>
 *
 * @author amraleth
 * @since 1.0
 */

@JBlobStaticClass
public final class JBlobBitFlag {

    /**
     * Private constructor.
     */
    private JBlobBitFlag() {
    }

    /**
     * Gets a flag at a given position.
     *
     * @param value    The value to get the flag from.
     * @param position The position to get the flag at.
     * @return True if the bit is 1, false otherwise.
     */
    public static boolean getFlagAt(int value, int position) {
        return (value >> position & 1) == 1;
    }

    /**
     * Sets a flag at a given position in a given value.
     *
     * @param value    The value to set the flag in.
     * @param position The position to set the flag at.
     * @param flag     The flag to set.
     * @return The new integer.
     */
    public static int setFlagAt(int value, int position, boolean flag) {
        if (flag) {
            return value | (1 << position);
        } else {
            return value & ~(1 << position);
        }
    }

    /**
     * Toggle's the flag at a given position. Programmatically it will be flipped.
     *
     * @param value    The value in which to flip.
     * @param position The position at which to flip.
     * @return The new integer.
     */
    public static int toggleFlagAt(int value, int position) {
        return value ^ (1 << position);
    }

    /**
     * Checks multiple flags at once.
     *
     * @param value The value in which to check.
     * @param mask  The mask to check.
     * @return True if the bits are set, false otherwise.
     */
    public static boolean hasFlags(int value, int mask) {
        return (value & mask) == mask;
    }

    /**
     * Clears all flags of a value.
     *
     * @param value The value in which to clear the bits.
     * @param mask  The mask to check.
     * @return The new integer.
     */
    public static int clearFlags(int value, int mask) {
        return value & ~mask;
    }
}
