package dev.amraleth.jblob.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jetbrains.annotations.Nullable;

/**
 * Informational annotation that marks a class as being thread-safe. It is purely for informational
 * purposes and is not enforced during runtime.
 *
 * @author amraleth
 * @see JBlobThreadUnsafe
 * @since 1.1
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface JBlobThreadSafe {
    /**
     * An optional argument for since when the annotated class is thread-safe.
     *
     * @return Since when the class is thread-safe.
     */
    @Nullable String since() default "";

    /**
     * An optional argument for additional notes about either the usage of the class, or if restrictions apply for when
     * the class is thread-safe.
     *
     * @return Additional notes.
     */
    @Nullable String notes() default "";
}
