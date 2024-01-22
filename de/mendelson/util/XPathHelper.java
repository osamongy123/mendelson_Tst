//$Header: /as4/de/mendelson/util/XPathHelper.java 30    27/10/22 13:25 Heller $
package de.mendelson.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.XPathSyntaxException;
import org.jaxen.dom.DOMXPath;
import org.jaxen.dom.NamespaceNode;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
/**
 * Helper to get some values out of XML files, this could be reimplemented in
 * XPATH syntax if a nice API exists. Using this class you could check existing
 * parameters of XPATH pathes, get values of nodes ...
 *
 * @author S.Heller
 * @version $Revision: 30 $
 */
public class XPathHelper {

    private Logger logger = Logger.getAnonymousLogger();
    /**
     * Document to look into
     */
    private Document document = null;
    /**
     * Namespace to set to the xpath that is used
     */
    private SimpleNamespaceContext namespaceContext = null;
    //Stores all defined namespaces to look up the alias by providing the URI. Its [URI,ALIAS]
    private final Map<String, String> namespaceLookupMap = new ConcurrentHashMap<String, String>();

    /**
     * Parses the passed filename document and creates a DOM document
     *
     * @param filename Name of the xml file to parse
     */
    public XPathHelper(String filename) throws Exception {
        InputStream inStream = null;
        try {
            inStream = Files.newInputStream(Paths.get(filename));
            this.parse(new InputSource(inStream));
        } finally {
            if (inStream != null) {
                inStream.close();
            }
        }
    }

    public XPathHelper(InputSource source) throws Exception {
        this.parse(source);
    }

    public XPathHelper(Document document) throws Exception {
        this.parse(document);
    }

    public XPathHelper(InputStream inStream) throws Exception {
        this.parse(new InputSource(inStream));
    }

