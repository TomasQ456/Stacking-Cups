package tower;

import java.util.Random;
import static java.lang.Math.*;
import java.util.Iterator;
import java.util.HashSet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.function.Predicate;

import shapes.*;

/**
 * Main controller class for the stacking tower simulator.
 * Manages a tower where cups and lids can be stacked vertically.
 * Inspired by the ICPC 2025 Problem J "Stacking Cups".
 *
 * @author Acero - Quiceno
 * @version 6.0
 */
public class Tower {

    private final int width;
    private final int maxHeight;

    public ArrayList<Item> roots;

    private boolean ok;
    private boolean visible;

    private final HashMap<Integer, Color> assignedColors;
    private static final Random rand = new Random();
    private int colorIndex;

    // -----------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------

    /**
     * Creates a new empty tower with the specified dimensions.
     *
     * @param width     width of the tower in cm
     * @param maxHeight maximum height of the tower in cm
     */
    public Tower(int width, int maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;
        this.roots = new ArrayList<>();
        this.assignedColors = new HashMap<>();
        this.ok = true;
        this.visible = false;
        this.colorIndex = 0;
        
        TowerCanvas.getInstance().bindTower(this);
    }

    /**
     * Creates a new tower pre-filled with n cups stacked from largest to smallest.
     *
     * @param n number of cups to create
     */
    public Tower(int n) {
        this.width = (2 * n) - 1;
        this.maxHeight = (int) pow(n, 2) + n;
        this.roots = new ArrayList<>();
        this.assignedColors = new HashMap<>();
        this.ok = true;
        this.visible = false;
        this.colorIndex = 0;

        TowerCanvas.getInstance().bindTower(this);
        
        for (int i = n; i >= 1; i--) {
            pushCup(i);
        }
        repaint();
    }

    // -----------------------------------------------------------------------
    // GENERIC MANAGEMENT
    // -----------------------------------------------------------------------

    /**
     * Checks if the given item already exists in the tower.
     *
     * @param item the item to check
     * @return true if the item is present
     */
    public boolean contains(Item item) {
        for (Item root : roots) {
            if (root.containsItem(item)) return true;
        }
        return false;
    }

    /**
     * Returns the last root item, or null if the tower is empty.
     *
     * @return the top-most root item
     */
    public Item lastRoot() { return roots.isEmpty() ? null : roots.get(roots.size() - 1); }

    /**
     * Returns the current stacked height of all elements in the tower.
     *
     * @return stacked height >= 0
     */
    public int height() {
        return roots.stream().mapToInt(r -> r.getEffectiveHeight()).sum();
    }

    /**
     * Pushes an item into the tower by delegating to its executePushBehavior.
     * Catches StackingException internally and updates the ok flag.
     *
     * @param newItem the item to push
     * @return true if successful
     */
    private boolean push(Item newItem) {
        try {
            newItem.executePushBehavior(this);
            fullReconcile();
            repaint();
            return true;
        } catch (StackingException e) {
            showErrorIfVisible(e.getMessage());
            return false;
        }
    }

    /** 
     * Returns the list of root items in the tower. 
     */
    public ArrayList<Item> getRoots() { return roots; }

    /**
     * Adds an item as a root element of the tower.
     *
     * @param item the item to add
     */
    public void addRoot(Item item) { roots.add(item); }

    /**
     * Directly removes an item from the tower structure without cascade logic.
     * Used internally by OpenerCup to remove blocking lids.
     *
     * @param target the item to remove
     */
    public void removeItemDirectly(Item target) {
        for (int i = 0; i < roots.size(); i++) {
            Item root = roots.get(i);
            if (root.equals(target)) {
                roots.remove(i);
                return;
            }
            try {
                if (root.removeRecursive(target)) return;
            } catch (StackingException e) {
                // Continue searching
            }
        }
        repaint();
    }

    /**
     * Removes the first item matching the condition from the tower.
     *
     * @param condition predicate to match items
     */
    private void pop(Predicate<Item> condition) {
        this.ok = false;

        Item firstTarget = findFirstOccurrence(roots, condition);

        if (firstTarget != null) {
            this.ok = remove(firstTarget);
        } else {
            showErrorIfVisible("Not Found");
        }
    }

