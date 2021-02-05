package it.lib.builder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

public final class TypedFixedWidthStream<T>{

    private final ObjectMapper<T> objectMapper;

    public TypedFixedWidthStream(ObjectMapper<T> objectMapper) {
        this.objectMapper = objectMapper;
    }

    @NotNull
    public PreparedFixedWidthStream<T, FixedWidthFormatStreamReader<T>> fromFile(@NotNull Path path) {
        return new PreparedFixedWidthStream<>(()->new FixedWidthFormatStreamReader<>(objectMapper, path));

    }

    @NotNull
    public PreparedFixedWidthStream<T, FixedWidthFormatStreamReader<T>> fromFile(@NotNull String filePath) {
        return fromFile(new File(filePath));
    }

    @NotNull
    public PreparedFixedWidthStream<T, FixedWidthFormatStreamReader<T>> fromFile(@NotNull File file) {
        return fromFile(file.toPath());
    }

    @NotNull
    public PreparedFixedWidthStream<T, FixedWidthFormatStreamWriter<T>> toFile(@NotNull Path path) {
        return toFile(path.toFile());
    }

    @NotNull
    public PreparedFixedWidthStream<T, FixedWidthFormatStreamWriter<T>> toFile(@NotNull String filePath) {
        return toFile(new File(filePath));
    }

    @NotNull
    public PreparedFixedWidthStream<T, FixedWidthFormatStreamWriter<T>> toFile(@NotNull File file) {
        return new PreparedFixedWidthStream<>(()->new FixedWidthFormatStreamWriter<>(objectMapper, file));
    }
}
