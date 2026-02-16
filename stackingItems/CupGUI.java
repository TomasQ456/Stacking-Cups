/**
 * Visual representation of a Cup using Rectangle shapes from the shapes project.
 * Draws a cup as a U-shaped container (left wall, bottom, right wall) with
 * the cup color showing ONLY the walls and bottom, leaving the interior hollow.
 * The cup is centered horizontally within the tower.
 * 
 * @author Tomás
 * @version 2.0
 */
public class CupGUI {
    private Cup cup;
    private Rectangle leftWall;
    private Rectangle rightWall;
    private Rectangle bottom;
    private boolean isDrawn;
    private int scaleFactor;
    private int towerPixelWidth;

    /** Thickness of the cup walls in pixels. */
    private static final int CUP_WALL = 2;

    /**
     * Create a CupGUI for the given cup.
     * @param cup the cup to represent visually
     * @param scaleFactor pixels per logical cm unit
     * @param towerPixelWidth total pixel width of the tower area
     */
    public CupGUI(Cup cup, int scaleFactor, int towerPixelWidth) {
        this.cup = cup;
        this.leftWall = new Rectangle();
        this.rightWall = new Rectangle();
        this.bottom = new Rectangle();
        this.isDrawn = false;
        this.scaleFactor = scaleFactor;
        this.towerPixelWidth = towerPixelWidth;
    }

    /**
     * Draw the cup at the given position on the canvas.
     * The cup is drawn as a U-shape: only the left wall, right wall, and bottom
     * in the cup's color. The interior is left hollow (white).
     * Centered horizontally in the tower.
     * @param x the x coordinate of the tower left edge in pixels
     * @param y the y coordinate of the top edge of this cup in pixels
     */
    public void draw(int x, int y) {
        int pixelWidth = cup.getDiameter() * scaleFactor;
        int pixelHeight = cup.getHeight() * scaleFactor;
        int centeredX = x + (towerPixelWidth - pixelWidth) / 2;
        String color = cup.getColor();

        // Left wall (colored - part of the cup structure)
        leftWall.changeSize(pixelHeight, CUP_WALL);
        leftWall.setPosition(centeredX, y);
        leftWall.changeColor(color);
        leftWall.makeVisible();

        // Right wall (colored - part of the cup structure)
        rightWall.changeSize(pixelHeight, CUP_WALL);
        rightWall.setPosition(centeredX + pixelWidth - CUP_WALL, y);
        rightWall.changeColor(color);
        rightWall.makeVisible();

        // Bottom (colored - part of the cup structure)
        bottom.changeSize(CUP_WALL, pixelWidth);
        bottom.setPosition(centeredX, y + pixelHeight - CUP_WALL);
        bottom.changeColor(color);
        bottom.makeVisible();

        isDrawn = true;
    }

    /**
     * Erase the cup from the canvas.
     */
    public void erase() {
        if (isDrawn) {
            leftWall.makeInvisible();
            rightWall.makeInvisible();
            bottom.makeInvisible();
            isDrawn = false;
        }
    }

    /**
     * Update the position of the cup on the canvas.
     * Erases the current drawing and redraws at the new position.
     * @param x the new x coordinate of the tower left edge in pixels
     * @param y the new y coordinate of the top edge in pixels
     */
    public void updatePosition(int x, int y) {
        erase();
        draw(x, y);
    }

    /**
     * Get the underlying Cup domain object.
     * @return the Cup object associated with this GUI
     */
    public Cup getCup() {
        return cup;
    }
}