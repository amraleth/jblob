package dev.amraleth.jblob.annotation;

import java.lang.annotation.*;

/**
 * Indicates, that a class or method is considered experimental.
 *
 * @author amraleth
 * @since 1.1
 */

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface JBlobExperimental {

    /**
     * An optional reason as to why it is marked as experimental.
     *
     * @return Reason.
     */
    String reason() default "";

    /**
     * An optional argument indicating since when it is marked as experimental.
     *
     * @return Since when.
     */
    String since() default "";

    /**
     * An optional argument indicating until when it is marked as experimental.
     *
     * @return Until when.
     */
    String until() default "";
}
