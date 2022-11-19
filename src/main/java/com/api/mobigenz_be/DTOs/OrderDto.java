package com.api.mobigenz_be.DTOs;

import com.api.mobigenz_be.entities.Customer;
import com.api.mobigenz_be.entities.Transaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Integer id;

    private CustomerDTO customerDTO;

    private TransactionDto transactionDto;

    private String recipientName;

    private String recipientPhone;

    private String recipientEmail;

    private String address;

    private Double totalMoney;

    private Double shipFee;

    private Double goodsValue;

    private Double checkout;

    private Integer quantity;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime shipDate;

    private String carrier;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ctime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime mtime;

    private Integer payStatus;

    private Integer orderStatus;

    private List<OrderDetailDto> orderDetailDtos;

    private String note;

    private String cancelNote;

}
