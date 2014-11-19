package com.search.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.search.exceptions.InvalidReutersParameterException;
import com.search.exceptions.InvalidReutersQueryParameterException;

/**
 * This is the REUTERS specific validator class. 
 * 
 * @author 271299
 * 
 */
public class QueryValidator {

    private String params[] = {};
    StringBuilder builder = new StringBuilder();
    private String[] customerCareFacets = { "gradeRange_facet", "productType_facet", "kitFlag_facet", "copyrightYear_facet", "eGoodFlag_facet", "publicationStatus_facet", "author_facet",
	    "imprint_facet", "professionalDevelopmentFlag_facet", "hierarchySubjectName_facet", "state_facet", "commonCore_facet", "sellableProduct_facet", "punchOutAvailable_CH_facet",
	    "punchOutAvailable_MI_facet", "languagesDescription_facet" };

    private final String FACET_FIELD = "facet.field";

    private final String FACET_PARAMS = "facet.params";

    private Logger logger = Logger.getLogger(QueryValidator.class);

    public QueryValidator(String[] solrParams) {
	this.params = solrParams;
    }

    /**
     * This method will validate the customer care specific parameters as per the business requirement.
     * 
     * @return
     * @throws Exception
     */
    public String validate() throws Exception {
	logger.debug("Entered validate() method");

	ArrayList<String> facets = new ArrayList<String>(Arrays.asList(customerCareFacets));

	String pName = "";
	String pValue = "";

	for (String param : params) {
	    pName = param.substring(0, param.indexOf("="));
	    pValue = param.substring(param.indexOf("=") + 1);

	    logger.debug("Paramater[" + pName + "]");
	    logger.debug("Value[" + pValue + "]");

	    if (null != pName && !"".equalsIgnoreCase(pName.trim()) && null != pValue && !"".equalsIgnoreCase(pValue.trim())) {

		if (FACET_PARAMS.equalsIgnoreCase(pName.trim()) && "ALL".equals(pValue.trim().toUpperCase())) {
		    // Preparing all Facets
		    prepareFacetParams();
		} else if ("sort".equalsIgnoreCase(pName.trim()) && !pValue.trim().isEmpty()) {
		    // Appending sort order details
		    builder.append(sortOrderMap.get(pValue.trim().toLowerCase()));
		} else {
		    if (FACET_FIELD.equalsIgnoreCase(pName.trim()) && !facets.contains(pValue.trim()))
			throw new InvalidReutersQueryParameterException("Facet [" + pValue + "] not found in the Query Parameters");

		    if ("q".equalsIgnoreCase(pName.trim()) && "*".equalsIgnoreCase(pValue.trim())) {
			pValue = "";
		    }
		    // Reueters specific search filtering to resolve defects
		    else if ("q".equalsIgnoreCase(pName.trim())) {
			pValue = getFilteredQuery(pValue);
		    }
		    if ("rows".equalsIgnoreCase(pName.trim()) && (Integer.parseInt(pValue.trim()) > 500)) {
			pValue = "500";
		    }
		    if (!"dTime".equals(pName))
			builder.append(pName.trim() + "=" + pValue.trim() + "&");
		}
	    }
	}

	logger.debug("Exited validate() method");

	return builder.toString();
    }

    private static final Set<String> stopWordsSet = new HashSet<String>();

    private static final Map<String, String> sortOrderMap = new HashMap<String, String>();

    static {
	stopWordsSet.add("a");
	stopWordsSet.add("an");
	stopWordsSet.add("and");
	stopWordsSet.add("are");
	stopWordsSet.add("as");
	stopWordsSet.add("at");
	stopWordsSet.add("be");
	stopWordsSet.add("but");
	stopWordsSet.add("by");
	stopWordsSet.add("for");
	stopWordsSet.add("if");
	stopWordsSet.add("in");
	stopWordsSet.add("into");
	stopWordsSet.add("is");
	stopWordsSet.add("it");
	stopWordsSet.add("no");
	stopWordsSet.add("not");
	stopWordsSet.add("of");
	stopWordsSet.add("on");
	stopWordsSet.add("or");
	stopWordsSet.add("s");
	stopWordsSet.add("such");
	stopWordsSet.add("t");
	stopWordsSet.add("that");
	stopWordsSet.add("the");
	stopWordsSet.add("their");
	stopWordsSet.add("then");
	stopWordsSet.add("there");
	stopWordsSet.add("these");
	stopWordsSet.add("they");
	stopWordsSet.add("this");
	stopWordsSet.add("to");
	stopWordsSet.add("was");
	stopWordsSet.add("will");
	stopWordsSet.add("with");

	sortOrderMap.put("standard", "sort=publicationStatusRank+asc,copyrightYear+desc,productHierarchy+asc,stateRank+asc,productTypeRank+asc&");
	sortOrderMap.put("title_temp", "sort=title_temp+asc&");
	sortOrderMap.put("isbn", "sort=isbn+asc&");
	sortOrderMap.put("material", "sort=material+asc&");

    }

    private String getFilteredQuery(String pValue) throws InvalidReutersParameterException {

	// remove all single quote characters
	pValue = pValue.replaceAll("'", "");

	// replace hyphen with a space character.
	pValue = pValue.replaceAll("-", "%20");

	// remove any stop words from the query
	StringBuilder builder = new StringBuilder();
	String[] tokens = pValue.split("%20");
	int count = 0;
	for (String token : tokens) {
	    count++;
	    logger.debug("Token words [" + token + "]");
	    if (!stopWordsSet.contains(token.toLowerCase())) {
		builder.append(token);

		if (count != tokens.length) {
		    builder.append("%20");
		}
	    }
	}
	logger.debug("builder data [" + builder + "]");
	if (builder.toString().trim().length() <= 0) {
	    logger.error("Search term is empty or stopwords :Please check your search string");
	    throw new InvalidReutersParameterException("Please check your search string");
	}
	return builder.toString();
    }

    private void prepareFacetParams() {

	logger.debug("Preparing Facet params for 'ALL'");
	for (String str : customerCareFacets) {
	    logger.debug("Prepare[" + FACET_FIELD + "]");
	    logger.debug("Value[" + str + "]");
	    builder.append(FACET_FIELD + "=" + str.trim() + "&");
	}

    }
}
