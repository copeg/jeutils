package com.algosome.eutils.blast;

/**
 * Defines a parser to parse blast output
 * @author Greg Cope
 *
 */
public interface BlastParser {

	/**
	 * Parses the blast output. 
	 * @param output
	 */
	public void parseBlastOutput(String output) ;
	
}
