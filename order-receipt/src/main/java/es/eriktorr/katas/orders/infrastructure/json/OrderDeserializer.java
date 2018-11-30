package es.eriktorr.katas.orders.infrastructure.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
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
import java.time.LocalDateTime;
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
        val orderId = new OrderId(textOrEmptyFrom(jsonNode, ORDER_ID_FIELD));
        val storeId = new StoreId(textOrEmptyFrom(jsonNode, ORDER_STORE_FIELD));
        val orderReference = new OrderReference(textOrEmptyFrom(jsonNode, ORDER_REFERENCE_FIELD));
        val createdAt = objectOrNullFrom(jsonNode, jsonParser.getCodec(), ORDER_CREATED_AT_FIELD, LocalDateTime.class);
        return new Order(orderId, storeId, orderReference, createdAt);
    }

    private String textOrEmptyFrom(JsonNode jsonNode, String field) {
        return Optional.ofNullable(jsonNode.get(field))
                .orElse(EMPTY_NODE)
                .asText();
    }

    private <T> T objectOrNullFrom(JsonNode jsonNode, ObjectCodec objectCodec, String field, Class<T> type) throws IOException {
        val fieldJsonNode = jsonNode.get(field);
        if (fieldJsonNode != null) {
            val jsonParser = fieldJsonNode.traverse();
            jsonParser.setCodec(objectCodec);
            return jsonParser.readValueAs(type);
        }
        return null;
    }

}