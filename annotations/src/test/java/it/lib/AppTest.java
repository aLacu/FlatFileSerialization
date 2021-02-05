package it.lib;

import it.lib.testModel.StatefulSupplier;
import org.junit.Test;
import it.lib.builder.FixedWidthFormatStreamBuilder;
import it.lib.testModel.SampleObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest
{

    private static final long size=1000;

    private static final File path=new File("fake-database.txt");

    @Test
    public void write()
    {
        try {
            final StatefulSupplier statefulSupplier = new StatefulSupplier();
            FixedWidthFormatStreamBuilder
                    .of(SampleObject.class)
                    .toFile(path)
                    .build()
                    .write(
                            Stream.generate(statefulSupplier::supply)
                            .limit(size)
                    );
            assertEquals("Error writing to file", size, Files.lines(path.toPath())
                    .count());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void read(){
        write();
        try {
            final long count = FixedWidthFormatStreamBuilder
                    .of(SampleObject.class)
                    .fromFile(path)
                    .build()
                    .read()
                    .count();
            assertEquals("Error reading  file", size, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readKeyChange(){
        write();
        try {
            final long count = FixedWidthFormatStreamBuilder
                    .of(SampleObject.class)
                    .fromFile(path)
                    .build()
                    .readWithKeyChange()
                    .count();
            assertEquals("Error reading file with keychange", (int)Math.floor(Math.sqrt(size*2)), count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
