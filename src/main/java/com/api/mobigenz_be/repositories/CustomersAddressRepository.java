package com.api.mobigenz_be.repositories;

import com.api.mobigenz_be.entities.Customer;
import com.api.mobigenz_be.entities.CustomersAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomersAddressRepository extends JpaRepository<CustomersAddress, Integer> {


//    @Query("Select ca from CustomersAddress as ca \n" +
//            " join Customer as c on ca.customerId= c.id where c.customerName=:customerName")
//    Page<CustomersAddress> findByCustomerName(@Param("customerName") String customerName, Pageable pageable);

    @Query("Select ca from CustomersAddress as ca \n" +
            " join Customer as c on ca.customerId= c.id where c.customerName=:customerName")
    List<CustomersAddress> findByCustomerName(@Param("customerName") String customerName);
}