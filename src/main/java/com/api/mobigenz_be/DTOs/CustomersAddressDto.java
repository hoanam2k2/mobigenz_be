package com.api.mobigenz_be.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomersAddressDto {
    private Integer id;
    private CustomerDTO customerId;
    private Integer paymentMethod;
    private Instant ctime;
    private String note;
}
