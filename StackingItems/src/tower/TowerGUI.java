package tower;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import shapes.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Native Java Swing visualization endpoint for the stacking tower.
 * Discards legacy static geometry arrays in favor of real-time recursive Graphics rendering.
 *
 * @author Automation & Efficiency Architect
 */
public class TowerGUI extends JPanel {
    
    private Tower tower;
    private ArrayList<Rectangle> towerStructure;
    private List<Item> previouslyDrawnItems = new ArrayList<>();
    
    private final int scaleFactor = Item.SCALE; // 15
    private final int CANVAS_HEIGHT = 800;
    private final int MARGIN_BOTTOM = 50;
    private final int MARGIN_LEFT = 150;
    private final int WALL_THICKNESS = Item.SCALE;
    private final int MARK_HEIGHT = 2;
    private final int MARK_WIDTH = 10;

    public TowerGUI(Tower tower) {
        this.tower = tower;
        this.towerStructure = new ArrayList<Rectangle>();
        drawTowerStructure();
    }
    
    
    private void drawTowerStructure() {
        int towerPixelWidth = tower.getWidth() * scaleFactor;
        int towerPixelHeight = tower.getMaxHeight() * scaleFactor;
        int baseY = CANVAS_HEIGHT - MARGIN_BOTTOM;
        int leftX = MARGIN_LEFT;

        Rectangle leftWall = new Rectangle();
        leftWall.changeSize(towerPixelHeight, WALL_THICKNESS);
        leftWall.setPosition(leftX - WALL_THICKNESS, baseY - towerPixelHeight);
        leftWall.changeColor(Color.black);
        towerStructure.add(leftWall);

        Rectangle rightWall = new Rectangle();
        rightWall.changeSize(towerPixelHeight, WALL_THICKNESS);
        rightWall.setPosition(leftX + towerPixelWidth, baseY - towerPixelHeight);
        rightWall.changeColor(Color.black);
        towerStructure.add(rightWall);

        Rectangle base = new Rectangle();
        base.changeSize(WALL_THICKNESS, towerPixelWidth + 2 * WALL_THICKNESS);
        base.setPosition(leftX - WALL_THICKNESS, baseY);
        base.changeColor(Color.black);
        towerStructure.add(base);

        centimeterTickMarks(leftX, baseY);
    }

    private void centimeterTickMarks(int leftX, int baseY) {
        for (int cm = 1; cm <= tower.getMaxHeight(); cm++) {
            Rectangle mark = new Rectangle();
            mark.changeSize(MARK_HEIGHT, MARK_WIDTH);
            mark.setPosition(leftX - WALL_THICKNESS - MARK_WIDTH, baseY - (cm * scaleFactor));
            mark.changeColor(Color.black);
            towerStructure.add(mark);
        }
    }

    /**
     * Forces a full redraw of the tower. 
     * Crucial for operations like orderTower or reverseTower.
     */
    public void render() {
        
        eraseAllItems();
        
        for (Rectangle rect : towerStructure) {
            rect.makeVisible();
        }
        
        previouslyDrawnItems.addAll(tower.getAllItems());

        int towerPixelWidth = tower.getWidth() * scaleFactor;
        int centerX = MARGIN_LEFT + (towerPixelWidth / 2);
        int currentBaseY = CANVAS_HEIGHT - MARGIN_BOTTOM;

        for (Item root : tower.getRoots()) {
            root.draw(centerX, currentBaseY);
            currentBaseY -= root.getEffectiveHeight() * scaleFactor;
        }
    }

    public void eraseAllItems() {
        if (previouslyDrawnItems != null) {
            for (Item item : previouslyDrawnItems) {
                if (item != null) {
                    item.erase();
                }
            }
            previouslyDrawnItems.clear(); 
        }
    }

    public void eraseAll() {
        for (Rectangle rect : towerStructure) {
            rect.makeInvisible();
        }
        eraseAllItems();
    }

    // Contracts mapped to TowerCanvas validation
    public int requiredWidth() { return 800; }
    public int requiredHeight() { return 700; }
    public boolean message() { return false; }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 700);
    }
}