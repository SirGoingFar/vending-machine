package application;

import operation.ConsumerOperation;
import operation.MaintenanceOperation;

import java.math.BigDecimal;
import java.util.Collection;

public final class VendingMachine implements MaintenanceOperation, ConsumerOperation {

    //#region Consumer
    @Override
    public Collection<Double> buy(int productSlotId, Collection<Double> coinCollection) {
        return null;
    }

    @Override
    public BigDecimal getProductPrice(int productSlotId) {
        return null;
    }
    //#endregion

    //#region Maintenance
    @Override
    public void setProductInventory(int productSlotId, long newInventorySize) {

    }

    @Override
    public long getProductInventorySize(int productSlotId) {
        return 0;
    }

    @Override
    public void setProductPrice(int productSlotId, BigDecimal productPrice) {

    }

    @Override
    public void setCoinAvailableCount(double coinValue, long newAvailableCount) {

    }
    //#endregion
}
