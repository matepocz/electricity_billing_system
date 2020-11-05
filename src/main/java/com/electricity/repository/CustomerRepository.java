package com.electricity.repository;

import com.electricity.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerByEmailAddress(String emailAddress);

    Optional<Customer> findCustomerByCustomerNumber(String customerNumber);
}
