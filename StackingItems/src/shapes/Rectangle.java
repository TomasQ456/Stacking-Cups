package shapes;

import java.awt.*;

/**
 * A rectangle that can be manipulated and that draws itself on a canvas.
 *
 * @author Michael Kolling and David J. Barnes (Modified)
 * @version 2.0
 */
public class Rectangle extends Shape {

    public static int EDGES = 4;

    private int height;
    private int width;

    /**
     * Creates a new rectangle at default position with default color.
     */
    public Rectangle() {
        super(70, 15, Color.decode("#FAEBD7"));
        height = 30;
        width = 40;
    }

    /**
     * Creates a square given its side.
     *
     * @param side the side of the square
     */
    public Rectangle(int side) {
        this();
        height = side;
        width = side;
    }

    /**
     * Creates a square given its perimeter.
     * The perimeter must be divisible by 4.
     *
     * @param perimeterSquare total perimeter of the desired square (pixels)
     * @param byPerimeter     flag to distinguish from side constructor
     * @throws IllegalArgumentException if perimeter is not positive or not divisible by 4
     */
    public Rectangle(final int perimeterSquare, final boolean byPerimeter) {
        this();
        if (perimeterSquare <= 0 || perimeterSquare % 4 != 0) {
            throw new IllegalArgumentException("Perimeter must be > 0 and divisible by 4");
        }
        int side = perimeterSquare / 4;
        height = side;
        width = side;
    }

    /**
     * Returns the current position as an array [x, y].
     *
     * @return position array
     */
    public int[] getPosition() {
        return new int[]{xPosition, yPosition};
    }

    /**
     * Walks the rectangle around the canvas the specified number of times.
     *
     * @param times number of trips (absolute value used)
     */
    public void route(int times) {
        times = Math.abs(times);
        for (int i = 0; i < times; i++) {
            slowMoveHorizontal(130);
            slowMoveVertical(245);
            slowMoveHorizontal(-130);
            slowMoveVertical(-245);
        }
    }

    /**
     * Returns the perimeter of this rectangle.
     *
     * @return perimeter in pixels
     */
    public int perimeter() {
        return 2 * (height + width);
    }

    /**
     * Zooms the rectangle: '+' doubles dimensions, '-' halves them.
     *
     * @param z the zoom direction character
     */
    public void zoom(char z) {
        switch (z) {
            case '+':
                height *= 2;
                width *= 2;
                break;
            case '-':
                height /= 2;
                width /= 2;
                break;
            default:
        }
    }

    /**
     * Moves the rectangle back and forth the specified number of times.
     *
     * @param times number of walks (absolute value used)
     */
    public void walk(int times) {
        times = Math.abs(times);
        for (int i = 0; i < 2 * times; i++) {
            if (xPosition == 70) {
                slowMoveHorizontal(130);
            } else if (xPosition == 200) {
                slowMoveHorizontal(-130);
            }
        }
    }

    /**
     * Slowly moves the rectangle horizontally.
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
     * Slowly moves the rectangle vertically.
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
            canvas.draw(this, color,
                new java.awt.Rectangle(xPosition, yPosition,
                                       width, height));
        }
    }
}
