package com.algosome.eutils;
/**
JeUtils.java

Created by Greg Cope.
Copyright © 2009 Algosome. All rights reserved.

JeUtils Project
Initial Version by:
Greg Cope
website: www.algosome.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
or visit
<http://www.gnu.org/licenses/>.
**/

import com.algosome.eutils.*;
import com.algosome.eutils.util.OutputListener;

import java.util.HashMap;
import java.io.*;


/**
*Main Class to provide a Basic Unix input to query NCBI. To construct a valid query to NCBI, users must set the -term flag with valid search terms on the command line. 
*Other flags include:</p><p>
*<p><b>-db</b>     Entrez Database</p>
*<p style="padding-left:20px;">              Database Options</p>
*<p>               <table style="padding-left:20px;"><tr><td>nucleotide, protein, pubmed, nuccore, nucest, structure, genome,</td>
*                <td>biosystems, blastdbinfo, books, cancerchromosomes, cdd, gap, </td>
*                <td>domains, gene, genomeprj, gensat, geo, gds, </td>
*                <td>homologene, homologene journals, mesh, ncbisearch, omia, nlmcatalog, </td>
*               <td>omim, pepdome, pmc, popset, probe, proteinclusters,</td>
*              <td> pcassay, pccompound, pcsubstance, snp, sra, taxonimy, </td>
*              <td> toolkit, unigene, unists</td></tr></table>
*        <b>-o</b>      Save the genbank eFetch output to the file path provided. Warning: this will </p>
*<p style="padding-left:20px;">                overwrite any file with the same name. Relative paths are relative </p>
*<p style="padding-left:20px;">               to the calling object (Main.class or J-eUtils.jar)</p>
*<p>       <b>-os</b>     Save the genbank eSearch output to the file path provided.Warning: this will </p>
*<p style="padding-left:20px;">                overwrite any file with the same name. Relative paths are relative </p>
*<p style="padding-left:20px;">                to the calling object (Main.class or J-eUtils.jar)</p>
*<p>        <b>-printesearch</b>   Flag to indicate whether to print the eSearch output. y or n. Default is n.</p>
*<p>        <b>-printefetch</b>    Flag to indicate whether to print the eFetch output. y or n. Default is y.</p>
*<p>      <b> -rettype</b>        The retrieval mode. eSearch defaults to XML. eFetch allows the following retrieval modes:</p>
*<table style="padding-left:20px"><tr><td><h4><u>rettype</u></h4></td><td><h4><u>scope</u></h4></td></tr>
*<tr><td>native</td><td>All but gene</td></tr>
*<tr><td> fasta    </td><td>       sequence only</td></tr>
*<tr><td> gb       </td><td>       Nucleotide Sequence Only</td></tr>
*<tr><td>gp            </td><td>  Protein Sequence Only</td></tr>
*<tr><td>gvwtgoarts </td><td>             Nucleotide Sequence Only</td></tr>
*<tr><td> gbc      </td><td>       Nucleotide Sequence Only</td></tr>
*<tr><td>gpc       </td><td>      Protein Sequence Only</td></tr>
*<tr><td>est        </td><td>     dbEST Sequence Only</td></tr>
*<tr><td>gss        </td><td>     dbGSS Sequence Only</td></tr>
*<tr><td>seqid      </td><td>     Sequence Only</td></tr>
*<tr><td>acc        </td><td>     Sequence Only</td></tr>
*<tr><td>ft         </td><td>     Sequence Only</td></tr></table>
* <p>       <b>-retmax</b> Maximum Number to Retrieve</p>
*<p>        <b>-reldate</b>        Limit items a number of days immediately preceding today's date.</p>
*<p>        <b>-datetype</b>       Limit dates to a specific date field based on database. For example, edat.</p>
*<p>        <b>-mindate</b>        Limit results bounded by two specific dates. Both mindate and maxdate </p>
*<p style="padding-left:20px;">                        are required if date range limits are applied using these variables.</p>
*<p>        <b>-maxdate</b>        Limit results bounded by two specific dates. Both mindate and maxdate</p> 
* <p style="padding-left:20px;">                       are required if date range limits are applied using these variables.eg mindate=2001 maxdate=2002/01/01</p>
*<p>        <b>-retstart</b>       The number to start from in the entire found results.eg mindate=2001 maxdate=2002/01/01</p>
*<p>        <b>-usehistory</b>     Whether to use history or not. Requests NCBI utility to maintain results in user's environment.</p>
*<p>        <b>-email</b>  User email. NCBI will use an email it to contact you if there are problems </p>
* <p style="padding-left:20px;">                       with your queries or if we are changing software interfaces that might </p>
* <p style="padding-left:20px;">                       specifically affect your requests</p>
*<p>        <b>-field</b>  Use this command to specify a specific search field</p> 
* <p style="padding-left:20px;">                       (one of:affl, auth, ecno, jour, iss, mesh, majr, mhda, page, pdat, </p>
* <p style="padding-left:20px;">                       ptyp, si, subs, subh, tiab, word, titl, lang, uid, fltr, vol)</p>
*<p>        <b>-sort</b>   Use this command to sort the retrieval.</p>
* <p style="padding-left:20px;">               Pubmed Values:</p>
*<p style="padding-left:40px;">                        author</p>
*<p style="padding-left:40px;">                        journal</p>
*<p style="padding-left:40px;">                        pub+date</p>
*<p style="padding-left:20px;">                Gene Values:</p>
*<p style="padding-left:40px;">                        Weight</p>
*<p style="padding-left:40px;">                        Name</p>
*<p style="padding-left:40px;">                        Chromosome</p>
*<p>        <b>-esearchonly</b>    Use this command to run only the eSearch and not an eFetch. One of y or n. Default is n.</p>
* <p>       <b>Examples:</b> </p>
*<p style="padding-left:20px;">                        -db journals -search obstetrics</p>
* <p style="padding-left:20px;">                       -db pubmed -search cancer -reldate 60 -datetype edat -retmax 100 -usehistory y</p><p>
*@author Greg Cope
*@version 1.1
*/
public final class CommandLineInterface implements OutputListener{
	private static final String[] ARGUMENTS = {"-db", "-term", "-o", "-os", "-printesearch", "-printefetch", "-rettype", "-retmax", "-reldate", 
				"-datetype", "-mindate", "-maxdate", "-retstart","-usehistory", "-email", "-field", "-esearchonly", "-sort"};
	
	
	private HashMap<String, String> params = new HashMap<String, String>();
	
