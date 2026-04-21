package shapes;

import java.awt.Color;

/**
 * Abstract base class for all graphical shapes in the simulation.
 * Provides common position, color, and visibility behavior.
 */
public abstract class Shape {

    protected int xPosition;
    protected int yPosition;
    protected Color color;
    protected boolean isVisible;

    /**
     * Creates a new Shape at the given position with the given color.
     *
     * @param xPosition initial horizontal position in pixels
     * @param yPosition initial vertical position in pixels
     * @param color     initial color
     */
    public Shape(int xPosition, int yPosition, Color color) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.color = color;
        this.isVisible = false;
    }

    /** Makes this shape visible on the canvas. */
    public void makeVisible() {
        isVisible = true;
        draw();
    }

    /** Makes this shape invisible and erases it from the canvas. */
    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    /** Moves this shape 20 pixels to the right. */
    public void moveRight() { moveHorizontal(20); }

    /** Moves this shape 20 pixels to the left. */
    public void moveLeft() { moveHorizontal(-20); }

    /** Moves this shape 20 pixels upward. */
    public void moveUp() { moveVertical(-20); }

    /** Moves this shape 20 pixels downward. */
    public void moveDown() { moveVertical(20); }

    /**
     * Moves this shape horizontally by the given distance.
     *
     * @param distance pixels to move (positive = right, negative = left)
     */
    public void moveHorizontal(int distance) {
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Moves this shape vertically by the given distance.
     *
     * @param distance pixels to move (positive = down, negative = up)
     */
    public void moveVertical(int distance) {
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Changes the color of this shape.
     *
     * @param newColor the new color
     */
    public void changeColor(Color newColor) {
        color = newColor;
        draw();
    }

    /** Returns the current color of this shape. */
    public Color getColor() { return color; }

    /** Returns the current horizontal position of this shape. */
    public int getXPosition() { return xPosition; }

    /** Returns the current vertical position of this shape. */
    public int getYPosition() { return yPosition; }

    /**
     * Sets the position of this shape.
     *
     * @param x the new horizontal position in pixels
     * @param y the new vertical position in pixels
     */
    public void setPosition(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    /** Erases this shape from the canvas without changing visibility state. */
    protected void erase() {
        if (isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }

    /**
     * Draws this shape on the canvas according to its specific geometry.
     * Implementors must render the shape using the current xPosition,
     * yPosition, and color fields.
     */
    protected abstract void draw();
}
