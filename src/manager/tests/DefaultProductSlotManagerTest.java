package manager.tests;

import manager.DefaultProductSlotManager;
import model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static util.Constants.*;

class DefaultProductSlotManagerTest {

    private final int PRODUCT_SLOT_SIZE = 5;
    private DefaultProductSlotManager defaultProductSlotManager;

    @BeforeEach
    void setUp() {
        defaultProductSlotManager = new DefaultProductSlotManager(PRODUCT_SLOT_SIZE);
    }

    @Test
    void updateSlotProductInventorySize_updateInventorySizeForProductSlotTheMachineDoesNotHave_throwsIllegalArgumentException() {
        Exception thrownException = null;
        final int invalidProductSlot = 6;
        try {
            defaultProductSlotManager.updateSlotProductInventorySize(invalidProductSlot, 3);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_SLOT, thrownException.getLocalizedMessage());
    }

    @Test
    void updateSlotProductInventorySize_updateProductSlotWhoseItemPriceIsNotSet_throwsIllegalStateException() {
        Exception thrownException = null;
        Product product = new Product(null, 5);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        try {
            defaultProductSlotManager.updateSlotProductInventorySize(addedProductIndex, 3);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertEquals(ERROR_MESSAGE_PRODUCT_PRICE_NOT_SET, thrownException.getLocalizedMessage());
    }

    @Test
    void updateSlotProductInventorySize_updateValidProductSlotWhoseItemPriceIsSetToAnInvalidInventorySize_throwsIllegalStateException() {
        Exception thrownException = null;
        final int OLD_INVENTORY_SIZE = 3;
        final int NEW_INVALID_INVENTORY_SIZE = -1;
        Product product = new Product(BigDecimal.TEN, OLD_INVENTORY_SIZE);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        try {
            defaultProductSlotManager.updateSlotProductInventorySize(addedProductIndex, NEW_INVALID_INVENTORY_SIZE);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalStateException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_INVENTORY_SIZE, thrownException.getLocalizedMessage());
    }

    @Test
    void updateSlotProductInventorySize_updateValidProductSlotWhoseItemPriceIsSet_operationIsSuccessful() {
        int OLD_INVENTORY_SIZE = 3;
        int NEW_INVENTORY_SIZE = 6;
        Product product = new Product(BigDecimal.TEN, OLD_INVENTORY_SIZE);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        defaultProductSlotManager.updateSlotProductInventorySize(addedProductIndex, NEW_INVENTORY_SIZE);
        Assertions.assertEquals(NEW_INVENTORY_SIZE, defaultProductSlotManager.getSlotProductInventorySize(addedProductIndex));
    }


    @Test
    void getSlotProductInventorySize_getProductInventorySizeFromProductSlotTheMachineDoesNotHave_throwsIllegalArgumentException() {
        Exception thrownException = null;
        final int invalidProductSlot = -1;
        try {
            defaultProductSlotManager.getSlotProductInventorySize(invalidProductSlot);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_SLOT, thrownException.getLocalizedMessage());
    }

    @Test
    void getSlotProductInventorySize_fromValidProductSlot_returnsCorrectValue() {
        final long INVENTORY_SIZE = 4;
        Product product = new Product(BigDecimal.TEN, INVENTORY_SIZE);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        Assertions.assertEquals(INVENTORY_SIZE, defaultProductSlotManager.getProductAtSlot(addedProductIndex).getInventorySize());
    }


    @Test
    void setSlotProductPrice_attemptSettingSlotProductPriceToNull_throwsIllegalArgumentException() {
        Exception thrownException = null;
        Product product = new Product(BigDecimal.TEN, 3);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        try {
            defaultProductSlotManager.setSlotProductPrice(addedProductIndex, null);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_PRICE, thrownException.getLocalizedMessage());
    }

    @Test
    void setSlotProductPrice_attemptSettingSlotProductPriceToInvalidValue_throwsIllegalArgumentException() {
        Exception thrownException = null;
        Product product = new Product(BigDecimal.TEN, 3);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        try {
            defaultProductSlotManager.setSlotProductPrice(addedProductIndex, BigDecimal.valueOf(-100.0));
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_PRICE, thrownException.getLocalizedMessage());
    }

    @Test
    void setSlotProductPrice_setSlotProductPriceForProductSlotTheMachineDoesNotHave_throwsIllegalArgumentException() {
        Exception thrownException = null;
        final int invalidProductSlot = 6;
        try {
            defaultProductSlotManager.setSlotProductPrice(invalidProductSlot, BigDecimal.TEN);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_SLOT, thrownException.getLocalizedMessage());
    }

    @Test
    void setSlotProductPrice_setSlotProductPriceToValidValue_operationIsSuccessful() {
        final BigDecimal validPrice = BigDecimal.valueOf(100.0);
        Product product = new Product(BigDecimal.TEN, 3);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        defaultProductSlotManager.setSlotProductPrice(addedProductIndex, validPrice);
        Assertions.assertEquals(0, validPrice.compareTo(defaultProductSlotManager.getSlotProductPrice(addedProductIndex)));
    }

    @Test
    void getSlotProductPrice_getSlotProductPriceFromProductSlotTheMachineDoesNotHave_throwsIllegalArgumentException() {
        Exception thrownException = null;
        final int invalidProductSlot = 6;
        try {
            defaultProductSlotManager.getSlotProductPrice(invalidProductSlot);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_SLOT, thrownException.getLocalizedMessage());
    }

    @Test
    void getSlotProductPrice_fromValidProductSlot_returnsCorrectValue() {
        final BigDecimal PRODUCT_PRICE = BigDecimal.TEN;
        Product product = new Product(PRODUCT_PRICE, 4);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        Assertions.assertEquals(0, defaultProductSlotManager.getProductAtSlot(addedProductIndex).getPrice().compareTo(PRODUCT_PRICE));
    }

    @Test
    void addProductToSlot_maxProductSlotExceeded_throwsIllegalArgumentException() {
        final int TEST_CASE_PRODUCT_SLOT_SIZE = 5;
        defaultProductSlotManager = new DefaultProductSlotManager(TEST_CASE_PRODUCT_SLOT_SIZE);
        Exception thrownException = null;

        Product p1 = new Product(BigDecimal.TEN, 2);
        Product p2 = new Product(BigDecimal.ONE, 3);
        Product p3 = new Product(BigDecimal.valueOf(12.9), 6);
        Product p4 = new Product(BigDecimal.valueOf(33.9), 0);
        Product p5 = new Product(BigDecimal.valueOf(1.1), 1);
        Product p6 = new Product(BigDecimal.TEN, 3);

        defaultProductSlotManager.addProductToSlot(p1);
        defaultProductSlotManager.addProductToSlot(p2);
        defaultProductSlotManager.addProductToSlot(p3);
        defaultProductSlotManager.addProductToSlot(p4);
        defaultProductSlotManager.addProductToSlot(p5);

        try {
            defaultProductSlotManager.addProductToSlot(p6);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_NO_MORE_SLOT_FOR_PRODUCT, thrownException.getLocalizedMessage());
    }

    @Test
    void addProductToSlot_addWithinVendingMachineProductSlotLimit_operationIsSuccessful() {
        Product addedProduct = new Product(BigDecimal.TEN, 2);
        defaultProductSlotManager.addProductToSlot(addedProduct);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(addedProduct);
        Product retrievedProduct = defaultProductSlotManager.getProductAtSlot(addedProductIndex);
        Assertions.assertEquals(addedProduct, retrievedProduct);
    }

    @Test
    void removeProductAtSlot_removeFromProductSlotTheMachineDoesNotHave_throwsIllegalArgumentException() {
        Exception thrownException = null;
        final int invalidProductSlot = 6;
        try {
            defaultProductSlotManager.removeProductAtSlot(invalidProductSlot);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_SLOT, thrownException.getLocalizedMessage());
    }

    @Test
    void removeProductAtSlot_removeFromValidProductSlot_operationIsSuccessful() {
        Product addedProduct = new Product(BigDecimal.TEN, 2);
        defaultProductSlotManager.addProductToSlot(addedProduct);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(addedProduct);
        Assertions.assertEquals(0, addedProductIndex);
        defaultProductSlotManager.removeProductAtSlot(addedProductIndex);
        addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(addedProduct);
        Assertions.assertEquals(-1, addedProductIndex);
    }

    @Test
    void updateProductSlot_updateProductWithProductWithNullPrice_throwsIllegalArgumentException() {
        Exception thrownException = null;
        Product addedProduct = new Product(BigDecimal.TEN, 2);
        defaultProductSlotManager.addProductToSlot(addedProduct);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(addedProduct);

        addedProduct.setPrice(null);

        try {
            defaultProductSlotManager.updateProductSlot(addedProductIndex, addedProduct);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_PRICE, thrownException.getLocalizedMessage());
    }

    @Test
    void updateProductSlot_updateProductWithProductWithInvalidPrice_throwsIllegalArgumentException() {
        Exception thrownException = null;
        Product addedProduct = new Product(BigDecimal.TEN, 2);
        defaultProductSlotManager.addProductToSlot(addedProduct);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(addedProduct);

        addedProduct.setPrice(BigDecimal.valueOf(-100.0));

        try {
            defaultProductSlotManager.updateProductSlot(addedProductIndex, addedProduct);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_PRICE, thrownException.getLocalizedMessage());
    }

    @Test
    void updateProductSlot_attemptToUpdateProductSlotTheMachineDoesNotHave_throwsIllegalArgumentException() {
        Exception thrownException = null;
        Product product = new Product(BigDecimal.TEN, 2);
        final int invalidProductSlot = 6;
        try {
            defaultProductSlotManager.updateProductSlot(invalidProductSlot, product);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_SLOT, thrownException.getLocalizedMessage());
    }

    @Test
    void updateProductSlot_updateWithValidValues_operationIsSuccessful() {
        Product product = new Product(BigDecimal.TEN, 2);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);

        Product updateProduct = new Product(BigDecimal.ZERO, 15);

        defaultProductSlotManager.updateProductSlot(addedProductIndex, updateProduct);

        Assertions.assertEquals(defaultProductSlotManager.getProductAtSlot(addedProductIndex), updateProduct);
    }

