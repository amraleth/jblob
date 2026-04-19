package dev.amraleth.jblob.annotation;

import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;

import java.lang.annotation.*;

/**
 * Indicates, that a class only has static methods, like {@link dev.amraleth.jblob.JBlobAssert}.
 *
 * @author amraleth
 * @since 1.1
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
@JBlobImmutable
public @interface JBlobStaticClass {
}
