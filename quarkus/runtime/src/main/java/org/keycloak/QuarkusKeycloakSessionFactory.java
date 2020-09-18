package org.keycloak;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.keycloak.common.Logger;
import org.keycloak.provider.KeycloakDeploymentInfo;
import org.keycloak.provider.ProviderFactory;
import org.keycloak.provider.ProviderLoader;
import org.keycloak.provider.ProviderManagerRegistry;
import org.keycloak.provider.Spi;
import org.keycloak.services.DefaultKeycloakSessionFactory;
import org.keycloak.services.ServicesLogger;
import org.keycloak.services.resources.admin.permissions.AdminPermissions;

public final class QuarkusKeycloakSessionFactory extends DefaultKeycloakSessionFactory {

    private static final Logger logger = Logger.getLogger(QuarkusKeycloakSessionFactory.class);

    public static QuarkusKeycloakSessionFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new QuarkusKeycloakSessionFactory();
        }

        return INSTANCE;
    }

    public static void setInstance(QuarkusKeycloakSessionFactory instance) {
        INSTANCE = instance;
    }

    private static QuarkusKeycloakSessionFactory INSTANCE;
    private final Boolean reaugmented;
    private final Map<Spi, Set<Class<? extends ProviderFactory>>> factories;

    public QuarkusKeycloakSessionFactory(Map<Spi, Set<Class<? extends ProviderFactory>>> factories, Boolean reaugmented) {
        this.factories = factories;
        this.reaugmented = reaugmented;
    }

    private QuarkusKeycloakSessionFactory() {
        reaugmented = false;
        factories = Collections.emptyMap();
    }

    @Override
    public void init() {
        serverStartupTimestamp = System.currentTimeMillis();
        spis = factories.keySet();

        for (Spi spi : spis) {
            for (Class<? extends ProviderFactory> factoryClazz : factories.get(spi)) {
                ProviderFactory factory = lookupProviderFactory(factoryClazz);
                Config.Scope scope = Config.scope(spi.getName(), factory.getId());

                if (isEnabled(factory, scope)) {
                    factory.init(scope);

                    if (spi.isInternal() && !isInternal(factory)) {
                        ServicesLogger.LOGGER.spiMayChange(factory.getId(), factory.getClass().getName(), spi.getName());
                    }

                    factoriesMap.computeIfAbsent(spi.getProviderClass(), aClass -> new HashMap<>()).put(factory.getId(),
                            factory);
                } else {
                    logger.debugv("SPI {0} provider {1} disabled", spi.getName(), factory.getId());
                }
            }

            checkProviders(spi);
        }

        for (Map<String, ProviderFactory> f : factoriesMap.values()) {
            for (ProviderFactory factory : f.values()) {
                factory.postInit(this);
            }
        }

        AdminPermissions.registerListener(this);
        // make the session factory ready for hot deployment
        ProviderManagerRegistry.SINGLETON.setDeployer(this);
    }

    private ProviderFactory lookupProviderFactory(Class<? extends ProviderFactory> factoryClazz) {
        ProviderFactory factory;

        try {
            factory = factoryClazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return factory;
    }

    private void checkProviders(Spi spi) {
        String defaultProvider = Config.getProvider(spi.getName());

        if (defaultProvider != null) {
            if (getProviderFactory(spi.getProviderClass(), defaultProvider) == null) {
                throw new RuntimeException("Failed to find provider " + provider + " for " + spi.getName());
            }
        } else {
            Map<String, ProviderFactory> factories = factoriesMap.get(spi.getProviderClass());
            if (factories != null && factories.size() == 1) {
                defaultProvider = factories.values().iterator().next().getId();
            }

            if (factories != null) {
                if (defaultProvider == null) {
                    Optional<ProviderFactory> highestPriority = factories.values().stream()
                            .max(Comparator.comparing(ProviderFactory::order));
                    if (highestPriority.isPresent() && highestPriority.get().order() > 0) {
                        defaultProvider = highestPriority.get().getId();
                    }
                }
            }

            if (defaultProvider == null && (factories == null || factories.containsKey("default"))) {
                defaultProvider = "default";
            }
        }

        if (defaultProvider != null) {
            this.provider.put(spi.getProviderClass(), defaultProvider);
            logger.debugv("Set default provider for {0} to {1}", spi.getName(), defaultProvider);
        } else {
            logger.debugv("No default provider for {0}", spi.getName());
        }
    }
}
