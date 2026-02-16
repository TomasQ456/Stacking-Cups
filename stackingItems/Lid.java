/**
 * Represents a lid for a cup in the tower.
 * Each lid has a unique id (matching its corresponding cup), a logical width,
 * and a color (which should match its cup's color).
 * Lids are always 1 cm tall.
 * 
 * <p>The width is a logical value used for relative sizing. The actual
 * pixel dimensions are computed by the GUI layer (LidGUI) using a
 * scale factor.</p>
 * 
 * @author Tomás
 * @version 1.0
 */
public class Lid {
    private int id;
    private String color;
    private int width;

    /**
     * Create a new Lid with the given properties.
     * @param id the unique identifier (should match its cup's id)
     * @param width the logical width of the lid
     * @param color the color of the lid (should match its cup's color)
     */
    public Lid(int id, int width, String color) {
        this.id = id;
        this.width = width;
        this.color = color;
    }

    /**
     * Get the unique identifier of this lid.
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the logical width of this lid.
     * Used for visual proportional sizing and width validation.
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the color of this lid.
     * @return the color string (e.g. "red", "blue")
     */
    public String getColor() {
        return color;
    }
}
