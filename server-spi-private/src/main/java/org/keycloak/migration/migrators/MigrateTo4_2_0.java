/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.migration.migrators;

import static java.util.Comparator.comparing;

import java.util.List;
import java.util.stream.Collectors;

import org.keycloak.common.Logger;
import org.keycloak.migration.ModelVersion;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RequiredActionProviderModel;
import org.keycloak.representations.idm.RealmRepresentation;

/**
 * @author <a href="mailto:wadahiro@gmail.com">Hiroyuki Wada</a>
 */
public class MigrateTo4_2_0 implements Migration {

    public static final ModelVersion VERSION = new ModelVersion("4.2.0");

    private static final Logger LOG = Logger.getLogger(MigrateTo4_2_0.class);

    @Override
    public ModelVersion getVersion() {
        return VERSION;
    }

    @Override
    public void migrate(KeycloakSession session) {
        session.realms().getRealms().stream().forEach(r -> {
            migrateRealm(session, r, false);
        });
    }

    @Override
    public void migrateImport(KeycloakSession session, RealmModel realm, RealmRepresentation rep, boolean skipUserDependent) {
        migrateRealm(session, realm, true);
    }

    protected void migrateRealm(KeycloakSession session, RealmModel realm, boolean json) {
        // Set default priority of required actions in alphabetical order
        List<RequiredActionProviderModel> actions = realm.getRequiredActionProviders().stream()
                .sorted(comparing(RequiredActionProviderModel::getName)).collect(Collectors.toList());
        int priority = 10;
        for (RequiredActionProviderModel model : actions) {
            LOG.debugf("Setting priority '%d' for required action '%s' in realm '%s'", priority, model.getAlias(),
                    realm.getName());
            model.setPriority(priority);
            priority += 10;

            // Save
            realm.updateRequiredActionProvider(model);
        }
    }
}
