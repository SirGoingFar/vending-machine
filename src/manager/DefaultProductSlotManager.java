package manager;

import com.sun.istack.internal.NotNull;
import model.Product;
import util.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static util.Constants.*;

/**
 * Default implementation of {@link ProductSlotManager}
 * */
public class DefaultProductSlotManager implements ProductSlotManager {

    //#region class constants
    private final Object PRODUCT_SLOT_ACCESS_MONITOR_OBJECT = new Object();
    private final Class TAG = this.getClass();
    //#end region

    private final int PRODUCT_SLOT_SIZE;
    private volatile List<Product> productSlots;

    //#region Class Constructor
    private DefaultProductSlotManager() {
        //Added in case an instantiation is attempted using reflection
        String errorMessage = "Cannot instantiate class with this constructor";
        Logger.error(TAG, errorMessage);
        throw new UnsupportedOperationException();
    }

    public DefaultProductSlotManager(final int productSlotSize) {
        if (productSlotSize < 1) {
            //Minimum of 1 product slot - no vending machine has NO product slot
            String errorMessage = ERROR_MESSAGE_INVALID_PRODUCT_SLOT_SIZE;
            Logger.error(TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        this.PRODUCT_SLOT_SIZE = productSlotSize;
        this.productSlots = new ArrayList<>(PRODUCT_SLOT_SIZE);
        Logger.info(TAG, "Product Slot Size = " + productSlotSize);
    }
    //#endregion


    @Override
    public void updateSlotProductInventorySize(final int productSlotIndex, final long newInventorySize) {
        synchronized (PRODUCT_SLOT_ACCESS_MONITOR_OBJECT) {
            Product ctxProduct = getProductAtSlot(productSlotIndex);
            if (ctxProduct.getPrice() == null) {
                String errorMsg = ERROR_MESSAGE_PRODUCT_PRICE_NOT_SET;
                Logger.error(TAG, errorMsg);
                throw new IllegalStateException(errorMsg);
            } else if (newInventorySize < 0) {
                String errorMsg = ERROR_MESSAGE_INVALID_PRODUCT_INVENTORY_SIZE;
                Logger.error(TAG, errorMsg);
                throw new IllegalStateException(errorMsg);
            }
            ctxProduct.setInventorySize(newInventorySize);
            updateProductSlot(productSlotIndex, ctxProduct);
        }
    }

    @Override
    public long getSlotProductInventorySize(int productSlotIndex) {
        synchronized (PRODUCT_SLOT_ACCESS_MONITOR_OBJECT) {
            validateProductSlotIndex(productSlotIndex);
            return getProductAtSlot(productSlotIndex).getInventorySize();
        }
    }

    @Override
    public void setSlotProductPrice(int productSlotIndex, @NotNull BigDecimal productPrice) {
        synchronized (PRODUCT_SLOT_ACCESS_MONITOR_OBJECT) {
            validateProductSlotIndex(productSlotIndex);
            Product product = getProductAtSlot(productSlotIndex);
            product.setPrice(productPrice);
            updateProductSlot(productSlotIndex, product);
        }
    }

    @Override
    public BigDecimal getSlotProductPrice(int productSlotIndex) {
        synchronized (PRODUCT_SLOT_ACCESS_MONITOR_OBJECT) {
            validateProductSlotIndex(productSlotIndex);
            return getProductAtSlot(productSlotIndex).getPrice();
        }
    }

    @Override
    public void addProductToSlot(final Product product) {
        assertProductSlotAvailabilityForNewProductAddition();
        productSlots.add(product);
        Logger.info(TAG, "Product added to slot: " + product.toString());
        Logger.info(TAG, "New Product Slot: " + productSlots);
    }

    @Override
    public void removeProductAtSlot(final int productSlotIndex) {
        validateProductSlotIndex(productSlotIndex);
        productSlots.remove(productSlotIndex);
        Logger.info(TAG, "Product DELETE from slot. New product slots: " + productSlots);
    }

    public void updateProductSlot(final int productSlotIndex, final Product product) {
        validatePrice(product.getPrice());
        validateProductSlotIndex(productSlotIndex);
        productSlots.remove(productSlotIndex);
        productSlots.add(productSlotIndex, product);
        Logger.info(TAG, "Product UPDATED in slot. New product slots: " + productSlots);
    }

    public Product getProductAtSlot(final int productSlotIndex) {
        validateProductSlotIndex(productSlotIndex);
        return productSlots.get(productSlotIndex);
    }

    public int getIndexOfProductInSlot(final Product product) {
        return productSlots.indexOf(product);
    }

    private void validateProductSlotIndex(final int productSlotIndex) {
        if (productSlotIndex > (productSlots.size() - 1) || productSlotIndex > (PRODUCT_SLOT_SIZE - 1) || productSlotIndex < 0) {
            String errorMessage = ERROR_MESSAGE_INVALID_PRODUCT_SLOT;
            Logger.error(TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validatePrice(final BigDecimal productPrice) {
        if (productPrice == null || productPrice.compareTo(BigDecimal.ZERO) < 0) {
            String errorMessage = ERROR_MESSAGE_INVALID_PRODUCT_PRICE;
            Logger.error(TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void assertProductSlotAvailabilityForNewProductAddition() {
        if ((productSlots.size() + 1) > PRODUCT_SLOT_SIZE) {
            String errorMessage = ERROR_MESSAGE_NO_MORE_SLOT_FOR_PRODUCT;
            Logger.error(TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

}
