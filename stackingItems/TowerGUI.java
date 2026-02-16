import java.util.ArrayList;

/**
 * Visual representation of the Tower on the Canvas.
 * Manages the drawing of all cups, lids, tower walls, and centimeter marks.
 * Reuses Rectangle shapes from the shapes project for all drawing.
 * The tower is drawn with walls on left and right, a base at the bottom,
 * and horizontal tick marks for each centimeter of height on the left wall.
 * 
 * <p>The scale factor is calculated dynamically so that the tower always
 * fits within the Canvas (300x300 pixels), regardless of the tower's
 * logical dimensions.</p>
 * 
 * @author Tomás
 * @version 1.0
 */
public class TowerGUI {
    private Tower tower;
    private boolean isVisible;
    private ArrayList<CupGUI> cupGUIs;
    private ArrayList<LidGUI> lidGUIs;
    private ArrayList<Rectangle> towerStructure;

    /** Width of the Canvas in pixels. */
    private static final int CANVAS_WIDTH = 300;
    /** Height of the Canvas in pixels. */
    private static final int CANVAS_HEIGHT = 300;
    /** Left margin in pixels before the tower starts. */
    private static final int MARGIN_LEFT = 40;
    /** Bottom margin in pixels below the tower base. */
    private static final int MARGIN_BOTTOM = 15;
    /** Top margin in pixels above the tower. */
    private static final int MARGIN_TOP = 10;
    /** Right margin in pixels after the tower. */
    private static final int MARGIN_RIGHT = 10;
    /** Width of each centimeter tick mark in pixels. */
    private static final int MARK_WIDTH = 5;
    /** Height (thickness) of each centimeter tick mark in pixels. */
    private static final int MARK_HEIGHT = 2;
    /** Thickness of the tower walls and base in pixels. */
    private static final int WALL_THICKNESS = 2;
    /** Minimum scale factor to ensure items are visible. */
    private static final int MIN_SCALE = 2;

    /** Calculated pixels per logical cm — computed at draw time. */
    private int scaleFactor;

    /**
     * Create a TowerGUI for the given tower.
     * Initializes internal lists but does not draw anything until
     * setVisible(true) is called.
     * @param tower the Tower domain object to represent visually
     */
    public TowerGUI(Tower tower) {
        this.tower = tower;
        this.isVisible = false;
        this.cupGUIs = new ArrayList<CupGUI>();
        this.lidGUIs = new ArrayList<LidGUI>();
        this.towerStructure = new ArrayList<Rectangle>();
        this.scaleFactor = MIN_SCALE;
    }

    /**
     * Calculate the scale factor so the tower fits within the Canvas.
     * Takes the minimum of horizontal and vertical scales, and ensures
     * it is at least MIN_SCALE.
     * @return the computed scale factor in pixels per cm
     */
    private int calculateScaleFactor() {
        int availableWidth = CANVAS_WIDTH - MARGIN_LEFT - MARGIN_RIGHT;
        int availableHeight = CANVAS_HEIGHT - MARGIN_TOP - MARGIN_BOTTOM;
        int towerLogicalWidth = tower.getWidth();
        int towerLogicalHeight = tower.getMaxHeight();
        if (towerLogicalWidth <= 0 || towerLogicalHeight <= 0) {
            return MIN_SCALE;
        }
        int scaleW = availableWidth / towerLogicalWidth;
        int scaleH = availableHeight / towerLogicalHeight;
        int scale = Math.min(scaleW, scaleH);
        if (scale < MIN_SCALE) {
            scale = MIN_SCALE;
        }
        return scale;
    }

    /**
     * Check if the tower fits on the Canvas screen.
     * With dynamic scaling, the tower fits as long as the minimum
     * scale factor produces at least MIN_SCALE pixels per cm.
     * @return true if the tower can be displayed without exceeding Canvas bounds
     */
    public boolean fitsOnScreen() {
        int scale = calculateScaleFactor();
        int towerPixelWidth = tower.getWidth() * scale;
        int towerPixelHeight = tower.getMaxHeight() * scale;
        int requiredWidth = MARGIN_LEFT + towerPixelWidth + MARGIN_RIGHT;
        int requiredHeight = MARGIN_TOP + towerPixelHeight + MARGIN_BOTTOM;
        return requiredWidth <= CANVAS_WIDTH && requiredHeight <= CANVAS_HEIGHT;
    }

    /**
     * Draw the complete tower on the canvas, including structure and items.
     * Recalculates the scale factor before drawing.
     * Does nothing if the tower is not visible.
     */
    public void draw() {
        if (!isVisible) {
            return;
        }
        scaleFactor = calculateScaleFactor();
        eraseAll();
        drawTowerStructure();
        drawItems();
    }

