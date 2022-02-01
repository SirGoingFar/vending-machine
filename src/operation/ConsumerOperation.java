package operation;

import java.math.BigDecimal;
import java.util.Collection;

public interface ConsumerOperation {
    BigDecimal getProductPrice(final int productSlotId);

    Collection<Double> buyProduct(final int productSlotId, final Collection<Double> coinCollection);
}
