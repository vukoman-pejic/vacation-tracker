package com.rbt.vacationtracker.service;

import com.rbt.vacationtracker.entity.EmployeeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final EmployeeService employeeService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        EmployeeEntity employee = employeeService.findJwtUserByEmail(email);

        return new User(employee.getUsername(), employee.getPassword(), employee.isEnabled(), true, true, true, employee.getAuthorities());
    }

}
