package dev.amraleth.jblob.annotation.mutability;

import java.lang.annotation.*;

/**
 * Indicates, that a class is immutable.
 *
 * @author amraleth
 * @see JBlobMutable
 * @since 1.1
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface JBlobImmutable {
    /**
     * Addition notes and why and when this class is immutable.
     *
     * @return Additional notes.
     */
    String notes() default "";
}
