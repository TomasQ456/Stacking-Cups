package shapes;

import java.awt.*;
import java.awt.geom.*;

/**
 * A circle that can be manipulated and that draws itself on a canvas.
 *
 * @author Michael Kolling and David J. Barnes
 * @version 2.0
 */
public class Circle extends Shape {

    public static final double PI = 3.1416;

    private int diameter;

    /**
     * Creates a new circle at default position with default color.
     */
    public Circle() {
        super(20, 15, Color.decode("#FAEBD7"));
        diameter = 30;
    }

    /**
     * Returns the position as an array [x, y].
     *
     * @return the current position
     */
    public int[] getPosition() {
        return new int[]{xPosition, yPosition};
    }

    /**
     * Changes the size of this circle.
     *
     * @param newDiameter the new diameter in pixels (must be >= 0)
     */
    public void changeSize(int newDiameter) {
        erase();
        diameter = newDiameter;
        draw();
    }

    /**
     * Slowly moves the circle horizontally.
     *
     * @param distance the desired distance in pixels
     */
    public void slowMoveHorizontal(int distance) {
        int delta;
        if (distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }
        for (int i = 0; i < distance; i++) {
            xPosition += delta;
            draw();
        }
    }

    /**
     * Slowly moves the circle vertically.
     *
     * @param distance the desired distance in pixels
     */
    public void slowMoveVertical(int distance) {
        int delta;
        if (distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }
        for (int i = 0; i < distance; i++) {
            yPosition += delta;
            draw();
        }
    }

    @Override
    protected void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,
                new Ellipse2D.Double(xPosition, yPosition,
                diameter, diameter));
            canvas.wait(10);
        }
    }
}
