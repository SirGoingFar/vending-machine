package manager.tests;

import manager.DefaultCoinManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static util.Constants.*;

class DefaultCoinManagerTest {

    private final Collection<Double> SUPPORTED_COINS = Arrays.asList(0.1, 0.2, 0.5, 1.0);
    private DefaultCoinManager defaultCoinManager;

    @BeforeEach
    void setUp() {
        defaultCoinManager = new DefaultCoinManager(SUPPORTED_COINS);
    }

    @Test
    void setCoinAvailableCount_attemptSettingCountForUnsupportedCoin_throwsIllegalArgumentException() {
        Exception exception = null;
        double unsupportedCoin = 0.3;
        int count = 3;
        Assertions.assertFalse(SUPPORTED_COINS.contains(unsupportedCoin));
        try {
            defaultCoinManager.setCoinAvailableCount(unsupportedCoin, count);
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_COIN_NOT_SUPPORTED, exception.getLocalizedMessage());
    }

    @Test
    void setCoinAvailableCount_setCountForSupportedCoin_operationIsSuccessful() {
        double supportedCoin = 0.5;
        int count = 3;
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoin));
        defaultCoinManager.setCoinAvailableCount(supportedCoin, count);
        Assertions.assertEquals(count, defaultCoinManager.getCoinAvailableCount(supportedCoin));
    }

    @Test
    void getCoinAvailableCount_getCountForUnsupportedCoin_throwsIllegalArgumentException() {
        Exception exception = null;
        double unsupportedCoin = 0.3;
        Assertions.assertFalse(SUPPORTED_COINS.contains(unsupportedCoin));
        try {
            defaultCoinManager.getCoinAvailableCount(unsupportedCoin);
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_COIN_NOT_SUPPORTED, exception.getLocalizedMessage());
    }

    @Test
    void getCoinAvailableCount_getCountForSupportedCoin_operationIsSuccessful() {
        double supportedCoin = 0.5;
        int count = 3;
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoin));
        defaultCoinManager.setCoinAvailableCount(supportedCoin, count);
        int fetchedValue = defaultCoinManager.getCoinAvailableCount(supportedCoin);
        Assertions.assertEquals(count, fetchedValue);
    }

    @Test
    void areCoinsSupported_listContainsUnsupportedCoins_returnsFalse() {
        Assertions.assertFalse(defaultCoinManager.areCoinsSupported(Arrays.asList(1.2, 3.1, 1.0)));
    }

    @Test
    void areCoinsSupported_listContainsSupportedCoins_returnsTrue() {
        Assertions.assertTrue(defaultCoinManager.areCoinsSupported(SUPPORTED_COINS));
    }

    @Test
    void areCoinsSupported_emptyCoinList_throwsIllegalArgumentException() {
        Exception exception = null;
        double unsupportedCoin = 0.3;
        Assertions.assertFalse(SUPPORTED_COINS.contains(unsupportedCoin));
        try {
            defaultCoinManager.areCoinsSupported(Collections.EMPTY_LIST);
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_COIN_LIST_IS_REQUIRED, exception.getLocalizedMessage());
    }

    @Test
    void balanceCoins_nullCreditCoinList_throwsIllegalArgumentException() {
        Exception exception = null;
        try {
            defaultCoinManager.balanceCoins(null, Collections.singletonList(1.0));
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_CREDIT_COIN_LIST_REQUIRED, exception.getLocalizedMessage());
    }

    @Test
    void balanceCoins_nullDebitCoinList_throwsIllegalArgumentException() {
        Exception exception = null;
        try {
            defaultCoinManager.balanceCoins(Collections.singletonList(1.0), null);
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_DEBIT_COIN_LIST_REQUIRED, exception.getLocalizedMessage());
    }

    @Test
    void balanceCoins_balanceWithUnsupportedCoin_throwsIllegalArgumentException() {
        Exception exception = null;
        try {
            defaultCoinManager.balanceCoins(Collections.singletonList(3.6), Collections.singletonList(1.2));
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_COIN_NOT_SUPPORTED, exception.getLocalizedMessage());
    }

    @Test
    void balanceCoins_balanceWithDebitCoinThatLeaveVendingMachineAvailableCoinOnInvalidState_availableCoinStateRemainsTheSame() {
        double supportedCoinOne = 1.0;
        int supportedCoinOneCount = defaultCoinManager.getCoinAvailableCount(supportedCoinOne);
        double supportedCoinTwo = 0.5;
        int supportedCoinTwoCount = defaultCoinManager.getCoinAvailableCount(supportedCoinTwo);
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinOne));
        Assertions.assertTrue(SUPPORTED_COINS.contains(supportedCoinTwo));
        try {
            defaultCoinManager.balanceCoins(Collections.singletonList(supportedCoinOne), Collections.singletonList(supportedCoinTwo));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assertions.assertEquals(supportedCoinOneCount, defaultCoinManager.getCoinAvailableCount(supportedCoinOne));
        Assertions.assertEquals(supportedCoinTwoCount, defaultCoinManager.getCoinAvailableCount(supportedCoinTwo));
    }

    @Test
    void balanceCoins_balanceWithSupportedCoin_updatedCoinCountIsUpdated() {
        double supportedCoinToDebit = 1.0;
        int supportedCoinToDebitCount = 3;
        defaultCoinManager.setCoinAvailableCount(supportedCoinToDebit, supportedCoinToDebitCount);
        double supportedCoinToCredit = 0.5;
        int supportedCoinToCreditCount = defaultCoinManager.getCoinAvailableCount(supportedCoinToCredit);
        defaultCoinManager.balanceCoins(Collections.singletonList(supportedCoinToCredit), Collections.singletonList(supportedCoinToDebit));

        //If operation successful, increment/decrement coin as appropriate
        --supportedCoinToDebitCount;
        ++supportedCoinToCreditCount;

        Assertions.assertEquals(supportedCoinToDebitCount, defaultCoinManager.getCoinAvailableCount(supportedCoinToDebit));
        Assertions.assertEquals(supportedCoinToCreditCount, defaultCoinManager.getCoinAvailableCount(supportedCoinToCredit));
    }

    /*@Test
    void getPossibleCoinCombination() {
        Set<Double> coinList = new HashSet<>();
        coinList.add(0.1);
        coinList.add(0.2);
        coinList.add(0.5);
        coinList.add(1.0);
        VendingMachine vm = new VendingMachine(10, coinList);

        //Add initial coins
        vm.setCoinAvailableCount(0.1, 3);
        vm.setCoinAvailableCount(0.2, 2);
        vm.setCoinAvailableCount(0.5, 7);
        vm.setCoinAvailableCount(1.0, 1);

        //Add product
        vm.addProductToSlot(BigDecimal.valueOf(0.5), 2);

        //Buy product
        Collection<Double> changeList = vm.buy(0, Arrays.asList(1.0,0.5,0.2));
        Logger.info("Change: " + changeList);
    }*/

    @Test
    void getPossibleCoinCombination_amountIsNull_throwsIllegalArgumentException() {
        Exception exception = null;
        try {
            defaultCoinManager.getPossibleCoinCombination(null);
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_AMOUNT_PROVIDED, exception.getLocalizedMessage());
    }

    @Test
    void getPossibleCoinCombination_amountIsLessThanZero_throwsIllegalArgumentException() {
        Exception exception = null;
        try {
            defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(-100));
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalArgumentException);
        Assertions.assertEquals(ERROR_MESSAGE_INVALID_AMOUNT_PROVIDED, exception.getLocalizedMessage());
    }

    @Test
    void getPossibleCoinCombination_amountIsZero_returnsAnEmptyListOfCoinCombination() {
        Collection<Double> coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(0));
        Assertions.assertEquals(0, coinCombination.size());
    }

    @Test
    void getPossibleCoinCombination_amountEqualsToTotalSumOfAvailableCoins_returnsListOfCoinCombinationOfAllNonZeroCountCoinAndTheOccurrence() {
        double supportedCoinOne = 0.1;
        double supportedCoinTwo = 1;
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

        defaultCoinManager.setCoinAvailableCount(supportedCoinOne, supportedCoinOneCount);
        defaultCoinManager.setCoinAvailableCount(supportedCoinTwo, supportedCoinTwoCount);
        defaultCoinManager.setCoinAvailableCount(supportedCoinThree, supportedCoinThreeCount);
        defaultCoinManager.setCoinAvailableCount(supportedCoinFour, supportedCoinFourCount);

        double expectedSumOfAvailableCoin = (supportedCoinOne * supportedCoinOneCount) + (supportedCoinTwo * supportedCoinTwoCount) + (supportedCoinThree * supportedCoinThreeCount);
        double amount = expectedSumOfAvailableCoin;// NB: Test case action is about amount EQUALS TO total sum of available coins

        Collection<Double> coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(amount));

        Assertions.assertEquals(supportedCoinOneCount, getCountOfItem(coinCombination, supportedCoinOne));
        Assertions.assertEquals(supportedCoinTwoCount, getCountOfItem(coinCombination, supportedCoinTwo));
        Assertions.assertEquals(supportedCoinThreeCount, getCountOfItem(coinCombination, supportedCoinThree));
        Assertions.assertEquals(supportedCoinFourCount, getCountOfItem(coinCombination, supportedCoinFour));
    }

    @Test
    void getPossibleCoinCombination_amountGreaterThanTotalSumOfAvailableCoins_throwsIllegalStateException() {
        Exception exception = null;
        double supportedCoinOne = 0.1;
        double supportedCoinTwo = 1;
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

        defaultCoinManager.setCoinAvailableCount(supportedCoinOne, supportedCoinOneCount);
        defaultCoinManager.setCoinAvailableCount(supportedCoinTwo, supportedCoinTwoCount);
        defaultCoinManager.setCoinAvailableCount(supportedCoinThree, supportedCoinThreeCount);
        defaultCoinManager.setCoinAvailableCount(supportedCoinFour, supportedCoinFourCount);

        double expectedSumOfAvailableCoin = (supportedCoinOne * supportedCoinOneCount) + (supportedCoinTwo * supportedCoinTwoCount) + (supportedCoinThree * supportedCoinThreeCount);
        double amount = expectedSumOfAvailableCoin + 1;// NB: Test case action is about "amount GREATER THAN total sum of available coins

        try {
            defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(amount));
        } catch (Exception ex) {
            exception = ex;
        }
        Assertions.assertTrue(exception instanceof IllegalStateException);
        Assertions.assertEquals(ERROR_MESSAGE_INSUFFICIENT_COIN_S_FOR_CHANGE, exception.getLocalizedMessage());
    }

    @Test
    void getPossibleCoinCombination_amountDeduceableFromAvailableCoin_returnsListOfCoinCombination() {
        //Achieve {0.5=0, 1.0=1, 0.1=0, 0.2=0}
        defaultCoinManager.setCoinAvailableCount(1.0, 1);
        Collection<Double> coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(1));
        Assertions.assertEquals(Collections.singletonList(1.0), coinCombination);

        //Achieve {0.5=1, 1.0=0, 0.1=0, 0.2=0}
        defaultCoinManager.setCoinAvailableCount(1.0, 0);
        defaultCoinManager.setCoinAvailableCount(0.5, 1);
        coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(0.5));
        Assertions.assertEquals(Collections.singletonList(0.5), coinCombination);

        //Achieve {0.5=4, 1.0=1, 0.1=0, 0.2=0}
        defaultCoinManager.setCoinAvailableCount(1.0, 1);
        defaultCoinManager.setCoinAvailableCount(0.5, 4);
        coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(2.5));
        Assertions.assertEquals(Arrays.asList(1.0, 0.5, 0.5, 0.5), coinCombination);

        //Achieve {1.6=4, 0.4=1, 1.0=0, 0.1=2}
        defaultCoinManager = new DefaultCoinManager(Arrays.asList(1.6, 0.4, 1.0, 0.1));
        defaultCoinManager.setCoinAvailableCount(1.6, 4);
        defaultCoinManager.setCoinAvailableCount(0.4, 1);
        defaultCoinManager.setCoinAvailableCount(0.1, 2);
        coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(2.0));
        Assertions.assertEquals(Arrays.asList(1.6, 0.4), coinCombination);

        //Achieve {1.6=4, 0.4=0, 1.0=0, 0.1=4}
        defaultCoinManager.setCoinAvailableCount(0.4, 0);
        defaultCoinManager.setCoinAvailableCount(0.1, 4);
        coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(2.0));
        Assertions.assertEquals(Arrays.asList(1.6, 0.1, 0.1, 0.1, 0.1), coinCombination);

        //Achieve {0.1=2, 0.4=3, 2.04=1, 0.06=1}
        defaultCoinManager = new DefaultCoinManager(Arrays.asList(0.1, 0.4, 2.04, 0.06));
        defaultCoinManager.setCoinAvailableCount(0.1, 2);
        defaultCoinManager.setCoinAvailableCount(0.4, 3);
        defaultCoinManager.setCoinAvailableCount(2.04, 1);
        defaultCoinManager.setCoinAvailableCount(0.06, 1);
        coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(3.0));
        Assertions.assertEquals(Arrays.asList(2.04, 0.4, 0.4, 0.1, 0.06), coinCombination);
    }

    @Test
    void getPossibleCoinCombination_amountNotDeduceableFromAvailableCoin_throwsIllegalStateException() {
        //Achieve {0.5=0, 1.0=1, 0.1=0, 0.2=0}
        defaultCoinManager.setCoinAvailableCount(1.0, 1);
        Exception exception = null;
        try {
            Collection<Double> coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(0.1));
        } catch (Exception ex) {
            exception = ex;
        }

        Assertions.assertTrue(exception instanceof IllegalStateException);
        Assertions.assertEquals(ERROR_MESSAGE_AVAILABLE_COIN_S_CANNOT_PROVIDE_CHANGE, exception.getLocalizedMessage());

        //Achieve {0.1=2, 0.4=3, 2.04=1, 0.06=1}
        defaultCoinManager = new DefaultCoinManager(Arrays.asList(0.1, 0.4, 2.04, 0.06));
        defaultCoinManager.setCoinAvailableCount(0.1, 2);
        defaultCoinManager.setCoinAvailableCount(0.4, 3);
        defaultCoinManager.setCoinAvailableCount(2.04, 1);
        defaultCoinManager.setCoinAvailableCount(0.06, 1);
        try {
            Collection<Double> coinCombination = defaultCoinManager.getPossibleCoinCombination(BigDecimal.valueOf(3.2));
        } catch (Exception ex) {
            exception = ex;
        }

        Assertions.assertTrue(exception instanceof IllegalStateException);
        Assertions.assertEquals(ERROR_MESSAGE_AVAILABLE_COIN_S_CANNOT_PROVIDE_CHANGE, exception.getLocalizedMessage());
    }

    private int getCountOfItem(Collection<Double> coinCombination, double coin) {
        int count = 0;
        for (double c : coinCombination) {
            if (c == coin) {
                count++;
            }
        }
        return count;
    }
}