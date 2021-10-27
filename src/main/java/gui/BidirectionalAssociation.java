package gui;

import java.awt.*;

// draws a bidirectional arrow based on coordinates given
public class BidirectionalAssociation implements Arrow {
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;

    public BidirectionalAssociation(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public void drawArrow(Graphics g) {
        // draw the line
        Graphics2D g2D = (Graphics2D) g;
        g2D.setColor(Color.BLACK);
        g2D.setStroke(new BasicStroke(3));

        g2D.drawLine(fromX, fromY, toX, toY);

    }
}
