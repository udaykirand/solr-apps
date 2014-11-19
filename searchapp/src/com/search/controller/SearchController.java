/**
 * 
 */
package com.search.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.search.exceptions.InvalidReutersParameterException;
import com.search.utils.Constants;
import com.search.utils.URLGenerator;

/**
 * @author Uday
 * 
 */


public class SearchController extends AbstractController {

    private static final Logger LOG = Logger.getLogger(SearchController.class);

    private int port;

    private String appContext;

    private String servletName;

    protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) {
	LOG.info("Entered getSearchQuery method");
	final String viewName = "jsonresults";
	String qs = request.getQueryString();
	final String coreVal = request.getParameter("core");
	String exceptionData = "{\"responseHeader\":{\"status\":0,\"QTime\":3},\"response\":{\"numFound\":-1,\"start\":0,\"docs\":[]}}";
	if (coreVal == null || coreVal.equals("")) {
	    return new ModelAndView(viewName, "data", exceptionData);
	}

	try {
	    String baseQuery = "http://" + getHost() + ":" + getPort() + "/" + getAppContext() + "/" + coreVal + "/" + getServletName() + "/?";
	    if (coreVal != null && coreVal.equalsIgnoreCase(Constants.REUTERSCORE)) {
		if (qs.indexOf("&sort=") != -1)
		    qs = qs.substring(0, qs.indexOf("&sort=")) + "&" + qs.substring(qs.indexOf("&sort="));
	    }

	    LOG.debug("Modified Initial Solr Query [" + qs + "]");

	    URLGenerator urlGen = new URLGenerator(qs);
	    String solrQuery = urlGen.getSolrQuery();

	    if ((solrQuery != null) && (solrQuery.length() > 0)) {
		if (solrQuery.endsWith("&")) {
		    solrQuery = solrQuery.substring(0, solrQuery.length() - 1);
		}
		LOG.debug("Solr Query [" + solrQuery + "]");

		URL solrURL = new URL(baseQuery + solrQuery);

		LOG.debug("Final Solr Query [" + baseQuery + solrQuery + "]");

		URLConnection urlConn = solrURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF8"));
		String inputLine;
		StringBuffer sb = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
		    sb.append(inputLine);
		}
		in.close();

		return new ModelAndView(viewName, "data", sb.toString());
	    }
	} catch (InvalidReutersParameterException icp) {
	    exceptionData = "{\"responseHeader\":{\"status\":0,\"QTime\":3},\"response\":{\"numFound\":-2,\"start\":0,\"docs\":[]}}";
	} catch (IOException ioe) {
	    LOG.error("IOException occured while querying the Solr engine", ioe);
	} catch (Exception e) {
	    LOG.error("Exception occured while querying the Solr engine", e);
	}

	LOG.debug("Exited handleRequestInternal() method");

	return new ModelAndView(viewName, "data", exceptionData);
    }

    public String getHost() throws Exception {
	LOG.debug("Host Name[" + InetAddress.getLocalHost().getHostName() + "]");
	return InetAddress.getLocalHost().getHostName();
    }

    /**
     * @return the port
     */
    public int getPort() {
	return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
	this.port = port;
    }

    /**
     * @return the servletName
     */
    public String getServletName() {
	return servletName;
    }

    /**
     * @return the appContext
     */
    public String getAppContext() {
	return appContext;
    }

    /**
     * @param appContext
     *            the appContext to set
     */
    public void setAppContext(String appContext) {
	this.appContext = appContext;
    }
    
    /**
     * @param servletName
     *            the servletName to set
     */
    public void setServletName(String servletName) {
	this.servletName = servletName;
    }

}
