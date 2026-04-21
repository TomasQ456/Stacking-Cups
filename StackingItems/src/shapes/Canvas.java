package shapes;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Canvas is a class to allow for simple graphical drawing on a canvas.
 * This is a modification of the general purpose Canvas, specially made for
 * the BlueJ "shapes" example.
 *
 * @author Bruce Quig
 * @author Michael Kolling (mik)
 * @version 2.0 (shapes)
 */
public class Canvas {

    private static Canvas canvasSingleton;

    /**
     * Factory method to get the canvas singleton object.
     *
     * @return the single Canvas instance
     */
    public static Canvas getCanvas() {
        if (canvasSingleton == null) {
            canvasSingleton = new Canvas("Stacking Items Project", 600, 900,
                                         Color.white);
        }
        canvasSingleton.setVisible(true);
        return canvasSingleton;
    }

    //  ----- instance part -----

    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColour;
    private Image canvasImage;
    private List<Object> objects;
    private HashMap<Object, ShapeDescription> shapes;

    /**
     * Creates a Canvas with the given title, dimensions, and background color.
     *
     * @param title    title to appear in Canvas Frame
     * @param width    the desired width for the canvas
     * @param height   the desired height for the canvas
     * @param bgColour the desired background colour of the canvas
     */
    private Canvas(String title, int width, int height, Color bgColour) {
        frame = new JFrame();
        canvas = new CanvasPane();
        frame.setContentPane(canvas);
        frame.setTitle(title);
        canvas.setPreferredSize(new Dimension(width, height));
        backgroundColour = bgColour;
        frame.pack();
        objects = new ArrayList<Object>();
        shapes = new HashMap<Object, ShapeDescription>();
    }

    /**
     * Sets the canvas visibility and brings canvas to the front of screen
     * when made visible.
     *
     * @param visible boolean value representing the desired visibility of the canvas
     */
    public void setVisible(boolean visible) {
        if (graphic == null) {
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D) canvasImage.getGraphics();
            graphic.setColor(backgroundColour);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        frame.setVisible(visible);
    }

    /**
     * Draws a given shape onto the canvas.
     *
     * @param referenceObject an object to define identity for this shape
     * @param color           the color of the shape
     * @param shape           the AWT shape object to be drawn on the canvas
     */
    public void draw(Object referenceObject, Color color, java.awt.Shape shape) {
        objects.remove(referenceObject);
        objects.add(referenceObject);
        shapes.put(referenceObject, new ShapeDescription(shape, color));
        redraw();
    }

    /**
     * Erases a given shape from the screen.
     *
     * @param referenceObject the shape object to be erased
     */
    public void erase(Object referenceObject) {
        objects.remove(referenceObject);
        shapes.remove(referenceObject);
        redraw();
    }

    /**
     * Sets the foreground colour of the Canvas.
     *
     * @param color the new colour for the foreground of the Canvas
     */
    public void setForegroundColor(Color color) {
        graphic.setColor(color);
    }

    /**
     * Waits for a specified number of milliseconds before finishing.
     *
     * @param milliseconds the number of milliseconds to wait
     */
    public void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            // ignoring exception at the moment
        }
    }

    /**
     * Redraws all shapes currently on the Canvas.
     */
    private void redraw() {
        erase();
        for (Iterator i = objects.iterator(); i.hasNext(); ) {
            shapes.get(i.next()).draw(graphic);
        }
        canvas.repaint();
    }

    /**
     * Erases the whole canvas (does not repaint).
     */
    private void erase() {
        Color original = graphic.getColor();
        graphic.setColor(backgroundColour);
        Dimension size = canvas.getSize();
        graphic.fill(new java.awt.Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }

    /**
     * Inner class CanvasPane — the actual canvas component contained in the
     * Canvas frame.
     */
    private class CanvasPane extends JPanel {
        public void paint(Graphics g) {
            g.drawImage(canvasImage, 0, 0, null);
        }
    }

    /**
     * Inner class ShapeDescription — stores a shape and its color for drawing.
     */
    private class ShapeDescription {
        private java.awt.Shape shape;
        private Color colorC;

        public ShapeDescription(java.awt.Shape shape, Color color) {
            this.shape = shape;
            colorC = color;
        }

        public void draw(Graphics2D graphic) {
            setForegroundColor(colorC);
            graphic.draw(shape);
            graphic.fill(shape);
        }
    }
}
