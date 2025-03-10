package com.fooddelivery.chefs.security;

import com.fooddelivery.chefs.model.Customer;
import com.fooddelivery.chefs.repository.CustomerRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Component
public class DeviceIdFilter extends OncePerRequestFilter {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String deviceId = request.getHeader("X-Device-Id");

        if (deviceId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "X-Device-Id header is required");
            return;
        }

        // Поиск заказчика по device_id
        Customer customer = customerRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid device ID"));

        // Создание аутентифицированного пользователя
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customer, null, List.of(new SimpleGrantedAuthority("CUSTOMER")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
