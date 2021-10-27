package ast;

public class ClassField {
    private String fieldName;
    // public or private or protected
    private String modifier;
    private boolean isNameConventional;

    public ClassField(String fieldName, String modifier, boolean isNameConventional) {
        this.fieldName = fieldName;
        this.modifier = modifier;
        this.isNameConventional = isNameConventional;
    }

    public String getModifier() {
        return modifier;
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isNameAppropriate() {
        return isNameConventional;
    }
}
