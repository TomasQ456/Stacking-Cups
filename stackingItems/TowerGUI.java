import java.util.ArrayList;
import java.awt.Color;
import static java.lang.Math.*;

/**
 * Visual representation of the Tower on the Canvas.
 * Manages the drawing of all cups, lids, tower walls, and centimeter marks.
 * Reuses Rectangle shapes from the shapes project for all drawing.
 * The tower is drawn with walls on left and right, a base at the bottom,
 * and horizontal tick marks for each centimeter of height on the left wall.
 * 
 * @author Acero - Quiceno
 * @version 3.0
 */
public class TowerGUI {
    private Tower tower;
    private boolean isVisible;
    private ArrayList<CupGUI> cupGUIs;
    private ArrayList<LidGUI> lidGUIs;
    private ArrayList<Rectangle> towerStructure;
    
    // Pixels
    private static final int CANVAS_WIDTH = 300;
    private static final int CANVAS_HEIGHT = 300;
    
    private static final int MARGIN_LEFT = 40;
    private static final int MARGIN_BOTTOM = 15;
    private static final int MARGIN_TOP = 10;
    private static final int MARGIN_RIGHT = 10;
    
    private static final int MARK_WIDTH = 5;
    private static final int MARK_HEIGHT = 2;

    private static final int WALL_THICKNESS = 2;
    
    private static final int MIN_SCALE = 2; // Minimum scale factor to ensure items are visible

    public static int scaleFactor; // Calculated pixels per logical cm — computed at draw time.

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
     * 
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
     * 
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
     */
    public void refresh() {
        if (isVisible) {
            draw();
        }
    }

    /**
     * Set the visibility of the tower.
     * 
     * @param visibility true to make visible, false to hide
     */
    public void setVisible(boolean visibility) {
        isVisible = visibility;
        if (visibility) {
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
        leftWall.changeColor(Color.black);
        leftWall.makeVisible();
        towerStructure.add(leftWall);

        // Right wall
        Rectangle rightWall = new Rectangle();
        rightWall.changeSize(towerPixelHeight, WALL_THICKNESS);
        rightWall.setPosition(leftX + towerPixelWidth, baseY - towerPixelHeight);
        rightWall.changeColor(Color.black);
        rightWall.makeVisible();
        towerStructure.add(rightWall);

        // Base
        Rectangle base = new Rectangle();
        base.changeSize(WALL_THICKNESS, towerPixelWidth + 2 * WALL_THICKNESS);
        base.setPosition(leftX - WALL_THICKNESS, baseY);
        base.changeColor(Color.black);
        base.makeVisible();
        towerStructure.add(base);

        // Marks
        centimeterTickMarks(leftX, baseY);

    }

    /**
     * Constructs centimeter tick marks on the left side
     * @param leftX correct position of the marks in left side
     * @param baseY correct position of the bottom marks (up and down side) (Y axis)
     */
    private void centimeterTickMarks(int leftX, int baseY){
        for (int cm = 1; cm <= tower.getMaxHeight(); cm++) {
            Rectangle mark = new Rectangle();
            mark.changeSize(MARK_HEIGHT, MARK_WIDTH);
            mark.setPosition(leftX - WALL_THICKNESS - MARK_WIDTH,
                             baseY - (cm * scaleFactor));
            mark.changeColor(Color.black);
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
        cupGUIs.clear();  // To have a clean workstation 
        lidGUIs.clear();

        int towerPixelWidth = tower.getWidth() * scaleFactor;
        int baseY = CANVAS_HEIGHT - MARGIN_BOTTOM;  // e.g. 285
                    // CANVAS_HEIGHT = 300 MARGIN_BOTTOM = 15
        int leftX = MARGIN_LEFT;
        int currentY; 

        ArrayList<Item> items = tower.getStack();
        for (Item item : items) {
            int itemPixelHeight =  item.getTopPosition() * scaleFactor; // Obtain heigth, but in scale factor
                                        // e.g. height = 7 and scaleFactor = 30, itemPixelHeight = 210
            currentY = baseY - itemPixelHeight;    
                                    // Top of the Item (under)

            if (item.getType().equals("cup")) {
                CupGUI cupGUI = new CupGUI((Cup) item, scaleFactor,
                                           towerPixelWidth);
                cupGUI.draw(leftX, currentY);
                cupGUIs.add(cupGUI);
            } else {
                LidGUI lidGUI = new LidGUI((Lid) item, scaleFactor,
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
