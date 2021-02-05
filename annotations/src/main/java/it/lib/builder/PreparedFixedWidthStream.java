package it.lib.builder;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class PreparedFixedWidthStream<T, FWF extends FinalFixedWidthFromatStream<T>> {


    private final Supplier<FWF> finalFixedWidthFormatStream;

    protected PreparedFixedWidthStream(@NotNull final Supplier<FWF> finalFixedWidthFormatStream) {
        this.finalFixedWidthFormatStream = finalFixedWidthFormatStream;
    }


    public FWF build() {
        return finalFixedWidthFormatStream.get();
    }
}
