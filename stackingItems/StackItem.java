/**
 * Represents a stackable item in the tower.
 * A StackItem wraps either a Cup or a Lid, providing a uniform interface
 * for the Tower to manage its stack of items. Each item has an id,
 * a height (thickness), and a type.
 * 
 * <p>When wrapping a Cup, the height equals the cup's height (2*id - 1 cm).
 * When wrapping a Lid, the height is always 1 cm.</p>
 * 
 * @author Tomás
 * @version 1.0
 */
public class StackItem {
    private int id;
    private int thick;
    private Cup cup;
    private Lid lid;

    /**
     * Create a StackItem wrapping a Cup.
     * The id and height are derived from the Cup.
     * @param cup the Cup to wrap (must not be null)
     */
    public StackItem(Cup cup) {
        this.cup = cup;
        this.lid = null;
        this.id = cup.getId();
        this.thick = cup.getHeight();
    }

    /**
     * Create a StackItem wrapping a Lid.
     * The id is derived from the Lid; height is fixed at 1 cm.
     * @param lid the Lid to wrap (must not be null)
     */
    public StackItem(Lid lid) {
        this.lid = lid;
        this.cup = null;
        this.id = lid.getId();
        this.thick = 1;
    }

    /**
     * Get the id of this item.
     * Matches the id of the wrapped Cup or Lid.
     * @return the unique identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Get the height (thickness) of this item in cm.
     * For cups: 2*id - 1. For lids: 1.
     * @return the height in cm
     */
    public int getHeight() {
        return thick;
    }

    /**
     * Get the type of this item as a lowercase string.
     * @return "cup" if wrapping a Cup, "lid" if wrapping a Lid
     */
    public String getType() {
        if (cup != null) {
            return "cup";
        }
        return "lid";
    }

    /**
     * Get the wrapped Cup, if this item wraps a cup.
     * @return the Cup object, or null if this item wraps a Lid
     */
    public Cup getCup() {
        return cup;
    }

    /**
     * Get the wrapped Lid, if this item wraps a lid.
     * @return the Lid object, or null if this item wraps a Cup
     */
    public Lid getLid() {
        return lid;
    }
}
