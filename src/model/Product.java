package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private BigDecimal price;
    private long inventorySize;

    public Product(BigDecimal price, long inventorySize) {
        this.price = price;
        this.inventorySize = inventorySize;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getInventorySize() {
        return inventorySize;
    }

    public void setInventorySize(long inventorySize) {
        this.inventorySize = inventorySize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return inventorySize == product.inventorySize && price.equals(product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, inventorySize);
    }

    @Override
    public String toString() {
        return "Product{" +
                "price=" + price +
                ", inventorySize=" + inventorySize +
                '}';
    }
}
