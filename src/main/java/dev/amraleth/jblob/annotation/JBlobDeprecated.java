package dev.amraleth.jblob.annotation;

import java.lang.annotation.*;

/**
 * Indicates that something is being deprecated. Use {@link #replacedBy()} to indicate the replacement for
 * the annotated thing.
 *
 * @author amraleth
 * @since 1.1
 */

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface JBlobDeprecated {
    /**
     * A required reason as to why it is being marked as deprecated.
     *
     * @return The reason.
     */
    String reason();

    /**
     * An optional argument for what this is being replaced with.
     *
     * @return Replaced with.
     */
    String replacedBy() default "";

    /**
     * An optional argument for since when this has been deprecated.
     *
     * @return Since when.
     */
    String since() default "";
}