    /**
     * Erase the entire tower from the canvas and set visibility to false.
     */
    public void erase() {
        eraseAll();
        isVisible = false;
    }

    /**
     * Refresh the display by erasing and redrawing everything.
     * Only redraws if the tower is currently visible.
     */
    public void refresh() {
        if (isVisible) {
            draw();
        }
    }

    /**
     * Set the visibility of the tower.
     * When set to true, initializes the Canvas singleton and draws the tower.
     * When set to false, erases all visual elements.
     * @param v true to make visible, false to hide
     */
    public void setVisible(boolean v) {
        isVisible = v;
        if (v) {
            Canvas.getCanvas();
            draw();
        } else {
            eraseAll();
        }
    }

    /**
     * Check if the tower is currently visible on the canvas.
     * @return true if the tower is visible
     */
    public boolean isVisible() {
        return isVisible;
    }

    // ---- Private drawing methods ----

    /**
     * Draw the tower walls (left, right), base, and centimeter tick marks.
     * Uses Rectangle shapes for all structural elements.
     */
    private void drawTowerStructure() {
        int towerPixelWidth = tower.getWidth() * scaleFactor;
        int towerPixelHeight = tower.getMaxHeight() * scaleFactor;
        int baseY = CANVAS_HEIGHT - MARGIN_BOTTOM;
        int leftX = MARGIN_LEFT;

        // Left wall
        Rectangle leftWall = new Rectangle();
        leftWall.changeSize(towerPixelHeight, WALL_THICKNESS);
        leftWall.setPosition(leftX - WALL_THICKNESS, baseY - towerPixelHeight);
        leftWall.changeColor("black");
        leftWall.makeVisible();
        towerStructure.add(leftWall);

        // Right wall
        Rectangle rightWall = new Rectangle();
        rightWall.changeSize(towerPixelHeight, WALL_THICKNESS);
        rightWall.setPosition(leftX + towerPixelWidth, baseY - towerPixelHeight);
        rightWall.changeColor("black");
        rightWall.makeVisible();
        towerStructure.add(rightWall);

        // Base
        Rectangle base = new Rectangle();
        base.changeSize(WALL_THICKNESS, towerPixelWidth + 2 * WALL_THICKNESS);
        base.setPosition(leftX - WALL_THICKNESS, baseY);
        base.changeColor("black");
        base.makeVisible();
        towerStructure.add(base);

        // Centimeter tick marks on the left side (no numbers)
        for (int cm = 1; cm <= tower.getMaxHeight(); cm++) {
            Rectangle mark = new Rectangle();
            mark.changeSize(MARK_HEIGHT, MARK_WIDTH);
            mark.setPosition(leftX - WALL_THICKNESS - MARK_WIDTH,
                             baseY - (cm * scaleFactor));
            mark.changeColor("black");
            mark.makeVisible();
            towerStructure.add(mark);
        }
    }

    /**
     * Draw all stacked items (cups and lids) in the tower from base to top.
     * Each item is centered horizontally within the tower walls.
     * Cups and lids are created as CupGUI/LidGUI instances with the
     * current dynamic scale factor and tower pixel width.
     */
    private void drawItems() {
        cupGUIs.clear();
        lidGUIs.clear();

        int towerPixelWidth = tower.getWidth() * scaleFactor;
        int baseY = CANVAS_HEIGHT - MARGIN_BOTTOM;
        int leftX = MARGIN_LEFT;
        int currentY = baseY;

        ArrayList<StackItem> items = tower.getItems();
        for (StackItem item : items) {
            int itemPixelHeight = item.getHeight() * scaleFactor;
            currentY -= itemPixelHeight;

            if (item.getType().equals("cup")) {
                CupGUI cupGUI = new CupGUI(item.getCup(), scaleFactor,
                                           towerPixelWidth);
                cupGUI.draw(leftX, currentY);
                cupGUIs.add(cupGUI);
            } else {
                LidGUI lidGUI = new LidGUI(item.getLid(), scaleFactor,
                                           towerPixelWidth);
                lidGUI.draw(leftX, currentY);
                lidGUIs.add(lidGUI);
            }
        }
    }

    /**
     * Erase all visual elements from the canvas: cup GUIs, lid GUIs,
     * and tower structure rectangles.
     */
    private void eraseAll() {
        for (CupGUI cg : cupGUIs) {
            cg.erase();
        }
        cupGUIs.clear();
        for (LidGUI lg : lidGUIs) {
            lg.erase();
        }
        lidGUIs.clear();
        for (Rectangle r : towerStructure) {
            r.makeInvisible();
        }
        towerStructure.clear();
    }
}
