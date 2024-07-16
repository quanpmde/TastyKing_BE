package com.example.TastyKing.mapper;

import com.example.TastyKing.dto.request.PaymentRequest;
import com.example.TastyKing.dto.response.PaymentResponse;
import com.example.TastyKing.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toPayment(PaymentRequest request);

    @Mapping(target = "paymentUrl", ignore = true) // Ignore during automatic mapping
    PaymentResponse toPaymentResponse(Payment payment);

    // Add a method to manually set paymentUrl
    default PaymentResponse toPaymentResponseWithUrl(Payment payment, String paymentUrl) {
        PaymentResponse response = toPaymentResponse(payment);
        response.setPaymentUrl(paymentUrl);
        return response;
    }
}
