package dev.amraleth.jblob.exception;

/**
 * Represents an Exception when performing an assert.
 *
 * @author amraleth
 * @since 1.0
 */
public final class JBlobAssertException extends RuntimeException {
    /**
     * Constructs a new assert exception.
     *
     * @param message The message of the exception.
     */
    public JBlobAssertException(String message) {
        super(message);
    }
}