	/**The JeUtils object to perform queries, input, and output. */
	private JeUtils		eUtils = new JeUtils();
	
	
	public CommandLineInterface(){
		super();
		eUtils.addOutputListener(this);
	}
	/**
	*Program entry point. Checks for valid parameters and <ul><li>prints out the manual if they do not meet guidelines.</li><li>Parses the 
	*given parameters then calls run asyncronously to start the search.</li></ul> 
	*@see #run()
	*/
	public static void commandLineStart(String[] args){
		//URLConnect conn = new URLConnect("http://");
		
		//perform some basic checks.
		if ( args.length == 0 ){
			System.out.println("Valid arguments are necessary to perform an eUtils search.\n");
			manual();
			return;
		}
		if ( args[0].equals("man") || args[0].equals("manual")){
			manual();
			return;
		}
		
		int st = search( "-term", args);
		if ( st == -1 ){
			System.out.println("Valid arguments are necessary to perform an eUtils search.\n");
			manual();
			return;
		}

		int increment = 0;
		CommandLineInterface m = new CommandLineInterface();
		m.getEUtils().setESearchOnly(false);//set default
		m.params.put("-esearchonly", "n");
		outer : for ( int i = 0; i < args.length; i++ ){		
			if ( validParameter(args[i].trim()) ){
				StringBuffer user_params = new StringBuffer();
				for ( int j = i+1; j < args.length; j++ ){
					if ( validParameter(args[j].trim()) ){
						m.params.put(args[i], user_params.toString());
						if ( args[i].equals("-esearchonly") ){
							if ( user_params.toString().equals("y") ){
								m.getEUtils().setESearchOnly(true);
							}
						}
						i = j-1;
						continue outer;
					}
					user_params.append(args[j]);
					user_params.append(" ");
				}
				m.params.put(args[i], user_params.toString());
			}
		}
		try{
			setFilePaths(m);
		}catch (IOException io){
			System.out.println("Unable to set the output file paths. Terminating Query.");
		}
	
		m.start();//run the query
	}
	
	/**
	 * Retrieves the eUtils object associated with this query.
	 * @return An eUtils object.
	 */
	public JeUtils getEUtils(){
		return eUtils;
	}
	
