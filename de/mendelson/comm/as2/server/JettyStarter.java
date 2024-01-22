//$Header: /as2/de/mendelson/comm/as2/server/JettyStarter.java 4     2/05/22 10:33 Heller $
package de.mendelson.comm.as2.server;

import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfo;
import de.mendelson.util.httpconfig.server.HTTPServerConfigInfoProcessor;
import de.mendelson.util.systemevents.SystemEvent;
import de.mendelson.util.systemevents.SystemEventManagerImplAS2;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;
import java.util.logging.Logger;


/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Helper class that starts up the internal jetty web server
 *
 * @author S.Heller
 * @version $Revision: 4 $
 */
public class JettyStarter {

    private final String MODULE_NAME;
    private final MecResourceBundle rb;
    private HTTPServerConfigInfo httpServerConfigInfo = null;
    private final Logger logger;

    public JettyStarter(Logger logger) {
        this.logger = logger;
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleJettyStarter.class.getName());
        } //load up  resourcebundle
        catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.MODULE_NAME = this.rb.getResourceString("module.name");
    }

    /**
     * starts the web server
     */
    public Server startWebserver() throws Exception {
        this.logger.info(MODULE_NAME + " " + this.rb.getResourceString("httpserver.willstart"));
        SystemEventManagerImplAS2.newEvent(
                SystemEvent.SEVERITY_INFO,
                SystemEvent.ORIGIN_SYSTEM,
                SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN,
                rb.getResourceString("httpserver.willstart"),
                "");
        try {
            //Read the user defined properties file to overwrite the default settings of the
            //jetty.xml file
            Properties userConfiguration = new Properties();
            InputStream userConfigurationStream = null;
            Path userConfigurationFile = Paths.get("jetty10", "jetty.config");
            this.logger.info(MODULE_NAME + " " + this.rb.getResourceString("userconfiguration.reading",
                    userConfigurationFile.toAbsolutePath().toString()));
            try {
                userConfigurationStream = Files.newInputStream(userConfigurationFile);
                userConfiguration.load(userConfigurationStream);
            } catch (Exception e) {
                this.logger.warning(MODULE_NAME + " " + this.rb.getResourceString("userconfiguration.readerror",
                        new Object[]{
                            userConfigurationFile.toAbsolutePath().toString(),
                            "[" + e.getClass().getSimpleName() + "] " + e.getMessage()
                        }));
            } finally {
                if (userConfigurationStream != null) {
                    userConfigurationStream.close();
                }
            }
            Map<String, String> userConfigurationMap = new HashMap<String, String>();
            for (Object key : userConfiguration.keySet()) {
                String keyStr = key.toString();
                String valueStr = userConfiguration.getProperty(key.toString());
                userConfigurationMap.put(keyStr, valueStr);
                this.logger.info( MODULE_NAME + " " + this.rb.getResourceString("userconfiguration.setvar",
                        new Object[]{
                            keyStr, 
                            valueStr
                        }));
            }
            //org.eclipse.jetty.start.Main.main(new String[0]);
            Path jettyXMLConfigurationPath = Paths.get("jetty10", "etc", "jetty.xml");
            Resource jettyConfigResource = Resource.newResource(jettyXMLConfigurationPath);
            XmlConfiguration jettyXMLConfiguration = new XmlConfiguration(jettyConfigResource);
            jettyXMLConfiguration.getProperties().putAll(userConfigurationMap);
            org.eclipse.jetty.server.Server tempHTTPServer = new org.eclipse.jetty.server.Server();
            jettyXMLConfiguration.configure(tempHTTPServer);
            //add life cycle listener to jetty
            tempHTTPServer.addEventListener(new LifeCycle.Listener() {
                @Override
                public void lifeCycleStarted(LifeCycle lifeCycle) {
                    logger.info(MODULE_NAME + " " + rb.getResourceString("httpserver.running", "Jetty " + Server.getVersion()));
                }

                @Override
                public void lifeCycleStarting(LifeCycle lifeCycle) {
                }

                @Override
                public void lifeCycleFailure(LifeCycle lifeCycle, Throwable failure) {
                    logger.info(MODULE_NAME + " " + rb.getResourceString("httpserver.startup.problem",
                            "[" + failure.getClass().getSimpleName() + "] " + failure.getMessage()));
                    failure.printStackTrace();
                }

                @Override
                public void lifeCycleStopped(LifeCycle lifeCycle) {
                    logger.info(MODULE_NAME + " " + rb.getResourceString("httpserver.stopped"));
                }
            });
            //add bean listener to jetty
            tempHTTPServer.addEventListener(new Container.InheritedListener() {
                @Override
                public void beanAdded(Container parent, Object child) {
                    //Acceptor is a private class in AbstractConnector...
                    if (parent instanceof ServerConnector
                            && child.getClass().getName().equals("org.eclipse.jetty.server.AbstractConnector$Acceptor")) {
                        ServerConnector connector = (ServerConnector) parent;
                        String connectorStr = String.format("(%s{%s:%d})",
                                connector.getDefaultProtocol(),
                                connector.getHost() == null ? "0.0.0.0" : connector.getHost(),
                                connector.getLocalPort() <= 0 ? connector.getPort() : connector.getLocalPort());
                        logger.info(MODULE_NAME + " " + rb.getResourceString("listener.started", connectorStr));
                    }
                }

                @Override
                public void beanRemoved(Container parent, Object child) {
                }
            }
            );
            tempHTTPServer.start();
            //ensure the wars have been deployed

            for (Handler handler : tempHTTPServer.getChildHandlersByClass(WebAppContext.class
            )) {
                WebAppContext context = (WebAppContext) handler;
                //see if wars had any exceptions that would cause it to be unavailable
                if (context.getUnavailableException()
                        != null) {
                    this.logger.warning(MODULE_NAME + " " + this.rb.getResourceString("deployment.failed",
                            new Object[]{
                                context.getDisplayName(),
                                "["
                                + context.getUnavailableException().getClass().getSimpleName()
                                + "] " + context.getUnavailableException().getMessage()
                            }));
                    SystemEventManagerImplAS2.newEvent(
                            SystemEvent.SEVERITY_WARNING,
                            SystemEvent.ORIGIN_SYSTEM,
                            SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN,
                            context.getDisplayName(),
                            this.rb.getResourceString("deployment.failed",
                                    new Object[]{
                                        context.getDisplayName(),
                                        "["
                                        + context.getUnavailableException().getClass().getSimpleName()
                                        + "] " + context.getUnavailableException().getMessage()
                                    }));
                } else {
                    this.logger.info(MODULE_NAME + " " + this.rb.getResourceString("deployment.success",
                            context.getDisplayName()));
                }
            }
            this.httpServerConfigInfo = HTTPServerConfigInfo.computeHTTPServerConfigInfo(tempHTTPServer, true,
                    "/as2/HttpReceiver", "/as2/ServerState");
            HTTPServerConfigInfoProcessor infoProcessor = new HTTPServerConfigInfoProcessor(this.getHttpServerConfigInfo());
            StringBuilder body = new StringBuilder();
            body.append(infoProcessor.getMiscConfigurationText());
            SystemEventManagerImplAS2.newEvent(SystemEvent.SEVERITY_INFO,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_HTTP_SERVER_RUNNING,
                    rb.getResourceString("httpserver.running",
                            "jetty " + this.getHttpServerConfigInfo().getJettyHTTPServerVersion()),
                    body.toString());
            return (tempHTTPServer);
        } catch (Exception e) {
            SystemEventManagerImplAS2.newEvent(
                    SystemEvent.SEVERITY_ERROR,
                    SystemEvent.ORIGIN_SYSTEM,
                    SystemEvent.TYPE_HTTP_SERVER_STARTUP_BEGIN,
                    rb.getResourceString("httpserver.willstart"),
                    "[" + e.getClass().getSimpleName() + "]: " + e.getMessage());
            throw e;
        }
    }

    /**
     * @return the httpServerConfigInfo
     */
    public HTTPServerConfigInfo getHttpServerConfigInfo() {
        return httpServerConfigInfo;
    }

}
