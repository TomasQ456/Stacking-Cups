package tower;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;
import shapes.*;

/**
 * Represents a lid for a cup in the tower.
 * Each lid has a unique id (matching its corresponding cup), a logical width,
 * and a color. Lids are always 1 cm tall.
 *
 * @author Acero - Quiceno
 * @version 6.0
 */
public class Lid extends Item {

    public static final int LID_HEIGHT = 1;
    private Cup owner;
    
    private Rectangle body = new Rectangle();
    // -----------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------
    
    /**
     * Creates a new Lid with the given properties.
     *
     * @param id    the unique identifier (should match its cup's id)
     * @param color the color of the lid
     */
    public Lid(int id, Color color) {
        super(id, color);
        this.height = LID_HEIGHT;
        this.diameter = id;
        this.name = "normalLid";
        this.owner = null;
    }

    // -----------------------------------------------
    //  OWNER AND AUXILIARY
    // -----------------------------------------------

    /** * Returns true if this lid is currently covering a cup. 
     */
    public boolean hasOwner() { return (owner != null); }

    /**
     * Sets the owner (cup) of this lid.
     *
     * @param item the cup that owns this lid
     */
    public void setOwner(Item item) {
        this.owner = (Cup) item;
    }

    /** * Returns the cup that owns this lid, or null. 
     */
    public Cup getOwner() {
        return this.owner;
    }

    // -----------------------------------------------
    // @OVERRIDES AND IMPLEMENTATIONS
    // -----------------------------------------------

    /**
     * Standard insertion behavior for a normal lid.
     *
     * @param tower the tower to push into
     * @throws StackingException if the lid cannot be inserted
     */
    @Override
    public void executePushBehavior(Tower tower) throws StackingException {
        Item lastR = tower.lastRoot();
        int futureHeight = 0;
        int currentH = tower.height();
        boolean newItemHost;

        if (tower.contains(this)) {
            throw new StackingException(StackingException.DUPLICATE_ITEM);
        }

        if (lastR == null) {
            if (this.getHeight() > tower.getMaxHeight()) {
                throw new StackingException(StackingException.TOWER_FULL);
            }
            newItemHost = true;
        } else {
            newItemHost = lastR.canHost(this);
            if (newItemHost) {
                futureHeight = (lastR.calculatePotencialHeight(this) - lastR.getEffectiveHeight()) + currentH;
            } else {
                futureHeight = currentH + this.getHeight();
            }
            if (futureHeight > tower.getMaxHeight()) {
                throw new StackingException(StackingException.TOWER_FULL);
            }
        }

        if (tower.getRoots().isEmpty() || !newItemHost) {
            this.setContainer(null);
            tower.addRoot(this);
        } else {
            lastR.insert(this);
        }
    }

    @Override
    public ArrayList<Item> getFlattened() {
        ArrayList<Item> self = new ArrayList<>();
        self.add(this);
        return self;
    }

    @Override
    public boolean isLid() { return true; }

    /**
     * A lid cannot host any items.
     *
     * @param newItem the item to test
     * @return always false
     */
    public boolean canHost(Item newItem) {
        return false;
    }

    /**
     * Calculates the potential height if a new item were stacked on this lid.
     *
     * @param newItem the hypothetical new item
     * @return the combined height
     */
    public int calculatePotencialHeight(Item newItem) {
        return this.height + newItem.getHeight();
    }

    @Override
    public List<Integer> collectLidedIds() { return new ArrayList<>(); }

    @Override
    public boolean canBeCoupledWith(Item other) {
        if (!other.isCup()) return false;
        if (this.hasOwner()) return false;
        if (other.getID() != this.getID()) return false;

        Cup cup = (Cup) other;
        return cup.canBeLidded();
    }

    @Override
    public boolean coupleWith(Item other) {
        if (!canBeCoupledWith(other)) return false;

        Cup cup = (Cup) other;
        cup.setLid(this);
        return true;
    }

    @Override
    public Item findCouplingPartner(ArrayList<Item> candidates) {
        for (Item candidate : candidates) {
            if (candidate.isCup() && candidate.getID() == this.getID()) {
                Cup cup = (Cup) candidate;
                if (!cup.isLidded() && cup.canBeLidded()) {
                    return candidate;
                }
            }
        }
        return null;
    }

    @Override
    public int getEffectiveHeight() {
        return this.height;
    }

    @Override
    public boolean isAttached() {
        return hasOwner();
    }

    @Override
    public boolean canBeCovered() {
        return false;
    }

    @Override
    public boolean canSwapWith(Item other) {
        if (other == null || this.equals(other)) return false;

        if (this.hasOwner()) return false;

        if (other.isLid()) {
            Lid otherLid = (Lid) other;
            if (otherLid.hasOwner()) return false;
        } else if (other.isCup()) {
            Cup otherCup = (Cup) other;
            if (otherCup.isLidded()) return false;
        } else {
            return false;
        }

        return true;
    }

    @Override
    public void draw(int x, int baseY) {
        // CORRECCIÓN MATEMÁTICA: El ancho debe coincidir con el 2n - 1 de la taza
        int pixelWidth = (2 * this.diameter - 1) * SCALE;
        int pixelHeight = this.height * SCALE;
        body.changeSize(pixelHeight, pixelWidth);
        body.setPosition(x - (pixelWidth / 2), baseY - pixelHeight);
        body.changeColor(color);
        body.makeVisible();
    }

    @Override
    public void erase() {
        if (body != null) {
            body.makeInvisible();
        }
    }
    
    /**
     * Legacy Override: Abstract method
     */
    @Override
    public int draw(int x, int y, int scale, int towerWidth) {
        return 0; 
    }

    @Override
    public String[][] stack() { return new String[][] { this.tarjet() }; }
}