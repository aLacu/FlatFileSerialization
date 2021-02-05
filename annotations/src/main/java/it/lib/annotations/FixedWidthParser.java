package it.lib.annotations;

import org.jetbrains.annotations.NotNull;

public interface FixedWidthParser<Type> {

    @NotNull
    Type fromString(@NotNull final String input);
    @NotNull
    default String toString(@NotNull final Type input) {
        return input.toString();
    }
}
