///$Header: /mec_as2/de/mendelson/util/oauth2/OAuth2Config.java 3     2/01/23 16:47 Heller $
package de.mendelson.util.oauth2;

import java.io.Serializable;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Dummy class - not implemented in the community edition
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class OAuth2Config implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final int PKCE_NONE = 0;
    public static final int PKCE_PLAIN = 1;
    public static final int PKCE_S256 = 2;

    public OAuth2Config() {
    }

    public void setDefaults(Object a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getServerAuthorizationEndpoint() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setServerAuthorizationEndpoint(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getAuthorizationScope() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setAuthorizationScope(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getServerTokenEndpoint() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setServerTokenEndpoint(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getClientSecretStr() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setClientSecretStr(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getClientIdStr() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setClientIdStr(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getAuthorizationCodeStr() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setAuthorizationCodeStr(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getAccessTokenStr() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setAccessTokenStr(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getRefreshTokenStr() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setRefreshTokenStr(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public long getAccessTokenValidUntil() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setAccessTokenValidUntil(long a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public int getDBId() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setDBId(int dbId) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getUserName() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setUserName(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String toXML(int a, String b, boolean c) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public static OAuth2Config fromXML(Object a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public static boolean fromXMLIsEnabled(Object a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getCustomParameterAuthorization() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setCustomParameterAuthorization(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public int getPKCE() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setPKCE(int a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public String getPKCEVerifier() {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

    public void setPKCEVerifier(String a) {
        throw new IllegalArgumentException("Not implemented in the community edition");
    }

}
