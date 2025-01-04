package application;  // Ensure this matches the package where you want to place the class

public class Product {
    // Fields for product details
    private String name;
    private String category;
    private double price;
    private int quantity;

    // Constructor to initialize the product
    public Product(String name, String category, double price, int quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    // Getter method for the name
    public String getName() {
        return name;
    }

    // Getter method for the category
    public String getCategory() {
        return category;
    }

    // Getter method for the price
    public double getPrice() {
        return price;
    }

    // Getter method for the quantity
    public int getQuantity() {
        return quantity;
    }

    // Setter method for the name
    public void setName(String name) {
        this.name = name;
    }

    // Setter method for the category
    public void setCategory(String category) {
        this.category = category;
    }

    // Setter method for the price
    public void setPrice(double price) {
        this.price = price;
    }

    // Setter method for the quantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Override the toString method to return a string representation of the product
    @Override
    public String toString() {
        return "Product{name='" + name + "', category='" + category + "', price=" + price + ", quantity=" + quantity + "}";
    }
}
