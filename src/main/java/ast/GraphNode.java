package ast;

import java.util.List;
import java.util.Map;

public class GraphNode {

    // TRUE if implementing any interfaces
    private String name;
    private boolean implementing;
    private List<String> interfacesImplemented;
    // TRUE if extending a class
    private boolean extending;
    private String extendedClass;
    private List<ClassField> fields;
    private List<ClassMethod> methods;
    private Map<String, Association> associations;

    public GraphNode(String name, List<ClassField> fields, List<ClassMethod> methods, String extendedClass,
                     List<String> interfacesImplemented){
        this.name = name;
        this.fields = fields;
        this.methods = methods;
        if (!extendedClass.equals("")) {
            this.extending = true;
            this.extendedClass = extendedClass;
        }
        if (!interfacesImplemented.isEmpty()) {
            this.implementing = true;
            this.interfacesImplemented = interfacesImplemented;
        }
    }

    public String getName() { return name; }

    public List<ClassMethod> getMethods() {
        return methods;
    }

    public List<ClassField> getFields() {
        return fields;
    }

    public String getExtendedClass() {
        return extendedClass;
    }

    public boolean isExtending() {
        return extending;
    }

    public List<String> getInterfacesImplemented() {
        return interfacesImplemented;
    }

    public boolean isImplementing() {
        return implementing;
    }

    public Map<String, Association> getAssociations(){
        return associations;
    }

    public void setAssociations(Map<String, Association> associations){
        this.associations = associations;
    }

    public enum Association {
        UNIDIRECTIONAL,
        BIDIRECTIONAL
    }
}
