/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.models.jpa;

import org.keycloak.common.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.connections.jpa.util.JpaUtils;
import org.keycloak.migration.MigrationModel;
import org.keycloak.models.ClientInitialAccessModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientProvider;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.GroupProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ModelDuplicateException;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.RoleProvider;
import org.keycloak.models.jpa.entities.ClientEntity;
import org.keycloak.models.jpa.entities.ClientInitialAccessEntity;
import org.keycloak.models.jpa.entities.ClientScopeEntity;
import org.keycloak.models.jpa.entities.GroupEntity;
import org.keycloak.models.jpa.entities.RealmEntity;
import org.keycloak.models.jpa.entities.RoleEntity;
import org.keycloak.models.utils.KeycloakModelUtils;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.keycloak.models.ModelException;

import static org.keycloak.common.util.StackUtil.getShortStackTrace;
import static org.keycloak.utils.StreamsUtil.closing;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JpaRealmProvider implements RealmProvider, ClientProvider, GroupProvider, RoleProvider {
    protected static final Logger logger = Logger.getLogger(JpaRealmProvider.class);
    private final KeycloakSession session;
    protected EntityManager em;

    public JpaRealmProvider(KeycloakSession session, EntityManager em) {
        this.session = session;
        this.em = em;
    }

    @Override
    public MigrationModel getMigrationModel() {
        return new MigrationModelAdapter(em);
    }

    @Override
    public RealmModel createRealm(String name) {
        return createRealm(KeycloakModelUtils.generateId(), name);
    }

    @Override
    public RealmModel createRealm(String id, String name) {
        RealmEntity realm = new RealmEntity();
        realm.setName(name);
        realm.setId(id);
        em.persist(realm);
        em.flush();
        final RealmModel adapter = new RealmAdapter(session, em, realm);
        session.getKeycloakSessionFactory().publish(new RealmModel.RealmCreationEvent() {
            @Override
            public RealmModel getCreatedRealm() {
                return adapter;
            }
            @Override
            public KeycloakSession getKeycloakSession() {
            	return session;
            }
        });
        return adapter;
    }

    @Override
    public RealmModel getRealm(String id) {
        RealmEntity realm = em.find(RealmEntity.class, id);
        if (realm == null) return null;
        RealmAdapter adapter = new RealmAdapter(session, em, realm);
        return adapter;
    }
    
    @Override
    public List<RealmModel> getRealmsWithProviderType(Class<?> providerType) {
        TypedQuery<String> query = em.createNamedQuery("getRealmIdsWithProviderType", String.class);
        query.setParameter("providerType", providerType.getName());
        return getRealms(query);
    }

    @Override
    public List<RealmModel> getRealms() {
        TypedQuery<String> query = em.createNamedQuery("getAllRealmIds", String.class);
        return getRealms(query);
    }

    private List<RealmModel> getRealms(TypedQuery<String> query) {
        List<String> entities = query.getResultList();
        List<RealmModel> realms = new ArrayList<RealmModel>();
        for (String id : entities) {
            RealmModel realm = session.realms().getRealm(id);
            if (realm != null) realms.add(realm);
        }
        return realms;
    }

    @Override
    public RealmModel getRealmByName(String name) {
        TypedQuery<String> query = em.createNamedQuery("getRealmIdByName", String.class);
        query.setParameter("name", name);
        List<String> entities = query.getResultList();
        if (entities.isEmpty()) return null;
        if (entities.size() > 1) throw new IllegalStateException("Should not be more than one realm with same name");
        String id = query.getResultList().get(0);

        return session.realms().getRealm(id);
    }

    @Override
    public boolean removeRealm(String id) {
        RealmEntity realm = em.find(RealmEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (realm == null) {
            return false;
        }
        em.refresh(realm);
        final RealmAdapter adapter = new RealmAdapter(session, em, realm);
        session.users().preRemove(adapter);

        realm.getDefaultGroups().clear();
        em.flush();

        int num = em.createNamedQuery("deleteGroupRoleMappingsByRealm")
                .setParameter("realm", realm.getId()).executeUpdate();

        session.clients().removeClients(adapter);

        num = em.createNamedQuery("deleteDefaultClientScopeRealmMappingByRealm")
                .setParameter("realm", realm).executeUpdate();

        for (ClientScopeEntity a : new LinkedList<>(realm.getClientScopes())) {
            adapter.removeClientScope(a.getId());
        }

        removeRoles(adapter);

        adapter.getGroupsStream().forEach(adapter::removeGroup);
        
        num = em.createNamedQuery("removeClientInitialAccessByRealm")
                .setParameter("realm", realm).executeUpdate();

        em.remove(realm);

        em.flush();
        em.clear();

        session.getKeycloakSessionFactory().publish(new RealmModel.RealmRemovedEvent() {
            @Override
            public RealmModel getRealm() {
                return adapter;
            }

            @Override
            public KeycloakSession getKeycloakSession() {
                return session;
            }
        });

        return true;
    }

    @Override
    public void close() {
    }

    @Override
    public RoleModel addRealmRole(RealmModel realm, String name) {
       return addRealmRole(realm, KeycloakModelUtils.generateId(), name);

    }
    @Override
    public RoleModel addRealmRole(RealmModel realm, String id, String name) {
        if (getRealmRole(realm, name) != null) {
            throw new ModelDuplicateException();
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(id);
        entity.setName(name);
        RealmEntity ref = em.getReference(RealmEntity.class, realm.getId());
        entity.setRealm(ref);
        entity.setRealmId(realm.getId());
        em.persist(entity);
        em.flush();
        RoleAdapter adapter = new RoleAdapter(session, realm, em, entity);
        return adapter;

    }

    @Override
    public RoleModel getRealmRole(RealmModel realm, String name) {
        TypedQuery<String> query = em.createNamedQuery("getRealmRoleIdByName", String.class);
        query.setParameter("name", name);
        query.setParameter("realm", realm.getId());
        List<String> roles = query.getResultList();
        if (roles.isEmpty()) return null;
        return session.roles().getRoleById(realm, roles.get(0));
    }

    @Override
    public RoleModel addClientRole(ClientModel client, String name) {
        return addClientRole(client, KeycloakModelUtils.generateId(), name);
    }

    @Override
    public RoleModel addClientRole(ClientModel client, String id, String name) {
        if (getClientRole(client, name) != null) {
            throw new ModelDuplicateException();
        }
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(id);
        roleEntity.setName(name);
        roleEntity.setClientId(client.getId());
        roleEntity.setClientRole(true);
        roleEntity.setRealmId(client.getRealm().getId());
        em.persist(roleEntity);
        RoleAdapter adapter = new RoleAdapter(session, client.getRealm(), em, roleEntity);
        return adapter;
    }

    @Override
    public Stream<RoleModel> getRealmRolesStream(RealmModel realm) {
        TypedQuery<String> query = em.createNamedQuery("getRealmRoleIds", String.class);
        query.setParameter("realm", realm.getId());
        Stream<String> roles = query.getResultStream();

        return closing(roles.map(realm::getRoleById));
    }

    @Override
    public RoleModel getClientRole(ClientModel client, String name) {
        TypedQuery<String> query = em.createNamedQuery("getClientRoleIdByName", String.class);
        query.setParameter("name", name);
        query.setParameter("client", client.getId());
        List<String> roles = query.getResultList();
        if (roles.isEmpty()) return null;
        return session.roles().getRoleById(client.getRealm(), roles.get(0));
    }
    
    @Override
    public Stream<RoleModel> getRealmRolesStream(RealmModel realm, Integer first, Integer max) {
        TypedQuery<RoleEntity> query = em.createNamedQuery("getRealmRoles", RoleEntity.class);
        query.setParameter("realm", realm.getId());
        
        return getRolesStream(query, realm, first, max);
    }

    @Override
    public Stream<RoleModel> getClientRolesStream(ClientModel client, Integer first, Integer max) {
        TypedQuery<RoleEntity> query = em.createNamedQuery("getClientRoles", RoleEntity.class);
        query.setParameter("client", client.getId());

        return getRolesStream(query, client.getRealm(), first, max);
    }

    protected Stream<RoleModel> getRolesStream(TypedQuery<RoleEntity> query, RealmModel realm, Integer first, Integer max) {
        if(Objects.nonNull(first) && Objects.nonNull(max)
                && first >= 0 && max >= 0) {
            query= query.setFirstResult(first).setMaxResults(max);
        }

        Stream<RoleEntity> results = query.getResultStream();

        return closing(results.map(role -> new RoleAdapter(session, realm, em, role)));
    }
    
    @Override
    public Stream<RoleModel> searchForClientRolesStream(ClientModel client, String search, Integer first, Integer max) {
        TypedQuery<RoleEntity> query = em.createNamedQuery("searchForClientRoles", RoleEntity.class);
        query.setParameter("client", client.getId());
        return searchForRoles(query, client.getRealm(), search, first, max);
    }
    
    @Override
    public Stream<RoleModel> searchForRolesStream(RealmModel realm, String search, Integer first, Integer max) {
        TypedQuery<RoleEntity> query = em.createNamedQuery("searchForRealmRoles", RoleEntity.class);
        query.setParameter("realm", realm.getId());
        
        return searchForRoles(query, realm, search, first, max);
    }
    
    protected Stream<RoleModel> searchForRoles(TypedQuery<RoleEntity> query, RealmModel realm, String search, Integer first, Integer max) {

        query.setParameter("search", "%" + search.trim().toLowerCase() + "%");
        if(Objects.nonNull(first) && Objects.nonNull(max)
                && first >= 0 && max >= 0) {
            query= query.setFirstResult(first).setMaxResults(max);
        }
        
        Stream<RoleEntity> results = query.getResultStream();
        
        return closing(results.map(role -> new RoleAdapter(session, realm, em, role)));
    }

    @Override
    public boolean removeRole(RoleModel role) {
        RealmModel realm;
        if (role.getContainer() instanceof RealmModel) {
            realm = (RealmModel) role.getContainer();
        } else if (role.getContainer() instanceof ClientModel) {
            realm = ((ClientModel)role.getContainer()).getRealm();
        } else {
            throw new IllegalStateException("RoleModel's container isn not instance of either RealmModel or ClientModel");
        }
        session.users().preRemove(realm, role);
        RoleContainerModel container = role.getContainer();
        if (container.getDefaultRoles().contains(role.getName())) {
            container.removeDefaultRoles(role.getName());
        }
        RoleEntity roleEntity = em.getReference(RoleEntity.class, role.getId());
        if (roleEntity == null || !roleEntity.getRealmId().equals(realm.getId())) {
            // Throw model exception to ensure transaction rollback and revert previous operations (removing default roles) as well
            throw new ModelException("Role not found or trying to remove role from incorrect realm");
        }
        String compositeRoleTable = JpaUtils.getTableNameForNativeQuery("COMPOSITE_ROLE", em);
        em.createNativeQuery("delete from " + compositeRoleTable + " where CHILD_ROLE = :role").setParameter("role", roleEntity).executeUpdate();
        realm.getClientsStream().forEach(c -> c.deleteScopeMapping(role));
        em.createNamedQuery("deleteClientScopeRoleMappingByRole").setParameter("role", roleEntity).executeUpdate();
        int val = em.createNamedQuery("deleteGroupRoleMappingsByRole").setParameter("roleId", roleEntity.getId()).executeUpdate();

        em.flush();
        em.remove(roleEntity);

        session.getKeycloakSessionFactory().publish(new RoleContainerModel.RoleRemovedEvent() {
            @Override
            public RoleModel getRole() {
                return role;
            }

            @Override
            public KeycloakSession getKeycloakSession() {
                return session;
            }
        });

        em.flush();
        return true;

    }

    @Override
    public void removeRoles(RealmModel realm) {
        // No need to go through cache. Roles were already invalidated
        realm.getRolesStream().forEach(this::removeRole);
    }

    @Override
    public void removeRoles(ClientModel client) {
        // No need to go through cache. Roles were already invalidated
        client.getRolesStream().forEach(this::removeRole);
    }

    @Override
    public RoleModel getRoleById(RealmModel realm, String id) {
        RoleEntity entity = em.find(RoleEntity.class, id);
        if (entity == null) return null;
        if (!realm.getId().equals(entity.getRealmId())) return null;
        RoleAdapter adapter = new RoleAdapter(session, realm, em, entity);
        return adapter;
    }

    @Override
    public GroupModel getGroupById(RealmModel realm, String id) {
        GroupEntity groupEntity = em.find(GroupEntity.class, id);
        if (groupEntity == null) return null;
        if (!groupEntity.getRealm().equals(realm.getId())) return null;
        GroupAdapter adapter =  new GroupAdapter(realm, em, groupEntity);
        return adapter;
    }

    @Override
    public void moveGroup(RealmModel realm, GroupModel group, GroupModel toParent) {
        if (toParent != null && group.getId().equals(toParent.getId())) {
            return;
        }
        if (group.getParentId() != null) {
            group.getParent().removeChild(group);
        }
        group.setParent(toParent);
        if (toParent != null) toParent.addChild(group);
        else session.groups().addTopLevelGroup(realm, group);
    }

    @Override
    public Stream<GroupModel> getGroupsStream(RealmModel realm) {
        RealmEntity ref = em.getReference(RealmEntity.class, realm.getId());

        return ref.getGroups().stream()
                .map(g -> session.groups().getGroupById(realm, g.getId()))
                .sorted(Comparator.comparing(GroupModel::getName));
    }

    @Override
    public Long getGroupsCount(RealmModel realm, Boolean onlyTopGroups) {
        if(Objects.equals(onlyTopGroups, Boolean.TRUE)) {
            return em.createNamedQuery("getTopLevelGroupCount", Long.class)
                    .setParameter("realm", realm.getId())
                    .setParameter("parent", GroupEntity.TOP_PARENT_ID)
                    .getSingleResult();
        } else {
            return em.createNamedQuery("getGroupCount", Long.class)
                    .setParameter("realm", realm.getId())
                    .getSingleResult();
        }
    }

    @Override
    public long getClientsCount(RealmModel realm) {
        final Long res = em.createNamedQuery("getRealmClientsCount", Long.class)
          .setParameter("realm", realm.getId())
          .getSingleResult();
        return res == null ? 0l : res;
    }

    @Override
    public Long getGroupsCountByNameContaining(RealmModel realm, String search) {
        return searchForGroupByNameStream(realm, search, null, null).count();
    }
    
    @Override
    public Stream<GroupModel> getGroupsByRoleStream(RealmModel realm, RoleModel role, int firstResult, int maxResults) {
        TypedQuery<GroupEntity> query = em.createNamedQuery("groupsInRole", GroupEntity.class);
        query.setParameter("roleId", role.getId());
        if (firstResult != -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != -1) {
            query.setMaxResults(maxResults);
        }
        Stream<GroupEntity> results = query.getResultStream();

        return closing(results
        		.map(g -> (GroupModel) new GroupAdapter(realm, em, g))
                .sorted(Comparator.comparing(GroupModel::getName)));
    }

    @Override
    public Stream<GroupModel> getTopLevelGroupsStream(RealmModel realm) {
        RealmEntity ref = em.getReference(RealmEntity.class, realm.getId());

        return ref.getGroups().stream()
                .filter(g -> GroupEntity.TOP_PARENT_ID.equals(g.getParentId()))
                .map(g -> session.groups().getGroupById(realm, g.getId()))
                .sorted(Comparator.comparing(GroupModel::getName));
    }

    @Override
    public Stream<GroupModel> getTopLevelGroupsStream(RealmModel realm, Integer first, Integer max) {
        Stream<String> groupIds =  em.createNamedQuery("getTopLevelGroupIds", String.class)
                .setParameter("realm", realm.getId())
                .setParameter("parent", GroupEntity.TOP_PARENT_ID)
                .setFirstResult(first)
                .setMaxResults(max)
                .getResultStream();

        return closing(groupIds.map(realm::getGroupById));
    }

    @Override
    public boolean removeGroup(RealmModel realm, GroupModel group) {
        if (group == null) {
            return false;
        }

        GroupModel.GroupRemovedEvent event = new GroupModel.GroupRemovedEvent() {
            @Override
            public RealmModel getRealm() {
                return realm;
            }

            @Override
            public GroupModel getGroup() {
                return group;
            }

            @Override
            public KeycloakSession getKeycloakSession() {
                return session;
            }
        };
        session.getKeycloakSessionFactory().publish(event);

        session.users().preRemove(realm, group);

        realm.removeDefaultGroup(group);
        group.getSubGroupsStream().forEach(realm::removeGroup);

        GroupEntity groupEntity = em.find(GroupEntity.class, group.getId(), LockModeType.PESSIMISTIC_WRITE);
        if ((groupEntity == null) || (!groupEntity.getRealm().equals(realm.getId()))) {
            return false;
        }
        em.createNamedQuery("deleteGroupRoleMappingsByGroup").setParameter("group", groupEntity).executeUpdate();

        RealmEntity realmEntity = em.getReference(RealmEntity.class, realm.getId());
        realmEntity.getGroups().remove(groupEntity);

        em.remove(groupEntity);
        return true;


    }

    @Override
    public GroupModel createGroup(RealmModel realm, String id, String name, GroupModel toParent) {
        if (id == null) {
            id = KeycloakModelUtils.generateId();
        } else if (GroupEntity.TOP_PARENT_ID.equals(id)) {
            // maybe it's impossible but better ensure this doesn't happen
            throw new ModelException("The ID of the new group is equals to the tag used for top level groups");
        }
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setId(id);
        groupEntity.setName(name);
        RealmEntity realmEntity = em.getReference(RealmEntity.class, realm.getId());
        groupEntity.setRealm(realmEntity.getId());
        groupEntity.setParentId(toParent == null? GroupEntity.TOP_PARENT_ID : toParent.getId());
        em.persist(groupEntity);
        em.flush();
        realmEntity.getGroups().add(groupEntity);

        GroupAdapter adapter = new GroupAdapter(realm, em, groupEntity);
        return adapter;
    }

    @Override
    public void addTopLevelGroup(RealmModel realm, GroupModel subGroup) {
        subGroup.setParent(null);
    }

    @Override
    public ClientModel addClient(RealmModel realm, String clientId) {
        return addClient(realm, KeycloakModelUtils.generateId(), clientId);
    }

    @Override
    public ClientModel addClient(RealmModel realm, String id, String clientId) {
        if (clientId == null) {
            clientId = id;
        }

        logger.tracef("addClient(%s, %s, %s)%s", realm, id, clientId, getShortStackTrace());

        ClientEntity entity = new ClientEntity();
        entity.setId(id);
        entity.setClientId(clientId);
        entity.setEnabled(true);
        entity.setStandardFlowEnabled(true);
        RealmEntity realmRef = em.getReference(RealmEntity.class, realm.getId());
        entity.setRealm(realmRef);
        em.persist(entity);
        em.flush();
        final ClientModel resource = new ClientAdapter(realm, em, session, entity);

        em.flush();
        session.getKeycloakSessionFactory().publish(new RealmModel.ClientCreationEvent() {
            @Override
            public ClientModel getCreatedClient() {
                return resource;
            }
        });
        return resource;
    }

    @Override
    public Stream<ClientModel> getClientsStream(RealmModel realm) {
        return getClientsStream(realm, null, null);
    }

    @Override
    public Stream<ClientModel> getClientsStream(RealmModel realm, Integer firstResult, Integer maxResults) {
        TypedQuery<String> query = em.createNamedQuery("getClientIdsByRealm", String.class);
        if (firstResult != null && firstResult > 0) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != null && maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        query.setParameter("realm", realm.getId());
        Stream<String> clients = query.getResultStream();

        return closing(clients.map(c -> session.clients().getClientById(realm, c)).filter(Objects::nonNull));
    }

    @Override
    public Stream<ClientModel> getAlwaysDisplayInConsoleClientsStream(RealmModel realm) {
        TypedQuery<String> query = em.createNamedQuery("getAlwaysDisplayInConsoleClients", String.class);
        query.setParameter("realm", realm.getId());
        Stream<String> clientStream = query.getResultStream();

        return closing(clientStream.map(c -> session.clients().getClientById(realm, c)).filter(Objects::nonNull));
    }

    @Override
    public ClientModel getClientById(RealmModel realm, String id) {
        logger.tracef("getClientById(%s, %s)%s", realm, id, getShortStackTrace());

        ClientEntity app = em.find(ClientEntity.class, id);
        // Check if application belongs to this realm
        if (app == null || !realm.getId().equals(app.getRealm().getId())) return null;
        ClientAdapter client = new ClientAdapter(realm, em, session, app);
        return client;

    }

    @Override
    public ClientModel getClientByClientId(RealmModel realm, String clientId) {
        logger.tracef("getClientByClientId(%s, %s)%s", realm, clientId, getShortStackTrace());

        TypedQuery<String> query = em.createNamedQuery("findClientIdByClientId", String.class);
        query.setParameter("clientId", clientId);
        query.setParameter("realm", realm.getId());
        List<String> results = query.getResultList();
        if (results.isEmpty()) return null;
        String id = results.get(0);
        return session.clients().getClientById(realm, id);
    }

    @Override
    public Stream<ClientModel> searchClientsByClientIdStream(RealmModel realm, String clientId, Integer firstResult, Integer maxResults) {
        TypedQuery<String> query = em.createNamedQuery("searchClientsByClientId", String.class);
        if (firstResult != null && firstResult > 0) {
            query.setFirstResult(firstResult);
        }
        if (maxResults != null && maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        query.setParameter("clientId", clientId);
        query.setParameter("realm", realm.getId());
        Stream<String> results = query.getResultStream();
        return closing(results.map(c -> session.clients().getClientById(realm, c)));
    }

    @Override
    public void removeClients(RealmModel realm) {
        TypedQuery<String> query = em.createNamedQuery("getClientIdsByRealm", String.class);
        query.setParameter("realm", realm.getId());
        List<String> clients = query.getResultList();
        for (String client : clients) {
            // No need to go through cache. Clients were already invalidated
            removeClient(realm, client);
        }
    }

    @Override
    public boolean removeClient(RealmModel realm, String id) {

        logger.tracef("removeClient(%s, %s)%s", realm, id, getShortStackTrace());

        final ClientModel client = getClientById(realm, id);
        if (client == null) return false;

        session.users().preRemove(realm, client);

        removeRoles(client);

        ClientEntity clientEntity = em.find(ClientEntity.class, id, LockModeType.PESSIMISTIC_WRITE);

        session.getKeycloakSessionFactory().publish(new RealmModel.ClientRemovedEvent() {
            @Override
            public ClientModel getClient() {
                return client;
            }

            @Override
            public KeycloakSession getKeycloakSession() {
                return session;
            }
        });

        int countRemoved = em.createNamedQuery("deleteClientScopeClientMappingByClient")
                .setParameter("client", clientEntity)
                .executeUpdate();
        em.remove(clientEntity);  // i have no idea why, but this needs to come before deleteScopeMapping

        try {
            em.flush();
        } catch (RuntimeException e) {
            logger.errorv("Unable to delete client entity: {0} from realm {1}", client.getClientId(), realm.getName());
            throw e;
        }

        return true;
    }

    @Override
    public ClientScopeModel getClientScopeById(String id, RealmModel realm) {
        ClientScopeEntity app = em.find(ClientScopeEntity.class, id);

        // Check if application belongs to this realm
        if (app == null || !realm.getId().equals(app.getRealm().getId())) return null;
        ClientScopeAdapter adapter = new ClientScopeAdapter(realm, em, session, app);
        return adapter;
    }

    @Override
    public Stream<GroupModel> searchForGroupByNameStream(RealmModel realm, String search, Integer first, Integer max) {
        TypedQuery<String> query = em.createNamedQuery("getGroupIdsByNameContaining", String.class)
                .setParameter("realm", realm.getId())
                .setParameter("search", search);
        if(Objects.nonNull(first) && Objects.nonNull(max)) {
            query= query.setFirstResult(first).setMaxResults(max);
        }
        Stream<String> groups =  query.getResultStream();

        return closing(groups.map(id -> {
            GroupModel groupById = session.groups().getGroupById(realm, id);
            while (Objects.nonNull(groupById.getParentId())) {
                groupById = session.groups().getGroupById(realm, groupById.getParentId());
            }
            return groupById;
        }).sorted(Comparator.comparing(GroupModel::getName)).distinct());
    }

    @Override
    public ClientInitialAccessModel createClientInitialAccessModel(RealmModel realm, int expiration, int count) {
        RealmEntity realmEntity = em.find(RealmEntity.class, realm.getId());

        ClientInitialAccessEntity entity = new ClientInitialAccessEntity();
        entity.setId(KeycloakModelUtils.generateId());
        entity.setRealm(realmEntity);

        entity.setCount(count);
        entity.setRemainingCount(count);

        int currentTime = Time.currentTime();
        entity.setTimestamp(currentTime);
        entity.setExpiration(expiration);

        em.persist(entity);

        return entityToModel(entity);
    }

    @Override
    public ClientInitialAccessModel getClientInitialAccessModel(RealmModel realm, String id) {
        ClientInitialAccessEntity entity = em.find(ClientInitialAccessEntity.class, id);
        if (entity == null) return null;
        if (!entity.getRealm().getId().equals(realm.getId())) return null;
        return entityToModel(entity);
    }

    @Override
    public void removeClientInitialAccessModel(RealmModel realm, String id) {
        ClientInitialAccessEntity entity = em.find(ClientInitialAccessEntity.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (entity == null) return;
        if (!entity.getRealm().getId().equals(realm.getId())) return;
        em.remove(entity);
        em.flush();
    }

    @Override
    public List<ClientInitialAccessModel> listClientInitialAccess(RealmModel realm) {
        RealmEntity realmEntity = em.find(RealmEntity.class, realm.getId());

        TypedQuery<ClientInitialAccessEntity> query = em.createNamedQuery("findClientInitialAccessByRealm", ClientInitialAccessEntity.class);
        query.setParameter("realm", realmEntity);
        List<ClientInitialAccessEntity> entities = query.getResultList();

        return entities.stream()
                .map(this::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public void removeExpiredClientInitialAccess() {
        int currentTime = Time.currentTime();

        em.createNamedQuery("removeExpiredClientInitialAccess")
                .setParameter("currentTime", currentTime)
                .executeUpdate();
    }

    @Override
    public void decreaseRemainingCount(RealmModel realm, ClientInitialAccessModel clientInitialAccess) {
        em.createNamedQuery("decreaseClientInitialAccessRemainingCount")
                .setParameter("id", clientInitialAccess.getId())
                .executeUpdate();
    }

    private ClientInitialAccessModel entityToModel(ClientInitialAccessEntity entity) {
        ClientInitialAccessModel model = new ClientInitialAccessModel();
        model.setId(entity.getId());
        model.setCount(entity.getCount());
        model.setRemainingCount(entity.getRemainingCount());
        model.setExpiration(entity.getExpiration());
        model.setTimestamp(entity.getTimestamp());
        return model;
    }

}
