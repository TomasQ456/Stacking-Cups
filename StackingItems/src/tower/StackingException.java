package tower;

/**
 * Represents domain-specific errors that occur during tower simulation.
 */
public class StackingException extends Exception {

    public static final String CUP_NOT_FOUND = "Cup with the given ID was not found in the tower.";
    public static final String LID_NOT_FOUND = "Lid with the given ID was not found in the tower.";
    public static final String TOWER_FULL = "The tower has reached its maximum allowed height.";
    public static final String INVALID_SIZE = "The item size is invalid or incompatible with the tower.";
    public static final String CANNOT_REMOVE_ANCHORED = "This cup is anchored at the base and cannot be removed.";
    public static final String COMPANION_NOT_PRESENT = "The companion cup is not present in the tower.";
    public static final String COVERING_COMPANION = "This lid is currently covering its companion cup and cannot be removed.";
    public static final String INVALID_SWAP = "One or both items for the swap operation are invalid or not found.";
    public static final String DUPLICATE_ITEM = "This item already exists in the tower.";

    /**
     * Creates a new StackingException with the given message.
     *
     * @param message the error message; use a static constant from this class
     */
    public StackingException(String message) {
        super(message);
    }
}
