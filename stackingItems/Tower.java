import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.Random;
import java.awt.Color;
import static java.lang.Math.*;

/**
 * Main controller class for the stacking stack tower simulator.
 * Manages a tower where cups and lids can be stacked vertically.
 * Inspired by the ICPC 2025 Problem J "Stacking Cups".
 * 
 * @ Heigth = 2i - 1 
 * 
 * @author Acero - Quiceno
 * @version 3.0
 */
public class Tower {
    
    private int width;
    private int maxHeight;
    private int currentHeight;
    private boolean isOk;
    private ArrayList<Item> stack;   // Created to store our stack
    private TowerGUI towerGUI;       // GUI controller

    /**
     * Create a new Tower with the given dimensions.
     * @param width the logical width of the tower (max cup diameter that fits)
     * @param maxHeight the maximum stacking height in cm
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.currentHeight = 0;
        this.isOk = true;
        this.stack = new ArrayList<Item>();
        this.towerGUI = new TowerGUI(this);
        makeVisible();
    }

    /**
     * Push a new cup onto the top of the tower.
     * Creates a cup with the given id
     * Can occurs something, so, this changes tower status (isOk = false)
     * When:
     * <ul>
     *   <li> id is not positive</li>
     *   <li> a cup with the same id already exists</li>
     *   <li> the cup's height would exceed the remaining tower capacity</li>
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
        
        int cupHeight = (2 * i) - 1;
        int[] validateTopAndBaseItemPosition = topAndBaseItemPosition(i, cupHeight, stack);
        int itemTopPosition = validateTopAndBaseItemPosition[0];
        int itemBasePosition = validateTopAndBaseItemPosition[1];
        
        if (itemTopPosition > maxHeight) {
            isOk = false;
            showError("La taza " + i + " no cabe en la altura de la torre.");
            return;
        }  
        
        createCup(i, itemBasePosition);
        isOk = true;
        
        if (towerGUI.isVisible()) {
            towerGUI.refresh();
        }
    }

    /**
     * Auxiliary method for pushCup operation
     * @param i the unique id, given to see if the cup can create
     * @param itemBasePosition the y position when Item starts 
     */
    private void createCup(int i, int itemBasePosition){
        Color color;   // Our can use the random method of the Canvas class
        Lid lidItem = findLid(i);
        if (lidItem != null) {
            color = lidItem.getColor();
        } else {
            color = Canvas.randomColor();
        }
        
        Cup cup = new Cup(i, color);
        cup.setBasePosition(itemBasePosition);
        stack.add(cup);
        recalculatedStackPositions(stack);
    }
    
    /**
     * Push a new lid onto the top of the tower.
     * Creates a lid with the given id. If a cup with the same id exists,
     * the lid inherits its color and diameter; otherwise defaults are used.
     * Lids are always 1 cm tall.
     * 
     * Can occur something, so, this changes tower status (isOk = false)
     * When:
     * <ul>
     *   <li> id is not positive</li>
     *   <li> a lid with the same id already exists</li>
     *   <li> adding 1 cm would exceed the remaining tower capacity</li>
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
        
        int lidHeight = Item.thick;
        int[] validateTopAndBaseItemPosition = topAndBaseItemPosition(i, lidHeight, stack);
        int itemTopPosition = validateTopAndBaseItemPosition[0];
        int itemBasePosition = validateTopAndBaseItemPosition[1];
        
        if (itemTopPosition > maxHeight) {
            isOk = false;
            showError("La tapa " + i + " no cabe en la altura de la torre.");
            return;
        }
        
        createLid(i, itemBasePosition);
        isOk = true;
        
        if (towerGUI.isVisible()) {
            towerGUI.refresh();
        }
    }

    /**
     * Auxiliary method for pushLid operation
     * @param i the unique id, given to see if the lid can create
     * @param itemBasePosition the y position when Item starts 
     */
    private void createLid(int i, int itemBasePosition){
        Color color;
        Cup cupItem = findCup(i);
        if (cupItem != null) {
            color = cupItem.getColor();
        } else {
            color = Canvas.randomColor();
        }
        
        Lid lid = new Lid(i, color);
        lid.setBasePosition(itemBasePosition);
        stack.add(lid);
        recalculatedStackPositions(stack);
    }
    
