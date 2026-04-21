package tower;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all stackable items in the tower.
 * Provides common properties (id, height, diameter, color, name) and defines
 * the contract for push behavior, coupling, hosting, and representation.
 *
 * @author Acero - Quiceno
 * @version 6.0
 */
public abstract class Item {

    protected int id;
    protected int height;
    protected int diameter;
    protected final Color color;
    protected String name;
    protected Item container = null;
    protected ArrayList<Item> content;

    // GUI Scaling constant for pure Swing rendering
    protected static final int SCALE = 15;

    /**
     * Creates a new Item with the given id and color.
     *
     * @param id    the unique identifier of this item
     * @param color the display color of this item
     */
    public Item(int id, Color color) {
        this.id = id;
        this.color = color;
        this.content = new ArrayList<>();
    }

    // -----------------------------------------------
    //  GETTERS
    // -----------------------------------------------

    /** * Returns the unique identifier of this item. 
     */
    public int getID() {
        return this.id;
    }

    /** * Returns the height of this item in cm. 
     */
    public int getHeight() {
        return height;
    }

    /** * Returns the diameter of this item. 
     */
    public int getDiameter() {
        return this.diameter;
    }

    /** * Returns the display color of this item. 
     */
    public Color getColor() {
        return this.color;
    }

    /** * Returns the type name of this item. 
     */
    public String getName() {
        return this.name;
    }

// -----------------------------------------------
    //  GUI CONTRACT (Recursive shapes approach)
    // -----------------------------------------------

    /**
     * Legacy draw method.
     */
    public abstract int draw(int x, int y, int scale, int towerWidth);

    /**
     * Draws the item using its internal Rectangle components at the given coordinates.
     * @param x The horizontal center of the tower.
     * @param baseY The vertical base where the item sits.
     */
    public abstract void draw(int x, int baseY);

    /**
     * Erases all graphical components of this item and its children.
     */
    public abstract void erase();
    

    /** * Returns the container (parent) of this item. 
     */
    public Item getContainer() {
        return this.container;
    }

    /** * Returns the content list (children) of this item. 
     */
    public ArrayList<Item> getContent() {
        return this.content;
    }

    /**
     * Returns the effective height of this item including all nested content.
     * Each subclass must define how its total height is computed.
     *
     * @return the effective height in cm
     */
    public abstract int getEffectiveHeight();

    /**
     * Collects the IDs of all cups that are currently lidded within this item's
     * subtree.
     *
     * @return list of lidded cup IDs
     */
    public abstract List<Integer> collectLidedIds();

    /**
     * Finds a suitable coupling partner for this item in the given candidates.
     *
     * @param candidates list of potential partners
     * @return the partner Item, or null if none found
     */
    public abstract Item findCouplingPartner(ArrayList<Item> candidates);

    /**
     * Returns a flattened list of this item and all its non-lidded content.
     *
     * @return list of items including this one
     */
    public abstract ArrayList<Item> getFlattened();

    // -----------------------------------------------
    // SETTERS
    // -----------------------------------------------

    /**
     * Sets the container (parent) of this item.
     *
     * @param container the new parent item
     */
    public void setContainer(Item container) {
        this.container = container;
    }

    // -----------------------------------------------
    // CALCULOUS
    // -----------------------------------------------

    /**
     * Calculates the potential height of this item if a new item were added.
     *
     * @param newItem the item to hypothetically add
     * @return the projected height after adding newItem
     */
    public abstract int calculatePotencialHeight(Item newItem);

    // -----------------------------------------------
    // RELATIONSHIPS
    // -----------------------------------------------

    /**
     * Determines if this item is physically locked and cannot be moved.
     * An item is locked if it is an attached lid, or if it sits inside a lidded cup.
     *
     * @return true if locked
     */
    public boolean isLocked() {
        if (this.isLid() && this.isAttached()) return true;

        Item parent = this.container;
        while (parent != null) {
            if (parent.isCup() && ((Cup) parent).isLidded()) return true;
            parent = parent.container;
        }
        return false;
    }

    /**
     * Validates if this item can be swapped internally with another item
     * that shares the same container.
     *
     * @param other the item to swap with
     * @return true if swapping is possible
     */
    public boolean canSwapInternally(Item other) { return false; }

    /**
     * Executes an internal swap between two items in this container.
     *
     * @param a first item to swap
     * @param b second item to swap
     * @return true if swap was successful
     */
    public boolean swapInternally(Item a, Item b) { return false; }

