package tower;

import java.awt.Color;

/**
 * A crazy lid that places itself at the base of its companion cup
 * instead of on top. When pushed, it inserts just below its companion
 * cup in the tower structure.
 *
 * @author Acero - Quiceno
 * @version 2.0
 */
public class CrazyLid extends Lid {

    /**
     * Creates a new CrazyLid with the given properties.
     *
     * @param id    the unique identifier (should match its cup's id)
     * @param color the color of the lid
     */
    public CrazyLid(int id, Color color) {
        super(id, color);
        this.name = "crazyLid";
    }

    /**
     * Inserts this lid at the base of its companion cup in the tower,
     * rather than on top.
     *
     * @param tower the tower to push into
     * @throws StackingException if the companion cup is not found or insertion fails
     */
    @Override
    public void executePushBehavior(Tower tower) throws StackingException {
        if (tower.contains(this)) {
            throw new StackingException(StackingException.DUPLICATE_ITEM);
        }

        Cup companionCup = findCompanionCup(tower);
        if (companionCup == null) {
            throw new StackingException(StackingException.COMPANION_NOT_PRESENT);
        }

        Item cupContainer = companionCup.getContainer();
        int cupPositionInContainer;
        if (cupContainer == null) {
            cupPositionInContainer = tower.getRoots().indexOf(companionCup);
        } else {
            cupPositionInContainer = cupContainer.getContent().indexOf(companionCup);
        }

        if (cupPositionInContainer == -1) {
            throw new StackingException(StackingException.CUP_NOT_FOUND);
        }

        int futureHeight = tower.height() + this.getHeight();

        if (futureHeight > tower.getMaxHeight()) {
            throw new StackingException(StackingException.TOWER_FULL);
        }

        if (cupContainer == null) {
            tower.getRoots().add(cupPositionInContainer, this);
        } else {
            cupContainer.getContent().add(cupPositionInContainer, this);
        }

        this.setContainer(cupContainer);
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
