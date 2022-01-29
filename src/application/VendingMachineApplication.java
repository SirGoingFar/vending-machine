package application;

import java.util.HashSet;
import java.util.Set;

public class VendingMachineApplication {
    public static void main(String[] args) {
        Set<Double> coinList = new HashSet<>();
        coinList.add(0.1);
        coinList.add(0.2);
        coinList.add(0.5);
        coinList.add(1.0);
        VendingMachine vm = new VendingMachine(10, coinList);
    }
}
