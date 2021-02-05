package it.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FixedWidthField {
    int position();
    int length();
    int decimalLength() default 0;
    String name();
    char paddingChar() default ' ';
    boolean paddingLeft() default false;

     Type type();

    boolean key() default false;

    enum Type{
         NUMERIC,
         ALPHANUMERIC
     }
}
