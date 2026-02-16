/**
 * Represents a cup that is covered by its matching lid.
 * When a cup and its lid (same id) are adjacent in the tower
 * (lid directly on top of the cup), they form a CupMixLid unit
 * and conceptually move together.
 * 
 * <p>The combined height is the cup's height plus 1 cm (the lid).</p>
 * 
 * @author Tomás
 * @version 1.0
 */
public class CupMixLid {
    private Cup cup;
    private Lid lid;

    /**
     * Create a CupMixLid combining a cup and its matching lid.
     * @param cup the cup (must not be null)
     * @param lid the lid (must not be null, must have the same id as the cup)
     */
    public CupMixLid(Cup cup, Lid lid) {
        this.cup = cup;
        this.lid = lid;
    }

    /**
     * Get the cup component of this unit.
     * @return the Cup object
     */
    public Cup getCup() {
        return cup;
    }

    /**
     * Get the lid component of this unit.
     * @return the Lid object
     */
    public Lid getLid() {
        return lid;
    }

    /**
     * Get the combined height of the cup and lid.
     * Equals the cup's height plus 1 cm for the lid.
     * @return total height in cm
     */
    public int getHeight() {
        return cup.getHeight() + 1;
    }

    /**
     * Get the id shared by both the cup and lid.
     * @return the common id
     */
    public int getId() {
        return cup.getId();
    }
}
