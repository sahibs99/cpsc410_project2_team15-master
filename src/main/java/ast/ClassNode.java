package ast;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ClassNode {
    private List<String> methodNames;
    private String className;
    private ClassOrInterfaceDeclaration node;
    private List<MethodDeclaration> methods;
    private List<FieldDeclaration> fields;

    public ClassNode(String name, List<String> methodNames, ClassOrInterfaceDeclaration node,
                     List<MethodDeclaration> methodNodes, List<FieldDeclaration> fields) {
        className = name;
        this.methodNames = methodNames;
        this.node = node;
        this.methods = methodNodes;
        this.fields = fields;
    }

    public List<String> getMethodNames() {
        return methodNames;
    }

    public String getClassName() {
        return className;
    }

    public List<MethodDeclaration> getMethods() {
        return methods;
    }

    public List<FieldDeclaration> getFields() {
        return fields;
    }

    public String getExtendedClass(){ return node.getExtendedTypes().size() > 0 ? node.getExtendedTypes().get(0).asString() : ""; }

    public List<String> getImplemented(){
        List<String> implemented = new ArrayList<>();
        node.getImplementedTypes().forEach(type->{
            implemented.add(type.asString());
        });
        return implemented;
    }
}