    private void parse(InputSource source) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) throws SAXException {
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw (exception);
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw (exception);
            }

        });
        Document parseDocument = builder.parse(source);
        this.parse(parseDocument);
    }

    private void parse(Document document) {
        this.document = document;
        synchronized (this.document) {
            this.namespaceLookupMap.clear();
            this.namespaceContext = null;
            //add all found namespaces
            try {
                Map<String, String> tempNSMap = this.extractNamespaces();
                this.addNamespaces(tempNSMap);
            } catch (Exception e) {
            }
        }
    }

    /**
     * Adds a new, user defined namespace alias to the xpath processing. This is
     * necessary for xml data that contains something like
     * <semiramis xmlns="com.cisag.app.sales.obj.SalesOrder">
     * ...
     * </semiramis>
     */
    public void addNamespace(String alias, String uri) {
        if (this.namespaceContext == null) {
            this.namespaceContext = new SimpleNamespaceContext();
        }
        this.namespaceContext.addNamespace(alias, uri);
        this.namespaceLookupMap.put(uri, alias);
    }

    /**
     * Adds namespaces to the xpath processing. This is necessary for xml data
     * that contains something like
     * <semiramis xmlns="com.cisag.app.sales.obj.SalesOrder">
     * ...
     * </semiramis>
     */
    public void addNamespaces(Map<String, String> map) {
        for (String alias : map.keySet()) {
            String uri = map.get(alias);
            this.addNamespace(alias, uri);
        }
    }

    /**
     * Returns the alias name for a special namespace URI. This is required if
     * the xpath expression requires the alias and this is a variable value,
     * e.g. //xsi:Test/dsx:Test2
     *
     * @param namespaceURI
     * @return Null if the namespace URI is not assigned to an alias
     */
    public String getAliasForNamespace(String namespaceURI) {
        return (this.namespaceLookupMap.getOrDefault(namespaceURI, null));
    }

    /**
     * Gets the value of a node given by the node path, the node path has the
     * syntax /value/value where every path could have a repeat in [] like
     * /value/value/value[3]/value, without a repeat you are looking for the 1th
     * element
     *
     * @param nodePath Path to look for
     */
    public String getValue(String nodePath) throws Exception {
        try {
            XPath xPath = new DOMXPath(nodePath);
            if (this.namespaceContext != null) {
                xPath.setNamespaceContext(this.namespaceContext);
            }
            synchronized (this.document) {
                return (xPath.stringValueOf(this.document));
            }
        } catch (XPathSyntaxException e) {
            throw new Exception(e.getMultilineMessage());
        }
    }

    /**
     * Returns if a node of a passed nodepath exists in the actual document
     *
     * @param nodePath Path to look for
     */
    public boolean pathExists(String nodePath) throws Exception {
        XPath xPath = null;
        try {
            xPath = new DOMXPath(nodePath);
            if (this.namespaceContext != null) {
                xPath.setNamespaceContext(this.namespaceContext);
            }
        } catch (XPathSyntaxException e) {
            throw new Exception(e.getMultilineMessage());
        }
        synchronized (this.document) {
            return (xPath.selectSingleNode(this.document) != null);
        }
    }

    /**
     * Counts the number of results for the passed path. This is useful to
     * iterate on the results of an xpath if there could be more than one:
     * <a>vv</a>
     * <a>xy</a>
     * <a>dd</a>
     *
     * will return 3 and its possible the gain the single branch values adding a
     * a[1], a[2] or a[3] to the XPath.
     */
    public int getNodeCount(String nodePath) throws Exception {
        XPath xPath = null;
        try {
            xPath = new DOMXPath("count(" + nodePath + ")");
            if (this.namespaceContext != null) {
                xPath.setNamespaceContext(this.namespaceContext);
            }
        } catch (XPathSyntaxException e) {
            throw new Exception(e.getMultilineMessage());
        }
        synchronized (this.document) {
            Object object = xPath.evaluate(this.document);
            if (object == null) {
                return (0);
            }
            //double is expected here
            if (object instanceof Double) {
                double counter = ((Double) object).doubleValue();
                return ((int) counter);
            }
        }
        //should not happen
        return (0);
    }

    /**
     * Returns a list of nodes of the passed xpath expression
     */
    public List getNodes(String nodePath) throws Exception {
        XPath xPath = null;
        try {
            xPath = new DOMXPath(nodePath);
            if (this.namespaceContext != null) {
                xPath.setNamespaceContext(this.namespaceContext);
            }
        } catch (XPathSyntaxException e) {
            throw new Exception(e.getMultilineMessage());
        }
        synchronized (this.document) {
            Object object = xPath.evaluate(this.document);
            if (object == null) {
                return (new ArrayList());
            }
            if (object instanceof List) {
                return ((List) object);
            }
            List returnList = new ArrayList();
            returnList.add(object);
            return (returnList);
        }
    }

    /**
     * Returns all namespaces used in the passed document
     */
    public Map<String, String> extractNamespaces() throws Exception {
        Map<String, String> nsMap = new HashMap<String, String>();
        //get all namespace nodes
        List list = this.getNodes("//namespace::*");
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof NamespaceNode) {
                NamespaceNode nsNode = (NamespaceNode) list.get(i);
                nsMap.put(nsNode.getNodeName(), nsNode.getNodeValue());
            }
        }
        return (nsMap);
    }

    public static void main(String[] args) {
//        try {
//            XPathHelper helper = new XPathHelper("c:/temp/test.xml");
//            //helper.addNamespace( "x", "com.cisag.app.sales.obj.SalesOrder" );
//            long start = System.currentTimeMillis();
//            System.out.println("nodesOld=" + helper.getNodeCount("/List/RECADV/SG16/SG22/DTM"));
//            System.out.println(System.currentTimeMillis() - start + "ms");
//            start = System.currentTimeMillis();
//            System.out.println("existsOld=" + helper.pathExists("/List/RECADV/SG16/SG22/DTM1"));
//            System.out.println(System.currentTimeMillis() - start + "ms");
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

    }
}
