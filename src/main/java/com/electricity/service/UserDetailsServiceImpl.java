package com.electricity.service;

import com.electricity.model.Customer;
import com.electricity.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Autowired
    public UserDetailsServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {
        Customer customer = customerRepository.findCustomerByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("No user found (" + emailAddress + ")"));
        return new org.springframework.security.core.userdetails.User(
                customer.getEmailAddress(),
                customer.getPassword(),
                customer.isEnabled(),
                true,
                true,
                customer.isActive(),
                getAuthorities(customer.getRole())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}
