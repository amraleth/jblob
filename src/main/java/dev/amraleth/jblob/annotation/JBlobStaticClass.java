package dev.amraleth.jblob.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;

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
