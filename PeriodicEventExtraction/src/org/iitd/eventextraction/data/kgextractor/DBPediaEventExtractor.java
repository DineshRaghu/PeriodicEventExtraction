package org.iitd.eventextraction.data.kgextractor;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class DBPediaEventExtractor {

	String DBPEDIA_PREFIX = "http://dbpedia.org";
	String DBPEDIA_ONTOLOGY_PREFIX = "http://dbpedia.org/ontology";
	String DBPEDIA_RESOURCE_PREFIX = "http://dbpedia.org/resource";
	String DBPEDIA_PROPERTY_PREFIX = "http://dbpedia.org/property";
	
	String PREFIX =   "PREFIX dbont: <http://dbpedia.org/ontology/> " 
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
					+ "PREFIX dbp: <http://dbpedia.org/property/> "
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";

	public void GetDBPediaEntries() {
		
		String sparqlQueryString = PREFIX +
				"select ?event ?label ?freq where " + 
				"{ " +
					"?event rdf:type dbont:Event . " +
					"OPTIONAL{ ?event rdfs:label ?label . } " +
					"OPTIONAL{ ?event dbp:frequency ?freq . } " +
					"FILTER(LANGMATCHES(LANG(?label), \"en\")) " +  
				"}";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

		ResultSet results = qexec.execSelect();
		while(results.hasNext())
	    {
	      QuerySolution soln = results.nextSolution() ;
	      
	      @SuppressWarnings("unused")
		  String eventURI 		= soln.get("event").toString();
	      
	      String eventLabel 	= soln.get("label") == null ? "" : soln.get("label").toString();
	      String eventFreq 		= soln.get("freq") == null ? "" : soln.get("freq").toString();
	      
	      if(!eventFreq.equals(""))
	    	  System.out.println(eventLabel + "\n\t" + eventFreq);
	    }
		
		qexec.close() ;
	}
	
	public static void main(String[] args) {
		DBPediaEventExtractor dee = new DBPediaEventExtractor();
		dee.GetDBPediaEntries();
	}
}
