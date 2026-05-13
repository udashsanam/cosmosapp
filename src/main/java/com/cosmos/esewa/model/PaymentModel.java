package com.cosmos.esewa.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class PaymentModel {

    private Long id;

    private String packageName;

    private Double amount;

    private int questionCount;

    private int discount;

    private Double finalAmount;
}
