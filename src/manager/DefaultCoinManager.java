package manager;

import com.sun.istack.internal.NotNull;
import util.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static util.Constants.*;
import static util.NumbersUtil.compare;
import static util.NumbersUtil.toDp;

/**
 * Default implementation of {@link CoinManager}
 * */
public class DefaultCoinManager implements CoinManager {

    //#region class constants
    private final Object COIN_ACCESS_MONITOR_OBJECT = new Object();
    private final Class TAG = this.getClass();
    //#end region

    private volatile Map<Double, Integer> coinToCountMap = Collections.synchronizedMap(new HashMap<>());

    //#region Class Constructor
    private DefaultCoinManager() {
        //Added in case an instantiation is attempted using reflection
        throw new UnsupportedOperationException("Cannot instantiate class with this constructor");
    }

    public DefaultCoinManager(@NotNull final Collection<Double> allowedCoinList) {
        if (allowedCoinList.isEmpty()) {
            String errorMessage = "No coin specified";
            Logger.error(TAG, errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        allowedCoinList.forEach(coin -> coinToCountMap.put(coin, 0));
        Logger.info(TAG, "Map-to-Count of allowed coins: " + coinToCountMap);
    }
    //#endregion

    //#region maintenance
    @Override
    public void setCoinAvailableCount(final double coinValue, final int newAvailableCount) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            validateCoinSupport(coinValue);
            coinToCountMap.put(coinValue, newAvailableCount);
            Logger.info(TAG, "Available coin updated: " + coinToCountMap);
        }
    }

