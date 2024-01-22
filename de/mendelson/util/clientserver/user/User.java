//$Header: /as2/de/mendelson/util/clientserver/user/User.java 7     4/06/18 12:22p Heller $
package de.mendelson.util.clientserver.user;

import de.mendelson.util.security.PBKDF2;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * A single user for the client server system
 *
 * @author S.Heller
 * @version $Revision: 7 $
 */
public class User implements Serializable {

    public static final long serialVersionUID = 1L;
    private Map<Integer, String> permissions = new HashMap<Integer, String>();
    private String name = null;
    private String passwdCrypted = null;
    private PermissionDescription permissionDescription = new DefaultPermissionDescription();

    public User() {
    }

    public static String cryptPassword(char[] password) {
        try {
            return (PBKDF2.generateStrongPasswordHash(new String(password)));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String serialize(User user) {
        StringBuilder builder = new StringBuilder();
        builder.append(user.getName());
        builder.append("::");
        builder.append(user.getPasswdCrypted());
        for (int i = 0; i < 10; i++) {
            builder.append(":");
            builder.append(user.getPermission(i));
        }
        return (builder.toString());
    }

    /**
     * Returns null if the parsing process fails somehow
     * user:clean_passwd:crypted_passwd:permission1:permission2:permissionN
     */
    public static User parse(String userLine) {
        try {
            String[] token = userLine.split(":");
            User user = new User();
            user.setName(token[0]);
            //set the password
            if (token.length > 0 && token[1] != null && token[1].length() > 0) {
                user.setPasswdCrypted(cryptPassword(token[1].toCharArray()));
            } else if (token.length > 1 && token[2] != null && token[2].length() > 0) {
                user.setPasswdCrypted(token[2]);
            }
            int permissionOffset = 3;
            for (int i = permissionOffset; i < token.length; i++) {
                user.setPermission(i - permissionOffset, token[i]);
            }
            return (user);
        } catch (Throwable e) {
            e.printStackTrace();
            return (null);
        }
    }

    public void setPermissionDescription(PermissionDescription permissionDescription) {
        this.permissionDescription = permissionDescription;
    }

    /**
     * Will always return a NONE null value
     */
    public String getPermission(Integer index) {
        if (this.permissions.containsKey(index)) {
            return (this.permissions.get(index));
        }
        return ("");
    }

    public void setPermission(int index, String permission) {
        this.permissions.put(Integer.valueOf(index), permission);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.name != null ? this.name : "[null]");
        buffer.append(" (");
        boolean first = true;
        Iterator<Integer> iterator = this.permissions.keySet().iterator();
        while (iterator.hasNext()) {
            Integer index = iterator.next();
            String entry = this.permissions.get(index);
            if (entry != null && entry.length() > 0) {
                if (!first) {
                    buffer.append(",");
                } else {
                    first = false;
                }
                buffer.append("Permission[" + String.valueOf(index)).append("]=").append(entry);
                String description = this.permissionDescription.getDescription(index.intValue());
                if (description != null) {
                    buffer.append("[Description=\"").append(description).append("\"]");
                }
            }
        }
        buffer.append(")");
        return (buffer.toString());
    }

    /**
     * @return the passwdCrypted
     */
    public String getPasswdCrypted() {
        return passwdCrypted;
    }

    /**
     * @param passwdCrypted the passwdCrypted to set
     */
    public void setPasswdCrypted(String passwdCrypted) {
        this.passwdCrypted = passwdCrypted;
    }
}
