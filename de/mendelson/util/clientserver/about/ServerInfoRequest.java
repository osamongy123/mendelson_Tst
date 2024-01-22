//$Header: /as2/de/mendelson/util/clientserver/about/ServerInfoRequest.java 11    14/06/22 16:02 Heller $
package de.mendelson.util.clientserver.about;

import de.mendelson.util.clientserver.messages.ClientServerMessage;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */

/**
 * Msg for the client server protocol
 *
 * @author S.Heller
 * @version $Revision: 11 $
 */
public class ServerInfoRequest extends ClientServerMessage implements Serializable {

    public static final long serialVersionUID = 1L;
    public static final String SERVER_FULL_PRODUCT_NAME = "full_product_name";
    public static final String SERVER_PRODUCT_NAME = "serverprodname";
    public static final String SERVER_VERSION = "serverversion";
    public static final String SERVER_BUILD = "serverbuild";
    public static final String SERVER_BUILD_DATE = "server_build_date";
    public static final String SERVER_START_TIME = "serverstarttime";
    public static final String SERVER_USER = "serveruser";
    public static final String SERVER_VM_VERSION = "servervmversion";
    public static final String SERVER_OS = "serveros";
    public static final String SERVER_MAX_HEAP_GB = "serverheap_max_heap_gb";
    public static final String SERVER_CPU_CORES = "serverheap_cpu_cores";
    public static final String SERVERSIDE_TRANSACTION_COUNT = "transaction_count";
    public static final String SERVERSIDE_PID = "process_id_server";
    public static final String CLIENTSIDE_PID = "process_id_client";
    public static final String SERVER_START_METHOD_WINDOWS_SERVICE = "is_windows_service";
    public static final String LICENSEE = "server_licensee";
    public static final String SERVER_PATCH_LEVEL = "version_patch_level";    
    public static final String SERVER_LOCALE = "server_locale";    
    public static final String HTTP_SERVER_VERSION = "http_server_version";    
    public static final String DB_SERVER_VERSION = "db_server_version";    
    public static final String DB_CLIENT_VERSION = "db_client_version";    
    public static final String PLUGINS = "plugins";    
    public static final String DIR_POLL_THREAD_COUNT = "dir_poll_thread_count";    
    public static final String DIR_POLL_THREADS_PER_MIN = "dir_poll_threads_per_min";    
    public static final String UNIQUE_INSTANCE_ID = "unique_instance_id";    
    public static final String HA_NUMBER_OF_NODES_LAST_30DAYS = "ha_nodes_last_30_days";    
    public static final String EXPIRE_DATE = "expire_date";    
    
    /**32 or 64 bit?*/
    public static final String JVM_DATA_MODEL = "jvm_data_model";

    private long clientPID = -1;

    public ServerInfoRequest() {
        //process id client
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        this.clientPID = Long.valueOf(runtimeBean.getName().split("@")[0]);
    }

    @Override
    public String toString() {
        return ("Request server info");
    }

    /**
     * @return the clientPID
     */
    public long getClientPID() {
        return clientPID;
    }

    /**
     * @param clientPID the clientPID to set
     */
    public void setClientPID(long clientPID) {
        this.clientPID = clientPID;
    }

}