    /**
     * Searches recursively for the first item matching the condition.
     *
     * @param items     the list to search
     * @param condition the predicate to match
     * @return the first matching item, or null
     */
    private Item findFirstOccurrence(ArrayList<Item> items, Predicate<Item> condition) {
        for (int i = items.size() - 1; i >= 0; i--) {
            Item current = items.get(i);

            Item foundInChildren = findFirstOccurrence(current.getContent(), condition);
            if (foundInChildren != null) {
                return foundInChildren;
            }

            if (current.isCup()) {
                Cup cup = (Cup) current;
                if (cup.isLidded() && condition.test(cup.getLid())) {
                    return cup.getLid();
                }
            }

            if (condition.test(current)) {
                return current;
            }
        }
        return null;
    }

    /**
     * Removes the target item from the tower.
     *
     * @param target the item to remove
     * @return true if removal was successful
     */
    private boolean remove(Item target) {
        if (target.isLid() && target.isAttached()) {
            showErrorIfVisible("Lid is attached, remove its cup");
            return false;
        }
        this.ok = false;

        for (int i = 0; i < roots.size(); i++) {
            Item root = roots.get(i);

            if (root.equals(target)) {
                if (root.shouldCascadeOnDelete()) {
                    roots.remove(i);
                } else {
                    ArrayList<Item> orphans = root.getContent();
                    roots.remove(i);

                    for (Item orphan : orphans) {
                        orphan.setContainer(null);
                        roots.add(orphan);
                    }
                }
                fullReconcile();
                repaint();
                return true;
            }

            try {
                if (root.removeRecursive(target)) {
                    fullReconcile();
                    repaint();
                    return true;
                }
            } catch (StackingException e) {
                showErrorIfVisible(e.getMessage());
                return false;
            }
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // CUP MANAGEMENT
    // -----------------------------------------------------------------------

    /**
     * Pushes a specialized cup of the given type into the tower.
     * No subtype-specific insertion logic — delegates entirely to the item.
     *
     * @param type the cup type string (e.g. "opener", "hierarchical")
     * @param i    the id of the cup to create
     */
    public void pushCup(String type, int i) {
        this.ok = false;
        if (i <= 0 || i > Math.ceil(width / 2.0) + 1) {
            showErrorIfVisible(StackingException.INVALID_SIZE);
            return;
        }

        try {
            String className = "tower." + type.substring(0, 1).toUpperCase()
                             + type.substring(1).toLowerCase() + "Cup";
            Class<?> clazz = Class.forName(className);
            java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(int.class, Color.class);
            Cup newCup = (Cup) constructor.newInstance(i, assignedColors.getOrDefault(i, randomColor()));

            this.ok = push((Item) newCup);
            repaint();

        } catch (Exception e) {
            showErrorIfVisible("Cup type not recognized: " + type);
            this.ok = false;
        }
    }

    /**
     * Pushes a normal cup with the given id into the tower.
     * Validates preconditions, then delegates to the Cup's push behavior.
     *
     * @param i the unique id of the cup to create and push
     */
    public void pushCup(int i) {
        if (i <= 0 || i > ceil(width / 2) + 1) {
            ok = false;
            showErrorIfVisible(StackingException.INVALID_SIZE);
            return;
        }

        Cup cup = new Cup(i, resolveColor(i));

        this.ok = push(cup);
        repaint();
    }

    /**
     * Removes the topmost cup from the tower.
     * If the cup is lidded, its lid is also removed.
     */
    public void popCup() {
        pop(Item::isCup);
        repaint();
    }

    /**
     * Removes a cup with the given id from the tower.
     *
     * @param i the id of the cup to remove
     */
    public void removeCup(int i) {
        this.ok = remove(new Cup(i, null));
    }

    // -----------------------------------------------------------------------
    // LID MANAGEMENT
    // -----------------------------------------------------------------------

    /**
     * Pushes a specialized lid of the given type into the tower.
     * No subtype-specific insertion logic — delegates entirely to the item.
     *
     * @param type the lid type string (e.g. "fearful", "crazy")
     * @param i    the id of the lid to create
     */
    public void pushLid(String type, int i) {
        this.ok = false;
        if (i <= 0 || i > Math.ceil(width / 2.0) + 1) {
            showErrorIfVisible(StackingException.INVALID_SIZE);
            return;
        }

        try {
            String className = "tower." + type.substring(0, 1).toUpperCase()
                             + type.substring(1).toLowerCase() + "Lid";
            Class<?> clazz = Class.forName(className);
            java.lang.reflect.Constructor<?> constructor = clazz.getConstructor(int.class, Color.class);
            Lid newLid = (Lid) constructor.newInstance(i, assignedColors.getOrDefault(i, randomColor()));

            this.ok = push((Item) newLid);
            repaint();

        } catch (Exception e) {
            showErrorIfVisible("Lid type not recognized: " + type);
            this.ok = false;
        }
    }

    /**
     * Pushes a normal lid with the given id into the tower.
     *
     * @param i the unique id of the lid to create and push
     */
    public void pushLid(int i) {
        if (i <= 0 || i > ceil(width / 2) + 1) {
            ok = false;
            showErrorIfVisible(StackingException.INVALID_SIZE);
            return;
        }

        Lid lid = new Lid(i, resolveColor(i));

        this.ok = push(lid);
        repaint();
    }

    /**
     * Removes the topmost lid from the tower.
     */
    public void popLid() {
        pop(Item::isLid);
        repaint();
    }

    /**
     * Removes a lid with the given id from the tower.
     *
     * @param i the id of the lid to remove
     */
    public void removeLid(int i) {
        this.ok = remove(new Lid(i, null));
    }

    // -----------------------------------------------------------------------
    // INTERISTING SHORTCUTS
    // -----------------------------------------------------------------------

    /**
     * Swaps two items in the tower identified by their description arrays.
     * Each description is [type, id].
     *
     * @param o1 description of the first item
     * @param o2 description of the second item
     */
    public void swap(String[] o1, String[] o2) {
        this.ok = false;
        Item a = findItemByDescription(o1);
        Item b = findItemByDescription(o2);

        if (a != null && a.isLid() && a.isAttached()) {
            a = ((Lid) a).getOwner();
        }
        if (b != null && b.isLid() && b.isAttached()) {
            b = ((Lid) b).getOwner();
        }
        if (a == null || b == null || a.equals(b)) {
            showErrorIfVisible(StackingException.INVALID_SWAP);
            return;
        }
        if (a.isLocked() || b.isLocked()) {
            showErrorIfVisible("This Item is blocked and can not be interchange");
            return;
        }
        ArrayList<Item> sequence = new ArrayList<>();
        for (Item root : roots) {
            sequence.addAll(root.getFlattened());
        }
        int indexA = sequence.indexOf(a);
        int indexB = sequence.indexOf(b);
        if (indexA == -1 || indexB == -1) {
            rebuildFromSequence(sequence); 
            showErrorIfVisible(StackingException.INVALID_SWAP);
            return;
        }
        ArrayList<Item> backupSequence = new ArrayList<>(sequence);
        sequence.set(indexA, b);
        sequence.set(indexB, a);
        boolean success = rebuildFromSequence(sequence);
        if (success && this.height() > this.maxHeight) {
            showErrorIfVisible("This Item fsdfsdfsdnge");
            showErrorIfVisible(StackingException.TOWER_FULL);
            success = false;
        }
        if (!success) {
            rebuildFromSequence(backupSequence);
            this.ok = false;
        } else {
            this.ok = true;
        }

        fullReconcile();
        repaint();
    }

    /**
    * Rebuilds the tower sequentially from a flat list of items.
    * Use the executable `PushBehavior` to respect DOPO business rules.
    * @param sequence The ordered sequence of elements to insert.
    * @return true if all items were inserted without throwing a `StackingException`.
     */
    private boolean rebuildFromSequence(ArrayList<Item> sequence) {
        this.roots.clear(); 
        
        for (Item item : sequence) {
            item.setContainer(null);
            if (item.isCup() && !((Cup) item).isLidded()) {
                item.getContent().clear();
            }
        }

        boolean allOk = true;
        for (Item item : sequence) {
            try {
                item.executePushBehavior(this);
            } catch (StackingException e) {
                allOk = false;
                break; 
            }
        }
        return allOk;
    }

    /**
     * Finds an item by its type and id description.
     *
     * @param description array with [type, id]
     * @return the matching Item, or null
     */
    private Item findItemByDescription(String[] description) {
        if (description == null || description.length < 2) return null;

        String type = description[0].toLowerCase();
        int id;

        try {
            id = Integer.parseInt(description[1]);
        } catch (NumberFormatException e) {
            return null;
        }

        return findItemRecursive(roots, type, id);
    }

    /**
     * Recursively searches for an item by type and id.
     *
     * @param items the list to search
     * @param type  the type substring to match
     * @param id    the id to match
     * @return the matching Item, or null
     */
    private Item findItemRecursive(ArrayList<Item> items, String type, int id) {
        for (Item item : items) {
            if (item.getName().toLowerCase().contains(type.toLowerCase()) && item.getID() == id) {
                return item;
            }

            if (item.isCup()) {
                Cup cup = (Cup) item;
                Item found = findItemRecursive(cup.getContent(), type, id);
                if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * Covers all cups that have a matching free lid in the tower.
     * This follows the rule that items inside a lidded cup are locked and cannot be covered. 
     */
    public void cover() {
        this.ok = false;
        List<Item> allItems = getAllItems();
        
        List<Cup> availableCups = new ArrayList<>();
        List<Lid> availableLids = new ArrayList<>();

        for (Item item : allItems) {
            if (!item.isLocked()) {
                if (item.isCup()) {
                    Cup cup = (Cup) item;
                    if (!cup.isLidded()) availableCups.add(cup); 
                } else if (item.isLid()) {
                    Lid lid = (Lid) item;
                    if (!lid.hasOwner()) availableLids.add(lid);
                }
            }
        }

        boolean changes = false;
        
        for (Cup cup : availableCups) {
            if (cup.isLocked()) continue; 

            for (int i = 0; i < availableLids.size(); i++) {
                Lid lid = availableLids.get(i);
                if (cup.getID() == lid.getID()) {
                    if (this.height() + Lid.LID_HEIGHT <= this.maxHeight) {
                        removeItemDirectly(lid); 
                        
                        cup.coupleWith(lid); 
                        
                        availableLids.remove(i);
                        changes = true;
                        break;
                    }
                }
            }
        }

        this.ok = true;
        
        if (changes) {
            fullReconcile(); 
            repaint(); 
        }
    }

    /**
     * Placeholder for finding optimal swaps to reduce tower height.
     *
     * @return array of swap operation descriptions
     */
    public String[][] swapToReduce() {
        return new String[][]{{}};
    }

    // -----------------------------------------------------------------------
    // TOWER REORGANIZATION
    // -----------------------------------------------------------------------

    /**
     * Reorganizes all items in the tower by sorting their IDs.
     *
     * @param ascending true for ascending order, false for descending
     */
    public void organize(boolean ascending) {
        ArrayList<Item> allItems = new ArrayList<>();
        for (Item root : roots) {
            allItems.addAll(root.getFlattened());
        }
        roots.clear();

        ArrayList<Item> linkedItems = linkPairs(allItems);

        linkedItems.sort((a, b) -> {
            int idA = a.getID();
            int idB = b.getID();
            return ascending ? Integer.compare(idA, idB) : Integer.compare(idB, idA);
        });

        for (Item item : linkedItems) {
            this.push(item);
        }
        repaint();
    }

    /**
     * Links pairable items (cups with their matching lids).
     *
     * @param allItems all items to consider
     * @return list with paired items
     */
    private ArrayList<Item> linkPairs(ArrayList<Item> allItems) {
        ArrayList<Item> candidates = new ArrayList<>(allItems);
        ArrayList<Item> result = new ArrayList<>();

        while (!candidates.isEmpty()) {
            Item current = candidates.remove(0);
            boolean coupled = false;

            for (int i = 0; i < candidates.size(); i++) {
                Item potentialPartner = candidates.get(i);

                if (current.canBeCoupledWith(potentialPartner)) {
                    current.coupleWith(potentialPartner);
                    candidates.remove(i);
                    result.add(current);
                    coupled = true;
                    break;
                } else if (potentialPartner.canBeCoupledWith(current)) {
                    potentialPartner.coupleWith(current);
                    candidates.remove(i);
                    result.add(potentialPartner);
                    coupled = true;
                    break;
                }
            }

            if (!coupled) {
                result.add(current);
            }
        }
        return result;
    }

    /**
     * Sorts all elements from largest to smallest (descending ID order).
     */
    public void orderTower() {
        this.organize(false);
    }

    /**
     * Sorts all elements from smallest to largest (ascending ID order).
     */
    public void reverseTower() {
        this.organize(true);
    }

    // -----------------------------------------------------------------------
    // INFORMATION QUERIES
    // -----------------------------------------------------------------------

    /**
     * Returns the ids of cups that are currently covered by their lids,
     * sorted in ascending order.
     *
     * @return sorted array of lidded cup ids
     */
    public int[] lidedCups() {
        List<Integer> allLidedIds = new ArrayList<>();
        for (Item root : roots) {
            allLidedIds.addAll(root.collectLidedIds());
        }
        return allLidedIds.stream().distinct().mapToInt(Integer::intValue).sorted().toArray();
    }

    /**
     * Returns descriptions of all stacked items from base to top.
     * Each entry is [typeName, id].
     *
     * @return 2D array of item descriptions
     */
    public String[][] stackingItems() {
        return roots.stream().flatMap(root -> Arrays.stream(root.stack())).toArray(String[][]::new);
    }

    // -----------------------------------------------------------------------
    // VISIBILITY
    // -----------------------------------------------------------------------

    /**
     * Makes the tower's graphical representation visible.
     */
    public void makeVisible() {
        if (!TowerCanvas.getInstance().show()) {
            ok = false;
            return;
        }
        visible = true;
        ok = true;
    }

    /**
     * Makes the tower's graphical representation invisible.
     */
    public void makeInvisible() {
        TowerCanvas.getInstance().hide();
        visible = false;
        ok = true;
    }

    // -----------------------------------------------------------------------
    // LIFECYCLE
    // -----------------------------------------------------------------------

    /**
     * Reconciles all root items, pairing matching cups and lids.
     */
    private void fullReconcile() {
        for (Item root : roots) {
            root.reconcileContent();
        }

        for (int i = 0; i < roots.size() - 1; i++) {
            Item lower = roots.get(i);
            Item upper = roots.get(i + 1);
            
            if (lower.isCup() && upper.isLid() && lower.getID() == upper.getID()) {
                Cup cup = (Cup) lower;
                if (!cup.isLidded() && cup.canBeLidded()) {
                    cup.setLid(upper);       
                    roots.remove(i + 1);     
                    i--;                     
                }
            }
        }
    }

    /**
     * Exits the simulator, clearing all state.
     */
    public void exit() {
        roots.clear();
        visible = false;
        ok = true;
        TowerCanvas.getInstance().dispose();
    }

    // -----------------------------------------------------------------------
    // STATUS
    // -----------------------------------------------------------------------

    /**
     * Checks if the last operation was successful.
     *
     * @return true if the last operation succeeded
     */
    public boolean ok() {
        return ok;
    }

    // -----------------------------------------------------------------------
    // PRIVATE: UTILITY HELPERS
    // -----------------------------------------------------------------------

    /**
     * Resolves the color for a cup/lid id, assigning a new random color
     * if the id hasn't been seen before.
     *
     * @param i the cup/lid id
     * @return the assigned color
     */
    private Color resolveColor(int i) {
        if (!assignedColors.containsKey(i)) {
            Color color = randomColor();
            assignedColors.put(i, color);
            colorIndex++;
        }
        return assignedColors.get(i);
    }

    /**
     * Repaints the visual representation if the tower is visible.
     */
    private void repaint() {
        if (visible) {
            TowerCanvas.getInstance().refresh();
        }
    }

    /**
     * Shows an error message via TowerCanvas if the tower is visible.
     *
     * @param message the error message to display
     */
    private void showErrorIfVisible(String message) {
        TowerCanvas.getInstance().showError(message);
    }

    // -----------------------------------------------------------------------
    // GUI LAYER
    // -----------------------------------------------------------------------

    public void draw(){
        
    }
    
    /** 
     * Returns the tower width in cm.
     */
    public int getWidth() {
        return this.width;
    }

    /** 
     * Returns the tower maximum height in cm. 
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /** 
     * Returns whether the tower is currently visible. 
    */
    boolean isVisible() {
        return visible;
    }

    /**
     * Generates a random color for item assignment.
     *
     * @return a random Color
     */
    private static Color randomColor() {
        int r = 50 + rand.nextInt(206);
        int g = 50 + rand.nextInt(206);
        int b = 50 + rand.nextInt(206);
        return new Color(r, g, b);
    }

    /**
     * Returns a flat list of all items currently in the tower.
     *
     * @return list of all items
     */
    public List<Item> getAllItems() {
        List<Item> totalItems = new ArrayList<>();

        for (Item child : roots) {
            totalItems.addAll(child.getItems());
        }

        return totalItems;
    }
}