	/**
	*Changes relative file paths to absolute file paths. In the case the binaries are in a jar, the file path becomes relative to the jar, otherwise it is relative to this Main Class.
	*@m The main object containing a hashmap which may or may not contain file paths.
	*/
	private static final void setFilePaths(CommandLineInterface m) throws IOException{
		if ( m.params.get("-o") != null ){
			String fileO = m.params.get("-o");
			if ( !fileO.substring(0, 1).equals("/") ){//relative file path
				File temp = new File("temp");
					
				String aPath;
				aPath = temp.getCanonicalPath();
				m.params.put("-o", aPath+fileO);
			}
		}	
		if ( m.params.get("-os") != null ){
			String fileO = m.params.get("-os");
			if ( !fileO.substring(0, 1).equals("/") ){//relative file path
				File temp = new File("temp");
				String aPath = temp.getCanonicalPath();
				m.params.put("-os", aPath+fileO);
			}
		}	
	}
	/**
	*Prints out the J-eUtils manual to the command line.
	*/
	public static final void manual(){
		System.out.print("\t\t\u001B[1mAbout\u001B[0m \n\n" +
			"\t\tJ-eUtils is a command line utility written in Java by Greg Cope. \n"+
			"\t\tJ-eUtils allows scientists to access NCBI utilities to perform database\n" +
			"\t\tsearches for research purposes. J-eUtils is open source and comes with\n " +
			"\t\tno warranty whatsoever. To learn more about J-eUtils, please visit\n" +
			"\n\t\twww.algosome.com\n\n" +
			"\t\tJ-eUtils can be automated to retrieve data from NCBI via unix scripting or\n" +
			"\t\tby utilizing the J-eTools source code. NCBI has strict guidelines for automated\n" +
			"\t\tqueries: please DO NOT flood the NCBI server with several automated queries. Doing\n" +
			"\t\tso can result in being blacklisted from NCBI entirely.\n\n" +
			"\t\t\u001B[1mUser Requirements:\u001B[0m \n\n" +
			"\t\tJ-eUtils is multiplatform and requires Java Version 1.5 or later. J-eUtils requires\n" +
			"\t\tan active internet connection to connect online to NCBI.\n\n" +
			"\t\tDo not overload NCBI's systems. Users intending to send numerous queries and/or retrieve large numbers\n" +
			"\t\tof records from Entrez should comply with the following:\n" +
			"\t\t\t1) Run retrieval scripts on weekends or between 9 pm and 5 am Eastern \n"+
			"\t\t\t\tTime weekdays for any series of more than 100 requests.\n" +
			"\t\t\t2) Send E-utilities requests to http://eutils.ncbi.nlm.nih.gov, not the standard NCBI Web address.\n" +
			"\t\t\t3) Make no more than 3 requests every 1 second.\n" +
			"\t\t\t4) Use the URL parameter email, and tool for distributed software, so that we can track\n" + 
			"\t\t\t\tyour project and contact you if there is a problem.\n" + 
			"\t\t\t5) Read and understand the NCBI's Disclaimer and Copyright at http://www.ncbi.nlm.nih.gov/About/disclaimer.html\n\n"+
			"\t\t\tNLM does not claim the copyright on the abstracts in PubMed; however, journal publishers or authors may.\n"+
			"\t\t\tNLM provides no legal advice concerning distribution of copyrighted materials, consult your legal counsel.\n\n\n"

			);
		System.out.print(
				"\t\u001B[1mOptions:\u001B[0m \n" +
				"\t\u001B[4mThe minimum requirements:\u001B[0m\n" +
				"\t\u001B[1m-term\u001B[0m	Search Terms\n"+
				"\t\u001B[4mOther Options\u001B[0m\n" +
				//"\t-ot	OutputType\n" + 
				"\t\u001B[1m-db\u001B[0m	Entrez Database\n" +
				"\t\t\u001B[4mDatabase Options\u001B[0m\n" +
				"\t\t" );
		for ( int i = 0; i < Entrez.DATABASES.length; i++ ){
			System.out.print(Entrez.DATABASES[i]);
			if ( i < Entrez.DATABASES.length-1 ){
				System.out.print(", ");
			}
			if ( i % 6 == 0 && i != 0 ){
				System.out.print("\n\t\t");
			}
		}
	
		System.out.print(	
				"\n" +
				"\t\u001B[1m-o\u001B[0m	Save the genbank eFetch output to the file path provided. Warning: this will \n" +
				"\t\toverwrite any file with the same name. Relative paths are relative \n\t\tto the calling object (Main.class or J-eUtils.jar)\n" +
				"\t\u001B[1m-os\u001B[0m	Save the genbank eSearch output to the file path provided.Warning: this will \n" +
				"\t\toverwrite any file with the same name. Relative paths are relative \n\t\tto the calling object (Main.class or J-eUtils.jar)\n" +
				"\t\u001B[1m-printesearch\u001B[0m	Flag to indicate whether to print the eSearch output. y or n. Default is n.\n" +
				"\t\u001B[1m-printefetch\u001B[0m	Flag to indicate whether to print the eFetch output. y or n. Default is y.\n" +
				"\t\u001B[1m-rettype\u001B[0m	The retrieval type. eSearch defaults to XML. eFetch allows the following retrieval modes:\n"+
				"\t\t\u001B[4mrettype\u001B[0m\t\t\u001B[4mscope\u001B[0m\n" +
				"\t\tnative\t\tAll but gene\n" +
				"\t\tfasta\t\tsequence only\n" +
				"\t\tgb\t\tNucleotide Sequence Only\n" +
				"\t\tgp\t\tProtein Sequence Only\n" +
				"\t\tgvwtgoarts\t\tNucleotide Sequence Only\n" +
				"\t\tgbc\t\tNucleotide Sequence Only\n" +
				"\t\tgpc\t\tProtein Sequence Only\n" +
				"\t\test\t\tdbEST Sequence Only\n" +
				"\t\tgss\t\tdbGSS Sequence Only\n" +
				"\t\tseqid\t\tSequence Only\n" +
				"\t\tacc\t\tSequence Only\n" +
				"\t\tft\t\tSequence Only\n" +
				"\t\u001B[1m-retmax\u001B[0m	Maximum Number to Retrieve\n" +
				"\t\u001B[1m-reldate\u001B[0m	Limit items a number of days immediately preceding today's date.\n" +
				"\t\u001B[1m-datetype\u001B[0m	Limit dates to a specific date field based on database. For example, edat.\n" + 
				"\t\u001B[1m-mindate\u001B[0m	Limit results bounded by two specific dates. Both mindate and maxdate \n\t\t\tare required if date range limits are applied using these variables.\n" + 
				"\t\u001B[1m-maxdate\u001B[0m	Limit results bounded by two specific dates. Both mindate and maxdate \n\t\t\tare required if date range limits are applied using these variables.eg mindate=2001 maxdate=2002/01/01\n" + 
				"\t\u001B[1m-retstart\u001B[0m	The number to start from in the entire found results.eg mindate=2001 maxdate=2002/01/01\n" +
				"\t\u001B[1m-usehistory\u001B[0m	Whether to use history or not. Requests NCBI utility to maintain results in user's environment.\n"  +
				"\t\u001B[1m-email\u001B[0m	User email. NCBI will use an email it to contact you if there are problems \n\t\t\twith your queries or if they are changing software interfaces that might \n\t\t\tspecifically affect your requests\n" +
				"\t\u001B[1m-field\u001B[0m	Use this command to specify a specific search field \n\t\t\t(one of:affl, auth, ecno, jour, iss, mesh, majr, mhda, page, pdat, \n\t\t\tptyp, si, subs, subh, tiab, word, titl, lang, uid, fltr, vol)\n" +
				"\t\u001B[1m-sort\u001B[0m	Use this command to sort the retrieval.\n"+
				"\t\t\u001B[4mPubmed Values:\u001B[0m\n"+
				"\t\t\tauthor\n" +
				"\t\t\tjournal\n" +
				"\t\t\tpub+date\n" +
				"\t\t\u001B[4mGene Values:\u001B[0m\n"+
				"\t\t\tWeight\n" +
				"\t\t\tName\n" +
				"\t\t\tChromosome\n" +
				"\t\u001B[1m-esearchonly\u001B[0m	Use this command to run only the eSearch and not an eFetch. One of y or n. Default is n.\n" +

				""
			);	
		System.out.print(
				"\t\u001B[1mExamples:\u001B[0m \n" +
				"\t\t\t-db journals -search obstetrics\n" +
				"\t\t\t-db pubmed -search cancer -reldate 60 -datetype edat -retmax 100 -usehistory y\n"				
				);
	}
	
