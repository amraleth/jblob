package dev.amraleth.jblob.exception;

/**
 * Represents an Exception when performing an assert.
 *
 * @author amraleth
 * @since 1.0
 */
public final class JBlobAssertException extends RuntimeException {
    public JBlobAssertException(String message) {
        super(message);
    }
}
