package it.lib.testModel;

import it.lib.annotations.FixedWidthField;

@SuppressWarnings("ALL")
public class SampleObject {

    @FixedWidthField(position = 1, length = 10, name = "LABEL", type = FixedWidthField.Type.ALPHANUMERIC, key = true)
    private String label;

    @FixedWidthField(position = 2, length = 5, name = "VALUE", type = FixedWidthField.Type.NUMERIC)
    private int value;

    public SampleObject(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public SampleObject(){
        this.label="";
        this.value=1;
    }
}
