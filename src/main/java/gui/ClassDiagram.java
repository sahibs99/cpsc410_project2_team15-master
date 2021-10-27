package gui;
import ast.ClassField;
import ast.ClassMethod;
import ast.GraphNode;
import com.google.common.graph.Graph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.text.Bidi;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// A single class component of the UML diagram. Will display information about a certain class or interface or other types.
// Thinking of having the UMLDiagram class generate many ClassDiagrams and somehow connecting them using arrows and lines.
public class ClassDiagram extends JPanel {
    private final int panelWidth;
    private final int panelHeight;
    private final int rectangleWidth = 250;
    private final int rectangleHeight = 150;
    private final List<GraphNode> classList;
    private final List<CoordinatePair> coordinatesList;
    private int graphNodePos = 0;
    private final int screenDivideAmount;
    private final int screenWidth;
    private final int screenHeight;
    private final BufferedImage img;
    private int startCoordsY;
    private int startCoordsX;

    public ClassDiagram(List<GraphNode> classList, BufferedImage img, int screenWidth, int screenHeight) {
        this.classList = classList;
        this.coordinatesList = new ArrayList<>();
        int classesPerSide = (int) Math.ceil((double) classList.size() / 4.0);
        screenDivideAmount = classesPerSide + 2;
        panelWidth = ((rectangleWidth + 100) * screenDivideAmount) - 100;
        panelHeight = ((rectangleHeight + 100) * screenDivideAmount) - 100;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.img = img;
        startCoordsX = (screenWidth - panelWidth) / 2;
        startCoordsY = (screenHeight - panelHeight) / 2;
    }

    @Override
    //this is just drawing the panel of the class + className
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // draws the background image
        g.drawImage(img, 0, 0, Math.max(panelWidth, screenWidth), Math.max(panelHeight, screenHeight), null);

        // render legend
        drawLegend(g);

        // draws the class nodes
        for (int i = 0; i < screenDivideAmount; i++) {
            for (int j = 0; j < screenDivideAmount; j++) {
                if ((i != 0 && i != (screenDivideAmount - 1) && (j == 0 || j == (screenDivideAmount - 1)))
                || ((i == 0 || i == (screenDivideAmount - 1)) && (j != 0 && j!= (screenDivideAmount - 1)))) {
                    int xCoord;
                    int yCoord;
                    if (graphNodePos == classList.size()) {
                        break;
                    }
                    if (panelWidth >= screenWidth && panelHeight >= screenHeight) { // go with calculated panelWidth
                        xCoord = ((rectangleWidth + 100) * i);
                        yCoord = ((rectangleHeight + 100) * j);
                    }
                    else if (panelWidth >= screenWidth) { // go with calculated panel width and screen height
                        xCoord = ((rectangleWidth + 100) * i);
                        yCoord = startCoordsY + ((rectangleHeight + 100) * j);
                    }
                    else if (panelHeight >= screenHeight) { // go with calculated panel height and screen width
                        xCoord = startCoordsX + ((rectangleWidth + 100) * i);
                        yCoord = ((rectangleHeight + 100) * j);
                    }
                    else { // go with screen size
                        xCoord = startCoordsX + ((rectangleWidth + 100) * i);
                        yCoord = startCoordsY + ((rectangleHeight + 100) * j);
                    }
                    GraphNode currClass = classList.get(graphNodePos);
                    drawPanel(g, xCoord, yCoord, currClass.getName());
                    writeFunctionsAndVariables(g, xCoord, yCoord, currClass);
                    CoordinatePair coords = new CoordinatePair(xCoord, yCoord);
                    coordinatesList.add(coords);
                    graphNodePos++;
                }
            }
        }
        graphNodePos = 0;


