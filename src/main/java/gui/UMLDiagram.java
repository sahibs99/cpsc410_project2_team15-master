package gui;

import ast.GraphNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

// Might use java graphics to draw lines and arrows between classes.
public class UMLDiagram {
    private JFrame mainFrame;
    private List<GraphNode> classList;

    // not sure what the data the back-end will output yet. 2D array? jump table? hashmap?
    public UMLDiagram(List<GraphNode> classList) {
        this.classList = classList;
    }

    // renders our UML diagram. Currently only makes the main frame.
    public void makeUML() {
        mainFrame = new JFrame("UML Diagram");
        mainFrame.setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize(screenSize.width, screenSize.height);
        Container pane = mainFrame.getContentPane();

        // reads an image
        String pathName = System.getProperty("user.dir") + "/src/main/java/gui/sinnoh.png";
        try {
            BufferedImage img = ImageIO.read(new File(pathName));

            // creates the GUI
            ClassDiagram class1 = new ClassDiagram(classList, img, screenSize.width, screenSize.height);
            JScrollPane scrollPane = new JScrollPane(class1);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pane.add(scrollPane);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

    }
}
