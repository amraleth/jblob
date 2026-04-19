package dev.amraleth.jblob.reflection;

import dev.amraleth.jblob.annotation.JBlobStaticClass;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Process utils for extracting annotations from methods and fields.
 *
 * @author amraleth
 * @since 1.3
 */
@JBlobStaticClass
@JBlobImmutable
public class AnnotationProcessor {

    /**
     * Processes method annotations for a given class.
     *
     * @param clazz          The class to process.
     * @param annotationType The annotation to process.
     * @param consumer       The consumer on what to do with the annotation if found.
     * @param <A>            The type of the annotation.
     */
    public static <A extends Annotation> void processMethodAnnotations(@NotNull @NonNull Class<?> clazz,
                                                                       @NotNull @NonNull Class<A> annotationType,
                                                                       @NotNull @NonNull Consumer<A> consumer) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationType)) {
                A annotation = method.getAnnotation(annotationType);
                consumer.accept(annotation);
            }
        }
    }

    /**
     * Processes method annotations for a given class but accepts a bi-consumer for the annotation and the method itself.
     *
     * @param clazz          The class to process.
     * @param annotationType The annotation to process.
     * @param consumer       The consumer on what to do with the annotation and the method.
     * @param <A>            The type of the annotation.
     */
    public static <A extends Annotation> void processMethodAnnotations(@NotNull @NonNull Class<?> clazz,
                                                                       @NotNull @NonNull Class<A> annotationType,
                                                                       @NotNull @NonNull BiConsumer<A, Method> consumer) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationType)) {
                A annotation = method.getAnnotation(annotationType);
                consumer.accept(annotation, method);
            }
        }
    }

    /**
     * Processes field annotations for a given class.
     *
     * @param clazz          The class to process.
     * @param annotationType The annotation to process.
     * @param consumer       The consumer on what to do with the annotation if found.
     * @param <A>            THe type of the annotation.
     */
    public static <A extends Annotation> void processFieldAnnotations(@NotNull @NonNull Class<?> clazz,
                                                                      @NotNull @NonNull Class<A> annotationType,
                                                                      @NotNull @NonNull Consumer<A> consumer) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationType)) {
                A annotation = field.getAnnotation(annotationType);
                consumer.accept(annotation);
            }
        }
    }

    /**
     * Processes field annotations for a given class but accepts a bi-consumer for the annotation and the method itself.
     *
     * @param clazz          The class to process.
     * @param annotationType The annotation to process.
     * @param consumer       The consumer on what to do with the annotation and the method.
     * @param <A>            The type of the annotation.
     */
    public static <A extends Annotation> void processFieldAnnotations(@NotNull @NonNull Class<?> clazz,
                                                                      @NotNull @NonNull Class<A> annotationType,
                                                                      @NotNull @NonNull BiConsumer<A, Field> consumer) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationType)) {
                A annotation = field.getAnnotation(annotationType);
                consumer.accept(annotation, field);
            }
        }
    }

}