    /**
     * Remove the topmost cup from the tower.
     * Searches from the top of the stack downwards for the first cup.
     * Changes its status (isOk = false) if no cup is found in the tower
     */
    public void popCup() {
        for (int idx = stack.size() - 1; idx >= 0; idx--) {
            Item item = stack.get(idx);
            if (item.getType().equals("cup")) {
        
                stack.remove(idx);
                recalculatedStackPositions(stack);   // We need to reconfigure the tower
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
     * Changes its status (isOk = false) if no lid is found in the tower
     */
    public void popLid() {
        for (int idx = stack.size() - 1; idx >= 0; idx--) {
            Item item = stack.get(idx);
            if (item.getType().equals("lid")) {
                
                stack.remove(idx);
                recalculatedStackPositions(stack);
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
     * Searches through all stack (base to top) to find the matching cup.
     * 
     * Changes its status (isOk = false) if no cup with the given id is found.</p>
     * 
     * @param i the id of the cup to remove
     */
    public void removeCup(int i) {
        for (int idx = 0; idx < stack.size(); idx++) {
            Item item = stack.get(idx);
            if (item.getType().equals("cup") && item.getId() == i) {
                
                stack.remove(idx);
                recalculatedStackPositions(stack);
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
     * Searches through all stack (base to top) to find the matching lid.
     * 
     * Changes its status (isOk = false) if no lid with the given id is found.</p>
     * 
     * @param i the id of the lid to remove
     */
    public void removeLid(int i) {
        for (int idx = 0; idx < stack.size(); idx++) {
            Item item = stack.get(idx);
            if (item.getType().equals("lid") && item.getId() == i) {
                
                stack.remove(idx);
                recalculatedStackPositions(stack);
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
     * at the top. Only stack that fit within maxHeight are included; stack that
     * would cause overflow are excluded.
     */
    public void orderTower() {
        ArrayList<Item> cups = new ArrayList<Item>();
        ArrayList<Item> lids = new ArrayList<Item>();
        separateCupsAndLids(cups, lids);
        sortByIdDescending(cups);
        sortByIdDescending(lids);
        rebuildTower(cups, lids);
    }

    /**
     * Reverse the tower order (smallest id at bottom, largest at top).
     * If a cup and its matching lid (same id) are both present, the lid is placed
     * directly on top of its cup. Remaining lids without a matching cup are placed
     * at the top. Only stack that fit within maxHeight are included; stack that
     * would cause overflow are excluded.
     */
    public void reverseTower() {
        ArrayList<Item> cups = new ArrayList<Item>();
        ArrayList<Item> lids = new ArrayList<Item>();
        separateCupsAndLids(cups, lids);
        sortByIdAscending(cups);
        sortByIdAscending(lids);
        rebuildTower(cups, lids);
    }

    /**
     * Get the current total height of all stacked stack in the tower.
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
        if (stack.isEmpty()) {
            return new int[0];
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int idx = 0; idx < stack.size() - 1; idx++) {
            Item current = stack.get(idx);
            Item next = stack.get(idx + 1);
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
     * Get a description of all stacked stack from base to top.
     * Each item contributes two consecutive strings: its type ("cup" or "lid")
     * and its id. For example: ["cup","4","lid","4","cup","1"].
     * @return array of strings alternating type and id from base to top
     */
    public String[] stackingStack() {
        ArrayList<String> result = new ArrayList<String>();
        for (Item s : stack) {
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
     * Get the list of stacked stack from base to top.
     * Used internally by TowerGUI for drawing.
     * @return the ArrayList of Stackstack
     */
    public ArrayList<Item> getStack() {
        return stack;
    }

    // ---- Private helper methods ----

    /**
     * Find a cup StackItem by its id.
     * @param id the id to search for
     * @return the matching StackItem wrapping a Cup, or null if not found
     */
    private Cup findCup(int id) {
        for (Item s : stack) {
            if (s.getType().equals("cup") && s.getId() == id) {
                return (Cup) s;
            }
        }
        return null;
    }

    /**
     * Find a lid StackItem by its id.
     * @param id the id to search for
     * @return the matching StackItem wrapping a Lid, or null if not found
     */
    private Lid findLid(int id) {
        for (Item s : stack) {
            if (s.getType().equals("lid") && s.getId() == id) {
                return (Lid) s;
            }
        }
        return null;
    }

    /**
     * Find a lid StackItem in a specific list by its id.
     * @param lids the list of lid Stackstack to search
     * @param id the id to search for
     * @return the matching StackItem, or null if not found
     */
    private Lid findLidInList(ArrayList<Item> lids, int id) {
        for (Item s : lids) {
            if (s.getId() == id) {
                return (Lid) s;
            }
        }
        return null;
    }

    /**
     * Separate the current stack into cups and lids lists.
     * @param cups output list that will receive all cup Stackstack
     * @param lids output list that will receive all lid Stackstack
     */
    private void separateCupsAndLids(ArrayList<Item> cups,
                                     ArrayList<Item> lids) {
        for (Item s : stack) {
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
     * 
     * @param cups sorted list of cup Stackstack
     * @param lids sorted list of lid Stackstack
     */
    private void rebuildTower(ArrayList<Item> cups, ArrayList<Item> lids) {
        ArrayList<Item> ordered = new ArrayList<Item>();
    
        addCupsAndMatchingLids(cups, lids, ordered);
        addAnyRemainingLids(lids, ordered);
        
        this.stack = ordered;
        recalculatedStackPositions(this.stack);
        this.isOk = true;
    
        if (towerGUI.isVisible()) {
            towerGUI.refresh();
        }
    }
    /**
     * Auxiliary rebuildTower method to reconstruction cups those have own lids
     * @param cups The list of the cups (ordered)
     * @param lids The list of the lids (ordered)
     * @param ordered The parent list where we will organize cup and lid lists.
     */
    private void addCupsAndMatchingLids(ArrayList<Item> cups,ArrayList<Item> lids, ArrayList<Item> ordered){
        int[] metrics;
        for (Item cup : cups) {
            metrics = topAndBaseItemPosition(cup.getId(), cup.getHeight(), ordered);
            if (metrics[0] <= maxHeight) { 
                
                ordered.add(cup);
                recalculatedStackPositions(ordered);
    
                Item matchingLid = findLidInList(lids, cup.getId());
                if (matchingLid != null) {
                    metrics = topAndBaseItemPosition(matchingLid.getId(), matchingLid.getHeight(), ordered);
                    
                    if (metrics[0] <= maxHeight) {
                        ordered.add(matchingLid);
                        recalculatedStackPositions(ordered);
                        lids.remove(matchingLid); // Remove so it's not added again later
                    }
                }
            }
        }
    }
    /**
     * Auxiliary rebuildTower method to reconstruction for lids that don't have cups
     * @param lids The list of the lids (ordered)
     * @param ordered The parent list where we will organize cup and lid lists.
     */
    private void addAnyRemainingLids(ArrayList<Item> lids, ArrayList<Item> ordered){
        int[] metrics;
        for (Item lid : lids) {
            metrics = topAndBaseItemPosition(lid.getId(), lid.getHeight(), ordered);
            if (metrics[0] <= maxHeight) {
                ordered.add(lid);
                recalculatedStackPositions(ordered);
            }
        }
    }

    /**
     * Sort a list of Stackstack by id in descending order (largest first).
     * Uses selection sort for simplicity.
     * @param list the list to sort in place
     */
    private void sortByIdDescending(ArrayList<Item> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getId() < list.get(j).getId()) {
                    Item temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        }
    }

    /**
     * Sort a list of Stackstack by id in ascending order (smallest first).
     * Uses selection sort for simplicity.
     * @param list the list to sort in place
     */
    private void sortByIdAscending(ArrayList<Item> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getId() > list.get(j).getId()) {
                    Item temp = list.get(i);
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
    
    /**
     * This method works to currentHeight update and recalculate stack positions to have
     * a correct Y position about each item.
     * 
     * Its functionality is based in choose what is the biggest top height in some item,
     * and this answer corresponds to the currentHeight.
     * 
     * @param items An any ArrayList to recalculated Stack Positions
     */
    private void recalculatedStackPositions(ArrayList<Item> pile){
        int maxTopFound = 0;  // The top (the data sought)
        ArrayList<Item> stackTemp = new ArrayList<Item>(pile);
        pile.clear();   // Because we need to recalculated positions
        
        for (Item item : stackTemp){
            
            int[] itemPos = topAndBaseItemPosition(item.getId(), item.getHeight(), pile);
            
            item.setBasePosition(itemPos[1]);
            pile.add(item);
            
            maxTopFound = max(itemPos[0], maxTopFound);
        }
        this.currentHeight = maxTopFound;
    }
    
    /**
     * This method extends prev one to calculate the correct position of the specific item 
     * with new ArrayList and allow us to take decisions, if we can add or not the 
     * item to the stack 
     * 
     * @param id is the id of any item that we want to add
     * @param itemHeight Any item height to see if we can add it or not.
     * @param items the new list to find if is possible add
     * @return int[newItemTopPosition,newItemBasePosition] the new top and new base of the item that we want to add
     */
    private int[] topAndBaseItemPosition(int id, int itemHeight, ArrayList<Item> items){
        int newItemBasePosition; 
        
        if(items.isEmpty()){    //First add
            newItemBasePosition = 0;
        }
        else{
            Item lastItem = items.get(items.size() - 1);
            int lastId = lastItem.getId();
            int lastBasePosition = lastItem.getBasePosition();
            int lastHeight = lastItem.getHeight();
            
            if(id < lastId){
                newItemBasePosition = lastBasePosition + Item.thick;  // Only add the thick of the cup (general metric)
            }
            else{
                newItemBasePosition = lastBasePosition + lastHeight;         
            }
        }
        
        int newItemTopPosition = newItemBasePosition + itemHeight;
            
        return new int[]{newItemTopPosition, newItemBasePosition};
    }
}
