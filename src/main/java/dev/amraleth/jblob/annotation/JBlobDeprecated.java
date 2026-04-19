package dev.amraleth.jblob.annotation;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

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
    @NotNull @NonNull String reason();

    /**
     * An optional argument for what this is being replaced with.
     *
     * @return Replaced with.
     */
    @NotNull String replacedBy() default "";

    /**
     * An optional argument for since when this has been deprecated.
     *
     * @return Since when.
     */
    @NotNull String since() default "";
}