	/**
	*Utility which searches a haystack array for a needle, and returns the position relative to the array of the needle, or -1 if needle was not found.
	*@return The array position of the needle in the haystack, or -1 if none is found.
	*/
	public static int search( String needle, String...haystack ){
		for ( int i = 0; i < haystack.length; i++ ){
			if ( haystack[i].trim().indexOf(needle) != -1 ){
				return i;
			}
		}
		return -1;
	}
	/**
	*Validates a given parameter to make sure it is a valid option.
	*@param param A String to validate.
	*@return true if the the parameter is a valid option, otherwise false.
	*/
	private static final boolean validParameter(String param){
		for ( int i = 0; i < ARGUMENTS.length; i++ ){
			if ( param.equals(ARGUMENTS[i])) return true;
		}
		return false;
	}
	/**
	*Implementation of the runnable interface inherited from Thread. Performs the actual query to NCBI via eSearch and eFetch objects.
	*/
	public void start(){
		eUtils.setESearch( new EntrezSearch(params) );
		(new Thread(eUtils)).start();
	}
	/**
	 * Receives a message from the caller. 
	 * @param s The message received.
	 */
	public void message(String s){
		System.out.print(s);
	}
	
	/**
	 * Sends a notice to this listener. Notices involve programatic state changes, such
	 * as "Connecting" or "Receiving Results"
	 * @param s The notice received.
	 */
	public void notice(String s){
		System.out.print(s);
	}
	
	
	/**
	 * Receives an error from the caller. 
	 * @param s Information regarding the error that occurred.
	 */
	public void error( String s ){
		System.out.print(s);
	}
	
	/**
	 * Receives data from the caller. 
	 * @param s The data received. 
	 */
	public void data( String s ){
		System.out.print(s);
	}
}
