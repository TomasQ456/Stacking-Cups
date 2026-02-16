import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Main controller class for the stacking items tower simulator.
 * Manages a tower where cups and lids can be stacked vertically.
 * Inspired by the ICPC 2025 Problem J "Stacking Cups".
 * 
 * <p>The tower has a fixed width and maximum height. Cups have a height
 * of (2*id - 1) cm and lids are always 1 cm tall. Each cup/lid is
 * identified by a unique integer id. When a cup and its matching lid
 * (same id) are adjacent in the tower (lid directly on top of the cup),
 * they are considered a "lided cup" and move as a unit.</p>
 * 
 * <p>The tower supports ordering (largest id at bottom), reversing
 * (smallest id at bottom), and querying stacked items and lided cups.</p>
 * 
 * @author Tomás
 * @version 1.0
 */
public class Tower {
    private int width;
    private int maxHeight;
    private int currentHeight;
    private boolean isOk;
    private ArrayList<StackItem> items;
    private TowerGUI towerGUI;

    /** Color palette for automatic cup/lid coloring. */
    private static final String[] COLORS = {
        "red", "blue", "green", "yellow", "magenta", "black"
    };

    /**
     * Create a new Tower with the given dimensions.
     * The tower starts empty with height 0.
     * @param width the logical width of the tower (max cup diameter that fits)
     * @param maxHeight the maximum stacking height in cm
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.currentHeight = 0;
        this.isOk = true;
        this.items = new ArrayList<StackItem>();
        this.towerGUI = new TowerGUI(this);
    }

    /**
     * Push a new cup onto the top of the tower.
     * Creates a cup with the given id. The diameter equals the id (logical),
     * the height is calculated as (2*id - 1) cm, and the color is assigned
     * automatically from a cyclic palette.
     * 
     * <p>Fails (isOk = false) if:</p>
     * <ul>
     *   <li>id is not positive</li>
     *   <li>a cup with the same id already exists</li>
     *   <li>the cup's diameter exceeds the tower width</li>
     *   <li>the cup's height would exceed the remaining tower capacity</li>
     * </ul>
     * 
     * @param i the unique id of the cup to create and push
     */
    public void pushCup(int i) {
        if (i <= 0) {
            isOk = false;
            showError("El id de la taza debe ser positivo.");
            return;
        }
        if (findCup(i) != null) {
            isOk = false;
            showError("Ya existe una taza con id " + i + ".");
            return;
        }
        if (i > width) {
            isOk = false;
            showError("La taza " + i + " no cabe en el ancho de la torre.");
            return;
        }
        int cupHeight = 2 * i - 1;
        if (currentHeight + cupHeight > maxHeight) {
            isOk = false;
            showError("La taza " + i + " no cabe en la altura de la torre.");
            return;
        }
        String color = COLORS[(i - 1) % COLORS.length];
        Cup cup = new Cup(i, i, color);
        StackItem item = new StackItem(cup);
        items.add(item);
        currentHeight += cupHeight;
        isOk = true;
        if (towerGUI.isVisible()) {
            towerGUI.refresh();
        }
    }

    /**
     * Push a new lid onto the top of the tower.
     * Creates a lid with the given id. If a cup with the same id exists,
     * the lid inherits its color and width; otherwise defaults are used.
     * Lids are always 1 cm tall.
     * 
     * <p>Fails (isOk = false) if:</p>
     * <ul>
     *   <li>id is not positive</li>
     *   <li>a lid with the same id already exists</li>
     *   <li>the lid's width exceeds the tower width</li>
     *   <li>adding 1 cm would exceed the remaining tower capacity</li>
     * </ul>
     * 
     * @param i the unique id of the lid to create and push
     */
    public void pushLid(int i) {
        if (i <= 0) {
            isOk = false;
            showError("El id de la tapa debe ser positivo.");
            return;
        }
        if (findLid(i) != null) {
            isOk = false;
            showError("Ya existe una tapa con id " + i + ".");
            return;
        }
        String color;
        int lidWidth;
        StackItem cupItem = findCup(i);
        if (cupItem != null) {
            color = cupItem.getCup().getColor();
            lidWidth = cupItem.getCup().getDiameter();
        } else {
            color = COLORS[(i - 1) % COLORS.length];
            lidWidth = i;
        }
        if (lidWidth > width) {
            isOk = false;
            showError("La tapa " + i + " no cabe en el ancho de la torre.");
            return;
        }
        if (currentHeight + 1 > maxHeight) {
            isOk = false;
            showError("La tapa " + i + " no cabe en la altura de la torre.");
            return;
        }
        Lid lid = new Lid(i, lidWidth, color);
        StackItem item = new StackItem(lid);
        items.add(item);
        currentHeight += 1;
        isOk = true;
        if (towerGUI.isVisible()) {
            towerGUI.refresh();
        }
    }

