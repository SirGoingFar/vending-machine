package operation;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;

public interface MaintenanceOperation {
    void addProductToSlot(final BigDecimal price, final int inventorySize);
    void removeProductFromSlot(final int productSlot);
    void setProductInventorySize(final int productSlotId, final long newInventorySize);
    long getProductInventorySize(final int productSlotId);
    void setProductPrice(final int productSlotId, @NotNull final BigDecimal productPrice);
    BigDecimal getProductPrice(final int productSlotId);
    void setCoinAvailableCount(final double coinValue, final int newAvailableCount);
    long getCoinAvailableCount(final double coinValue);
}
