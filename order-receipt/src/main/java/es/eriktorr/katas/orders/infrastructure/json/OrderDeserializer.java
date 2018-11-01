package es.eriktorr.katas.orders.infrastructure.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderId;
import es.eriktorr.katas.orders.domain.model.OrderReference;
import es.eriktorr.katas.orders.domain.model.StoreId;
import lombok.val;

import java.io.IOException;
import java.util.Optional;

import static es.eriktorr.katas.orders.infrastructure.json.OrderFields.*;

public class OrderDeserializer extends StdDeserializer<Order> {

    private static final JsonNode EMPTY_NODE = new TextNode("");

    public OrderDeserializer() {
        super(Order.class);
    }

    @Override
    public Order deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        val orderId = new OrderId(valueOrEmptyFrom(jsonNode, ORDER_ID_FIELD));
        val storeId = new StoreId(valueOrEmptyFrom(jsonNode, ORDER_STORE_FIELD));
        val orderReference = new OrderReference(valueOrEmptyFrom(jsonNode, ORDER_REFERENCE_FIELD));
        return new Order(orderId, storeId, orderReference);
    }

    private String valueOrEmptyFrom(JsonNode jsonNode, String field) {
        return Optional.ofNullable(jsonNode.get(field))
                .orElse(EMPTY_NODE)
                .asText();
    }

}