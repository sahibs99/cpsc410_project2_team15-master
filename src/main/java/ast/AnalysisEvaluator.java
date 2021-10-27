package ast;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import java.util.*;
import java.util.regex.Pattern;

import static ast.GraphNode.Association.*;

public class AnalysisEvaluator {
    public AnalysisEvaluator(){}
    private Map<String, List<String>> methodsByClass = new HashMap<>();
    private Map<String, List<String>> fieldsByClass = new HashMap<>();
    // Nested Hashmap that contains altered fields in each method in each class
    private Map<String, Map<String, List<String>>> fieldsByMethodByClass = new HashMap<>();
    public List<GraphNode> runAnalysis(List<ClassNode> classNodes){
        buildDatabase(classNodes);
        List<GraphNode> graphNodes = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        for (ClassNode c : classNodes) {
            classNames.add(c.getClassName());
            List<ClassField> fields = new ArrayList<>();
            List<ClassMethod> methods = new ArrayList<>();
            Map<String, GraphNode.Association> associations = new HashMap<>();
            c.getFields().forEach(field -> {
                String fieldName = field.getVariables().get(0).getNameAsString();
                String fieldModifier;
                if (field.getModifiers().isEmpty()) {
                    fieldModifier = "";
                }
                else fieldModifier = field.getModifiers().get(0).toString(); // field.getModifiers().get(0) just gets whether it's private/public/protected
                String fieldAssociation = field.getElementType().asString();
                boolean isNameConventional = checkFieldConvention(fieldName);
                associations.put(fieldAssociation, UNIDIRECTIONAL);
                fields.add(new ClassField(fieldName, fieldModifier, isNameConventional));
            });
            c.getMethods().forEach(method -> {
                method.getParameters().forEach(param -> {
                    associations.put(param.getTypeAsString(), UNIDIRECTIONAL);
                });
                String methodName = method.getNameAsString();
                boolean extractable = canMethodBeExtracted(method, c.getClassName());
                boolean isNameConventional = checkNamingConvention(methodName);
                String modifier = "";
                if (!method.getModifiers().isEmpty()){
                    modifier = method.getModifiers().get(0).toString();
                }
                methods.add(new ClassMethod(methodName, modifier, extractable, isNameConventional));
            });
            GraphNode graphNode = new GraphNode(c.getClassName(), fields, methods, c.getExtendedClass(), c.getImplemented());

            graphNode.setAssociations(associations);
            graphNodes.add(graphNode);
        }

        //remove associations to classes not in our UML diagram as well as set bidirectionality
        for (GraphNode graphNode : graphNodes) {
            Map<String, GraphNode.Association> associations = graphNode.getAssociations();
            Map<String, GraphNode.Association> newAssociations = new HashMap<>(associations);
            associations.forEach((className, association) -> {
                if (!classNames.contains(className)){
                    newAssociations.remove(className);
                }
                for(GraphNode g : graphNodes){
                    if (g.getName().equals(className)){
                        if (g.getAssociations().containsKey(graphNode.getName())){
                            g.getAssociations().remove(graphNode.getName()); // currently removing the other association so only one shows as bidirectional - if want both to say bidirectional just uncomment the line below and remove this line
                            // g.getAssociations().replace(graphNode.getName(), BIDIRECTIONAL);
                            newAssociations.replace(className, BIDIRECTIONAL);
                        }
                        break;
                    }
                }
            });
            graphNode.setAssociations(newAssociations);
        }
        return graphNodes;
    }

    private boolean checkFieldConvention(String fieldName) {
        if ((fieldName.length() >= 35) || fieldName.startsWith("_") ||
                fieldName.startsWith("$") ){
            return false;
        }
        else return Pattern.matches("([a-z]|[A-Z])\\w+", fieldName);

    }

    private boolean checkNamingConvention(String methodName) {
        if (methodName.length() >= 35) {
            return false;
        }
        else return Pattern.matches("([a-z]+[A-Z]*)\\w+", methodName);
    }

    private boolean canMethodBeExtracted(MethodDeclaration method, String className) {
        boolean extractable = true;
        String methodName = method.getNameAsString();
        List<MethodCallExpr> methodCalls = method.findAll(MethodCallExpr.class);
        for (MethodCallExpr m : methodCalls) {
            String methodCalled = m.getNameAsString();
            if (methodsByClass.get(className).contains(methodCalled) &&
                !methodCalled.equals(methodName)) {
                extractable = false;
            }
        }
        if (!fieldsByMethodByClass.get(className).get(methodName).isEmpty() ||
                methodName.equals("main")) {
            extractable = false;
        }
        return extractable;
    }

    private void buildDatabase(List<ClassNode> classNodes) {
        for (ClassNode c : classNodes) {
            List<String> methodNames = c.getMethodNames();
            methodsByClass.put(c.getClassName(), methodNames);
            extractAndStoreFields(c);
            Map<String, List<String>> alteredFieldsInMethods = getAlteredFieldsInMethods(c);
            fieldsByMethodByClass.put(c.getClassName(), alteredFieldsInMethods);
        }
    }

    private Map<String, List<String>> getAlteredFieldsInMethods(ClassNode c) {
        Map<String, List<String>> alteredFieldsInMethods = new HashMap<>();
        List<MethodDeclaration> cMethods = c.getMethods();
        for (MethodDeclaration m : cMethods) {
            List<String> alteredFields = new ArrayList<>();
            List<AssignExpr> changedVars = m.findAll(AssignExpr.class);
            for (AssignExpr a : changedVars) {
                Expression target = a.getTarget();
                if (target.isNameExpr()) {
                    NameExpr targetNameExpr = target.asNameExpr();
                    String targetVar = targetNameExpr.getNameAsString();
                    if (fieldsByClass.get(c.getClassName()).contains(targetVar)) {
                        alteredFields.add(targetVar);
                    }
                }
            }
            alteredFieldsInMethods.put(m.getNameAsString(), alteredFields);
        }
        return alteredFieldsInMethods;
    }

    private void extractAndStoreFields(ClassNode c) {
        List<String> fields = new ArrayList<>();
        for (FieldDeclaration f : c.getFields()) {
            for (VariableDeclarator v : f.getVariables()) {
                fields.add(v.getNameAsString());
            }
        }
        fieldsByClass.put(c.getClassName(), fields);
    }
}
