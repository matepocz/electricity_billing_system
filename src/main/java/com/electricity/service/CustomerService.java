package com.electricity.service;

import com.electricity.dto.LoginRequest;
import com.electricity.dto.LoginResponse;
import com.electricity.dto.NewCustomerRequest;
import com.electricity.model.Address;
import com.electricity.model.Customer;
import com.electricity.repository.AddressRepository;
import com.electricity.repository.CustomerRepository;
import com.electricity.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.KeyException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
public class CustomerService {

    private static final Random random = new Random();
    private final CustomerRepository customerRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final ContractService contractService;
    private final AddressRepository addressRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder bCryptPasswordEncoder,
                           ContractService contractService, AddressRepository addressRepository,
                           JwtProvider jwtProvider, AuthenticationManager authenticationManager) {
        this.customerRepository = customerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.contractService = contractService;
        this.addressRepository = addressRepository;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Attempts to reqister the user.
     *
     * @param newCustomerRequest A DTO containing the required details
     * @return A boolean.
     */
    public boolean signup(NewCustomerRequest newCustomerRequest) {
        Customer customer = new Customer();

        customer.setFirstName(newCustomerRequest.getFirstName());
        customer.setMiddleName(newCustomerRequest.getMiddleName());
        customer.setLastName(newCustomerRequest.getLastName());
        customer.setCustomerNumber(generateCustomerNumber());
        customer.setEmailAddress(newCustomerRequest.getEmailAddress());
        Address address = createNewAddressForCustomer(newCustomerRequest, customer);
        customer.setAddress(address);
        customer.setPassword(bCryptPasswordEncoder.encode(newCustomerRequest.getPassword()));
        customer.setContract(contractService.createNewContract(customer));
        customer.setPhoneNumber(newCustomerRequest.getPhoneNumber());
        customer.setRole("ROLE_CUSTOMER");
        customer.setRegistered(LocalDateTime.now());
        customerRepository.save(customer);
        return true;
    }

    private Address createNewAddressForCustomer(NewCustomerRequest newCustomerRequest, Customer customer) {
        Address address = new Address();
        address.setCity(newCustomerRequest.getCity());
        address.setStreet(newCustomerRequest.getStreet());
        address.setNumber(newCustomerRequest.getNumber());
        address.setFloor(newCustomerRequest.getFloor());
        address.setDoor(newCustomerRequest.getDoor());
        address.setCustomer(customer);
        address.setCountry(newCustomerRequest.getCountry());
        return addressRepository.save(address);
    }

    private String generateCustomerNumber() {
        StringBuilder generatedNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            generatedNumber.append(random.nextInt(10));
        }
        return isCustomerNumberExists(generatedNumber.toString()) ?
                generateCustomerNumber() : generatedNumber.toString();
    }

    private boolean isCustomerNumberExists(String customerNumber) {
        Optional<Customer> customerByCustomerNumber = customerRepository.findCustomerByCustomerNumber(customerNumber);
        return customerByCustomerNumber.isPresent();
    }

    /**
     * Attempts to log in the user.
     *
     * @param loginRequest A DTO containing the required details.
     * @return A LoginResponse DTO that contains a JWT token and the user's email address.
     */
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<Customer> userByEmailAddress =
                customerRepository.findCustomerByEmailAddress(loginRequest.getEmailAddress());
        if (userByEmailAddress.isPresent()) {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmailAddress(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            LoginResponse response = new LoginResponse();
            try {
                response.setAuthenticationToken(jwtProvider.generateToken(authenticate));
                response.setEmailAddress(loginRequest.getEmailAddress());
                return response;
            } catch (KeyException e) {
                e.printStackTrace();
            }
        }
        throw new UsernameNotFoundException("User not found!");
    }
}
