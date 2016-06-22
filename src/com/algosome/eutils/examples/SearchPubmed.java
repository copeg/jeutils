package com.algosome.eutils.examples;

import org.apache.log4j.BasicConfigurator;

import com.algosome.eutils.EntrezSearch;
import com.algosome.eutils.JeUtils;
import com.algosome.eutils.util.OutputListener;
import com.algosome.eutils.util.OutputListenerAdapter;


/**
 * Searches pubmed for a specific gene and prints results to the command line
 * @author Greg Cope
 *
 */
public class SearchPubmed {

	
	public static void main(String[] args) throws Exception{
		//configure log4j
		BasicConfigurator.configure();
		//Create a new etils object and set the paramters for search	 
		JeUtils eutils = new JeUtils();
		eutils.getESearch().setDatabase(EntrezSearch.DB_PUBMED);
		eutils.getESearch().setParameter("-printefetch", "y");
		eutils.getESearch().setTerm("rri1");
		eutils.getESearch().setMaxRetrieval(1);
		
		//Print to command line
		eutils.addOutputListener(new OutputListenerAdapter(){

			@Override
			public void data(String s) {
				System.out.println(s);
			}
			
		});
		eutils.doRun();
		//print out the retrieved database IDs
		String ids = eutils.getESearch().getIds();
		System.out.println(ids);
		
	}
}
