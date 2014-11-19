package com.search.utils;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;



/**
 * @author 271299
 * 
 */
public class URLGenerator {

    String[] urlParams = {};
    String numParams = ",facet.limit,facet.mincount,rows,start,";
    String boolParams = ",spellcheck,facet,";
    String coreValue = "";
    String solrQuery = "";

    private static final Logger LOG = Logger.getLogger(URLGenerator.class);

    public URLGenerator(String url) {
	if (url != null)
	    urlParams = url.split("&");
    }

    /**
     * This method will return the refined/valid solr query.
     * 
     * @return String solr Query
     */
    public String getSolrQuery() throws Exception {
	LOG.debug("Entered getSolrQuery() method");
	StringBuffer urlSB = new StringBuffer();

	String pName = "";
	String pValue = "";

	boolean validatorFlag = true;

	for (String param : urlParams) {

	    LOG.info("URL Param [" + param + "]");

	    if (param.indexOf("=") >= 0) {
		pName = param.substring(0, param.indexOf("="));
		pValue = param.substring(param.indexOf("=") + 1);

		validatorFlag = validateParams(pName.trim(), pValue.trim());

		if (pName.equalsIgnoreCase("core")) {
		    coreValue = pValue;
		}
		if ("q".equalsIgnoreCase(pName)) {
		    pValue = validateSearchTerm(pValue);
		    param = "q=" + pValue;

		    if (pValue.equalsIgnoreCase("*")) {
			urlSB = new StringBuffer();
			urlSB.append("q.alt=*:*&");
			param = "q=*";
			LOG.info("Q value is *");
		    }

		}

		if (!validatorFlag) {
		    if ("rows".equals(pName)) {
			LOG.info("Invalid value assigned to rows param, Performing search with default value [" + Constants.DEFAULT_ROWS + "]");
			param = "rows=" + Constants.DEFAULT_ROWS;
		    } else if ("facet.mincount".equals(pName)) {
			LOG.info("Invalid value assigned to facet.mincount param, Performing search with default value [" + Constants.DEFAULT_FACET_MINCOUNT + "]");
			param = "facet.mincount=" + Constants.DEFAULT_FACET_MINCOUNT;
		    } else if ("facet.limit".equals(pName)) {
			LOG.info("Invalid value assigned to facet.limit param, Performing search with default value [" + Constants.DEFAULT_FACET_LIMIT + "]");
			param = "facet.limit=" + Constants.DEFAULT_FACET_LIMIT;
		    } else if ("facet".equals(pName)) {
			LOG.info("Invalid value assigned to facet param, Performing search with default value [" + Constants.DEFAULT_FACET + "]");
			param = "facet=" + Constants.DEFAULT_FACET;
		    } else if ("spellcheck".equals(pName)) {
			LOG.info("Invalid value assigned to spellcheck param, Performing search with default value [" + Constants.DEFAULT_SPELLCHECK + "]");
			param = "spellcheck=" + Constants.DEFAULT_SPELLCHECK;
		    } else {
			return "";
		    }
		}

		urlSB.append(param + "&");
	    }
	}
	if (coreValue.equalsIgnoreCase(Constants.REUTERSCORE)) {
	    QueryValidator tValidator = new QueryValidator(urlSB.toString().split("&"));
	    solrQuery = tValidator.validate();
	} else {
	    solrQuery = urlSB.toString();
	}
	LOG.debug("Exited getSolrQuery() method");
	return solrQuery;

    }

    /**
     * This method will validate all the parameters and return boolean value
     * 
     * @param name
     *            parameter name
     * @param value
     *            parameter value
     * @return boolean
     */
    private boolean validateParams(String name, String value) {
	LOG.debug("Entered validateParams() method");
	if ("stream.body".equals(name) && (value.contains("delete") || value.equals("commit"))) {
	    LOG.debug("Exited validateParams() method");
	    return false;
	}

	if (numParams.contains("," + name + ",")) {
	    try {
		Integer.parseInt(value);
	    } catch (NumberFormatException nfe) {
		LOG.debug("Exited validateParams() method");
		return false;
	    }
	} else if (boolParams.contains("," + name + ",")) {
	    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
		LOG.debug("Exited validateParams() method");
		return false;

	    }
	}
	LOG.debug("Exited validateParams() method");
	return true;
    }

    /**
     * This method will validate the search term and set the default search term if the search termdosn't contain any valid chars.
     * 
     * @param sTerm
     * @return
     */
    private String validateSearchTerm(String sTerm) {
	LOG.debug("Entered validateSearchTerm() method");
	LOG.info("User provided search term [" + sTerm + "]");
	Pattern p = Pattern.compile("[a-zA-Z0-9]+");

	if (p.matcher(sTerm).find()) {
	    LOG.debug("Exited validateSearchTerm() method");
	    return sTerm;
	} else {
	    LOG.info("Invalid searchTerm, Performing search with default searchTerm [" + Constants.DEFAULT_SEARCH_TERM + "]");
	    LOG.debug("Exited validateSearchTerm() method");
	    return Constants.DEFAULT_SEARCH_TERM;
	}
    }

    public static void main(String str[]) {
	Pattern p = Pattern.compile("[a-zA-Z0-9]+");

	if (p.matcher("&*(&()&)&()&)&").find()) {
	    System.out.println("valid");
	} else {
	    System.out.println("invalid");
	}
    }
}
