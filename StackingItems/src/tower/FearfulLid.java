package tower;

import java.awt.Color;

/**
 * A fearful lid that refuses to enter the tower unless its companion cup
 * is present, and refuses to leave if it is currently covering that cup.
 *
 * @author Acero - Quiceno
 * @version 2.0
 */
public class FearfulLid extends Lid {

    /**
     * Creates a new FearfulLid with the given properties.
     *
     * @param id    the unique identifier (should match its cup's id)
     * @param color the color of the lid
     */
    public FearfulLid(int id, Color color) {
        super(id, color);
        this.name = "FearfulLid";
    }

    /**
     * Inserts this lid only if its companion cup is present in the tower.
     *
     * @param tower the tower to push into
     * @throws StackingException if the companion cup is not present or insertion fails
     */
    @Override
    public void executePushBehavior(Tower tower) throws StackingException {
        Cup companionCup = findCompanionCup(tower);

        if (companionCup == null) {
            throw new StackingException(StackingException.COMPANION_NOT_PRESENT);
        }

        super.executePushBehavior(tower);
    }

    /**
     * Determines if this lid is locked. A fearful lid is additionally locked
     * when it is covering its companion cup.
     *
     * @return true if locked
     */
    @Override
    public boolean isLocked() {
        if (this.hasOwner() && this.getOwner().getID() == this.id) {
            return true;
        }
        return super.isLocked();
    }

    /**
     * Searches the tower for this lid's companion cup.
     *
     * @param tower the tower to search
     * @return the companion Cup, or null if not found
     */
    private Cup findCompanionCup(Tower tower) {
        for (Item root : tower.getRoots()) {
            Cup found = findCupRecursive(root);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Recursively searches for the companion cup in the item subtree.
     *
     * @param item the root of the subtree to search
     * @return the companion Cup, or null if not found
     */
    private Cup findCupRecursive(Item item) {
        if (item.isCup()) {
            Cup cup = (Cup) item;
            if (cup.getID() == this.id) {
                return cup;
            }
        }

        for (Item child : item.getContent()) {
            Cup found = findCupRecursive(child);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
