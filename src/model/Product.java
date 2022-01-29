package model;

import java.math.BigDecimal;

public class Product {
    private BigDecimal price;
    private long inventorySize;

    public Product() {
        this.price = null;
        this.inventorySize = 0;
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
}
