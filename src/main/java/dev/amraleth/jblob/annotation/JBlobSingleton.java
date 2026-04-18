package dev.amraleth.jblob.annotation;

import java.lang.annotation.*;

/**
 * Indicates that only a single instance of this class should exist at a given time.
 *
 * @author amraleth
 * @since 1.1
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface JBlobSingleton {
}
