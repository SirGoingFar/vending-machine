package application;

import manager.CoinManager;
import manager.DefaultCoinManager;
import manager.DefaultProductSlotManager;
import manager.ProductSlotManager;
import operation.ConsumerOperation;
import operation.MaintenanceOperation;

import java.math.BigDecimal;
import java.util.Collection;

public final class VendingMachine implements MaintenanceOperation, ConsumerOperation {

    //#region Class Variables
    private final CoinManager coinManager;
    private final ProductSlotManager productSlotManager;
    //#endregion

    //#region Class Constructor
    public VendingMachine(int productSlotSize, Collection<Double> coinList) {
        this.coinManager = new DefaultCoinManager(coinList);
        this.productSlotManager = new DefaultProductSlotManager(productSlotSize);
    }

    public VendingMachine(CoinManager coinManager, ProductSlotManager productSlotManager) {
        this.coinManager = coinManager;
        this.productSlotManager = productSlotManager;
    }
    //#endregion

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
