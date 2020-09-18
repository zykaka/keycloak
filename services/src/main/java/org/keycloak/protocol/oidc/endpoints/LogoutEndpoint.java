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

package org.keycloak.protocol.oidc.endpoints;

import org.keycloak.common.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.spi.HttpRequest;
import org.keycloak.OAuth2Constants;
import org.keycloak.OAuthErrorException;
import org.keycloak.TokenVerifier;
import org.keycloak.common.ClientConnection;
import org.keycloak.common.VerificationException;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.headers.SecurityHeadersProvider;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.BackchannelLogoutResponse;
import org.keycloak.protocol.oidc.LogoutTokenValidationCode;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.protocol.oidc.utils.AuthorizeClientUtil;
import org.keycloak.protocol.oidc.utils.RedirectUtils;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.LogoutToken;
import org.keycloak.representations.RefreshToken;
import org.keycloak.services.ErrorPage;
import org.keycloak.services.ErrorResponseException;
import org.keycloak.services.clientpolicy.ClientPolicyException;
import org.keycloak.services.clientpolicy.DefaultClientPolicyManager;
import org.keycloak.services.clientpolicy.LogoutRequestContext;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.UserSessionManager;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.resources.Cors;
import org.keycloak.services.util.MtlsHoKTokenUtil;
import org.keycloak.util.TokenUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class LogoutEndpoint {
    private static final Logger logger = Logger.getLogger(LogoutEndpoint.class);

    @Context
    private KeycloakSession session;

    @Context
    private ClientConnection clientConnection;

    @Context
    private HttpRequest request;

    @Context
    private HttpHeaders headers;

    private TokenManager tokenManager;
    private RealmModel realm;
    private EventBuilder event;

    public LogoutEndpoint(TokenManager tokenManager, RealmModel realm, EventBuilder event) {
        this.tokenManager = tokenManager;
        this.realm = realm;
        this.event = event;
    }

    /**
     * Logout user session.  User must be logged in via a session cookie.
     *
     * When the logout is initiated by a remote idp, the parameter "initiating_idp" can be supplied. This param will
     * prevent upstream logout (since the logout procedure has already been started in the remote idp).
     *
     * @param redirectUri
     * @param initiatingIdp The alias of the idp initiating the logout.
     * @return
     */
    @GET
    @NoCache
    public Response logout(@QueryParam(OIDCLoginProtocol.REDIRECT_URI_PARAM) String redirectUri, // deprecated
                           @QueryParam("id_token_hint") String encodedIdToken,
                           @QueryParam("post_logout_redirect_uri") String postLogoutRedirectUri,
                           @QueryParam("state") String state,
                           @QueryParam("initiating_idp") String initiatingIdp) {
        String redirect = postLogoutRedirectUri != null ? postLogoutRedirectUri : redirectUri;

        if (redirect != null) {
            String validatedUri = RedirectUtils.verifyRealmRedirectUri(session, redirect);
            if (validatedUri == null) {
                event.event(EventType.LOGOUT);
                event.detail(Details.REDIRECT_URI, redirect);
                event.error(Errors.INVALID_REDIRECT_URI);
                return ErrorPage.error(session, null, Response.Status.BAD_REQUEST, Messages.INVALID_REDIRECT_URI);
            }
            redirect = validatedUri;
        }

        UserSessionModel userSession = null;
        IDToken idToken = null;
        if (encodedIdToken != null) {
            try {
                idToken = tokenManager.verifyIDTokenSignature(session, encodedIdToken);
                TokenVerifier.createWithoutSignature(idToken).tokenType(TokenUtil.TOKEN_TYPE_ID).verify();
                userSession = session.sessions().getUserSession(realm, idToken.getSessionState());

                if (userSession != null) {
                    checkTokenIssuedAt(idToken, userSession);
                }
            } catch (OAuthErrorException | VerificationException e) {
                event.event(EventType.LOGOUT);
                event.error(Errors.INVALID_TOKEN);
                return ErrorPage.error(session, null, Response.Status.BAD_REQUEST, Messages.SESSION_NOT_ACTIVE);
            }
        }

        // authenticate identity cookie, but ignore an access token timeout as we're logging out anyways.
        AuthenticationManager.AuthResult authResult = AuthenticationManager.authenticateIdentityCookie(session, realm, false);
        if (authResult != null) {
            userSession = userSession != null ? userSession : authResult.getSession();
            return initiateBrowserLogout(userSession, redirect, state, initiatingIdp);
        }
        else if (userSession != null) {
            // identity cookie is missing but there's valid id_token_hint which matches session cookie => continue with browser logout
            if (idToken != null && idToken.getSessionState().equals(AuthenticationManager.getSessionIdFromSessionCookie(session))) {
                return initiateBrowserLogout(userSession, redirect, state, initiatingIdp);
            }
            // non browser logout
            event.event(EventType.LOGOUT);
            AuthenticationManager.backchannelLogout(session, realm, userSession, session.getContext().getUri(), clientConnection, headers, true);
            event.user(userSession.getUser()).session(userSession).success();
        }

        if (redirect != null) {
            UriBuilder uriBuilder = UriBuilder.fromUri(redirect);
            if (state != null) uriBuilder.queryParam(OIDCLoginProtocol.STATE_PARAM, state);
            return Response.status(302).location(uriBuilder.build()).build();
        } else {
            // TODO Empty content with ok makes no sense. Should it display a page? Or use noContent?
            session.getProvider(SecurityHeadersProvider.class).options().allowEmptyContentType();
            return Response.ok().build();
        }
    }

    /**
     * Logout a session via a non-browser invocation.  Similar signature to refresh token except there is no grant_type.
     * You must pass in the refresh token and
     * authenticate the client if it is not public.
     *
     * If the client is a confidential client
     * you must include the client-id and secret in an Basic Auth Authorization header.
     *
     * If the client is a public client, then you must include a "client_id" form parameter.
     *
     * returns 204 if successful, 400 if not with a json error response.
     *
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response logoutToken() {
        MultivaluedMap<String, String> form = request.getDecodedFormParameters();
        checkSsl();

        event.event(EventType.LOGOUT);

        ClientModel client = authorizeClient();
        String refreshToken = form.getFirst(OAuth2Constants.REFRESH_TOKEN);
        if (refreshToken == null) {
            event.error(Errors.INVALID_TOKEN);
            throw new ErrorResponseException(OAuthErrorException.INVALID_REQUEST, "No refresh token", Response.Status.BAD_REQUEST);
        }

        try {
            session.clientPolicy().triggerOnEvent(new LogoutRequestContext(form));
        } catch (ClientPolicyException cpe) {
            throw new ErrorResponseException(Errors.INVALID_REQUEST, cpe.getErrorDetail(), Response.Status.BAD_REQUEST);
        }

        RefreshToken token = null;
        try {
            // KEYCLOAK-6771 Certificate Bound Token
            token = tokenManager.verifyRefreshToken(session, realm, client, request, refreshToken, false);

            boolean offline = TokenUtil.TOKEN_TYPE_OFFLINE.equals(token.getType());

            UserSessionModel userSessionModel;
            if (offline) {
                UserSessionManager sessionManager = new UserSessionManager(session);
                userSessionModel = sessionManager.findOfflineUserSession(realm, token.getSessionState());
            } else {
                userSessionModel = session.sessions().getUserSession(realm, token.getSessionState());
            }

            if (userSessionModel != null) {
                checkTokenIssuedAt(token, userSessionModel);
                logout(userSessionModel, offline);
            }
        } catch (OAuthErrorException e) {
            // KEYCLOAK-6771 Certificate Bound Token
            if (MtlsHoKTokenUtil.CERT_VERIFY_ERROR_DESC.equals(e.getDescription())) {
                event.error(Errors.NOT_ALLOWED);
                throw new ErrorResponseException(e.getError(), e.getDescription(), Response.Status.UNAUTHORIZED);
            } else {
                event.error(Errors.INVALID_TOKEN);
                throw new ErrorResponseException(e.getError(), e.getDescription(), Response.Status.BAD_REQUEST);
            }
        }

        return Cors.add(request, Response.noContent()).auth().allowedOrigins(session, client).allowedMethods("POST").exposedHeaders(Cors.ACCESS_CONTROL_ALLOW_METHODS).build();
    }

    /**
     * Backchannel logout endpoint implementation for Keycloak, which tries to logout the user from all sessions via
     * POST with a valid LogoutToken.
     *
     * Logout a session via a non-browser invocation. Will be implemented as a backchannel logout based on the
     * specification
     * https://openid.net/specs/openid-connect-backchannel-1_0.html
     *
     * @return
     */
    @Path("/backchannel-logout")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response backchannelLogout() {
        MultivaluedMap<String, String> form = request.getDecodedFormParameters();
        event.event(EventType.LOGOUT);

        String encodedLogoutToken = form.getFirst(OAuth2Constants.LOGOUT_TOKEN);
        if (encodedLogoutToken == null) {
            event.error(Errors.INVALID_TOKEN);
            throw new ErrorResponseException(OAuthErrorException.INVALID_REQUEST, "No logout token",
                    Response.Status.BAD_REQUEST);
        }

        LogoutTokenValidationCode validationCode = tokenManager.verifyLogoutToken(session, realm, encodedLogoutToken);
        if (!validationCode.equals(LogoutTokenValidationCode.VALIDATION_SUCCESS)) {
            event.error(Errors.INVALID_TOKEN);
            throw new ErrorResponseException(OAuthErrorException.INVALID_REQUEST, validationCode.getErrorMessage(),
                    Response.Status.BAD_REQUEST);
        }

        LogoutToken logoutToken = tokenManager.toLogoutToken(encodedLogoutToken).get();

        List<String> identityProviderAliases = tokenManager.getValidOIDCIdentityProvidersForBackchannelLogout(realm,
                session, encodedLogoutToken, logoutToken).stream()
                .map(idp -> idp.getConfig().getAlias())
                .collect(Collectors.toList());

        boolean logoutOfflineSessions = Boolean.parseBoolean(logoutToken.getEvents()
                .getOrDefault(TokenUtil.TOKEN_BACKCHANNEL_LOGOUT_EVENT_REVOKE_OFFLINE_TOKENS, false).toString());

        BackchannelLogoutResponse backchannelLogoutResponse;

        if (logoutToken.getSid() != null) {
            backchannelLogoutResponse = backchannelLogoutWithSessionId(logoutToken.getSid(), identityProviderAliases,
                    logoutOfflineSessions);
        } else {
            backchannelLogoutResponse = backchannelLogoutFederatedUserId(logoutToken.getSubject(),
                    identityProviderAliases, logoutOfflineSessions);
        }

        if (!backchannelLogoutResponse.getLocalLogoutSucceeded()) {
            event.error(Errors.LOGOUT_FAILED);
            throw new ErrorResponseException(OAuthErrorException.SERVER_ERROR,
                    "There was an error in the local logout",
                    Response.Status.NOT_IMPLEMENTED);
        }

        session.getProvider(SecurityHeadersProvider.class).options().allowEmptyContentType();

        if (oneOrMoreDownstreamLogoutsFailed(backchannelLogoutResponse)) {
            return Cors.add(request)
                    .auth()
                    .builder(Response.status(Response.Status.GATEWAY_TIMEOUT)
                            .type(MediaType.APPLICATION_JSON_TYPE))
                    .build();
        }

        return Cors.add(request)
                .auth()
                .builder(Response.ok()
                        .type(MediaType.APPLICATION_JSON_TYPE))
                .build();
    }

    private BackchannelLogoutResponse backchannelLogoutWithSessionId(String sessionId,
            List<String> identityProviderAliases, boolean logoutOfflineSessions) {
        BackchannelLogoutResponse backchannelLogoutResponse = new BackchannelLogoutResponse();
        backchannelLogoutResponse.setLocalLogoutSucceeded(true);
        for (String identityProviderAlias : identityProviderAliases) {
            UserSessionModel userSession = session.sessions().getUserSessionByBrokerSessionId(realm,
                    identityProviderAlias + "." + sessionId);

            if (userSession != null) {
                backchannelLogoutResponse = logoutUserSession(userSession, logoutOfflineSessions);
            }
        }

        return backchannelLogoutResponse;
    }

    private BackchannelLogoutResponse backchannelLogoutFederatedUserId(String federatedUserId,
            List<String> identityProviderAliases, boolean logoutOfflineSessions) {
        BackchannelLogoutResponse backchannelLogoutResponse = new BackchannelLogoutResponse();
        backchannelLogoutResponse.setLocalLogoutSucceeded(true);
        for (String identityProviderAlias : identityProviderAliases) {
            List<UserSessionModel> userSessions = session.sessions().getUserSessionByBrokerUserId(realm,
                    identityProviderAlias + "." + federatedUserId);

            for (UserSessionModel userSession : userSessions) {
                BackchannelLogoutResponse userBackchannelLogoutResponse;
                userBackchannelLogoutResponse = logoutUserSession(userSession, logoutOfflineSessions);
                backchannelLogoutResponse.setLocalLogoutSucceeded(backchannelLogoutResponse.getLocalLogoutSucceeded()
                        && userBackchannelLogoutResponse.getLocalLogoutSucceeded());
                userBackchannelLogoutResponse.getClientResponses()
                        .forEach(backchannelLogoutResponse::addClientResponses);
            }
        }

        return backchannelLogoutResponse;
    }

    private BackchannelLogoutResponse logoutUserSession(UserSessionModel userSession, boolean logoutOfflineSessions) {
        BackchannelLogoutResponse backchannelLogoutResponse =
                AuthenticationManager.backchannelLogout(session, realm, userSession,
                        session.getContext().getUri(),
                        clientConnection, headers, false, logoutOfflineSessions);

        if (backchannelLogoutResponse.getLocalLogoutSucceeded()) {
            event.user(userSession.getUser())
                    .session(userSession)
                    .success();
        }

        return backchannelLogoutResponse;
    }
    
    private boolean oneOrMoreDownstreamLogoutsFailed(BackchannelLogoutResponse backchannelLogoutResponse) {
        BackchannelLogoutResponse filteredBackchannelLogoutResponse = new BackchannelLogoutResponse();
        for (BackchannelLogoutResponse.DownStreamBackchannelLogoutResponse response : backchannelLogoutResponse
                .getClientResponses()) {
            if (response.isWithBackchannelLogoutUrl()) {
                filteredBackchannelLogoutResponse.addClientResponses(response);
            }
        }

        return backchannelLogoutResponse.getClientResponses().stream()
                .filter(BackchannelLogoutResponse.DownStreamBackchannelLogoutResponse::isWithBackchannelLogoutUrl)
                .anyMatch(clientResponse -> !(clientResponse.getResponseCode().isPresent() &&
                        (clientResponse.getResponseCode().get() == Response.Status.OK.getStatusCode() ||
                                clientResponse.getResponseCode().get() == Response.Status.NO_CONTENT.getStatusCode())));
    }

    private void logout(UserSessionModel userSession, boolean offline) {
        AuthenticationManager.backchannelLogout(session, realm, userSession, session.getContext().getUri(), clientConnection, headers, true, offline);
        event.user(userSession.getUser()).session(userSession).success();
    }

    private ClientModel authorizeClient() {
        ClientModel client = AuthorizeClientUtil.authorizeClient(session, event).getClient();

        if (client.isBearerOnly()) {
            throw new ErrorResponseException(Errors.INVALID_CLIENT, "Bearer-only not allowed", Response.Status.BAD_REQUEST);
        }

        return client;
    }

    private void checkSsl() {
        if (!session.getContext().getUri().getBaseUri().getScheme().equals("https") && realm.getSslRequired().isRequired(clientConnection)) {
            throw new ErrorResponseException("invalid_request", "HTTPS required", Response.Status.FORBIDDEN);
        }
    }

    private void checkTokenIssuedAt(IDToken token, UserSessionModel userSession) throws OAuthErrorException {
        if (token.getIssuedAt() + 1 < userSession.getStarted()) {
            throw new OAuthErrorException(OAuthErrorException.INVALID_GRANT, "Refresh toked issued before the user session started");
        }
    }

    private Response initiateBrowserLogout(UserSessionModel userSession, String redirect, String state, String initiatingIdp ) {
        if (redirect != null) userSession.setNote(OIDCLoginProtocol.LOGOUT_REDIRECT_URI, redirect);
        if (state != null) userSession.setNote(OIDCLoginProtocol.LOGOUT_STATE_PARAM, state);
        userSession.setNote(AuthenticationManager.KEYCLOAK_LOGOUT_PROTOCOL, OIDCLoginProtocol.LOGIN_PROTOCOL);
        logger.debug("Initiating OIDC browser logout");
        Response response =  AuthenticationManager.browserLogout(session, realm, userSession, session.getContext().getUri(), clientConnection, headers, initiatingIdp);
        logger.debug("finishing OIDC browser logout");
        return response;
    }
}
