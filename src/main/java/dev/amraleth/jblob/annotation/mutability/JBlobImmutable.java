package dev.amraleth.jblob.annotation.mutability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jetbrains.annotations.Nullable;

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
    @Nullable String notes() default "";

}
