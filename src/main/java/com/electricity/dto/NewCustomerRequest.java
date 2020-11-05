package com.electricity.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewCustomerRequest {

    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private String password;
    private String phoneNumber;

    private String city;
    private String street;
    private String number;
    private String floor;
    private String door;
    private String country;
}
