package com.example.payment_service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @PostMapping("/create-payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
        Stripe.apiKey = "your-stripe-secret-key";

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(paymentRequest.getAmount())
                        .setCurrency("usd")
                        .addPaymentMethodType(
                                String.valueOf(List.of("card"))
                        )
                        .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            return ResponseEntity.ok(intent.getClientSecret());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
