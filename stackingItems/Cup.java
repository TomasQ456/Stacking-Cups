import java.awt.Color;

/**
 * Represents a cylindrical cup in the tower.
 * In this case, we have been extending the Item class
 * Each cup has a unique id, a logical diameter, and a color.
 * The height is calculated as (2 * id - 1) cm
 * 
 * @author Acero - Quiceno
 * @version 3.0
 */
public class Cup extends Item {
    
    /**
     * Create a new Cup with the given properties.
     * Height is automatically calculated as (2 * id - 1).
     * 
     * Diameter is automatically calculated too (2 * id - 1), the reason of its 
     * inclusion is because allow us to distinguish between height (altought 
     * are the same) and the time to make the GUI allow us to sizing with tower's width
     * 
     * @param id the unique identifier of the cup (positive integer)
     * @param color the color of the cup (e.g. Color.BLACK, Color.BLUE, etc.)
     */
    public Cup(int id, Color color) {
        super(id, color);
        this.diameter = 2 * id - 1;
        this.height = 2 * id - 1;
        this.type = "cup";
    }
}
