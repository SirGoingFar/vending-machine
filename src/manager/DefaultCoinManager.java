package manager;

import com.sun.istack.internal.NotNull;
import util.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    private void validateCoinSupport(double coinValue) {
        if (!coinToCountMap.containsKey(coinValue)) {
            String errorMessage = "Coin not supported";
            Logger.error(TAG, errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
