package tower;

import java.awt.Color;
import java.util.ArrayList;

/**
 * A specialized cup that displaces smaller items upward upon insertion
 * and anchors itself at the bottom of the tower if it reaches the base.
 * Once anchored, it cannot be removed.
 *
 * @author Acero - Quiceno
 * @version 2.0
 */
public class HierarchicalCup extends Cup {

    private boolean isAnchored = false;

    /**
     * Creates a new HierarchicalCup with the given properties.
     *
     * @param id    the unique identifier of the cup (positive integer)
     * @param color the color of the cup
     */
    public HierarchicalCup(int id, Color color) {
        super(id, color);
        this.name = "hierarchicalCup";
    }

    /** Returns true if this cup is anchored at the base of the tower. */
    public boolean isAnchored() {
        return isAnchored;
    }

    /**
     * Displaces upward any item whose size is strictly smaller than this cup,
     * then inserts at the resulting position. If this cup ends up at the
     * bottom of the tower, it becomes anchored and cannot be removed.
     *
     * @param tower the tower to push into
     * @throws StackingException if the cup cannot be inserted
     */
    @Override
    public void executePushBehavior(Tower tower) throws StackingException {
        if (tower.contains(this)) {
            throw new StackingException(StackingException.DUPLICATE_ITEM);
        }

        ArrayList<Item> roots = tower.getRoots();
        ArrayList<Item> displaced = new ArrayList<>();
        ArrayList<Item> remaining = new ArrayList<>();

        for (Item root : roots) {
            if (root.getDiameter() < this.getDiameter()) {
                displaced.add(root);
            } else {
                remaining.add(root);
            }
        }

        roots.clear();
        roots.addAll(remaining);

        super.executePushBehavior(tower);

        for (Item item : displaced) {
            item.setContainer(null);
            tower.addRoot(item);
        }

        if (tower.getRoots().indexOf(this) == 0) {
            this.isAnchored = true;
        }
    }

    /**
     * Determines if this cup is locked (anchored cups are locked).
     *
     * @return true if anchored or otherwise locked
     */
    @Override
    public boolean isLocked() {
        return isAnchored || super.isLocked();
    }
}
