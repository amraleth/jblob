package dev.amraleth.jblob.func;

import dev.amraleth.jblob.annotation.JBlobStaticClass;
import dev.amraleth.jblob.annotation.mutability.JBlobImmutable;
import dev.amraleth.jblob.data.types.JBlobResult;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Contains common abstractions for {@link JBlobThrowingFunction}.
 *
 * @author amraleth
 * @since 1.2
 */
@JBlobStaticClass
@JBlobImmutable
public final class JBlobThrowingFunctions {

    // parsing
    public static final Function<String, JBlobResult<Integer>> PARSE_INT = JBlobThrowingFunction.wrapToResult(Integer::parseInt);
    public static final Function<String, JBlobResult<Long>> PARSE_LONG = JBlobThrowingFunction.wrapToResult(Long::parseLong);
    public static final Function<String, JBlobResult<Double>> PARSE_DOUBLE = JBlobThrowingFunction.wrapToResult(Double::parseDouble);
    public static final Function<String, JBlobResult<UUID>> PARSE_UUID = JBlobThrowingFunction.wrapToResult(UUID::fromString);
    public static final Function<String, JBlobResult<URI>> PARSE_URI = JBlobThrowingFunction.wrapToResult(URI::new);

    // file io
    public static final Function<Path, JBlobResult<byte[]>> READ_BYTES = JBlobThrowingFunction.wrapToResult(Files::readAllBytes);
    public static final Function<Path, JBlobResult<List<String>>> READ_LINES = JBlobThrowingFunction.wrapToResult(Files::readAllLines);
    public static final Function<String, JBlobResult<byte[]>> READ_STRING = JBlobThrowingFunction.wrapToResult(p -> Files.readAllBytes(Path.of(p)));

    // reflections
    public static final Function<String, JBlobResult<Class<?>>> CLASS_FOR_NAME = JBlobThrowingFunction.wrapToResult(Class::forName);
}
