package dev.amraleth.jblob;

import dev.amraleth.jblob.data.types.JBlobResult;
import dev.amraleth.jblob.func.JBlobThrowingFunction;

import java.util.List;
import java.util.function.Function;

public class Test {
    public static void main(String[] args) {
        Function<String, JBlobResult<Integer>> safeInt = JBlobThrowingFunction.wrapToResult(Integer::parseInt);

        safeInt.apply("42")
                .ifSuccess(System.out::println)
                .ifFailure((s, e) -> System.out.printf("%s%n", e));
        safeInt.apply("tree")
                .ifSuccess(System.out::println)
                .ifFailure((s, e) -> System.out.printf("%s%n", e));

        List<String> inputs = List.of("1", "two", "3");
        List<JBlobResult<Integer>> res = inputs.stream()
                .map(JBlobThrowingFunction.wrapToResult(Integer::parseInt))
                .toList();

        for (JBlobResult<Integer> r : res) {
            r.ifSuccess(System.out::println).ifFailure((s, e) -> System.out.printf("%s%n", e));
        }

    }
}
