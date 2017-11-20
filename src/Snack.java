
/**
 * Snack class to be used in vending machine project. Stores
 * necessary values.
 * 
 * @author Deven R. & Robert Smithers
 * @version 11/17/17
 */
public class Snack
{
    private int inventory;
    private double price;
    private int popularity;
    private double cost;
    private String ID;

    /**
     * Constructor for objects of class Snack
     */
    public Snack(int inventory, double price, double cost, String ID) {
        this.inventory = inventory;
        this.price = price;
        this.cost = cost;
        this.ID = ID;
        popularity = 0;
    }

    /**
     * Allows user to change the inventory. (Mutator Method)
     */
    public void setInventory(int num) {
        inventory = num;
    }
    
    /**
     * Accessor method for inventory.
     */
    public int getInventory() {
        return inventory;
    }
    
    /**
     * Allows user to change the price. (Mutator Method)
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Accessor method for price.
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Allows user to change the cost. (Mutator Method)
     */
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    /**
     * Accessor method for cost.
     */
    public double getCost() {
        return cost;
    }
    
    /**
     * Accessor method for the ID.
     */
    public String getID() {
        return ID;
    }
    
    /**
     * Adds popularity to the snack.
     */
    public void addPopularity() {
        popularity++;
    }
    
    /**
     * Accessor method for popularity.
     */
    public int getPopularity() {
        return popularity;
    }
    
    /**
     * Sells one of the snacks.
     */
    public void sell() {
        inventory--;
        addPopularity();
    }
}
