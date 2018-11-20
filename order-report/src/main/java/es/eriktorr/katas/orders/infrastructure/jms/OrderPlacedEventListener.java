package es.eriktorr.katas.orders.infrastructure.jms;

import es.eriktorr.katas.orders.domain.model.Order;
import es.eriktorr.katas.orders.domain.model.OrderPlacedEvent;
import es.eriktorr.katas.orders.infrastructure.database.OrdersRepository;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jms.annotation.JmsListener;

@Slf4j
public class OrderPlacedEventListener {

    private final OrdersRepository ordersRepository;

    public OrderPlacedEventListener(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @JmsListener(destination = "${order.placed.event.queue.name}", containerFactory = "jmsListenerContainerFactory")
    public void process(OrderPlacedEvent orderPlacedEvent) {
        Try.ofSupplier(() -> {
            val order = orderPlacedEvent.getValue();
            ordersRepository.save(order);
            return order;
        }).onSuccess(this::writeSuccessfulLogMessage).onFailure(this::writeFailedLogMessage);
    }

    private void writeSuccessfulLogMessage(Order order) {
        log.info("Order successfully projected: " + order);
    }

    private void writeFailedLogMessage(Throwable error) {
        log.info("Failed to process event", error);
    }

}