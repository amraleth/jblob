package dev.amraleth.jblob.annotation.mutability;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;

/**
 * Indicates, that a class is mutable.
 *
 * @author amraleth
 * @see JBlobImmutable
 * @since 1.1
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface JBlobMutable {
    /**
     * Addition notes and why and when this class is mutable.
     *
     * @return Additional notes.
     */
    @Nullable String notes() default "";
}
