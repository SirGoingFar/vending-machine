package manager;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;
import java.util.Collection;

public interface CoinManager {
    void setCoinAvailableCount(final double coinValue, final int newAvailableCount);

    int getCoinAvailableCount(final double coinValue);

    boolean areCoinsSupported(@NotNull final Collection<Double> coinList);

    Collection<Double> getPossibleCoinCombination(@NotNull final BigDecimal amount);

    void balanceCoins(Collection<Double> creditCoinList, Collection<Double> debitCoinList);
}
