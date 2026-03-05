import java.awt.Color;
/**
 * Represents a stackable item in the tower.
 * A StackItem wraps either a Cup or a Lid, providing a uniform interface
 * for the Tower to manage its stack of items. Each item has an id,
 * a height (thickness), and a type.
 * 
 * <p>When wrapping a Cup, the height equals the cup's height (2*id - 1 cm).
 * When wrapping a Lid, the height is always 1 cm.</p>
 * 
 * @author Acero - Quiceno
 * @version 3.0
 */
public class Item {
    private int id;
    private Color color;
    protected String type;
    protected int diameter;
    protected int height;
    private int topPosition;
    private int basePosition;
    public static final int thick = 1;

    /**
     * Create a StackItem (for children)
     * The id and height are derived from the Cup.
     * @param id 
     * @param color for each object (e.g. Color.BLUE, Color.GREEN, Color.PINK, etc)
     */
    public Item(int id, Color color) {
        this.id = id;
        this.color = color;
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
     * Get the type of this item as a lowercase string.
     * @return "cup" if wrapping a Cup, "lid" if wrapping a Lid
     */
    public String getType() {
        return type;
    }
    
    /**
     * @return the color of the item
     */
    public Color getColor(){
        return this.color;
    }
    
    /**
     * @return the height of the item
     */
    public int getHeight(){
        return this.height;
    }
    
    /**
     * This method allow us to sizing with tower's width  
     * but is the same of height
     * @return the diameter of the item
     */
    public int getDiameter(){
        return this.diameter;
    }
    
    /**
     * This method allow us to assing the correct position in the map
     * @param y It is the position when the item starts
     */
    protected void setBasePosition(int y){
        this.basePosition = y;
    }
    
    /**
     * This method gives the basePosition
     * @return int the basePosition of any item
     */
    public int getBasePosition(){
        return this.basePosition;
    }
    
    /**
     * This method gives the topPosition
     * @return int the topPosition of any item
     */
    public int getTopPosition(){
        return (this.basePosition + this.height);
    }
    
}
