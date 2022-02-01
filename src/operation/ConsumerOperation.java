package operation;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Contract for the component that performs consumer-related operations
 */
public interface ConsumerOperation {
    /**
     * Get the price of a product at a slot in the vending machine
     *
     * @param productSlotId identifier of the slot of the product
     * @return the price of the product at slot {@param productSlotId}
     */
    BigDecimal getProductPrice(final int productSlotId);

    /**
     * Process customer product purchase request
     *
     * @param productSlotId  identifier of the slot of the product to purchase
     * @param coinCollection coins/money submitted by the customer to purchase the product
     * @return list of coin(s) combination of the change the customer is entitled to after deducting the product price from the sum of coins in {@param coinCollection}
     */
    Collection<Double> buyProduct(final int productSlotId, final Collection<Double> coinCollection);
}
