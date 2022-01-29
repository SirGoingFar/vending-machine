package manager;

import util.Logger;

public class DefaultProductSlotManager implements ProductSlotManager{

    //#region class constants
    //#end region

    private final int PRODUCT_SLOT_SIZE;

    //#region Class Constructor
    private DefaultProductSlotManager() {
        //Added in case an instantiation is attempted using reflection
        throw new UnsupportedOperationException("Cannot instantiate class with this constructor");
    }

    public DefaultProductSlotManager(int productSlotSize) {
        this.PRODUCT_SLOT_SIZE = productSlotSize;
        Logger.info(this.getClass(), "Product Slot Size = " + productSlotSize);
    }
    //#endregion
}
