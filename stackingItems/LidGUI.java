import java.awt.Color;

/**
 * Visual representation of a Lid using Rectangle shapes from the shapes project.
 * Draws a lid as a simple colored horizontal bar representing the lid covering.
 * The lid is visually distinct from cups and is centered horizontally within the tower.
 * Lids are always 1 cm (thin) in height.
 * 
 * @author Acero - Quiceno
 * @version 3.0
 */
public class LidGUI {
    private Lid lid;
    private Rectangle body;
    private boolean isDrawn;
    private int scaleFactor;
    private int towerPixelWidth;

    /**
     * Create a LidGUI for the given lid.
     * @param lid the lid to represent visually
     * @param scaleFactor pixels per logical cm unit
     * @param towerPixelWidth total pixel width of the tower area
     */
    public LidGUI(Lid lid, int scaleFactor, int towerPixelWidth) {
        this.lid = lid;
        this.body = new Rectangle();
        this.isDrawn = false;
        this.scaleFactor = scaleFactor;
        this.towerPixelWidth = towerPixelWidth;
    }

    /**
     * Draw the lid at the given position on the canvas.
     * Centered horizontally in the tower.
     * @param x the x coordinate of the tower left edge in pixels
     * @param y the y coordinate of the top edge of this lid in pixels
     */
    public void draw(int x, int y) {
        int pixelDiameter = lid.getDiameter() * scaleFactor;
        int pixelHeight = Item.thick * scaleFactor;
        int centeredX = x + (towerPixelWidth - pixelDiameter) / 2;
        Color color = lid.getColor();

        body.changeSize(pixelHeight, pixelDiameter);
        body.setPosition(centeredX, y);
        body.changeColor(color);
        body.makeVisible();

        isDrawn = true;
    }

    /**
     * Erase the lid from the canvas.
     */
    public void erase() {
        if (isDrawn) {
            body.makeInvisible();
            isDrawn = false;
        }
    }

    /**
     * Update the position of the lid on the canvas.
     * Erases the current drawing and redraws at the new position.
     * @param x the new x coordinate of the tower left edge in pixels
     * @param y the new y coordinate of the top edge in pixels
     */
    public void updatePosition(int x, int y) {
        erase();
        draw(x, y);
    }

    /**
     * Get the underlying Lid domain object.
     * @return the Lid object associated with this GUI
     */
    public Lid getLid() {
        return lid;
    }
}
