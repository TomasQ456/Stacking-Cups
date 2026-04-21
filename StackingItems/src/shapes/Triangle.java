package shapes;

import java.awt.*;

/**
 * A triangle that can be manipulated and that draws itself on a canvas.
 *
 * @author Michael Kolling and David J. Barnes
 * @version 2.0
 */
public class Triangle extends Shape {

    public static int VERTICES = 3;

    private int height;
    private int width;

    /**
     * Creates a new triangle at default position with default color.
     */
    public Triangle() {
        super(140, 15, Color.decode("#FAEBD7"));
        height = 30;
        width = 40;
    }

    /**
     * Slowly moves the triangle horizontally.
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
     * Slowly moves the triangle vertically.
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

    /**
     * Changes the size to the new dimensions.
     *
     * @param newHeight the new height in pixels (must be >= 0)
     * @param newWidth  the new width in pixels (must be >= 0)
     */
    public void changeSize(int newHeight, int newWidth) {
        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }

    @Override
    protected void draw() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            int[] xpoints = { xPosition, xPosition + (width / 2), xPosition - (width / 2) };
            int[] ypoints = { yPosition, yPosition + height, yPosition + height };
            canvas.draw(this, color, new Polygon(xpoints, ypoints, 3));
            canvas.wait(10);
        }
    }
}
