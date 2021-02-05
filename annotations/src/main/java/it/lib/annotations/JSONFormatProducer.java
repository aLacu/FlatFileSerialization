package it.lib.annotations;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JSONFormatProducer{

    public static List<String> renderFieldsAnnotation(@NotNull List<FixedWidthField> fieldList, @NotNull AutogenerateJSONDefinition autogenerateFFDDefinition) {
        final ArrayList<String> stringList = new ArrayList<>();
        stringList.add("{\""+autogenerateFFDDefinition.value()+"\" : ");
        stringList.add("[");
        for (FixedWidthField annotations : fieldList) {
            stringList.add("{");
            stringList.add("\"name\":\""+annotations.name()+"\",");
            stringList.add("\"key\":\""+annotations.key()+"\",");
            stringList.add("\"length\":"+annotations.length()+",");
            if(annotations.decimalLength()>0)
                stringList.add("\"decimalLength\":"+annotations.decimalLength()+",");
            if(annotations.type()== FixedWidthField.Type.NUMERIC)
                stringList.add("\"type\":\"numeric\"");
            else
                stringList.add("\"type\":\"alphanumeric\"");
            stringList.add("}");
            stringList.add(",");
        }
        stringList.remove(stringList.size()-1);
        stringList.add("]");
        stringList.add("}");
        return stringList;
    }

    public static String getSuffix() {
        return ".json";
    }

}
