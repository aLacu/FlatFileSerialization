package it.lib.builder;

import it.lib.annotations.FixedWidthParser;
import org.jetbrains.annotations.NotNull;
import it.lib.annotations.FixedWidthCustomParser;
import it.lib.annotations.FixedWidthField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.lib.utils.StringRepeater.charRepeat;

public class ObjectMapper<T> {

    private static final Map<Class<?>, Function<String, ?>> mapper = Stream.of(
            new AbstractMap.SimpleEntry<Class<?>, Function<String, ?>>(Integer.class, Integer::parseInt),
            new AbstractMap.SimpleEntry<Class<?>, Function<String, ?>>(Double.class, Double::parseDouble),
            new AbstractMap.SimpleEntry<Class<?>, Function<String, ?>>(Float.class, Float::parseFloat),
            new AbstractMap.SimpleEntry<Class<?>, Function<String, ?>>(Long.class, Long::parseLong),
            new AbstractMap.SimpleEntry<Class<?>, Function<String, ?>>(Integer.TYPE, s->Integer.parseInt(s.trim())),
            new AbstractMap.SimpleEntry<Class<?>, Function<String, ?>>(Double.TYPE, s->Double.parseDouble(s.trim())),
            new AbstractMap.SimpleEntry<Class<?>, Function<String, ?>>(Float.TYPE, s->Float.parseFloat(s.trim())),
            new AbstractMap.SimpleEntry<Class<?>, Function<String, ?>>(Long.TYPE, s->Long.parseLong(s.trim()))
    ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

    private final Class<? extends T> tClass;
    private final List<Field> annotatedFields;
    private final Logger logger;
    private final int requiredLineSize;

    public ObjectMapper(Class<? extends T> tClass) {
        this.tClass = tClass;
        annotatedFields= Arrays.stream(tClass.getDeclaredFields()).parallel()
                .filter(field -> field.getAnnotation(FixedWidthField.class)!=null)
                .sorted(Comparator.comparing(f->f.getAnnotation(FixedWidthField.class).position()))
                .map(this::setFieldAccessible)
                .collect(Collectors.toList());
        logger= LoggerFactory.getLogger(this.getClass());
        requiredLineSize = annotatedFields
                .stream()
                .parallel()
                .map(field -> field.getAnnotation(FixedWidthField.class))
                .mapToInt(FixedWidthField::length)
                .sum();

    }

    @NotNull
    private Field setFieldAccessible(@NotNull final Field field) {
        field.setAccessible(true);
        return field;
    }

    public T lineToObject(@NotNull final String line){
        try {
            final Constructor<? extends T> declaredConstructor = tClass.getDeclaredConstructor();
            final T t = declaredConstructor.newInstance();
            final char[] chars = line.toCharArray();
            int currentIndex=0;
            for (Field annotatedField : annotatedFields) {

                final FixedWidthField annotation = annotatedField.getAnnotation(FixedWidthField.class);
                final StringBuilder stringBuilder = new StringBuilder();
                for(int i=0; i<annotation.length(); ++i){
                    stringBuilder.append(chars[currentIndex++]);
                }
                final FixedWidthCustomParser customParser = annotatedField.getAnnotation(FixedWidthCustomParser.class);

                final Object toSet=customParser==null?
                        fromString(annotatedField.getType(), stringBuilder.toString(), annotation.decimalLength())
                        :
                        ((FixedWidthParser)customParser.value().getDeclaredConstructor().newInstance()).fromString(stringBuilder.toString());

                annotatedField.set(t, toSet);
            }
            return t;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Object fromString(@NotNull final Class<?> type,
                              @NotNull final String toString,
                              int decimalLength) {
        final Object apply = mapper.getOrDefault(type, Function.identity()).apply(toString);
        if(decimalLength==0)
            return apply;
        else{
            final double v = Double.parseDouble(apply.toString());
            final double newValue=v/Math.pow(10, decimalLength);
            return type.cast(newValue);
        }
    }


    @NotNull
    public String objectToLine(@NotNull final T object) {
        return objectToLine(object, annotatedFields);
    }

    private String objectToLine(@NotNull final T object, @NotNull List<Field> annotatedFields){
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            for (Field annotatedField : annotatedFields) {

                final Object fieldObject = annotatedField.get(object);
                final FixedWidthField annotation = annotatedField.getAnnotation(FixedWidthField.class);
                final int targetLength = annotation.length();
                final int decimalLength = annotation.decimalLength();
                if(decimalLength>0) {
                    //double with decimal
                    final double v = Double.parseDouble(fieldObject.toString());
                    final int floor = (int)Math.floor(v);
                    final String intPart = String.valueOf(floor);
                    final int decimal=(int)(Math.pow(10,decimalLength)*(v-floor));
                    final String decimalPart = String.valueOf(decimal);
                    int integerLength = targetLength - decimalLength;

                    stringBuilder.append(createStringFrom(
                            intPart,
                            integerLength,
                            annotation.paddingChar(),
                            true)
                    )
                            .append(createStringFrom(
                                    decimalPart,
                                    decimalLength,
                                    annotation.paddingChar(),
                                    false
                                    )

                            );
                }
                else{
                    stringBuilder.append(
                            createStringFrom(fieldObject.toString(),
                                    targetLength,
                                    annotation.paddingChar(),
                                    annotation.paddingLeft())
                    );
                }
            }
            return stringBuilder.toString();
        } catch (IllegalAccessException e) {
            logger.error("Cannot access field for object {}", object.getClass());
            return "";
        }
    }

    @NotNull
    private String createStringFrom(@NotNull final String sourceString,
                                    int targetLength,
                                    char paddingChar,
                                    boolean padToLeft){
        return createStringFrom(sourceString,
                targetLength,
                String.valueOf(paddingChar),
                padToLeft);
    }

    @NotNull
    private String createStringFrom(@NotNull final String sourceString,
                                    int targetLength,
                                    @NotNull final String paddingChar,
                                    boolean padToLeft) {
        if(sourceString.length()==targetLength){
            return sourceString;
        }
        if(sourceString.length()>targetLength){
            return sourceString.substring(0, targetLength);
        }
        final int max = targetLength - sourceString.length();
        final StringBuilder stringBuilder = new StringBuilder();
        if(padToLeft){
            stringBuilder.append(charRepeat(paddingChar, max));
        }
        stringBuilder.append(sourceString);
        if(!padToLeft){
            stringBuilder.append(charRepeat(paddingChar, max));
        }
        return stringBuilder.toString();
    }



    protected boolean checkLineSize(@NotNull final String s) {
        if (s.length() == requiredLineSize) {
            return true;
        }
        if(s.length()>requiredLineSize){
            logger.warn("line {} has more characters than needed. This may cause some problems", s);
        }
        else{
            logger.error("line {} does not have a number of character to support the mapping", s);
        }
        return false;
    }

    public String extractKey(T t) {
        return objectToLine(t, annotatedFields.stream()
                .filter(f->f.getAnnotation(FixedWidthField.class).key())
                .sorted(Comparator.comparing(f->f.getAnnotation(FixedWidthField.class).position()))
                .map(this::setFieldAccessible)
                .collect(Collectors.toList()));

    }
}
