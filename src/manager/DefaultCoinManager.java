package manager;

import com.sun.istack.internal.NotNull;
import util.Logger;

import java.math.BigDecimal;
import java.util.*;

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
                throw new IllegalArgumentException("Coin list is required");
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
    public Collection<Double> getPossibleCoinCombinationFor(@NotNull final BigDecimal amount) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            if (amount == null || amount.doubleValue() < 0.0) {
                String errorMessage = "Invalid amount provided";
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            if (amount.doubleValue() == 0.0) {
                return new ArrayList<>();
            }

            List<Double> combinationList = new ArrayList<>();
            //Throw IllegalStateException if change combination does not exist
            //"Change currently unavailable"
            //Check if change is available (add coin list items together, less the product  amount... then compute change)

            return combinationList;
        }
    }

    //Todo: Trade off - we could have used the customer coin as part of the change computation. But that doesn't follow a proper accounting process
    //What if after adding the customer money to the available coin and another customer purchase hijack it in the process and we end up settling that
    //the other customer with the first customer's money
    @Override
    public void balanceCoins(final Collection<Double> creditCoinList, final Collection<Double> debitCoinList) {
        synchronized (COIN_ACCESS_MONITOR_OBJECT) {
            if (creditCoinList == null || creditCoinList.isEmpty()) {
                String errorMessage = "Credit coin list is required for this operation";
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            if (debitCoinList == null || debitCoinList.isEmpty()) {
                String errorMessage = "Debit coin list is required for this operation";
                Logger.error(TAG, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            //Remove debit coins
            int newCoinCount;
            for (Double coin : debitCoinList) {
                validateCoinSupport(coin);
                newCoinCount = coinToCountMap.get(coin) - 1;
                coinToCountMap.put(coin, newCoinCount);
            }

            //Add credit coins
            for (Double coin : creditCoinList) {
                validateCoinSupport(coin);
                newCoinCount = coinToCountMap.get(coin) + 1;
                coinToCountMap.put(coin, newCoinCount);
            }
        }
    }

    private void validateCoinSupport(double coinValue) {
        if (!coinToCountMap.containsKey(coinValue)) {
            String errorMessage = "Coin not supported";
            Logger.error(TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
