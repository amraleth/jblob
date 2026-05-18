package dev.amraleth.jblob.annotation.mutability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jetbrains.annotations.NotNull;

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
    @NotNull String notes() default "";
}
