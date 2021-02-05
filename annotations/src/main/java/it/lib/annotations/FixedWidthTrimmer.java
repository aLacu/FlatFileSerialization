package it.lib.annotations;

import org.jetbrains.annotations.NotNull;

public class FixedWidthTrimmer implements FixedWidthParser<String>{
    @Override
    public @NotNull String fromString(@NotNull String input) {
        return input.trim();
    }
}
