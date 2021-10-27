package ui;

import ast.AnalysisEvaluator;
import ast.ClassNode;
import ast.GraphNode;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.printer.YamlPrinter;
import com.github.javaparser.utils.SourceRoot;
import gui.UMLDiagram;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AnalyzerMain {

    private static List<CompilationUnit> locu  = new ArrayList<>();

    private static List<CompilationUnit> returnAllCompilationUnitsInSRC(String path){
        File f = new File(path);
        String[] paths = f.list();
        for (String possibleFile:
             paths) {
            File f1 = new File(path+"/"+possibleFile);
            if (f1.isFile() && !possibleFile.equals(".DS_Store")){
                try {
                    CompilationUnit cu = StaticJavaParser.parse(Files.newInputStream(Paths.get(path+"/"+possibleFile)));
                    locu.add(cu);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                if (f1.isDirectory() && !possibleFile.equals(".DS_Store")){
                    SourceRoot sourceRoot = new SourceRoot(Paths.get(path+"/"+possibleFile));
                    try {
                        sourceRoot.tryToParse();
                        if (sourceRoot.getCompilationUnits().size() == 0) {
                            returnAllCompilationUnitsInSRC(path+"/"+possibleFile);
                        }
                        locu.addAll(sourceRoot.getCompilationUnits());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return locu;
    }
    public static void main(String[] args) {
        List<CompilationUnit> compilationUnits = returnAllCompilationUnitsInSRC("src/main/resources");
//        System.out.println(compilationUnits.get(1));
        List<ClassNode> classNodes = new ArrayList<>();
        for(CompilationUnit cu : compilationUnits) {
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
                String className = c.getNameAsString();
                List<FieldDeclaration> classFields = c.getFields();
                List<MethodDeclaration> methods = c.getMethods();
                List<String> methodNames = new ArrayList<>();
                for (MethodDeclaration m : methods) {
                    String methodName = m.getNameAsString();
                    methodNames.add(methodName);
                }
                ClassNode classNode = new ClassNode(className, methodNames, c, methods, classFields);
                classNodes.add(classNode);
            });
        }
        AnalysisEvaluator analysisEvaluator = new AnalysisEvaluator();
        List<GraphNode> graphNodes = analysisEvaluator.runAnalysis(classNodes);
        
        UMLDiagram uml = new UMLDiagram(graphNodes);
        uml.makeUML();
    }
}
