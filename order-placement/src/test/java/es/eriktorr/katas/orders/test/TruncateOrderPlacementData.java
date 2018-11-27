package es.eriktorr.katas.orders.test;

public class TruncateOrderPlacementData extends TruncateDataExtension {

    private static final String DATASOURCE_URL = "jdbc:postgresql://localhost:5432/test_order_placement";

    public TruncateOrderPlacementData() {
        super(DATASOURCE_URL);
    }

}