    /**
     * Remove the topmost cup from the tower.
     * Searches from the top of the stack downwards for the first cup.
     * 
     * <p>Fails (isOk = false) if no cup is found in the tower.</p>
     */
    public void popCup() {
        for (int idx = items.size() - 1; idx >= 0; idx--) {
            StackItem item = items.get(idx);
            if (item.getType().equals("cup")) {
                items.remove(idx);
                currentHeight -= item.getHeight();
                isOk = true;
                if (towerGUI.isVisible()) {
                    towerGUI.refresh();
                }
                return;
            }
        }
        isOk = false;
        showError("No hay tazas en la torre.");
    }

    /**
     * Remove the topmost lid from the tower.
     * Searches from the top of the stack downwards for the first lid.
     * 
     * <p>Fails (isOk = false) if no lid is found in the tower.</p>
     */
    public void popLid() {
        for (int idx = items.size() - 1; idx >= 0; idx--) {
            StackItem item = items.get(idx);
            if (item.getType().equals("lid")) {
                items.remove(idx);
                currentHeight -= item.getHeight();
                isOk = true;
                if (towerGUI.isVisible()) {
                    towerGUI.refresh();
                }
                return;
            }
        }
        isOk = false;
        showError("No hay tapas en la torre.");
    }

    /**
     * Remove a cup with the given id from the tower.
     * Searches through all items (base to top) to find the matching cup.
     * 
     * <p>Fails (isOk = false) if no cup with the given id is found.</p>
     * 
     * @param i the id of the cup to remove
     */
    public void removeCup(int i) {
        for (int idx = 0; idx < items.size(); idx++) {
            StackItem item = items.get(idx);
            if (item.getType().equals("cup") && item.getId() == i) {
                items.remove(idx);
                currentHeight -= item.getHeight();
                isOk = true;
                if (towerGUI.isVisible()) {
                    towerGUI.refresh();
                }
                return;
            }
        }
        isOk = false;
        showError("No se encontró la taza con id " + i + ".");
    }

    /**
     * Remove a lid with the given id from the tower.
     * Searches through all items (base to top) to find the matching lid.
     * 
     * <p>Fails (isOk = false) if no lid with the given id is found.</p>
     * 
     * @param i the id of the lid to remove
     */
    public void removeLid(int i) {
        for (int idx = 0; idx < items.size(); idx++) {
            StackItem item = items.get(idx);
            if (item.getType().equals("lid") && item.getId() == i) {
                items.remove(idx);
                currentHeight -= item.getHeight();
                isOk = true;
                if (towerGUI.isVisible()) {
                    towerGUI.refresh();
                }
                return;
            }
        }
        isOk = false;
        showError("No se encontró la tapa con id " + i + ".");
    }

    /**
     * Order the tower from largest to smallest id (largest at bottom, smallest at top).
     * If a cup and its matching lid (same id) are both present, the lid is placed
     * directly on top of its cup. Remaining lids without a matching cup are placed
     * at the top. Only items that fit within maxHeight are included; items that
     * would cause overflow are excluded.
     */
    public void orderTower() {
        ArrayList<StackItem> cups = new ArrayList<StackItem>();
        ArrayList<StackItem> lids = new ArrayList<StackItem>();
        separateCupsAndLids(cups, lids);
        sortByIdDescending(cups);
        sortByIdDescending(lids);
        rebuildTower(cups, lids);
    }

    /**
     * Reverse the tower order (smallest id at bottom, largest at top).
     * If a cup and its matching lid (same id) are both present, the lid is placed
     * directly on top of its cup. Remaining lids without a matching cup are placed
     * at the top. Only items that fit within maxHeight are included; items that
     * would cause overflow are excluded.
     */
    public void reverseTower() {
        ArrayList<StackItem> cups = new ArrayList<StackItem>();
        ArrayList<StackItem> lids = new ArrayList<StackItem>();
        separateCupsAndLids(cups, lids);
        sortByIdAscending(cups);
        sortByIdAscending(lids);
        rebuildTower(cups, lids);
    }

    /**
     * Get the current total height of all stacked items in the tower.
     * @return the height in cm
     */
    public int height() {
        return currentHeight;
    }

