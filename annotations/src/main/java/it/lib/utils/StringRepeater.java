package it.lib.utils;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringRepeater {

    public static String charRepeat(@NotNull final String character, int times){
        return IntStream.range(0, times)
                .mapToObj(i-> character)
                .collect(Collectors.joining());
    }
}
