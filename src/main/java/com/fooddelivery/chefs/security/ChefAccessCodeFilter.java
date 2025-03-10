package com.fooddelivery.chefs.security;

import com.fooddelivery.chefs.model.Chef;
import com.fooddelivery.chefs.repository.ChefRepository;
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
public class ChefAccessCodeFilter extends OncePerRequestFilter {
    @Autowired
    private ChefRepository chefRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String accessCode = request.getHeader("X-Access-Code");
        String deviceId = request.getHeader("X-Device-Id");

        if (accessCode == null || deviceId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "X-Access-Code and X-Device-Id headers are required");
            return;
        }

        // Поиск повара по access_code
        Chef chef = chefRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access code"));

        // Проверка статуса is_working
        if (!chef.getIsWorking()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Chef is not working");
            return;
        }

        // Создание аутентифицированного пользователя
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(chef, null, List.of(new SimpleGrantedAuthority("CHEF")));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
