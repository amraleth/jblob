package dev.amraleth.jblob.annotation;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.*;

/**
 * Informational annotation that marks a class as being thread-unsafe. It is purely for informational
 * purposes and is not enforced during runtime.
 *
 * @author amraleth
 * @see JBlobThreadSafe
 * @since 1.1
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface JBlobThreadUnsafe {
    /**
     * An optional argument for since when the annotated class is thread-unsafe.
     *
     * @return Since when the class is thread-unsafe.
     */
    @Nullable String since() default "";

    /**
     * An optional argument for additional notes about either the usage of the class, or if restrictions apply for when
     * the class is thread-unsafe.
     *
     * @return Additional notes.
     */
    @Nullable String notes() default "";
}
