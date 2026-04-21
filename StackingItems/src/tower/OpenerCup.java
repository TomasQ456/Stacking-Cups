package tower;

import java.awt.Color;
import java.util.ArrayList;

/**
 * A specialized cup that removes all lids blocking its path when pushed
 * into the tower. After clearing blocking lids, it inserts normally.
 *
 * @author Acero - Quiceno
 * @version 2.0
 */
public class OpenerCup extends Cup {

    /**
     * Creates a new OpenerCup with the given properties.
     *
     * @param id    the unique identifier of the cup (positive integer)
     * @param color the color of the cup
     */
    public OpenerCup(int id, Color color) {
        super(id, color);
        this.name = "openerCup";
    }

    /**
     * Removes all lids that block this cup's path from top to base,
     * then inserts normally via the superclass behavior.
     *
     * @param tower the tower to push into
     * @throws StackingException if the cup cannot be inserted after clearing lids
     */
    @Override
    public void executePushBehavior(Tower tower) throws StackingException {
        if (tower.contains(this)) {
            throw new StackingException(StackingException.DUPLICATE_ITEM);
        }

        ArrayList<Item> lidsToRemove = new ArrayList<>();
        for (Item root : tower.getRoots()) {
            collectBlockingLids(root, lidsToRemove);
        }

        for (Item lid : lidsToRemove) {
            tower.removeItemDirectly(lid);
        }

        super.executePushBehavior(tower);
    }

    /**
     * Recursively collects all unattached lids in the tower subtree.
     *
     * @param item        the current item to inspect
     * @param lidsToRemove accumulator for lids to remove
     */
    private void collectBlockingLids(Item item, ArrayList<Item> lidsToRemove) {
        if (item.isLid() && !item.isAttached()) {
            lidsToRemove.add(item);
        }

        for (Item child : item.getContent()) {
            collectBlockingLids(child, lidsToRemove);
        }
    }
}
