package application.tests;

import application.VendingMachine;
import manager.DefaultCoinManager;
import manager.DefaultProductSlotManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static util.Constants.*;
import static util.NumbersUtil.getCountOfItem;

class VendingMachineTest {

    private final int PRODUCT_SLOT_SIZE = 5;
    private final Collection<Double> SUPPORTED_COINS = Arrays.asList(0.1, 0.2, 0.5, 1.0);

    private VendingMachine vendingMachine;

    @BeforeEach
    void setUp() {
        vendingMachine = new VendingMachine(
                new DefaultCoinManager(SUPPORTED_COINS),
                new DefaultProductSlotManager(PRODUCT_SLOT_SIZE)
        );
    }

    @Test
    void buyProduct_withoutSubmittingCoins_throwsIllegalArgumentException() {
        Exception exception = null;

        //Add initial coins
        vendingMachine.setCoinAvailableCount(0.1, 3);
        vendingMachine.setCoinAvailableCount(0.2, 2);
        vendingMachine.setCoinAvailableCount(0.5, 7);
        vendingMachine.setCoinAvailableCount(1.0, 1);

        //Add product
        vendingMachine.addProductToSlot(BigDecimal.valueOf(1), 2);

        try {
            vendingMachine.buyProduct(0, null);
        } catch (Exception ex) {
            exception = ex;
        }

        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_COIN_IS_REQUIRED_FOR_PURCHASE_OPERATION, exception.getLocalizedMessage());

