package it.lib.annotations;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static it.lib.utils.StringRepeater.charRepeat;

public class FFDProducer {
    public static List<String> renderFieldsAnnotation(@NotNull List<FixedWidthField> fieldList, @NotNull AutogenerateFFDefinition autogenerateFFDefinition) {
        final ArrayList<String> stringList = new ArrayList<>();
        stringList.add("[D] "+ autogenerateFFDefinition.description());
        stringList.add("#");
        for (FixedWidthField annotation: fieldList) {
            stringList.add(annotationToString(annotation));
        }
        return stringList;
    }

    private static String annotationToString(FixedWidthField annotation) {
        // I know, I know, it sucks.
        // But IntelliJ made it instead of a StringBuilder.
        // Who am I to disprove the infallibility of IntelliJ
        return "[" +
                (annotation.key() ? 'K' : 'F') +
                "] " +
                (annotation.type() == FixedWidthField.Type.NUMERIC ? 'N' : 'A') +
                ' ' +
                charRepeat("0",
                        Math.max(0, 5 -
                                String.valueOf(annotation.length()).length())
                ) +
                annotation.length() +
                ' ' +
                charRepeat("0",
                        Math.max(0, 5 -
                                String.valueOf(annotation.decimalLength()).length())
                ) +
                annotation.decimalLength() +
                ' ' +
                annotation.name().toUpperCase();
    }

    public static String getSuffix() {
        return ".ffd";
    }

}
