package tower;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.List;
import shapes.*;

/**
 * Represents a cylindrical cup in the tower.
 * Each cup has a unique id, a logical diameter, and a color.
 * The height is calculated as (2 * id - 1) cm.
 *
 * @author Acero - Quiceno
 * @version 6.0
 */
public class Cup extends Item {

    private static final int THICKNESS = 1;
    protected Lid lid;

    // GUI Components
    private Rectangle leftWall = new Rectangle();
    private Rectangle rightWall = new Rectangle();
    private Rectangle bottom = new Rectangle();

    // -----------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------

    /**
     * Creates a new Cup with the given properties.
     * Height is automatically calculated as (2 * id - 1).
     * Diameter equals the id value.
     *
     * @param id    the unique identifier of the cup (positive integer)
     * @param color the color of the cup
     */
    public Cup(int id, Color color) {
        super(id, color);
        this.height = 2 * id - 1;
        this.diameter = id;
        this.name = "normalCup";
        this.lid = null;
    }

    // -----------------------------------------------
    //  OWNER AND AUXILIARY
    // -----------------------------------------------

    /**
     * Returns true if this cup has a lid attached. 
     */
    public boolean isLidded() { return (lid != null); }

    /**
     * Attaches a lid to this cup.
     *
     * @param item the lid item to attach
     */
    public void setLid(Item item) {
        if (item != null && item.isLid()) {
            this.lid = (Lid) item;
            this.lid.setOwner(this);
            this.lid.setContainer(this);
        }
    }

    /**
     * Determines if this cup can physically accept a lid.
     *
     * @return true if the cup can be lidded
     */
    public boolean canBeLidded() {
        if (content.isEmpty()) return true;
        return this.getEffectiveHeight() <= this.height;
    }

    /**
     * Returns the last item in this cup's content.
     *
     * @return the last content item, or null if empty
     */
    private Item lastContent() {
        if (content.isEmpty()) { return null; }
        return content.get(content.size() - 1);
    }

    /** Returns the lid attached to this cup, or null. */
    public Lid getLid() {
        return this.lid;
    }

    // -----------------------------------------------
    // @OVERRIDES AND IMPLEMENTATIONS
    // -----------------------------------------------

    /**
     * Standard insertion behavior for a normal cup.
     * Validates preconditions and inserts into the tower.
     *
     * @param tower the tower to push into
     * @throws StackingException if the cup cannot be inserted
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
        ArrayList<Item> all = new ArrayList<>();
        all.add(this);

        if (!this.isLidded()) {
            for (Item child : content) {
                all.addAll(child.getFlattened());
            }
            this.content.clear();
        }

        return all;
    }

    /**
     * Calculates the potential height if a new item were added.
     *
     * @param newItem the hypothetical new item
     * @return the projected height
     */
    public int calculatePotencialHeight(Item newItem) {
        int sumContent = content.stream().mapToInt(r -> r.getEffectiveHeight()).sum();
        int potencialH;
        if (this.id == newItem.getID()) potencialH = this.height + newItem.getHeight();
        if (content.isEmpty()) {
            potencialH = Math.max(this.height, newItem.getHeight() + THICKNESS);
        } else {
            Item lastC = lastContent();
            if (lastC.canHost(newItem)) {
                int lastContentPotencialH = lastC.calculatePotencialHeight(newItem);
                potencialH = sumContent + (lastContentPotencialH - lastC.getEffectiveHeight()) + THICKNESS;
            } else {
                potencialH = sumContent + newItem.getHeight() + THICKNESS;
            }
        }
        return potencialH;
    }

    /**
     * Determines if this cup can host the given item inside it.
     *
     * @param newItem the item to test
     * @return true if hosting is possible
     */
    public boolean canHost(Item newItem) {
        if (isLidded()) return false;
        if (this.diameter < newItem.getDiameter()) return false;

        if (!content.isEmpty()) {
            Item lastBasePlatform = lastContent();
            if (this.height < getEffectiveHeight() && !lastBasePlatform.canHost(newItem))
                return false;
        }

        if (this.id == newItem.getID()) {
            if (newItem.isLid()) return true;
            return false;
        }
        return true;
    }

    @Override
    public int getEffectiveHeight() {
        int totalH = 0;
        if (content.isEmpty()) totalH = this.height;
        else {
            int sumContent = content.stream().mapToInt(Item::getEffectiveHeight).sum();
            totalH = Math.max(this.height, sumContent + THICKNESS);
        }
        if (isLidded()) totalH = this.height + this.lid.getHeight();
        return totalH;
    }

    @Override
    public boolean canBeCoupledWith(Item other) {
        if (!other.isLid()) return false;
        if (this.isLidded()) return false;
        if (other.getID() != this.getID()) return false;

        return this.canBeLidded();
    }

    @Override
    public boolean coupleWith(Item other) {
        if (!canBeCoupledWith(other)) return false;

        Lid lid = (Lid) other;
        this.setLid(lid);
        this.lid.setContainer(this);
        return true;
    }

    @Override
    public Item findCouplingPartner(ArrayList<Item> candidates) {
        for (Item candidate : candidates) {
            if (candidate.isLid() && candidate.getID() == this.getID()) {
                Lid lid = (Lid) candidate;
                if (!lid.hasOwner() && this.canBeLidded()) {
                    return candidate;
                }
            }
        }
        return null;
    }

