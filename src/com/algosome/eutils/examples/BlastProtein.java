package com.algosome.eutils.examples;

import org.apache.log4j.BasicConfigurator;

import com.algosome.eutils.blast.Blast;
import com.algosome.eutils.blast.BlastParser;
import com.algosome.eutils.blast.GetCommand;
import com.algosome.eutils.blast.PutCommand;

/**
 * Blastp example.
 * @author Greg Cope
 *
 */
public class BlastProtein {

	public static void main(String[] args) throws Exception{
		//configure log4j
		BasicConfigurator.configure();
		//set up blast request and formatting
	  	PutCommand put = new PutCommand();
    	put.setQuery("MTCLDELAHSLESKSGTTNSKTRNSKIKTIDLYQQNELSGQHSQDQDKFYRLPAMDPIARDKKPWKQDVN"+
    				"YFNKCYISSLALMKMCTHAQTGGSIEIMGMLVGKISGHSIIVMDTYRLPVEGTETRVNAQNEAYTYMVEY"+
    				"LTERQQLSNGKNEENIVGWYHRHPRYGCWLKGIDVSTQSLNQGLQDPYLAIVVDPVKTLKQGKVEIGAFR"+
    				"NVS");
    	put.setProgram("blastp");
    	put.setDatabase("nr");
    	//print to command line
    	GetCommand get = new GetCommand(new BlastParser(){

			@Override
			public void parseBlastOutput(String output) {
				System.out.println(output);
			}
    		
    	});
    	Blast blast = new Blast(put, get);
    	blast.run();
	}
}
