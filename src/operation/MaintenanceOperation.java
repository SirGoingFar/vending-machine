package operation;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;

/**
 * Contract for the component that performs maintainer-related operations
 */
public interface MaintenanceOperation {
    /**
     * Add product to the next available slot in the vending machine
     *
     * @param price price of the product
     * @param inventorySize available count of the product item
     * */
    void addProductToSlot(final BigDecimal price, final int inventorySize);

    /**
     * Remove product from the product slot
     *
     * @param productSlotId position of product to remove
     * */
    void removeProductFromSlot(final int productSlotId);
    /**
     * Updates the number of product item on the product slot
     *
     * @param productSlotId position of product on the product slot
     * @param newInventorySize number of item at slot after update
     * */
    void setProductInventorySize(final int productSlotId, final long newInventorySize);

    /**
     * Get the number of items on a product slot
     *
     * @param productSlotId position of product on the product slot
     *
     * @return number of items at product slot {@param productSlotIndex}
     * */
    long getProductInventorySize(final int productSlotId);

    /**
     * Set/update the price of a product on the product slot
     *
     * @param productSlotId position of product on the product slot
     * @param productPrice product price after update
     * */
    void setProductPrice(final int productSlotId, @NotNull final BigDecimal productPrice);

    /**
     * Get the price of a product at a slot in the vending machine
     *
     * @param productSlotId identifier of the slot of the product
     * @return the price of the product at slot {@param productSlotId}
     */
    BigDecimal getProductPrice(final int productSlotId);

    /**
     * Set the available count of a supported coin
     *
     * @param coinValue         the unit value of the coin
     * @param newAvailableCount the number of {@param coinValue} available after operation
     */
    void setCoinAvailableCount(final double coinValue, final int newAvailableCount);

    /**
     * Get the number of available unit(s) of a coin type
     *
     * @param coinValue value of the coin
     * @return available count of {@param coinValue}
     */
    long getCoinAvailableCount(final double coinValue);
}
