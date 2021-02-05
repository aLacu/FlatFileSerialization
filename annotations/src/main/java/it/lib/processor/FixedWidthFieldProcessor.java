package it.lib.processor;

import it.lib.annotations.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixedWidthFieldProcessor extends AbstractProcessor {

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.NOTE,
                        "Annotation processing: " + roundEnv);

        doJSON(roundEnv);
        doFFD(roundEnv);

        return true;

    }

    private void doFFD(RoundEnvironment roundEnv) {
        execute(roundEnv, AutogenerateFFDefinition.class, FFDProducer::renderFieldsAnnotation, FFDProducer.getSuffix());
    }


    private void doJSON(RoundEnvironment roundEnv) {
        execute(roundEnv, AutogenerateJSONDefinition.class, JSONFormatProducer::renderFieldsAnnotation, JSONFormatProducer.getSuffix());
    }

    private <A extends Annotation> void execute(RoundEnvironment roundEnvironment, Class<A> annotationClass, BiFunction<List<FixedWidthField>, A, List<String>> annotationListHandler, String suffix){
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotationClass);
        elements.stream()
                .parallel()
                .filter(element -> element.getKind() == ElementKind.CLASS)
                .map(e-> (TypeElement) e)
                .forEach(e->generateDefinition(e,  annotationListHandler, suffix, annotationClass));
    }

    private <A extends Annotation> void generateDefinition(TypeElement classElement, BiFunction<List<FixedWidthField>, A, List<String>> annotationListHandler, String suffix, Class<A> annotationClass) {
        processingEnv.getMessager()
                .printMessage(Diagnostic.Kind.NOTE,
                        "Processing: " + classElement.getQualifiedName());

        try {
            final FileObject resource = processingEnv.getFiler()
                    .createResource(StandardLocation.SOURCE_OUTPUT,"",classElement.getSimpleName()+suffix);
            final A annotation = classElement.getAnnotation(annotationClass);
            final List<FixedWidthField> annotationList = getAnnotationList(classElement);
            final List<String> apply = annotationListHandler.apply(annotationList, annotation);
            final Writer writer = resource.openWriter();
            for (String line : apply) {
                writer.append(line)
                        .append("\n");
            }
            writer.close();
        } catch (IOException e) {
            processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, e.getMessage(), classElement);
        }
    }

    private List<FixedWidthField> getAnnotationList(TypeElement classElement) {
        return processingEnv.getElementUtils()
                .getAllMembers(classElement)
                .stream()
                .filter(e -> e.getKind().equals(ElementKind.FIELD))
                .map(t -> t.getAnnotation(FixedWidthField.class))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(FixedWidthField::position))
                .collect(Collectors.toList());
    }


    /**
     * If the processor class is annotated with {@link
     * SupportedAnnotationTypes}, return an unmodifiable set with the
     * same set of strings as the annotation.  If the class is not so
     * annotated, an empty set is returned.
     * <p>
     * If the {@link ProcessingEnvironment#getSourceVersion source
     * version} does not support modules, in other words if it is less
     * than or equal to {@link SourceVersion#RELEASE_8 RELEASE_8},
     * then any leading {@link Processor#getSupportedAnnotationTypes
     * module prefixes} are stripped from the names.
     *
     * @return the names of the annotation types supported by this
     * processor, or an empty set if none
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(AutogenerateJSONDefinition.class.getCanonicalName(),
                AutogenerateFFDefinition.class.getCanonicalName(),
                FixedWidthField.class.getCanonicalName())
                .collect(Collectors.toSet());
    }

    /**
     * If the processor class is annotated with {@link
     * SupportedSourceVersion}, return the source version in the
     * annotation.  If the class is not so annotated, {@link
     * SourceVersion#RELEASE_6} is returned.
     *
     * @return the latest source version supported by this processor
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }




}
