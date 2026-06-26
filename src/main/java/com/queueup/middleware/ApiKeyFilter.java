//package com.queueup.middleware;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class ApiKeyFilter extends OncePerRequestFilter {
//
//    @Value("${ADMIN_API_KEY:default-key}")
//    private String adminApiKey;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        // Only protect /api/admin/** endpoints
//        if (request.getRequestURI().startsWith("/api/admin")) {
//            String apiKey = request.getHeader("X-API-Key");
//
//            if (apiKey == null || !apiKey.equals(adminApiKey)) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.setContentType("application/json");
//                response.getWriter().write("{\"error\":\"Invalid or missing API key\"}");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}