package application;

import util.Logger;

import java.math.BigDecimal;
import java.util.*;

public class VendingMachineApplication {
    public static void main(String[] args) {
        Set<Double> coinList = new HashSet<>();
        coinList.add(0.1);
        coinList.add(0.2);
        coinList.add(0.5);
        coinList.add(1.0);
        VendingMachine vm = new VendingMachine(10, coinList);

        //Add initial coins
        vm.setCoinAvailableCount(0.1, 3);
        vm.setCoinAvailableCount(0.2, 2);
        vm.setCoinAvailableCount(0.5, 7);
        vm.setCoinAvailableCount(1.0, 1);

        //Add product
        vm.addProductToSlot(BigDecimal.valueOf(0.5), 2);

        //Buy product
        Collection<Double> changeList = vm.buyProduct(0, Arrays.asList(1.0,0.5,0.2));
        Logger.info("Change: " + changeList);
    }
}