        try {
            vendingMachine.buyProduct(0, Collections.EMPTY_LIST);
        } catch (Exception ex) {
            exception = ex;
        }

        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_COIN_IS_REQUIRED_FOR_PURCHASE_OPERATION, exception.getLocalizedMessage());
    }


    @Test
    void buyProduct_withUnsupportedCoins_throwsIllegalArgumentException() {
        Exception exception = null;

        //Add initial coins
        vendingMachine.setCoinAvailableCount(0.1, 3);
        vendingMachine.setCoinAvailableCount(0.2, 2);
        vendingMachine.setCoinAvailableCount(0.5, 7);
        vendingMachine.setCoinAvailableCount(1.0, 1);

        //Add product
        vendingMachine.addProductToSlot(BigDecimal.valueOf(1), 2);

        double unsupportedCoin = 0.3;
        Assertions.assertFalse(SUPPORTED_COINS.contains(unsupportedCoin));

        try {
            vendingMachine.buyProduct(0, Collections.singleton(unsupportedCoin));
        } catch (Exception ex) {
            exception = ex;
        }

        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_COIN_NOT_SUPPORTED, exception.getLocalizedMessage());
    }

    @Test
    void buyProduct_slotHasNoProductItem_throwsIllegalStateException() {
        Exception exception = null;

        //Add product
        vendingMachine.addProductToSlot(BigDecimal.valueOf(1), 0);

        try {
            vendingMachine.buyProduct(0, Collections.singleton(0.2));
        } catch (Exception ex) {
            exception = ex;
        }

        Assertions.assertTrue(exception instanceof IllegalStateException);
        Assertions.assertEquals(ERROR_MESSAGE_SLOT_OUT_OF_STOCK, exception.getLocalizedMessage());
    }

    @Test
    void buyProduct_productHasNoPriceSet_throwsIllegalStateException() {
        Exception exception = null;

        //Add product
        vendingMachine.addProductToSlot(null, 5);

        try {
            vendingMachine.buyProduct(0, Collections.singleton(0.2));
        } catch (Exception ex) {
            exception = ex;
        }

        Assertions.assertTrue(exception instanceof IllegalStateException);
        Assertions.assertEquals(ERROR_MESSAGE_TECHNICAL_ERROR, exception.getLocalizedMessage());
    }

    @Test
    void buyProduct_customerMoneyIsLessThanTheProductPrice_throwsIllegalStateException() {
        Exception exception = null;

        //Add product
        vendingMachine.addProductToSlot(BigDecimal.ONE, 5);

        try {
            vendingMachine.buyProduct(0, Collections.singleton(0.2));
        } catch (Exception ex) {
            exception = ex;
        }

        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_PRODUCT_PRICE_MORE_THAN_CUSTOMER_MONEY, exception.getLocalizedMessage());
    }

    @Test
    void buyProduct_withValidInput_inventoryAndCoinAreBalanced() {

        double supportedCoinOne = 1;
        double supportedCoinTwo = 0.1;
        double supportedCoinThree = 0.2;
        double supportedCoinFour = 0.5;

        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinOne));
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinTwo));
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinThree));
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinFour));

        int supportedCoinOneCount = 1;
        int supportedCoinTwoCount = 2;
        int supportedCoinThreeCount = 3;
        int supportedCoinFourCount = 0;

        vendingMachine.setCoinAvailableCount(supportedCoinOne, supportedCoinOneCount);
        vendingMachine.setCoinAvailableCount(supportedCoinTwo, supportedCoinTwoCount);
        vendingMachine.setCoinAvailableCount(supportedCoinThree, supportedCoinThreeCount);
        vendingMachine.setCoinAvailableCount(supportedCoinFour, supportedCoinFourCount);

        //add product
        double productPrice = supportedCoinOne;
        vendingMachine.addProductToSlot(BigDecimal.valueOf(productPrice), supportedCoinOneCount);
        int productSlot = 0; //only item added - it's on index 0
        long productInventorySize = vendingMachine.getProductInventorySize(productSlot);

        //buy product
        vendingMachine.buyProduct(0, Arrays.asList(supportedCoinOne, supportedCoinTwo));

        //Confirm that inventory is balanced
        Assertions.assertEquals((productInventorySize - 1), vendingMachine.getProductInventorySize(productSlot));

        //Confirm that coin state is correct
        Assertions.assertEquals((supportedCoinOneCount + 1), vendingMachine.getCoinAvailableCount(supportedCoinOne));
        Assertions.assertEquals(supportedCoinTwoCount, vendingMachine.getCoinAvailableCount(supportedCoinTwo));
        Assertions.assertEquals(supportedCoinThreeCount, vendingMachine.getCoinAvailableCount(supportedCoinThree));
        Assertions.assertEquals(supportedCoinFourCount, vendingMachine.getCoinAvailableCount(supportedCoinFour));
    }

    @Test
    void buyProduct_withValidInput_correctChangeIsReturned() {

        double supportedCoinOne = 1;
        double supportedCoinTwo = 0.1;
        double supportedCoinThree = 0.2;
        double supportedCoinFour = 0.5;

        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinOne));
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinTwo));
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinThree));
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinFour));

        int supportedCoinOneCount = 1;
        int supportedCoinTwoCount = 2;
        int supportedCoinThreeCount = 3;
        int supportedCoinFourCount = 0;

        vendingMachine.setCoinAvailableCount(supportedCoinOne, supportedCoinOneCount);
        vendingMachine.setCoinAvailableCount(supportedCoinTwo, supportedCoinTwoCount);
        vendingMachine.setCoinAvailableCount(supportedCoinThree, supportedCoinThreeCount);
        vendingMachine.setCoinAvailableCount(supportedCoinFour, supportedCoinFourCount);

        //add product
        double productPrice = supportedCoinOne;
        vendingMachine.addProductToSlot(BigDecimal.valueOf(productPrice), supportedCoinOneCount);
        int productSlot = 0; //only item added - it's on index 0
        long productInventorySize = vendingMachine.getProductInventorySize(productSlot);

        //buy product
        Collection<Double> coinCombination = vendingMachine.buyProduct(0, Arrays.asList(supportedCoinOne, supportedCoinTwo));

        //Assert change correctness
        Assertions.assertEquals(Collections.singletonList(supportedCoinTwo), coinCombination);
    }

}