    @Test
    void getProductAtSlot_attemptToGetProductFromSlotTheMachineDoesNotHave_throwsIllegalArgumentException() {
        Exception thrownException = null;
        try {
            defaultProductSlotManager.getProductAtSlot(0);
        } catch (Exception ex) {
            thrownException = ex;
        }
        Assertions.assertTrue(thrownException instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_PRODUCT_SLOT, thrownException.getLocalizedMessage());
    }

    @Test
    void getProductAtSlot_attemptToGetProductFromValidMachineSlot_operationIsSuccessful() {
        Product product = new Product(BigDecimal.TEN, 2);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        Assertions.assertEquals(defaultProductSlotManager.getProductAtSlot(addedProductIndex), product);
    }

    @Test
    void getIndexOfProductInSlot_attemptingGettingSlotIndexOfProductNotInTheMachine_returnsMinusOne() {
        Product product = new Product(BigDecimal.TEN, 2);
        Assertions.assertEquals(-1, defaultProductSlotManager.getIndexOfProductInSlot(product));
    }

    @Test
    void getIndexOfProductInSlot_getSlotIndexOfProductInTheMachine_returnsValidPosition() {
        Product product = new Product(BigDecimal.TEN, 2);
        defaultProductSlotManager.addProductToSlot(product);
        int addedProductIndex = defaultProductSlotManager.getIndexOfProductInSlot(product);
        Assertions.assertEquals(addedProductIndex, defaultProductSlotManager.getIndexOfProductInSlot(product));
    }

}