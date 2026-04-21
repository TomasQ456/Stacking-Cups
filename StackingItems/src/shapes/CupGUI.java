package shapes;

import java.awt.Color;

/**
 * Visual representation of a Cup using Rectangle shapes.
 * Draws a cup as a U-shape with left wall, right wall, and bottom,
 * leaving the interior hollow. The cup is centered horizontally within the tower.
 *
 * @author Acero - Quiceno
 * @version 5.0
 */
public class CupGUI extends Shape {

    private int cupWidth;
    private int cupHeight;
    private String cupType;

    private Rectangle leftWall;
    private Rectangle rightWall;
    private Rectangle bottom;
    private boolean isDrawn;
    private int scaleFactor;
    private int towerPixelWidth;
    private int cupWallThickness;

    /**
     * Creates a CupGUI for a cup at the given position.
     *
     * @param xPosition      the x position to draw
     * @param yPosition      the y position to draw
     * @param width          the logical width of the cup
     * @param height         the logical height of the cup
     * @param cupType        the cup type ("normal", "opener", "hierarchical")
     * @param scaleFactor    pixels per logical cm unit
     * @param towerPixelWidth total pixel width of the tower area
     */
    public CupGUI(int xPosition, int yPosition, int width, int height,
                   String cupType, int scaleFactor, int towerPixelWidth) {
        super(xPosition, yPosition, resolveColor(cupType));
        this.cupWidth = width;
        this.cupHeight = height;
        this.cupType = cupType;
        this.scaleFactor = scaleFactor;
        this.towerPixelWidth = towerPixelWidth;
        this.cupWallThickness = scaleFactor;
        this.leftWall = new Rectangle();
        this.rightWall = new Rectangle();
        this.bottom = new Rectangle();
        this.isDrawn = false;
    }

    /**
     * Resolves the fill color based on the cup type.
     *
     * @param type the cup type string
     * @return the resolved color
     */
    private static Color resolveColor(String type) {
        if (type == null) return Color.GRAY;
        switch (type.toLowerCase()) {
            case "normal":
            case "normalcup":
                return Color.BLUE;
            case "opener":
            case "openercup":
                return Color.RED;
            case "hierarchical":
            case "hierarchicalcup":
                return new Color(218, 165, 32); // goldenrod
            default:
                return Color.GRAY;
        }
    }

    /**
     * Draws the cup at the stored position using the given logical dimensions.
     *
     * @param x the x coordinate of the tower left edge in pixels
     * @param y the y coordinate of the top edge of this cup in pixels
     */
    public void drawAt(int x, int y) {
        int pixelWidth = cupHeight * scaleFactor;
        int pixelHeight = cupHeight * scaleFactor;
        int centeredX = x + (towerPixelWidth - pixelWidth) / 2;

        leftWall.changeSize(pixelHeight, cupWallThickness);
        leftWall.setPosition(centeredX, y);
        leftWall.changeColor(color);
        leftWall.makeVisible();

        rightWall.changeSize(pixelHeight, cupWallThickness);
        rightWall.setPosition(centeredX + pixelWidth - cupWallThickness, y);
        rightWall.changeColor(color);
        rightWall.makeVisible();

        bottom.changeSize(cupWallThickness, pixelWidth);
        bottom.setPosition(centeredX, y + pixelHeight - cupWallThickness);
        bottom.changeColor(color);
        bottom.makeVisible();

        isDrawn = true;
    }

    /**
     * Erases the cup from the canvas.
     */
    public void eraseCup() {
        if (isDrawn) {
            leftWall.makeInvisible();
            rightWall.makeInvisible();
            bottom.makeInvisible();
            isDrawn = false;
        }
    }

    /**
     * Updates the position of the cup on the canvas.
     *
     * @param x the new x coordinate of the tower left edge in pixels
     * @param y the new y coordinate of the top edge in pixels
     */
    public void updatePosition(int x, int y) {
        eraseCup();
        drawAt(x, y);
    }

    @Override
    protected void draw() {
        drawAt(xPosition, yPosition);
    }
}
