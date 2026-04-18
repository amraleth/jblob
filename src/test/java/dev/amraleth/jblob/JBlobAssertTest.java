package dev.amraleth.jblob;

import dev.amraleth.jblob.exception.JBlobAssertException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JBlobAssertTest {

    @Test
    public void testNotNullAssert() {
        Assertions.assertThrows(JBlobAssertException.class, () -> JBlobAssert.assertNotNull(null));
    }

    @Test
    public void testNullAssert() {
        Assertions.assertThrows(JBlobAssertException.class, () -> JBlobAssert.assertNull(""));
    }

    @Test
    public void testNotBlankAssert() {
        Assertions.assertThrows(JBlobAssertException.class, () -> JBlobAssert.assertNotBlank("  "));
    }

    @Test
    public void testPositiveAssert() {
        Assertions.assertThrows(JBlobAssertException.class, () -> JBlobAssert.assertPositive(-1));
    }

    @Test
    public void testNegativeAssert() {
        Assertions.assertThrows(JBlobAssertException.class, () -> JBlobAssert.assertNegative(1));
    }

    @Test
    public void testEqualsAssert() {
        Assertions.assertThrows(JBlobAssertException.class, () -> JBlobAssert.assertEquals("w", "w1"));
    }

    @Test
    public void testNotEqualsAssert() {
        Assertions.assertThrows(JBlobAssertException.class, () -> JBlobAssert.assertNotEquals("w", "w"));
    }
}
