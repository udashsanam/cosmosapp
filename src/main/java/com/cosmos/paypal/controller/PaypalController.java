package com.cosmos.paypal.controller;

import com.cosmos.payment.entity.PaymentDetail;
import com.cosmos.payment.repo.PaymentDetailRepo;
import com.cosmos.paypal.service.PaypalService;
import com.paypal.orders.Order;
import com.paypal.orders.OrderActionRequest;
import com.paypal.orders.OrdersCaptureRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("paypal")
@RequiredArgsConstructor
public class PaypalController {

    private final PaypalService paypalService;

    private final PaymentDetailRepo paymentDetailRepo;

    @PostMapping("/capture/{orderId}")
    public String capturePayment(@PathVariable String orderId) throws Exception {
        OrdersCaptureRequest request =
                new OrdersCaptureRequest(orderId);
        request.requestBody(new OrderActionRequest());
        var response = paypalService.getClient().execute(request);
        return response.result().status();
    }

    @GetMapping("/success")
    public String success(
            @RequestParam("token") String orderId
    ) throws Exception {

        OrdersCaptureRequest request =
                new OrdersCaptureRequest(orderId);

        request.requestBody(
                new OrderActionRequest()
        );

        var response =
                paypalService.getClient().execute(request);

        Order order = response.result();
        if("COMPLETED".equalsIgnoreCase(order.status())) {
           PaymentDetail paymentDetail =  paymentDetailRepo.findByPaymentCode(order.id());
           paymentDetail.setIsSuccess(Boolean.TRUE);
           paymentDetail.setIsPaypal(Boolean.TRUE);
           paymentDetailRepo.save(paymentDetail);
        }

        return order.status();
    }

    @GetMapping("/cancel")
    public String cancel() {

        return "Payment Cancelled";
    }

}
