package es.eriktorr.katas.orders.domain.problem;

import es.eriktorr.katas.orders.domain.common.SingleValue;
import lombok.Value;
import lombok.val;

import javax.validation.ConstraintViolation;

@Value
public class Violation {

    private final String field;
    private final String message;

    public static String fieldFrom(ConstraintViolation constraintViolation) {
        val leafBean = constraintViolation.getLeafBean();
        return leafBean instanceof SingleValue
                ? constraintViolation.getPropertyPath().iterator().next().getName()
                : constraintViolation.getPropertyPath().toString();
    }

}