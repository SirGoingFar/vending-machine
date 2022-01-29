package operation;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;

public interface MaintenanceOperation {
    void setProductInventory(int productSlotId, long newInventorySize);
    long getProductInventorySize(int productSlotId);
    void setProductPrice(int productSlotId, @NotNull BigDecimal productPrice);
    BigDecimal getProductPrice(int productSlotId);
    void setCoinAvailableCount(double coinValue, long newAvailableCount);
}