        // draws arrows
        for (int i = 0; i < classList.size(); i++) {
            GraphNode thisNode = classList.get(i);
            if (thisNode.isExtending()) {
                int extendedClassPos = findGraphNode(thisNode.getExtendedClass());
                if (extendedClassPos > -1) {
                    int[] arrowCoords = calculateArrowLen(coordinatesList.get(i).getX(), coordinatesList.get(i).getY(),
                            coordinatesList.get(extendedClassPos).getX(), coordinatesList.get(extendedClassPos).getY());
                Inheritance inheritanceArrow = new Inheritance(arrowCoords[0], arrowCoords[1], arrowCoords[2], arrowCoords[3]);
                inheritanceArrow.drawArrow(g);
                }
            }
            if (thisNode.isImplementing()) {
                for (String s: thisNode.getInterfacesImplemented()) {
                    int interfacePos = findGraphNode(s);
                    if (interfacePos > -1) {
                        int[] arrowCoords = calculateArrowLen(coordinatesList.get(i).getX(), coordinatesList.get(i).getY(),
                                coordinatesList.get(interfacePos).getX(), coordinatesList.get(interfacePos).getY());
                        Realization realizationArrow = new Realization(arrowCoords[0], arrowCoords[1], arrowCoords[2], arrowCoords[3]);
                        realizationArrow.drawArrow(g);
                    }
                }
            }
            for (Map.Entry<String, GraphNode.Association> entry: thisNode.getAssociations().entrySet()) {
                int associatedClassPos = findGraphNode(entry.getKey());
                int[] arrowCoords = calculateArrowLen(coordinatesList.get(i).getX(), coordinatesList.get(i).getY(),
                        coordinatesList.get(associatedClassPos).getX(), coordinatesList.get(associatedClassPos).getY());
                if (entry.getValue() == GraphNode.Association.UNIDIRECTIONAL) {
                    UnidirectionalAssociation uniAssocArrow = new UnidirectionalAssociation(arrowCoords[0], arrowCoords[1], arrowCoords[2], arrowCoords[3]);
                    uniAssocArrow.drawArrow(g);
                }
                if (entry.getValue() == GraphNode.Association.BIDIRECTIONAL) {
                    arrowCoords = calculateArrowLen(coordinatesList.get(i).getX(), coordinatesList.get(i).getY(),
                            coordinatesList.get(associatedClassPos).getX(), coordinatesList.get(associatedClassPos).getY());
                    BidirectionalAssociation biDirAssocArrow = new BidirectionalAssociation(arrowCoords[0], arrowCoords[1], arrowCoords[2], arrowCoords[3]);
                    biDirAssocArrow.drawArrow(g);
                }
            }
        }

    }

    // a hardcoded legend renderer
    private void drawLegend(Graphics g) {
        g.setColor(new Color(150, 255, 177));
        g.drawRoundRect(0, 0, rectangleWidth + 15, 225, 20, 20);
        g.fillRoundRect(0, 0, rectangleWidth + 15, 225, 20, 20);
        g.setColor(Color.BLACK);
        int tempY = 10;
        g.drawString("LEGEND", 110, tempY);
        tempY += 20;
        g.drawString("Inheritance Arrow:", 0, tempY);
        tempY += 10;
        Inheritance inheritArrow = new Inheritance(0, tempY, rectangleWidth - 100, tempY);
        inheritArrow.drawArrow(g);
        tempY += 20;
        g.drawString("Realization Arrow:", 0, tempY);
        tempY += 10;
        Realization realizeArrow = new Realization(0, tempY, rectangleWidth - 100, tempY);
        realizeArrow.drawArrow(g);
        tempY += 20;
        g.drawString("Unidirectional Association Arrow:", 0, tempY);
        tempY += 10;
        UnidirectionalAssociation uniDirArrow = new UnidirectionalAssociation(0, tempY, rectangleWidth - 100, tempY);
        uniDirArrow.drawArrow(g);
        tempY += 20;
        g.drawString("Bidirectional Association Arrow:", 0, tempY);
        tempY += 10;
        BidirectionalAssociation biDirArrow = new BidirectionalAssociation(0, tempY, rectangleWidth - 100, tempY);
        biDirArrow.drawArrow(g);
        tempY += 20;
        g.drawString("- Red text means unconventional name.", 0, tempY);
        tempY += 20;
        g.drawString("- Yellow text means method can possibly be", 0, tempY);
        tempY += 15;
        g.drawString("  extracted to a different class or refactored out.", 0, tempY);
        tempY += 20;
        g.drawString("- Orange text means both unconventional name", 0, tempY);
        tempY += 15;
        g.drawString("  and possible extraction.", 0, tempY);
        g.setColor(Color.BLACK);
    }

    // helper for finding index of graphNode in classList given name.
    private int findGraphNode(String className) {
        for (int i = 0; i < classList.size(); i++) {
            if (classList.get(i).getName().equals(className)) {
                return i;
            }
        }
        return -1;
    }


    // sets size of the panel
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Math.max(panelWidth, screenWidth), Math.max(panelHeight, screenHeight));
    }

    // draws the class node
    private void drawPanel(Graphics g, int x, int y, String className) {
        g.setColor(new Color(51,204,255));
        g.drawRoundRect(x, y, 250, 150, 30, 30);
        g.fillRoundRect(x, y, 250, 150, 30, 30);
        g.setColor(Color.black);
        g.drawString(className.toUpperCase(), x + 105, y + 25);
    }

    // figures out where the location of second rectangle is relative to first rectangle. E.g. north, southwest, east...
    // returns the correct start and end points of the arrow stored in an int array.
    // int array ordering is x1, y1, x2, y2
    private int[] calculateArrowLen(int rect1x, int rect1y, int rect2x, int rect2y) {
        int[] resultList = {0, 0, 0, 0};
        if (rect2x > rect1x) { // on right side
            resultList[0] = rect1x + rectangleWidth;
            resultList[1] = rect1y + (rectangleHeight / 2);
            resultList[2] = rect2x;
            resultList[3] = rect2y + (rectangleHeight / 2);
        }
        else if (rect2x < rect1x) { // on left side
            resultList[0] = rect1x;
            resultList[1] = rect1y + (rectangleHeight / 2);
            resultList[2] = rect2x + rectangleWidth;
            resultList[3] = rect2y + (rectangleHeight / 2);
        }
        else { // on top of each other
            if (rect1y > rect2y) { // above
                resultList[0] = rect1x + (rectangleWidth / 2);
                resultList[1] = rect1y;
                resultList[2] = rect2x + (rectangleWidth / 2);
                resultList[3] = rect2y + rectangleHeight;
            }
            else { // below
                resultList[0] = rect1x + (rectangleWidth / 2);
                resultList[1] = rect1y + rectangleHeight;
                resultList[2] = rect2x + (rectangleWidth / 2);
                resultList[3] = rect2y;
            }
        }
        if (rect1y >= (panelHeight - rectangleHeight)) { // if it's on bottom row
            if (rect2x - rect1x == (rectangleWidth + 100)) { // it's on the right
                resultList[0] = rect1x + rectangleWidth;
                resultList[1] = rect1y + (rectangleHeight / 2);
                resultList[2] = rect2x;
                resultList[3] = rect2y + (rectangleHeight / 2);
            }
            else if (rect1x - rect2x == (rectangleWidth + 100)) { // it is on the left
                resultList[0] = rect1x;
                resultList[1] = rect1y + (rectangleHeight / 2);
                resultList[2] = rect2x + rectangleWidth;
                resultList[3] = rect2y + (rectangleHeight / 2);
            }
            else {
                resultList[0] = rect1x + (rectangleWidth /2);
                resultList[1] = rect1y;
            }
        }
        if (rect2y >= (panelHeight - rectangleHeight)) { // bottom row
            if (rect2x - rect1x == (rectangleWidth + 100)) { // it's on the right
                resultList[0] = rect1x + rectangleWidth;
                resultList[1] = rect1y + (rectangleHeight / 2);
                resultList[2] = rect2x;
                resultList[3] = rect2y + (rectangleHeight / 2);
            }
            else if (rect1x - rect2x == (rectangleWidth + 100)) { // it is on the left
                resultList[0] = rect1x;
                resultList[1] = rect1y + (rectangleHeight / 2);
                resultList[2] = rect2x + rectangleWidth;
                resultList[3] = rect2y + (rectangleHeight / 2);
            }
            else {
                resultList[2] = rect2x + (rectangleWidth /2);
                resultList[3] = rect2y;
            }
        }
        if (rect1y == 0 || rect1y == startCoordsY) { // top row
            if (rect2x - rect1x == (rectangleWidth + 100)) { // it's on the right
                resultList[0] = rect1x + rectangleWidth;
                resultList[1] = rect1y + (rectangleHeight / 2);
                resultList[2] = rect2x;
                resultList[3] = rect2y + (rectangleHeight / 2);
            }
            else if (rect1x - rect2x == (rectangleWidth + 100)) { // it is on the left
                resultList[0] = rect1x;
                resultList[1] = rect1y + (rectangleHeight / 2);
                resultList[2] = rect2x + rectangleWidth;
                resultList[3] = rect2y + (rectangleHeight / 2);
            }
            else {
                resultList[0] = rect1x + (rectangleWidth /2);
                resultList[1] = rect1y + rectangleHeight;
            }

        }
        if (rect2y == 0 || rect2y == startCoordsY) { // top row
            if ((rect2x - rect1x) == (rectangleWidth + 100)) { // it's on the right
                resultList[0] = rect1x + rectangleWidth;
                resultList[1] = rect1y + (rectangleHeight / 2);
                resultList[2] = rect2x;
                resultList[3] = rect2y + (rectangleHeight / 2);
            }
            else if ((rect1x - rect2x) == (rectangleWidth + 100)) { // it is on the left
                resultList[0] = rect1x;
                resultList[1] = rect1y + (rectangleHeight / 2);
                resultList[2] = rect2x + rectangleWidth;
                resultList[3] = rect2y + (rectangleHeight / 2);
            }
            else {
                resultList[2] = rect2x + (rectangleWidth /2);
                resultList[3] = rect2y + rectangleHeight;
            }
        }

        return resultList;
    }

    private void writeFunctionsAndVariables(Graphics g, int x, int y, GraphNode className) {
        g.setColor(Color.black);
        List<ClassField> variables = className.getFields();
        List<ClassMethod> methods = className.getMethods();
        int nextRow = 50;

        for(int i = 0; i < variables.size(); i++) {
            if (!variables.get(i).isNameAppropriate()) {
                g.setColor(Color.red);
            }
            g.drawString("var: " + variables.get(i).getModifier() + " " + variables.get(i).getFieldName() + ";", x + 25, y + nextRow);
            g.setColor(Color.black);
            nextRow += 20;
        }

        for(int i = 0; i < methods.size(); i++) {
            if (!methods.get(i).isNameAppropriate()) {
                if (methods.get(i).isExtractable()) {
                    g.setColor(new Color(255, 142, 0));
                }
                else g.setColor(Color.red);
            }
            else if (methods.get(i).isExtractable()) {
                g.setColor(Color.yellow);
            }
            g.drawString("fun: " + methods.get(i).getModifier() + " " + methods.get(i).getMethodName() + ";", x + 25, y + nextRow);
            g.setColor(Color.black);
            nextRow += 20;
        }

    }
}
