package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.Point;
import java.awt.geom.Line2D;

// draws a extends arrow based on coordinates given
public class Inheritance implements Arrow {
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;

    public Inheritance(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public void drawArrow(Graphics g) {
        // Draw an arrowhead
        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, 0);
        arrowHead.addPoint(-10, -10);
        arrowHead.addPoint(10, -10);

        // draw the line
        Graphics2D g2D = (Graphics2D) g;
        double angle = Math.atan2(toY - fromY, toX - fromX);
        g2D.setColor(Color.BLACK);
        g2D.setStroke(new BasicStroke(3));

        g2D.drawLine(fromX, fromY, (int) (toX - 10 * Math.cos(angle)), (int) (toY - 10 * Math.sin(angle)));

        // transform the arrowhead to fit the line
        AffineTransform tx = g2D.getTransform();
        AffineTransform tx1 = (AffineTransform) tx.clone();
        tx1.translate(toX, toY);
        tx1.rotate(angle - Math.PI / 2);

        g2D.setTransform(tx1);
        g2D.setColor(Color.WHITE);
        g2D.fill(arrowHead);
        g2D.setTransform(tx);
        g2D.setColor(Color.BLACK);
    }
}