    @Override
    protected void insert(Item newItem) {
        if (content.isEmpty()) {
            newItem.setContainer(this);
            content.add(newItem);
            this.reconcileContent();
            return;
        }

        Item lastC = lastContent();

        if (lastC.canHost(newItem)) {
            lastC.insert(newItem);
        } else {
            newItem.setContainer(this);
            content.add(newItem);
        }

        this.reconcileContent();
    }

    @Override
    public List<Integer> collectLidedIds() {
        List<Integer> lidedIds = new ArrayList<>();

        if (this.isLidded()) {
            lidedIds.add(this.id);
        }

        for (Item child : content) {
            lidedIds.addAll(child.collectLidedIds());
        }

        return lidedIds;
    }

    @Override
    public boolean shouldCascadeOnDelete() {
        return isLidded();
    }

    /**
     * Recursively removes the target item from this cup's content.
     *
     * @param target the item to remove
     * @return true if removal was successful
     * @throws StackingException if the target cannot be removed
     */
    @Override
    public boolean removeRecursive(Item target) throws StackingException {
        for (int i = 0; i < content.size(); i++) {
            Item child = content.get(i);

            if (child.equals(target)) {
                if (child.isLid() && child.isAttached()) {
                    throw new StackingException("Lid is attached, remove its cup");
                }

                if (child.shouldCascadeOnDelete()) {
                    content.remove(i);
                } else {
                    ArrayList<Item> orphans = new ArrayList<>(child.getContent());
                    content.remove(i);
                    for (Item orphan : orphans) {
                        orphan.setContainer(this);
                        content.add(orphan);
                    }
                }
                this.reconcileContent();
                return true;
            }

            if (child.isCup()) {
                Cup cupChild = (Cup) child;
                if (cupChild.isLidded() && cupChild.getLid().equals(target)) {
                    throw new StackingException("Lid is attached, remove its cup");
                }
            }

            if (child.removeRecursive(target)) {
                this.reconcileContent();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLinkedComponent(Item target) {
        return isLidded() && lid.equals(target);
    }

    /**
     * Reconciles this cup's internal content, pairing matching cups and lids.
     */
    @Override
    public void reconcileContent() {
        if (content.isEmpty()) return;

        for (Item child : content) {
            child.reconcileContent();
        }

        for (int i = 0; i < content.size(); i++) {
            Item child = content.get(i);
            if (child.isLid() && child.getID() == this.id && !this.isLidded()) {
                if (this.canBeLidded()) { 
                    this.setLid(child);
                    content.remove(i);
                    return; 
                }
            }
        }
        for (int i = 0; i < content.size() - 1; i++) {
            Item lower = content.get(i);
            Item upper = content.get(i + 1);

            if (lower.isCup() && upper.isLid() && lower.getID() == upper.getID()) {
                Cup innerCup = (Cup) lower;
                if (!innerCup.isLidded() && innerCup.canBeLidded()) {
                    innerCup.setLid(upper);
                    content.remove(i + 1);
                    i--; 
                }
            }
        }
    }

    @Override
    public boolean isCup() { return true; }

    @Override
    public boolean canBeCovered() {
        return !this.isLidded();
    }

    @Override
    public boolean canSwapWith(Item other) {
        if (other == null || this.equals(other)) return false;
        if (!other.isCup() && !other.isLid()) return false;

        if (this.isLidded()) return false;

        if (other.isLid() && other.isAttached()) return false;

        if (other.isCup()) {
            Cup otherCup = (Cup) other;
            if (otherCup.isLidded()) return false;
        }

        return true;
    }
    
    /**
     * Legacy Override: Abstract method
     */
    @Override
    public int draw(int x, int y, int scale, int towerWidth) {
        return 0; 
    }

    @Override
    public void draw(int x, int baseY) {
        int pixelWidth = (2 * this.diameter - 1) * SCALE;
        int pixelHeight = this.height * SCALE;
        int wallThickness = SCALE;

        int drawX = x - (pixelWidth / 2);
        int drawY = baseY - pixelHeight;

        leftWall.changeSize(pixelHeight, wallThickness);
        leftWall.setPosition(drawX, drawY);
        leftWall.changeColor(color);
        leftWall.makeVisible();

        rightWall.changeSize(pixelHeight, wallThickness);
        rightWall.setPosition(drawX + pixelWidth - wallThickness, drawY);
        rightWall.changeColor(color);
        rightWall.makeVisible();

        bottom.changeSize(wallThickness, pixelWidth);
        bottom.setPosition(drawX, baseY - wallThickness);
        bottom.changeColor(color);
        bottom.makeVisible();

        int currentBaseY = baseY - wallThickness;
        for (Item child : content) {
            child.draw(x, currentBaseY);
            currentBaseY -= child.getEffectiveHeight() * SCALE;
        }

        if (this.lid != null) {
            this.lid.draw(x, drawY);
        }
    }

    @Override
    public void erase() {
        if (leftWall != null) leftWall.makeInvisible();
        if (rightWall != null) rightWall.makeInvisible();
        if (bottom != null) bottom.makeInvisible();
        
        for (Item child : content) {
            child.erase();
        }
        if (isLidded() && lid != null) {
            lid.erase();
        }
    }

    @Override
    public String[][] stack() {
        Stream<String[]> self = Stream.<String[]>of(this.tarjet());
        Stream<String[]> children = content.stream()
                .flatMap(item -> Arrays.stream(item.stack()));
        Stream<String[]> lidStream = (lid != null) ? Arrays.stream(lid.stack()) : Stream.empty();

        return Stream.concat(self, Stream.concat(children, lidStream)).toArray(String[][]::new);
    }
}