package manager;

import com.sun.istack.internal.NotNull;
import model.Product;

import java.math.BigDecimal;

public interface ProductSlotManager {
    void updateSlotProductInventorySize(final int productSlotIndex, final long newInventorySize);

    long getSlotProductInventorySize(final int productSlotIndex);

    void setSlotProductPrice(final int productSlotIndex, @NotNull final BigDecimal productPrice);

    BigDecimal getSlotProductPrice(final int productSlotIndex);

    void addProductToSlot(final Product product);

    void removeProductAtSlot(final int productSlotIndex);
}
