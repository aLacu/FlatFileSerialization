package it.lib.builder;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixedWidthFormatStreamReader<T> implements FinalFixedWidthFromatStream<T> {

    private final ObjectMapper<T> objectMapper;
    private final Path file;

    public FixedWidthFormatStreamReader(@NotNull final ObjectMapper<T> objectMapper,
                                        @NotNull final Path file) {
        this.objectMapper = objectMapper;
        this.file = file;
    }

    @NotNull
    public Stream<T> read() throws IOException {
        return Files
                .lines(file)
                .filter(objectMapper::checkLineSize)
                .map(objectMapper::lineToObject);
    }

    @NotNull
    public Stream<List<T>> readWithKeyChange() throws IOException{
        return readWithKeyChange(objectMapper::extractKey);
    }

    @NotNull
    public Stream<List<T>> readWithKeyChange(@NotNull final Function<T, Comparable<?>> keyExtractor) throws IOException{
        final ListAccumulator<T> listAccumulator = new ListAccumulator<>(keyExtractor);
        return Stream.concat(read()
                .map(listAccumulator::accumulate)
                .filter(Optional::isPresent)
                .map(Optional::get), Stream.of(listAccumulator.tList));
    }

    private static class ListAccumulator<T>{
        private final Function<T, Comparable<?>> keyExtractor;

        private final List<T> tList;

        private ListAccumulator(final Function<T, Comparable<?>> keyExtractor) {
            this.keyExtractor=keyExtractor;
            tList=new ArrayList<>();
        }

        public Optional<List<T>> accumulate(T current) {
            Optional<List<T>> empty = Optional.empty();
            if (!tList.isEmpty() && !keyExtractor.apply(current).equals(keyExtractor.apply(tList.get(0)))) {
                empty=Optional.of(new ArrayList<>(tList));
                tList.clear();
            }
            tList.add(current);
            return empty;
        }
    }





}
