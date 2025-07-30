package digitalwallet.model;

public enum Currency {
    TRY, USD, EUR;

    public static Currency fromString(String value) {
        for (Currency c : Currency.values()) {
            if (c.name().equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid currency: " + value);
    }
}
