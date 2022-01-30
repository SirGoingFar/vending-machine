package application;

import manager.CoinManager;
import manager.DefaultCoinManager;
import manager.DefaultProductSlotManager;
import manager.ProductSlotManager;
import model.Product;
import operation.ConsumerOperation;
import operation.MaintenanceOperation;

import java.math.BigDecimal;
import java.util.Collection;

public final class VendingMachine implements MaintenanceOperation, ConsumerOperation {

    //#region class constants
    private final Object BUY_PRODUCT_MONITOR_OBJECT = new Object();
    private final Class TAG = this.getClass();
    //#end region

    //#region Class Variables
    private final CoinManager coinManager;
    private final ProductSlotManager productSlotManager;
    //#endregion

    //#region Class Constructor
    public VendingMachine(final int productSlotSize, final Collection<Double> coinList) {
        this.coinManager = new DefaultCoinManager(coinList);
        this.productSlotManager = new DefaultProductSlotManager(productSlotSize);
    }

    //Mostly useful for test instantiation
    public VendingMachine(final CoinManager coinManager, final ProductSlotManager productSlotManager) {
        this.coinManager = coinManager;
        this.productSlotManager = productSlotManager;
    }
    //#endregion

    //#region Consumer
    @Override
    public Collection<Double> buy(final int productSlotIndex, final Collection<Double> coinCollection) {
        synchronized (BUY_PRODUCT_MONITOR_OBJECT) {
            //Check for empty coin list
            //check if provided coins are supported
            //Check if slot is available
            //Check for product inventory count (>=1)
            //Check if product price is set
            //Check if product amount < sum of coin list (sum using List.reduce)
            //Check if change is available (add coin list items together, less the product  amount... then compute change)
            //Balance inventory (less product inventory, balance coin - increment coin values in coin list, decrement the qty of coin)
            return null;
        }
    }
    //#endregion

    //#region Maintenance
    @Override
    public void addProductToSlot(final BigDecimal price, final int inventorySize) {
        productSlotManager.addProductToSlot(new Product(price, inventorySize));
    }

    @Override
    public void removeProductFromSlot(int productSlot) {
        productSlotManager.removeProductAtSlot(productSlot);
    }

    @Override
    public void setProductInventorySize(final int productSlotIndex, final long newInventorySize) {
        productSlotManager.updateSlotProductInventorySize(productSlotIndex, newInventorySize);
    }

    @Override
    public long getProductInventorySize(final int productSlotIndex) {
        return productSlotManager.getSlotProductInventorySize(productSlotIndex);
    }

    @Override
    public void setProductPrice(final int productSlotIndex, final BigDecimal productPrice) {
        productSlotManager.setSlotProductPrice(productSlotIndex, productPrice);
    }

    @Override
    public void setCoinAvailableCount(final double coinValue, final int newAvailableCount) {
        coinManager.setCoinAvailableCount(coinValue, newAvailableCount);
    }

    @Override
    public long getCoinAvailableCount(double coinValue) {
        return coinManager.getCoinAvailableCount(coinValue);
    }
    //#endregion

    @Override
    public BigDecimal getProductPrice(final int productSlotIndex) {
        return productSlotManager.getSlotProductPrice(productSlotIndex);
    }
}
