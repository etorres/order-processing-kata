package es.eriktorr.katas.orders.infrastructure.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import es.eriktorr.katas.orders.domain.model.OrderReference;
import es.eriktorr.katas.orders.domain.model.OrderRequest;
import lombok.val;

import java.io.IOException;
import java.util.Optional;

import static es.eriktorr.katas.orders.infrastructure.json.OrderFields.ORDER_REFERENCE_FIELD;

public class OrderRequestDeserializer extends StdDeserializer<OrderRequest> {

    private static final JsonNode BLANK_NODE = new TextNode(" ");

    public OrderRequestDeserializer() {
        super(OrderRequest.class);
    }

    @Override
    public OrderRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        val orderReference = new OrderReference(referenceFrom(jsonNode));
        return new OrderRequest(orderReference);
    }

    private String referenceFrom(JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode.get(ORDER_REFERENCE_FIELD))
                .orElse(BLANK_NODE)
                .asText();
    }

}