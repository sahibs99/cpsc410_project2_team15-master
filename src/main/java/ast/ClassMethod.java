package ast;

public class ClassMethod {
    private String methodName;
    private String modifier; // public, private, or protected or empty string "" if cannot be found
    private boolean extractable;
    private boolean isNameConventional;

    public ClassMethod(String methodName, String modifier, boolean extractable, boolean isNameConventional){
        this.methodName = methodName;
        this.modifier = modifier;
        this.extractable = extractable;
        this.isNameConventional = isNameConventional;
    }

    public String getModifier() {
        return modifier;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isExtractable() {
        return extractable;
    }

    public boolean isNameAppropriate() {
        return isNameConventional;
    }
}