    @Override
    public int getCoinAvailableCount(final double coinValue) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            validateCoinSupport(coinValue);
            return coinToCountMap.get(coinValue);
        }
    }
    //#endregion


    @Override
    public boolean areCoinsSupported(Collection<Double> coinList) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            if (coinList == null || coinList.isEmpty()) {
                Logger.error(TAG, "Coin list is empty. It is required for operation");
                throw new IllegalArgumentException(ERROR_MESSAGE_COIN_LIST_IS_REQUIRED);
            }

            for (Double coin : coinList) {
                if (!coinToCountMap.containsKey(coin)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public Collection<Double> getPossibleCoinCombination(@NotNull final BigDecimal amount) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            if (amount == null || amount.doubleValue() < 0.0) {
                String errorMessage = ERROR_MESSAGE_INVALID_AMOUNT_PROVIDED;
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            if (amount.doubleValue() == 0.0) {
                return new ArrayList<>();
            }

            BigDecimal totalAvailableCoinValue = computeAvailableCoinTotalValue(coinToCountMap);
            int compValue = amount.compareTo(totalAvailableCoinValue);
            if (compValue == 0) {
                //Total change amount is EQUAL TO the total value of available coins
                return prepareAllAvailableCoins();
            } else if (compValue > 0) {
                //Total change amount is MORE THAN the total value of available coins
                throwInsufficientAvailableCoinForChange();
            }

            //There's a sufficient coin for change, generate coin combination
            return generateChangeCoinCombination(amount);
        }
    }

    //Todo: Trade off - we could have used the customer coin as part of the change computation. But that doesn't follow a proper accounting process
    //What if after adding the customer money to the available coin and another customer purchase hijack it in the process and we end up settling that
    //the other customer with the first customer's money
    @Override
    public void balanceCoins(final Collection<Double> creditCoinList, final Collection<Double> debitCoinList) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            Map<Double, Integer> duplicatedCoinToCountMap = new HashMap<>(coinToCountMap);
            if (creditCoinList == null) {
                String errorMessage = ERROR_MESSAGE_CREDIT_COIN_LIST_REQUIRED;
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            if (debitCoinList == null) {
                String errorMessage = ERROR_MESSAGE_DEBIT_COIN_LIST_REQUIRED;
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            Logger.info(TAG, "Old Coin State: " + coinToCountMap);

            int newCoinCount;

            //Add credit coins
            for (Double coin : creditCoinList) {
                validateCoinSupport(coin);
                newCoinCount = coinToCountMap.get(coin) + 1;
                coinToCountMap.put(coin, newCoinCount);
            }

            Logger.info(TAG, "Updated (CREDIT) Coin State: " + coinToCountMap);

            //Remove debit coins
            for (Double coin : debitCoinList) {
                validateCoinSupport(coin);
                newCoinCount = coinToCountMap.get(coin) - 1;
                if (newCoinCount < 0) {
                    String errorMessage = ERROR_MESSAGE_COIN_COUNT_CANNOT_GO_TO_NEGATIVE;
                    Logger.error(TAG, errorMessage);
                    coinToCountMap = duplicatedCoinToCountMap; //reverse operation
                    throw new IllegalStateException(errorMessage);
                }
                coinToCountMap.put(coin, newCoinCount);
            }

            Logger.info(TAG, "Updated (DEBIT) Coin State: " + coinToCountMap);
        }
    }

    private Collection<Double> prepareAllAvailableCoins() {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            List<Double> coinCombination = new ArrayList<>();
            for (Map.Entry<Double, Integer> coin : coinToCountMap.entrySet()) {
                for (int i = 0; i < coin.getValue(); i++) {
                    coinCombination.add(coin.getKey());
                }
            }
            return coinCombination;
        }
    }

    private BigDecimal computeAvailableCoinTotalValue(@NotNull final Map<Double, Integer> inputMap) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            if (inputMap == null) {
                String errorMessage = "Valid input Map is required for this operation";
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<Double, Integer> coin : inputMap.entrySet()) {
                total = total.add(BigDecimal.valueOf(coin.getKey() * coin.getValue()));
            }
            return total.setScale(2, RoundingMode.HALF_UP);
        }
    }


    private List<Double> generateChangeCoinCombination(final BigDecimal amount) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            if (amount == null) {
                String errorMessage = "Valid amount is required for this operation";
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                //Amount supplied is less than or equals ZERO, return empty list
                return new ArrayList<>();
            }

            //Strategy: deplete-highest-denomination-first approach
            Map<Double, Integer> nonZeroBalanceCoins = getAllNonZeroBalanceCoins(coinToCountMap);
            BigDecimal remainingBalance = toDp(amount, 2);
            List<Double> coinCombination = new ArrayList<>();
            int numberOfCoinValueDeduceable;
            int reduceCoinCountBy;
            boolean breakLoop;
            int initialChangeCombinationSize;


            //While remainingBalance is MORE THAN zero
            while (compare(remainingBalance, BigDecimal.ZERO) > 0) {
                nonZeroBalanceCoins = getAllNonZeroBalanceCoins(nonZeroBalanceCoins);
                nonZeroBalanceCoins = sortMapByKeyValueInDescingOrder(nonZeroBalanceCoins); //bubble up higher coin denomination

                initialChangeCombinationSize = coinCombination.size();
                for (Map.Entry<Double, Integer> coin : nonZeroBalanceCoins.entrySet()) {

                    if (compare(toDp(BigDecimal.valueOf(coin.getKey()), 2), remainingBalance) > 0) {
                        //coin denomination is more than the remaining balance, pass
                        continue;
                    }

                    //How many of the coin denomination can we get from the remaining balance?
                    numberOfCoinValueDeduceable = (remainingBalance.divide(toDp(BigDecimal.valueOf(coin.getKey()), 2), RoundingMode.HALF_UP)).intValue();
                    if (numberOfCoinValueDeduceable <= 0) {
                        continue;
                    }

                    reduceCoinCountBy = Math.min(numberOfCoinValueDeduceable, coin.getValue());

                    for (int i = 0; i < reduceCoinCountBy; i++) {
                        coinCombination.add(coin.getKey());
                    }

                    remainingBalance = remainingBalance.subtract(toDp(BigDecimal.valueOf(coin.getKey() * reduceCoinCountBy), 2));
                    nonZeroBalanceCoins.put(coin.getKey(), coin.getValue() - reduceCoinCountBy);

                    //Coin combination generated, return
                    if (compare(remainingBalance, BigDecimal.ZERO) == 0) {
                        return coinCombination;
                    }
                }

                if (initialChangeCombinationSize == coinCombination.size()) {
                    String errorMessage = ERROR_MESSAGE_AVAILABLE_COIN_S_CANNOT_PROVIDE_CHANGE;
                    Logger.error(TAG, errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
            }

            return coinCombination;
        }
    }

    private void throwInsufficientAvailableCoinForChange() {
        String errorMessage = ERROR_MESSAGE_INSUFFICIENT_COIN_S_FOR_CHANGE;
        Logger.error(TAG, errorMessage);
        throw new IllegalStateException(errorMessage);
    }

    private Map<Double, Integer> sortMapByKeyValueInDescingOrder(Map<Double, Integer> unsortedMap) {
        Map<Double, Integer> reversedSortedMap = new TreeMap<Double, Integer>(Collections.reverseOrder());
        reversedSortedMap.putAll(unsortedMap);
        return reversedSortedMap;
    }

    private Map<Double, Integer> getAllNonZeroBalanceCoins(@NotNull final Map<Double, Integer> coinMap) {
        Map<Double, Integer> resultMap = new HashMap<>();
        for (Map.Entry<Double, Integer> coin : coinMap.entrySet()) {
            if (coin.getValue() > 0) {
                resultMap.put(coin.getKey(), coin.getValue());
            }
        }
        return resultMap;
    }

    private void validateCoinSupport(double coinValue) {
        if (!coinToCountMap.containsKey(coinValue)) {
            String errorMessage = ERROR_MESSAGE_COIN_NOT_SUPPORTED;
            Logger.error(TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
