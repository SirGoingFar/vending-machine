package manager;

import util.Logger;

import java.util.Collection;

public class DefaultCoinManager implements CoinManager {

    //#region class constants
    //#end region

    private final Collection<Double> allowedCoinList;

    //#region Class Constructor
    private DefaultCoinManager() {
        //Added in case an instantiation is attempted using reflection
        throw new UnsupportedOperationException("Cannot instantiate class with this constructor");
    }

    public DefaultCoinManager(Collection<Double> allowedCoinList) {
        this.allowedCoinList = allowedCoinList;
        Logger.info(this.getClass(), "List of allowed coins: " + allowedCoinList);
    }
    //#endregion

}
