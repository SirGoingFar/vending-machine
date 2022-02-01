package manager;

import com.sun.istack.internal.NotNull;
import model.Product;

import java.math.BigDecimal;

/**
 * Contract for the component that manages the product slots of the vending machine
 * */
public interface ProductSlotManager {
    /**
     * Updates the number of product item on the product slot
     *
     * @param productSlotIndex position of product on the product slot
     * @param newInventorySize number of item at slot after update
     * */
    void updateSlotProductInventorySize(final int productSlotIndex, final long newInventorySize);

    /**
     * Get the number of items on a product slot
     *
     * @param productSlotIndex position of product on the product slot
     *
     * @return number of items at product slot {@param productSlotIndex}
     * */
    long getSlotProductInventorySize(final int productSlotIndex);

    /**
     * Set/update the price of a product on the product slot
     *
     * @param productSlotIndex position of product on the product slot
     * @param productPrice product price after update
     * */
    void setSlotProductPrice(final int productSlotIndex, @NotNull final BigDecimal productPrice);

    /**
     * Get the price of a product at a slot
     *
     * @param productSlotIndex position of product on the product slot
     *
     * @return price of item at slot {@param productSlotIndex}
     * */
    BigDecimal getSlotProductPrice(final int productSlotIndex);

    /**
     * Add product to the next available product slot
     *
     * @param product to add
     * */
    void addProductToSlot(final Product product);

    /**
     * Remove product from the product slot
     *
     * @param productSlotIndex position of product to remove
     * */
    void removeProductAtSlot(final int productSlotIndex);
}
