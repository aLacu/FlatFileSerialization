package it.lib.builder;


import org.jetbrains.annotations.NotNull;

public final class FixedWidthFormatStreamBuilder {

    public static <T> TypedFixedWidthStream<T> of(@NotNull final Class<? extends T> tClass){
        return new TypedFixedWidthStream<>(new ObjectMapper<>(tClass));
    }
}
