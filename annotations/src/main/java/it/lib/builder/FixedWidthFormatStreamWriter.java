package it.lib.builder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class FixedWidthFormatStreamWriter<T> implements FinalFixedWidthFromatStream<T> {

    private final ObjectMapper<T> objectMapper;
    private final File file;


    public FixedWidthFormatStreamWriter(ObjectMapper<T> objectMapper, File file) {
        this.objectMapper = objectMapper;
        this.file = file;
    }

    public void write(@NotNull final Collection<T> objectCollection) throws IOException {
        write(objectCollection.stream());
    }

    public void write(@NotNull final Stream<T> objectStream) throws IOException {
        //tricks java to think this is an iterable
        Files.write(file.toPath(),
                (Iterable<String>)objectStream
                        .filter(Objects::nonNull)
                        .map(objectMapper::objectToLine)
                ::iterator
                );

    }



}
