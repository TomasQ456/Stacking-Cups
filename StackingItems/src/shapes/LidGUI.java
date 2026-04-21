package shapes;

import java.awt.Color;

/**
 * Visual representation of a Lid using Rectangle shapes.
 * Draws a lid as a simple colored horizontal bar representing the lid covering.
 * The lid is centered horizontally within the tower.
 * Lids are always 1 cm (thin) in height.
 *
 * @author Acero - Quiceno
 * @version 5.0
 */
public class LidGUI extends Shape {

    private int lidWidth;
    private int lidHeight;
    private String lidType;

    private Rectangle body;
    private boolean isDrawn;
    private int scaleFactor;
    private int towerPixelWidth;

    /**
     * Creates a LidGUI for a lid at the given position.
     *
     * @param xPosition       the x position to draw
     * @param yPosition       the y position to draw
     * @param width           the logical width of the lid
     * @param lidHeight       the logical height of the lid (always 1)
     * @param lidType         the lid type ("normal", "fearful", "crazy")
     * @param companionColor  the companion cup color (used for normal lids)
     * @param scaleFactor     pixels per logical cm unit
     * @param towerPixelWidth total pixel width of the tower area
     */
    public LidGUI(int xPosition, int yPosition, int width, int lidHeight,
                   String lidType, Color companionColor,
                   int scaleFactor, int towerPixelWidth) {
        super(xPosition, yPosition, resolveColor(lidType, companionColor));
        this.lidWidth = width;
        this.lidHeight = lidHeight;
        this.lidType = lidType;
        this.scaleFactor = scaleFactor;
        this.towerPixelWidth = towerPixelWidth;
        this.body = new Rectangle();
        this.isDrawn = false;
    }

    /**
     * Resolves the fill color based on the lid type and companion color.
     *
     * @param type           the lid type string
     * @param companionColor the companion cup color (used for normal lids)
     * @return the resolved color
     */
    private static Color resolveColor(String type, Color companionColor) {
        if (type == null) return Color.DARK_GRAY;
        switch (type.toLowerCase()) {
            case "normal":
            case "normallid":
                return companionColor != null ? companionColor : Color.DARK_GRAY;
            case "fearful":
            case "fearfullid":
                return Color.GRAY;
            case "crazy":
            case "crazylid":
                return Color.GREEN;
            default:
                return Color.DARK_GRAY;
        }
    }

    /**
     * Draws the lid at the given position on the canvas.
     *
     * @param x the x coordinate of the tower left edge in pixels
     * @param y the y coordinate of the top edge of this lid in pixels
     */
    public void drawAt(int x, int y) {
        int pixelDiameter = (2 * lidWidth - 1) * scaleFactor;
        int pixelHeight = lidHeight * scaleFactor;
        int centeredX = x + (towerPixelWidth - pixelDiameter) / 2;

        body.changeSize(pixelHeight, pixelDiameter);
        body.setPosition(centeredX, y);
        body.changeColor(color);
        body.makeVisible();

        isDrawn = true;
    }

    /**
     * Erases the lid from the canvas.
     */
    public void eraseLid() {
        if (isDrawn) {
            body.makeInvisible();
            isDrawn = false;
        }
    }

    /**
     * Updates the position of the lid on the canvas.
     *
     * @param x the new x coordinate of the tower left edge in pixels
     * @param y the new y coordinate of the top edge in pixels
     */
    public void updatePosition(int x, int y) {
        eraseLid();
        drawAt(x, y);
    }

    @Override
    protected void draw() {
        drawAt(xPosition, yPosition);
    }
}
