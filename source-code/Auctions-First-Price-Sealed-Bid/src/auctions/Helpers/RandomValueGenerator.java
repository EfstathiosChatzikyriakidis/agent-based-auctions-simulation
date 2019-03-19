package auctions.Helpers;

import java.util.Random;

public class RandomValueGenerator {
    private final Random rng = new Random();

    public int getNumber (int min, int max) {
        return rng.nextInt((max - min) + 1) + min;
    }
}