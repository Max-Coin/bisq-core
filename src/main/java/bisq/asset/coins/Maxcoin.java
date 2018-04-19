package bisq.asset.coins;

import bisq.asset.Coin;
import bisq.asset.DefaultAddressValidator;

public class Maxcoin extends Coin {

    public Maxcoin() {
        super("Maxcoin", "MAX", new DefaultAddressValidator());
    }
}



