package manager;

public interface CoinManager {
    void setCoinAvailableCount(final double coinValue, final int newAvailableCount);
    int getCoinAvailableCount(final double coinValue);
}