    /**
     * Calculates the future height after swapping two items in this container.
     *
     * @param a first item
     * @param b second item
     * @return future effective height after swap
     */
    public int calculateHeightAfterInternalSwap(Item a, Item b) {
        return this.getEffectiveHeight();
    }

    /**
     * Determines if this item can be covered by a lid.
     *
     * @return true if coverable
     */
    public boolean canBeCovered() {
        return false;
    }

    /**
     * Determines if this item can be swapped with another item at root level.
     *
     * @param other the item to check compatibility with
     * @return true if swappable
     */
    public boolean canSwapWith(Item other) {
        return false;
    }

    /**
     * Determines if this item can host (receive) a new item inside it.
     * Implementors must define the hosting rules for their type.
     *
     * @param newItem the item to test for hosting
     * @return true if this item can host newItem
     */
    public abstract boolean canHost(Item newItem);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Item other = (Item) obj;
        return java.util.Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getClass(), id);
    }

    /**
     * Searches if this item or any of its descendants contains the target item.
     *
     * @param target the item to search for
     * @return true if found
     */
    public boolean containsItem(Item target) {
        if (this.equals(target)) return true;

        for (Item child : this.content) {
            if (child.containsItem(target)) return true;
        }

        if (this.isCup()) {
            Cup c = (Cup) this;
            if (c.isLidded() && c.getLid().equals(target)) return true;
        }
        return false;
    }

    /** * Returns true if this item is a lid. 
     */
    public boolean isLid() {
        return false;
    }

    /** * Returns true if this item is a cup. 
     */
    public boolean isCup() {
        return false;
    }

    /**
     * Determines if this item should cascade-delete its content when removed.
     *
     * @return true if cascade deletion is needed
     */
    public boolean shouldCascadeOnDelete() {
        return false;
    }

    /** * Returns true if this item is attached (e.g., a lid to its cup). 
     */
    public boolean isAttached() { return false; }

    /**
     * Determines if the target item is a linked component of this item.
     *
     * @param target the item to check
     * @return true if linked
     */
    public boolean isLinkedComponent(Item target) { return false; }

    /**
     * Determines whether this item can be coupled with the other item.
     *
     * @param other the item to check coupling with
     * @return true if coupling is possible
     */
    public abstract boolean canBeCoupledWith(Item other);

    // -----------------------------------------------
    // OPERATIONS
    // -----------------------------------------------

    /**
     * Executes the specific push behavior for this item into the given tower.
     * Implementors must handle all insertion logic for their type,
     * including any precondition checks and side effects.
     *
     * @param tower the tower this item is being pushed into
     * @throws StackingException when this item cannot be inserted
     */
    public abstract void executePushBehavior(Tower tower) throws StackingException;

    /**
     * Inserts an item into this item's content.
     * By default does nothing; subclasses override as needed.
     *
     * @param item the item to insert
     */
    protected void insert(Item item) { }

    /**
     * Reconciles internal content after modifications.
     * By default does nothing; subclasses override as needed.
     */
    public void reconcileContent() { }

    /**
     * Couples this item with another compatible item.
     *
     * @param other the item to couple with
     * @return true if coupling was successful
     */
    public abstract boolean coupleWith(Item other);

    /**
     * Recursively removes the target item from this item's content.
     *
     * @param target the item to remove
     * @return true if removal was successful
     * @throws StackingException when the target cannot be removed
     */
    public boolean removeRecursive(Item target) throws StackingException { return false; }

    // -----------------------------------------------
    // REPRESENTATION
    // -----------------------------------------------

    /**
     * Returns a 2D string array representation of this item's stack.
     *
     * @return array of [name, id] pairs from base to top
     */
    public abstract String[][] stack();

    /**
     * Returns a label for this individual item as [name, id].
     *
     * @return string array with name and id
     */
    public String[] tarjet() {
        return new String[] {this.name, String.valueOf(this.id)};
    }

    /**
     * Returns a complete list of this item and all nested items.
     *
     * @return list of all items in this subtree
     */
    public List<Item> getItems() {
        List<Item> totalItems = new ArrayList<>();
        totalItems.add(this);

        for (Item child : content) {
            totalItems.addAll(child.getItems());
        }

        if (this.isCup()) {
            Cup self = (Cup) this;
            if (self.isLidded()) {
                totalItems.add(self.getLid());
            }
        }

        return totalItems;
    }
}