    /**
     * Get the ids of cups that are covered by their matching lid.
     * A cup is considered "lided" when the item directly above it is a lid
     * with the same id. Results are sorted from smallest to largest id.
     * @return array of ids for all lided cups, sorted ascending
     */
    public int[] lidedCups() {
        if (items.isEmpty()) {
            return new int[0];
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int idx = 0; idx < items.size() - 1; idx++) {
            StackItem current = items.get(idx);
            StackItem next = items.get(idx + 1);
            if (current.getType().equals("cup") && next.getType().equals("lid")
                    && current.getId() == next.getId()) {
                result.add(current.getId());
            }
        }
        int[] arr = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            arr[i] = result.get(i);
        }
        return arr;
    }

    /**
     * Get a description of all stacked items from base to top.
     * Each item contributes two consecutive strings: its type ("cup" or "lid")
     * and its id. For example: ["cup","4","lid","4","cup","1"].
     * @return array of strings alternating type and id from base to top
     */
    public String[] stackingItems() {
        ArrayList<String> result = new ArrayList<String>();
        for (StackItem s : items) {
            result.add(s.getType());
            result.add(String.valueOf(s.getId()));
        }
        return result.toArray(new String[0]);
    }

    /**
     * Make the tower visible on the canvas.
     * First ensures the Canvas singleton exists, then checks if the tower
     * fits on screen. If it does not fit, shows a JOptionPane error and
     * does not make the tower visible.
     */
    public void makeVisible() {
        Canvas.getCanvas();
        if (!towerGUI.fitsOnScreen()) {
            isOk = false;
            JOptionPane.showMessageDialog(null,
                "La torre no cabe en la pantalla. No se puede hacer visible.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        towerGUI.setVisible(true);
        isOk = true;
    }

    /**
     * Make the tower invisible. Erases all visual elements from the canvas
     * but keeps the tower data intact.
     */
    public void makeInvisible() {
        towerGUI.setVisible(false);
        isOk = true;
    }

    /**
     * Exit the simulator. Erases the tower from the canvas and sets
     * visibility to false.
     */
    public void exit() {
        towerGUI.erase();
        isOk = true;
    }

    /**
     * Check if the last operation was successful.
     * @return true if the last operation succeeded, false otherwise
     */
    public boolean ok() {
        return isOk;
    }

    /**
     * Get the logical width of the tower.
     * @return the width (maximum cup diameter that fits)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the maximum height of the tower.
     * @return the max height in cm
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * Get the list of stacked items from base to top.
     * Used internally by TowerGUI for drawing.
     * @return the ArrayList of StackItems
     */
    public ArrayList<StackItem> getItems() {
        return items;
    }

    // ---- Private helper methods ----

    /**
     * Find a cup StackItem by its id.
     * @param id the id to search for
     * @return the matching StackItem wrapping a Cup, or null if not found
     */
    private StackItem findCup(int id) {
        for (StackItem s : items) {
            if (s.getType().equals("cup") && s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * Find a lid StackItem by its id.
     * @param id the id to search for
     * @return the matching StackItem wrapping a Lid, or null if not found
     */
    private StackItem findLid(int id) {
        for (StackItem s : items) {
            if (s.getType().equals("lid") && s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * Find a lid StackItem in a specific list by its id.
     * @param lids the list of lid StackItems to search
     * @param id the id to search for
     * @return the matching StackItem, or null if not found
     */
    private StackItem findLidInList(ArrayList<StackItem> lids, int id) {
        for (StackItem s : lids) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * Separate the current items into cups and lids lists.
     * @param cups output list that will receive all cup StackItems
     * @param lids output list that will receive all lid StackItems
     */
    private void separateCupsAndLids(ArrayList<StackItem> cups,
                                     ArrayList<StackItem> lids) {
        for (StackItem s : items) {
            if (s.getType().equals("cup")) {
                cups.add(s);
            } else {
                lids.add(s);
            }
        }
    }

    /**
     * Rebuild the tower from sorted cups and lids lists.
     * Places cups in order, inserting matching lids directly on top.
     * Remaining unmatched lids are appended at the top.
     * Only includes items that fit within maxHeight.
     * Updates items, currentHeight, isOk and refreshes GUI.
     * @param cups sorted list of cup StackItems
     * @param lids sorted list of lid StackItems (modified: matched lids are removed)
     */
    private void rebuildTower(ArrayList<StackItem> cups,
                              ArrayList<StackItem> lids) {
        ArrayList<StackItem> ordered = new ArrayList<StackItem>();
        int accHeight = 0;
        for (StackItem cup : cups) {
            if (accHeight + cup.getHeight() > maxHeight) {
                continue;
            }
            ordered.add(cup);
            accHeight += cup.getHeight();
            StackItem matchingLid = findLidInList(lids, cup.getId());
            if (matchingLid != null) {
                if (accHeight + matchingLid.getHeight() <= maxHeight) {
                    ordered.add(matchingLid);
                    accHeight += matchingLid.getHeight();
                    lids.remove(matchingLid);
                }
            }
        }
        for (StackItem lid : lids) {
            if (accHeight + lid.getHeight() <= maxHeight) {
                ordered.add(lid);
                accHeight += lid.getHeight();
            }
        }
        items = ordered;
        currentHeight = accHeight;
        isOk = true;
        if (towerGUI.isVisible()) {
            towerGUI.refresh();
        }
    }

    /**
     * Sort a list of StackItems by id in descending order (largest first).
     * Uses selection sort for simplicity.
     * @param list the list to sort in place
     */
    private void sortByIdDescending(ArrayList<StackItem> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getId() < list.get(j).getId()) {
                    StackItem temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }

    /**
     * Sort a list of StackItems by id in ascending order (smallest first).
     * Uses selection sort for simplicity.
     * @param list the list to sort in place
     */
    private void sortByIdAscending(ArrayList<StackItem> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getId() > list.get(j).getId()) {
                    StackItem temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }

    /**
     * Show an error message using JOptionPane, but only if the tower is visible.
     * When invisible, errors are silent (only isOk is set to false).
     * @param message the error message to display
     */
    private void showError(String message) {
        if (towerGUI.isVisible()) {
            JOptionPane.showMessageDialog(null, message,
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
