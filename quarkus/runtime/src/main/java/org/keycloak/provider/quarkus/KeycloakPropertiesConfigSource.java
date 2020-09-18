/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.provider.quarkus;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.keycloak.common.Logger;

import io.smallrye.config.PropertiesConfigSource;

import static org.keycloak.common.util.StringPropertyReplacer.replaceProperties;
import static org.keycloak.provider.quarkus.MicroProfileConfigProvider.NS_KEYCLOAK;
import static org.keycloak.provider.quarkus.MicroProfileConfigProvider.NS_QUARKUS;

/**
 * A configuration source for {@code keycloak.properties}.
 */
public abstract class KeycloakPropertiesConfigSource extends PropertiesConfigSource {

    private static final Logger log = Logger.getLogger(KeycloakPropertiesConfigSource.class);
    static final String KEYCLOAK_PROPERTIES = "keycloak.properties";

    KeycloakPropertiesConfigSource(InputStream is, int ordinal) {
        super(readProperties(is), KEYCLOAK_PROPERTIES, ordinal);
    }

    private static Map<String, String> readProperties(final InputStream is) {
        if (is == null) {
            return Collections.emptyMap();
        }
        try (Closeable ignored = is) {
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                try (BufferedReader br = new BufferedReader(isr)) {
                    final Properties properties = new Properties();
                    properties.load(br);
                    return transform((Map<String, String>) (Map) properties);
                }
            }
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public static final class InJar extends KeycloakPropertiesConfigSource {
        public InJar() {
            super(openStream(), 245);
        }

        private static InputStream openStream() {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = KeycloakPropertiesConfigSource.class.getClassLoader();
            }
            InputStream is;
            String fileName = "META-INF" + File.separator + KEYCLOAK_PROPERTIES;
            if (cl == null) {
                is = ClassLoader.getSystemResourceAsStream(fileName);
            } else {
                is = cl.getResourceAsStream(fileName);
            }
            if (is != null) {
                log.debug("Loading the server configuration from the classpath");
            }
            return is;
        }
    }

    public static final class InFileSystem extends KeycloakPropertiesConfigSource {

        public InFileSystem(String fileName) {
            super(openStream(fileName), 255);
        }

        private static InputStream openStream(String fileName) {
            final Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                try {
                    log.debugf("Loading the server configuration from %s", fileName);
                    return Files.newInputStream(path);
                } catch (NoSuchFileException | FileNotFoundException e) {
                    return null;
                } catch (IOException e) {
                    throw new IOError(e);
                }
            } else {
                return null;
            }
        }
    }

    private static Map<String, String> transform(Map<String, String> properties) {
        Map<String, String> result = new HashMap<>(properties.size());
        properties.keySet().forEach(k -> result.put(transformKey(k), replaceProperties(properties.get(k))));
        return result;
    }

    /**
     * We need a better namespace resolution so that we don't need to add Quarkus extensions manually. Maybe the easiest 
     * path is to just have the "keycloak" namespace for Keycloak-specific properties.
     * 
     * @param key the key to transform
     * @return the same key but prefixed with the namespace
     */
    private static String transformKey(String key) {
        String namespace;
        String[] keyParts = key.split("\\.");
        String extension = keyParts[0];
        String profile = "";

        if (extension.startsWith("%")) {
            profile = String.format("%s.", keyParts[0]);
            extension = keyParts[1];
            key = key.substring(key.indexOf('.') + 1);
        }

        switch (extension) {
            case "hibernate-orm":
            case "datasource":
            case "http":
            case "vertx":
            case "log":
                namespace = NS_QUARKUS;
                break;
            default:
                namespace = NS_KEYCLOAK;
        }

        return profile + namespace + "." + key;

    }
}
