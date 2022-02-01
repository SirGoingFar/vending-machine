package application;

import manager.CoinManager;
import manager.DefaultCoinManager;
import manager.DefaultProductSlotManager;
import manager.ProductSlotManager;
import model.Product;
import operation.ConsumerOperation;
import operation.MaintenanceOperation;
import util.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static util.Constants.*;
import static util.NumbersUtil.compare;
import static util.NumbersUtil.toDp;

/**
 * Implementation of the Vending Machine that performs both {@link MaintenanceOperation} and {@link ConsumerOperation}
 * */
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
    public Collection<Double> buyProduct(final int productSlotIndex, final Collection<Double> coinCollection) {
        synchronized (BUY_PRODUCT_MONITOR_OBJECT) {
            //Check for empty coin list
            if (coinCollection == null || coinCollection.isEmpty()) {
                String errorMessage = ERROR_MESSAGE_COIN_IS_REQUIRED_FOR_PURCHASE_OPERATION;
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            //check if provided coins are supported
            if (!coinManager.areCoinsSupported(coinCollection)) {
                String errorMessage = ERROR_MESSAGE_COIN_NOT_SUPPORTED;
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            //Check if slot has item available for purchase
            long availableProductInventorySize = productSlotManager.getSlotProductInventorySize(productSlotIndex);
            if (availableProductInventorySize < 1) {
                String errorMessage = ERROR_MESSAGE_SLOT_OUT_OF_STOCK;
                Logger.error(TAG, errorMessage);
                throw new IllegalStateException(errorMessage);
            }

            //Check if product price is set
            BigDecimal productPrice = productSlotManager.getSlotProductPrice(productSlotIndex);
            if (productPrice == null) {
                Logger.error(TAG, String.format("Customer was interested in product on slot %d but the price has not been set", productSlotIndex));
                throw new IllegalStateException(ERROR_MESSAGE_TECHNICAL_ERROR);
            }

            //Check if the customer has the purchasing power
            BigDecimal coinSum = toDp(BigDecimal.valueOf(coinCollection.stream().mapToDouble(item -> item).sum()), 2);
            int compValue = compare(coinSum, productPrice);
            if (compValue < 0) {
                String errorMessage = ERROR_MESSAGE_PRODUCT_PRICE_MORE_THAN_CUSTOMER_MONEY;
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            Collection<Double> customerChangeCombination = new ArrayList<>();
            if (compValue > 0) {
                //Customer has change to collect
                customerChangeCombination = coinManager.getPossibleCoinCombination(coinSum.subtract(productPrice));
            }

            //Balance inventory
            productSlotManager.updateSlotProductInventorySize(productSlotIndex, --availableProductInventorySize);

            //Balance coin
            coinManager.balanceCoins(coinCollection, customerChangeCombination);

            //return customer change
            return customerChangeCombination;
        }
    }
    //#endregion

    //#region Maintenance
    @Override
    public void addProductToSlot(final BigDecimal price, final int inventorySize) {
        productSlotManager.addProductToSlot(new Product(price, inventorySize));
    }

    @Override
    public void removeProductFromSlot(int productSlotId) {
        productSlotManager.removeProductAtSlot(productSlotId);
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
