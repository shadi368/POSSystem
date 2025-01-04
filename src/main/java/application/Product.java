package application;

import java.util.Objects;

public class Product {
    private int id; // Database primary key
    private String name;
    private String description;
    private String category;
    private double sellingPrice;
    private double costPrice;
    private int quantity;
    private String image;
    private String barcode; // Changed to String

    // Constructor
    public Product(int id, String name, String description, String category, double sellingPrice, double costPrice, int quantity, String image, String barcode) {
        this.id = id;
        setName(name);
        setDescription(description);
        setCategory(category);
        setSellingPrice(sellingPrice);
        setCostPrice(costPrice);
        setQuantity(quantity);
        this.image = image;
        this.barcode = barcode;
    }

    // Default constructor
    public Product() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return sellingPrice;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        if (sellingPrice < 0) {
            throw new IllegalArgumentException("Selling price cannot be negative");
        }
        this.sellingPrice = sellingPrice;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        if (costPrice < 0) {
            throw new IllegalArgumentException("Cost price cannot be negative");
        }
        this.costPrice = costPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            throw new IllegalArgumentException("Barcode cannot be null or empty");
        }
        this.barcode = barcode;
    }

    // toString Method
    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', description='" + description +
                "', category='" + category + "', sellingPrice=" + sellingPrice +
                ", costPrice=" + costPrice + ", quantity=" + quantity +
                ", image='" + image + "', barcode='" + barcode + "'}";
    }

    // equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return id == product.id &&
                Double.compare(product.sellingPrice, sellingPrice) == 0 &&
                Double.compare(product.costPrice, costPrice) == 0 &&
                quantity == product.quantity &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) &&
                Objects.equals(category, product.category) &&
                Objects.equals(image, product.image) &&
                Objects.equals(barcode, product.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, category, sellingPrice, costPrice, quantity, image, barcode);
    }
}
