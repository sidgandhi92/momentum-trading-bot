package ca.mcgill.dp2.group52.enums;

/**
 * Created by sgandhi on 1/24/16.
 */
public enum PriceType {
    BID(1), ASK(2), LAST(4), HIGH(6), LOW(7), CLOSE(9);

    private int value;

    private PriceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }


}
