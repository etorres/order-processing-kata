package es.eriktorr.katas.orders.test;

public class TruncateOrderReceiptData extends TruncateDataExtension {

    private static final String DATASOURCE_URL = "jdbc:postgresql://localhost:5432/test_order_receipt";

    public TruncateOrderReceiptData() {
        super(DATASOURCE_URL);
    }

}