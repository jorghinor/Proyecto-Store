package com.gutti.store.configs;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Ivan Alban
 */
@Component
public class TenantResolver implements CurrentTenantIdentifierResolver<UUID> {

    @Override
    public UUID resolveCurrentTenantIdentifier() {

        if (null != AppRequestContext.get()) {
            return AppRequestContext.get().getOrganizationId();
        }

        //At deployment time hibernate validates the repository queries and to complete that process is required a fake tenant
        return UUID.randomUUID();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
