package manager;

import com.sun.istack.internal.NotNull;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Contract for the component that manages the coin type(s) the vending machine supports
 */
public interface CoinManager {
    /**
     * Set the available count of a supported coin
     *
     * @param coinValue         the unit value of the coin
     * @param newAvailableCount the number of {@param coinValue} available after operation
     */
    void setCoinAvailableCount(final double coinValue, final int newAvailableCount);

    /**
     * Get the number of available unit(s) of a coin type
     *
     * @param coinValue value of the coin
     * @return available count of {@param coinValue}
     */
    int getCoinAvailableCount(final double coinValue);

    /**
     * Check if all coins in {@param coinList} are supported by the vending machine
     *
     * @param coinList coin(s) to check
     * @return true if all coins are supported, false if any of the coins is not supported
     */
    boolean areCoinsSupported(@NotNull final Collection<Double> coinList);

    /**
     * Generate the combination of coin(s) that sum up to {@param amount} from available coin(s)
     *
     * @param amount value whose coin combination is generated
     * @return coin combination that sum up to {@param amount} from the coin(s) available in the vending machine
     */
    Collection<Double> getPossibleCoinCombination(@NotNull final BigDecimal amount);

    /**
     * Balances the supported coin(s) count based on {@param creditCoinList} and {@param debitCoinList}.
     * Number of occurrence of coins in the two lists increment/decrement the count of the corresponding coin in the vending machine.
     *
     * @param creditCoinList list of coin whose number of occurrence increment the count of the corresponding coin in the vending machine
     *
     * @param debitCoinList  list of coin whose number of occurrence decrement the count of the corresponding coin in the vending machine
     */
    void balanceCoins(Collection<Double> creditCoinList, Collection<Double> debitCoinList);
}
