package com.gutti.store.configs;

import lombok.*;

import java.util.UUID;

/**
 * @author Ivan Alban
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppRequestContext {

    private static final ThreadLocal<AppRequestContext> context = new ThreadLocal<>();

    private UUID organizationId;

    public static AppRequestContext get() {
        return context.get();
    }

    public static void set(AppRequestContext src) {
        AppRequestContext.context.set(src);
    }

    public static void clear() {
        AppRequestContext.context.remove();
    }
}
