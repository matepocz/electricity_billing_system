package com.electricity.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "customer")
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "customer_number", length = 30)
    private String customerNumber;

    @NotNull
    @Column(name = "first_name", length = 60)
    private String firstName;

    @Column(name = "middle_name", length = 60)
    private String middleName;

    @NotNull
    @Column(name = "last_name", length = 60)
    private String lastName;

    @Email
    @NotNull
    @Column(name = "email_address", unique = true)
    private String emailAddress;

    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne(targetEntity = Address.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "address", referencedColumnName = "id")
    @JsonManagedReference
    private Address address;

    @OneToOne(targetEntity = Contract.class, orphanRemoval = true)
    @JoinColumn(name = "contract")
    @JsonManagedReference
    private Contract contract;

    @Column(name = "registered")
    private LocalDateTime registered;

    @Column(name = "role")
    private String role;

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "is_enabled")
    private boolean enabled;
}
