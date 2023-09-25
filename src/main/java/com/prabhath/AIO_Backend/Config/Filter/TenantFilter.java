package com.prabhath.AIO_Backend.Config.Filter;

import com.prabhath.AIO_Backend.Model.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Order(1)
public class TenantFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String subdomain = extractSubdomain(req.getRequestURL().toString());
        String tenantName = req.getHeader("X-TenantID");

        if (subdomain != null && !subdomain.isEmpty()) {
            // If subdomain is available, use it as the tenant identifier
            TenantContext.setCurrentTenant(subdomain);
            System.out.println("Tenant Set ");
        } else if (tenantName != null && !tenantName.isEmpty()) {
            // If subdomain is not available but tenantName is, use it as the tenant identifier
            System.out.println("Tenant Set ");
            TenantContext.setCurrentTenant(tenantName);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.setCurrentTenant("");
            System.out.println("Tenant Clear ");
        }

    }
    private String extractSubdomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();

            // Split the host into parts based on the dot (.) separator
            String[] parts = host.split("\\.");

            // Check if there are at least two parts (subdomain and domain)
            if (parts.length >= 2) {
                // Return the first part as the subdomain
                return parts[0];
            } else {
                // No subdomain found, return an empty string or handle accordingly
                return "";
            }
        } catch (URISyntaxException e) {
            // Handle URI syntax exception
            e.printStackTrace();
            return "";
        }
    }
}
