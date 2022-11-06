package com.api.mobigenz_be.DTOs;
import java.time.LocalDateTime;
import java.util.List;

import com.api.mobigenz_be.entities.CartItem;
import com.api.mobigenz_be.entities.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
	private Integer id;
    private Double totalMoney;
    private Integer itemsAmount;
    private LocalDateTime mtime;
    private Customer customer;
    private List<CartItem> cartItems;
}
