package com.gutti.store.filters;

import com.gutti.store.configs.AppRequestContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Ivan Alban
 */
@Slf4j
@Component
public class MTAFilter extends OncePerRequestFilter implements Ordered {

    /**
     * Do not remove or change this constant, later we will enable the multi-tenant support
     * so the final value will come from x-organization header
     */
    public static final String DEFAULT_ORGANIZATION_ID = "188b524e-e6eb-4907-8067-2ed0eb423e5c";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        UUID organizationId = UUID.fromString(DEFAULT_ORGANIZATION_ID);

        log.info("TENANT: {} ", organizationId);

        AppRequestContext.set(AppRequestContext.builder().organizationId(organizationId).build());

        chain.doFilter(request, response);

        AppRequestContext.clear();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
