package com.algosome.eutils;
/**
Entrez.java

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

import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

import com.algosome.eutils.io.*;
import com.algosome.eutils.net.*;

/**
*An abstract class to access entrez via a query string based HTTP request. This class defines serveral query strings and command line parameters necessary for an Entrez eUtils query.</p>
<p>Entrez eUtils are most readily used via search terms: an eSearch can be used to retrieve actual Genbank Id's based upon search terms of the appropriate database. Upon retrieval, 
*the Genbank Id's from an eSearch can then be used in an eFetch to retrieve the actual data in the specified type.</p>
*<p>This class contains several static fields that specify Entrez database and field values. These are not directly used in this or other classes, but are provided
*for convenience.</p>
*@see <a href="http://www.ncbi.nlm.nih.gov/corehtml/query/static/esearch_help.html">NCBI</a> for further information
*@author Greg Cope
*@version 1.1
*@see http://eutils.ncbi.nlm.nih.gov/About/disclaimer.html
*/
public abstract class Entrez extends URLConnect implements InputStreamParser{

	protected Logger logger = Logger.getLogger(Entrez.class);
	
	/**Constant that specifies access cannot be performed faster than every 5 seconds*/
	public static final long NCBI_ACCESS_TIME = 5000L;

	
	/**Used to indicate the start time of a query, 
	 * and prevent access to NCBI faster than every 5 seconds.
	 * Declared static to prevent several windows from overlapping queries.
	 * */
	public static  long	 ACCESS_START_TIME = 0;
	
	/**
	*Possible Database parameters.
	*/
	public static final String[] DATABASES = {"nucleotide", "protein", "pubmed","nuccore","nucest","nucgss", "gene", "sequences"};
	//	"unigene", "unists","books","popset","probe", "proteinclusters""snp","toolkit","structure",
//	"biosystems", "blastdbinfo",
//	 "cancerchromosomes","cdd","gap","domains","gene", "genomeprj","gensat","geo","gds", "homologene", "homologene journals","mesh",
//	 "ncbisearch","omia","nlmcatalog","omim","pepdome","pmc","pcassay","pccompound","pcsubstance", ,"snp",
	
//	
//	public static final String[] DATABASES_SEQUENCE = {"gene",
//		"genome",
//		"nucleotide",
//		"nuccore",//nucleotide
//		"nucest",//nucleotide
//		"nucgss",//nucleotide
//		"protein",
//		"popset",
//		"snp",
//		"sequences"};// - Composite name including nucleotide, protein, popset and genome};
//	
	/**
	 * See http://www.ncbi.nlm.nih.gov/corehtml/query/static/efetchseq_help.html
	 * */

	/**Lookup table of DATABSES to RETTYPES*/
	private static final int[] NUCLEOTIDE_RETTYPES = {0,1,2,4,5,9,10,11};
	
	private static final int[] PROTEIN_RETTYPES = {0,1,3,6,9,10,11};

	public static final int[][] DATABASE_TO_RETTYPES = {NUCLEOTIDE_RETTYPES, PROTEIN_RETTYPES, {0}, 
														NUCLEOTIDE_RETTYPES, NUCLEOTIDE_RETTYPES, NUCLEOTIDE_RETTYPES, /*nucgss (5)*/
														 NUCLEOTIDE_RETTYPES, {0}};//{0},
//														{0}, {3}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {3},/*gene*/
//														{0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0},
//														{0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0}, {0,1,9,10,11},
//														{0}, {0}, {0}, {0}, {0}};
	
	/**Looup table to DATABSES for nucleotide only Databases */
	private static final int[] NUCLEOTIDE_ONLY = {0,3,4,5};
	/**Looup table to DATABSES for protein only Databases */
	private static final int[] PROTEIN_ONLY = {1};
	/**Looup table to DATABSES for sequences only Databases */
	private static final int[] SEQUENCES_ONLY = {0,1,3,4,5,7};
	/**Looup table for RETTYPES to DATABASES  */
	//public static final int[][] RETTYPES_TO_DATABASE = {{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40},
	//	SEQUENCES_ONLY, NUCLEOTIDE_ONLY, PROTEIN_ONLY, NUCLEOTIDE_ONLY, NUCLEOTIDE_ONLY, PROTEIN_ONLY, {4}, {5}, SEQUENCES_ONLY, SEQUENCES_ONLY, SEQUENCES_ONLY  };
	
	/**Valid retmodes*/
	public static final String[] RETMODE = {"xml", "text", "html", "asn1"};
	
	/**Valid RetTypes*/
	public static final String[] RETTYPE = {"native", "fasta", "gb", "gp", "gbwithparts", "gbc", "gpc", "est", "gss", "seqid", "acc", "ft" };//, "chr", "flt", "rsr", "brief", "docset"};
	
	public static final String[] RETTYPE_DESC = {"Default (native)", "Fasta Sequence View (fasta)", "GenBank Report (gb)", "GenPept Report (gp)", "Genbank With Parts (gbwithparts)", "Nucleotide INSDSeq (gbc)", "Protein INSDSeq (gbp)",
													"EST Report (est)", "GSS Report (gss)", "To Convert GIS to seqids (seqids)", "To convert GIS to accessions (acc)", "Feature Table Report (ft)"};
	
	/**A lookup table of RETMODE to valid RETTYPES for that mode */
	public static final int RETMODE_TO_RETTYPE[][] = {{0,1}, {0,1,2,3,4,5,6,7,8,9,10,11,12}, {0,1,2,3,4,5,6,7,8,9,10,11,12}, {0,10} }; 
	
	public static final int RETTYPE_TO_RETMODE[][] = {{0,1,2,3}, {0,1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2}, {1,2,3}, {1,2}};
	
	public static final String DB_NUCLEOTIDE = "nucleotide";
	public static final String DB_PROTEIN = "protein";
	public static final String DB_PUBMED = "pubmed";
	public static final String DB_NUCCORE = "nuccore";
	public static final String DB_NUCEST = "nucest";
	public static final String DB_STRUCTURE = "structure";
	public static final String DB_GENOME = "genome";
	public static final String DB_BIOSYSTEMS = "biosystems";
	public static final String DB_BLASTDBINFO = "blastdbinfo";
	public static final String DB_BOOKS = "books";
	public static final String DB_CANCERCHROMOSOMES = "cancerchromosomes";
	public static final String DB_CDD = "cdd";
	public static final String DB_GAP = "gap";
	public static final String DB_DOMAINS = "domains";
	public static final String DB_GENE = "gene";
	public static final String DB_GENOMEPRJ = "genomeprj";
	public static final String DB_GENDAT = "gensat";
	public static final String DB_GEO = "geo";
	public static final String DB_GDS = "gds";
	public static final String DB_HOMOLOGENE = "homologene";
	public static final String DB_JOURNALS = "homologene journals";
	public static final String DB_MESH = "mesh";
	public static final String DB_NCBISEARCH = "ncbisearch";
	public static final String DB_OMIA = "omia";
	public static final String DB_NLMCATALOG = "nlmcatalog";
	public static final String DB_OMIM = "omim";
	public static final String DB_PEPDOME = "pepdome";
	public static final String DB_PMC = "pmc";
	public static final String DB_POPSET = "popset";
	public static final String DB_PROBE = "probe";
	public static final String DB_PROTEINCLUSTERS = "proteinclusters";
	public static final String DB_PCASSAY = "pcassay";
	public static final String DB_PCCOMPOUND = "pccompound";
	public static final String DB_PCSUBSTANCE = "pcsubstance";
	public static final String DB_SNP = "snp";
	public static final String DB_SRA = "sra";
	public static final String DB_TAXONIMY = "taxonimy";
	public static final String DB_TOOLKIT = "toolkit";
	public static final String DB_UNIGENE = "unigene";
	public static final String DB_UNISTS = "unists";
	
	/**
	*Possible Field submissions
	affl, auth, ecno, jour, iss, mesh, majr, mhda, page, pdat, ptyp, si, subs, subh, tiab, word, titl, lang, uid, fltr, vol
	*/
	public static final String FIELD_AFFL = "affl";
	public static final String FIELD_AUTH = "auth";
	public static final String FIELD_ECNO = "ecno";
	public static final String FIELD_JOU = "jour";
	public static final String FIELD_ISS = "iss";
	public static final String FIELD_MESH = "mesh";
	public static final String FIELD_MAJR = "majr";
	public static final String FIELD_MHDA = "mhda";
	public static final String FIELD_PAGE = "page";
	public static final String FIELD_PDAT = "pdat";
	public static final String FIELD_PTYP = "ptyp";
	public static final String FIELD_SI = "si";
	public static final String FIELD_SUBS = "subs";
	public static final String FIELD_SUBH = "subh";
	public static final String FIELD_TIAB = "tiab";
	public static final String FIELD_WORD = "word";
	public static final String FIELD_TITL = "titl";
	public static final String FIELD_LANG = "lang";
	public static final String FIELD_UID = "uid";
	public static final String FIELD_FLTR = "fltr";
	public static final String FIELD_VOL = "vol";	
	
	
	/**
	*Creates an empty Entrez object containing no parameters.  
	*/
	public Entrez(){
		parameters = new HashMap<String,String>();
	
	}
	/**
	*Creates an Entrez object from a previous Entrez object. 
	*@param e A pre-formed Entrez object containing set parameters to use.
	*/
	public Entrez(Entrez e){
		parameters = e.parameters;
	}
	
	/**
	*Sets the debugging flag to output debugging information in the command line.
	*@param b A boolean indicating whether debugging should be used. true to output debugging information, false otherise.
	*/
	public void setDebug(boolean b){
		DEBUG = b;
	}
	/**
	*Sets the Genbank IDs.. 
	*@param ids A String representation of ID's. Must be space delimited.  
	*/
	public void setIds( String ids ){
		setParameter("-id", ids);
		if ( DEBUG ){
			if( output == null ){
				System.out.println("Genbank IDs being set: " + ids);
			}else{
				output.notice( "Genbank IDs being set: " + ids );
			}
		}
	}	
	/**
	*Retrieves the Genbank Ids found in an eSearch. 
	*@return  A space delimited String of Genbank ID's, or null if none have been set. 
	*/
	public String getIds( ){
		return getParameter("-id");
	}
	
	/**
	*Sets the file path to save eFetch output. 
	*@param filepath A String representation of the full path to the output file.  
	*/
	public void setOutputFile( String filepath ){
		setParameter("-o", filepath);
	}	
	/**
	*Gets the file path to save eFetch output. 
	*@return max_number A String representation of the full path to the output file, or null if none have been set.  
	*/
	public String getOutputFile( ){
		return getParameter("-o");
	}

	/**
	*Sets the file path to save output. 
	*@param filepath A String representation of the full file path to save.  
	*/
	public void setESearchOutput( String filepath ){
		setParameter("-os", filepath);
	}	
	/**
	*Gets the file path to save eSearch output. 
	*@return A String representation of the full path to the output file to save an eSearch, or null if nothing have been set.  
	*/
	public String getESearchOutput( ){
		return getParameter("-os");
	}		
	
	/**
	*Sets a flag to System Print out the eSearch results. Defaults to "y" to print output
	*@param s A String representation of type "y" or "n".  
	*/
	public void setPrintESearchOutput( String s ){
		setParameter("-printesearch", s);
	}	
	/**
	*Gets a flag to System Print out the eSearch results. 
	*@return filepath A String representation of type "y" or "n"  
	*/
	public String getPrintESearchOutput( ){
		return getParameter("-printesearch");
	}	
	/**
	*Sets a flag to System Print out the eFetch results. Defaults to "y" to print output
	*@param s A String representation of type "y" or "n".  
	*/
	public void setPrintEFetchOutput( String s ){
		setParameter("-printefetch", s);
	}	
	/**
	*Gets a flag to System Print out the eFetch results. 
	*@return filepath A String representation of type "y" or "n". 
	*/
	public String getPrintEFetchOutput( ){
		return getParameter("-printefetch");
	}		
	/**
	*Sets the retrieval output type. 
	*@param rettype A String representation of the type of retrieval. 
	*/
	public void setRetType( String rettype ){
		setParameter("-rettype", rettype);
	}
	/**
	*Gets the retrieval output type. 
	*@return A String representation of the mode, or null if none have been set. 
	*/
	public String getRetType(){
		return getParameter("-rettype");
	}
	/**
	*Gets the retrieval output mode. 
	*@return A String representation of the mode, or null if none have been set. 
	*/
	public String getRetMode(){
		return getParameter("-retmode");
	}
	/**
	*Sets the retrieval output mode. eSearch output is always XML, so setting this flag effects only eFetch.
	*@param rettype A String representation of the type of retrieval. 
	*/
	public void setRetMode( String retmode ){
		setParameter("-retmode", retmode);
	}

	/**
	*Sets the email Paramter. This parameter can be set and NCBI may contact you if a feature that effects your queries may change. 
	*@param email String representation of the tool to use. Not required for a valid query. 
	*/
	public void setEmail( String email){
		setParameter("-email", email);
	}
	/**
	*Gets the email Paramter. This parameter can be set and NCBI may contact you if a feature that effects your queries may change. 
	*@return String representation of the tool to use, or null if none have been set. 
	*/
	public String getEmail(){
		return getParameter("-email");
	}	
	/**
	*Sets the database to search. If a parameter is not set, the NCBI server defaults to Pubmed.
	*@param db A String representation of the NCBI Database. All databases are stored in this class. 	
	*
	*/
	public void setDatabase(String db){
		setParameter("-db", db);
	}
	/**
	*Gets the database to search. 
	*@return A String representation of the NCBI Database. 
	*
	*/
	public String getDatabase(){
		return getParameter("-db");
	}
	/**
	*Sets whether to use history or not. Upon eUtils query, NCBI outputs an XML format. If this parameter is set to yes, 
	*the output contains both a QueryKey and WebEnv parameters for later retrieval of the data. 
	*@param use_history A string representation "y" or "n"
	*/
	public void setUseHistory(String use_history){
		setParameter("-usehistory", use_history);
	}
	/**
	*Gets whether to use history or not. Upon eUtils query, NCBI outputs an XML format. If this parameter is set to yes, 
	*the output contains both a QueryKey and WebEnv parameters for later retrieval of the data. 
	*@return One of "y" or "n"
	*/
	public String getUseHistory(){
		return getParameter("-usehistory");
	}
	/**
	*Sets the QueryKey Paramter. Upon eUtils query, NCBI outputs an XML format. If the usehistory parameter is set to yes,
	*the output contains both a QueryKey and WebEnv parameters for later retrieval of the data. 
	*@param queryKey An int representation of the NCBI provided QueryKey
	*/
	public void setQueryKey(int queryKey){
		setParameter("-query_key", Integer.toString(queryKey));
	}
	/**
	*Sets the QueryKey Paramter. Upon eUtils query, NCBI outputs an XML format. If the usehistory parameter is set to yes, 
	*the output contains both a QueryKey and WebEnv parameters for later retrieval of the data. 
	*@param queryKey A String representation of the NCBI provided QueryKey
	*/
	public void setQueryKey(String queryKey){
		setParameter("-query_key", queryKey);
	}
	/**
	*Gets the QueryKey Paramter. Upon eUtils query, NCBI outputs an XML format. If the usehistory parameter is set to yes,
	*the output contains both a QueryKey and WebEnv parameters for later retrieval of the data. 
	*@return  A String representation of the NCBI provided QueryKey, or null if none have been set.
	*/
	public String getQueryKey(){
		return getParameter("-query_key");
	}
	/**
	*Sets the WebEnv Paramter. Upon eUtils query, NCBI outputs an XML format. If the usehistory parameter is set to yes, 
	*the output contains both a QueryKey and WebEnv parameters for later retrieval of the data. 
	*@param webEnv A String representation of the NCBI provided WebEnv
	*/
	public void setWebEnv( String webEnv){
		setParameter("-WebEnv", webEnv);
	}
	/**
	*Gets the WebEnv Paramter. Upon eUtils query, NCBI outputs an XML format. If the usehistory parameter is set to yes, 
	*the output contains both a QueryKey and WebEnv parameters for later retrieval of the data. 
	*@return A String representation of the NCBI provided WebEnv, or null if none have been set.
	*/
	public String getWebEnv(){
		return getParameter("-WebEnv");
	}
	/**
	*Sets the Tool Parameter. This is set to let NCBI know of third party tools that are accessing their databases. 
	*@param tool String representation of the tool to use. Not required for a valid query. 
	*/
	public void setTool( String tool){
		setParameter("-tool", tool);
	}
	/**
	*Gets the Tool Parameter. This is set to let NCBI know of third party tools that are accessing their databases. 
	*@return String representation of the tool to use. 
	*/
	public String getTool(){
		return getParameter("-tool");
	}
	
	/**
	*Sets the term parameter to submit. This is the search query to submit, and should be considered required. 
	*@param term a String representation of the search query. Multiple words must be separated by boolean operators (+, -)
	*/
	public void setTerm( String term ){
		setParameter("-term", term);
	}
	/**
	*Gets the term parameter. This is the search query to submit, and should be considered required. 
	*@return A space delimited String representation of the search query, or null if none have been set.
	*/
	public String getTerm(  ){
		return getParameter("-term");
	}
	/**
	*Sets the field parameter to submit.
	*@param field a String representation of the field. Valid fields include:
	*affl, auth, ecno, jour, iss, mesh, majr, mhda, page, pdat, ptyp, si, subs, subh, tiab, word, titl, lang, uid, fltr, vol
	*/
	public void setSearchField( String field ){
		setParameter("-field", field);
	}
	/**
	*Gets the field parameter to submit.
	*@return a String representation of the field. Valid fields include:
	*affl, auth, ecno, jour, iss, mesh, majr, mhda, page, pdat, ptyp, si, subs, subh, tiab, word, titl, lang, uid, fltr, vol
	*/
	public String setSearchField(  ){
		return getParameter("-field");
	}
	/**
	*Sets the Relative Data parameter to submit.
	*@param reldate a String representation of the relative date. This must be a number that indicates the number of days to search 
	*preceeding the date of query. 
	*/
	public void setRelativeDate( String reldate ){
		setParameter("-reldate", reldate);
	}
	/**
	*Gets the Relative Data parameter to submit.
	*@return a String representation of the relative date, or null if none have been set. This is a number that indicates the number of days to search 
	*preceeding the date of query. 
	*/
	public String getRelativeDate( ){
		return getParameter("-reldate");
	}
	/**
	*Sets the Relative Data parameter to submit.
	*@param reldate an integer representation of the relative date, or null if none have been set. This must be a number that indicates the number of days to search 
	*preceeding the date of query. 
	*/
	public void setRelativeDate( int reldate ){
		setParameter("-reldate", Integer.toString(reldate));
	}

	/**
	*Sets the Mimimum Date parameter to submit. The maximum date must be set for this to function.
	*@param date a String representation of the date. This must be of a specifi format of YEAR or MO/DAY/YEAR
	*/
	public void setMinDate( String date ){
		setParameter("-mindate", date);
	}
	/**
	*Gets the Mimimum Date parameter to submit. The maximum date must be set for this to function.
	*@return a String representation of the date, or null if none have been set. This must be of a specifi format of YEAR or MO/DAY/YEAR
	*/
	public String getMinDate( ){
		return getParameter("-mindate");
	}
	/**
	*The Maximum Date parameter to submit. The Mimimum date must be set for this to function.
	*@param date a String representation of the date. This must be of a specific format of YEAR or MO/DAY/YEAR
	*/
	public void setMaxDate( String date ){
		setParameter("-maxdate", date);
	}
	/**
	*Gets the Maximum Date parameter to submit. The Mimimum date must be set for this to function.
	*@return a String representation of the date. This must be of a specific format of YEAR or MO/DAY/YEAR
	*/
	public String setMaxDate( ){
		return getParameter("-maxdate");
	}
	/**
	*Limit dates to a specific date field based on database.
	*@param date a String representation of the date type. 
	*/
	public void setDateType( String date ){
		setParameter("-datetype", date);
	}
	/**
	*Gets the date type to a specific date field based on database.
	*@return a String representation of the date type. 
	*/
	public String getDateType(  ){
		return getParameter("-datetype");
	}	
	/**
	*Limit the number to display from the results. Sets the mimimum number to start from.
	*@param min_number an integer representation of the minumum number. 
	*/
	public void setRetStart( int min_number ){
		setParameter("-retstart", Integer.toString(min_number));
	}
	/**
	*Gets the limit on start location for the number to display from the results.
	*@return an integer representation of the minumum number. 
	*/
	public String getRetStart(  ){
		return getParameter("-retstart");
	}
	/**
	*Limit the number to display from the results. Sets the mimimum number.
	*@param min_number an integer representation of the minumum number. 
	*/
	public void setRetStart( String min_number ){
		setParameter("-retstart", min_number);
	}
	/**
	*Limit the number to display from the results. Sets the maximum number to retrieve.
	*@param max_number an integer representation of the maximum number. 
	*/
	public void setMaxRetrieval( int max_number ){
		setParameter("-retmax", Integer.toString(max_number));
	}
	/**
	*Limit the number to fetch. Sets the maximum number to retrieve.
	*@param max_number A String representation of the maximum number. 
	*/
	public void setMaxRetrieval( String max_number ){
		setParameter("-retmax", max_number);
	}
	/**
	*Gets the limit to the number to fetch. 
	*@return A String representation of the maximum number of entries to retrieve, or null if none have been set. 
	*/
	public String getMaxRetrieval( ){
		return getParameter("-retmax");
	}
	
	/**
	*Use in conjunction with Web Environment to display sorted results in ESummary and EFetch.</p>
	*
	*<p>PubMed values:</p>
	
	*<p style="padding-left:20px">author</p> 
	*<p style="padding-left:20px">last+author</p> 
	*<p style="padding-left:20px">journal </p>
	*<p style="padding-left:20px">pub+date</p>
	*
	*<p>Gene values:</p>
	*
	*<p style="padding-left:20px">Weight </p>
	*<p style="padding-left:20px">Name</p> 
	*<p style="padding-left:20px">Chromosome</p><p>
	*@param sort A string representation of the above.
	*/
	public void setSort(String sort){
			setParameter("-sort", sort);
	}
	/**
	*Use in conjunction with Web Environment to display sorted results in ESummary and EFetch.</p>
	*
	*<p>PubMed values:</p>
	
	*<p style="padding-left:20px">author</p> 
	*<p style="padding-left:20px">last+author</p> 
	*<p style="padding-left:20px">journal </p>
	*<p style="padding-left:20px">pub+date</p>
	*
	*<p>Gene values:</p>
	*
	*<p style="padding-left:20px">Weight </p>
	*<p style="padding-left:20px">Name</p> 
	*<p style="padding-left:20px">Chromosome</p><p>
	*@return A string representation of the above, or null if none have been set.
	*/
	public String getSort(){
			return getParameter("-sort");
	}

	/**
	*Filters program specific parameters for the construction of a URL query to NCBI.
	*@param s The string to check for program specific params.
	*@return true if the parameter contains program specific terms, false otherwise.
	*/
	protected static final boolean isESearchSpecificParam(String s ){
		if ( s.equals("printesearch")   ||
				s.equals("printefetch")  ||
					s.equals("esearchonly")||
						s.equals("o") ||
							s.equals("os") ){
			return true;
		}
		return false;
	}
	
	/**
	*Utility that calls doQuery using the implemented InputStreamParser interface of this object.
	*@throws IOException if the connecetion cannot be read or established.
	*@throws UnsupportedEncodingException If the submission cannot be UTF-8 Encoded.
	*/
	public void doQuery()  throws IOException, UnsupportedEncodingException{
		this.doQuery(this);
	} 
	
	/**
	 * Resets the mappings of this object by clearing the parameter hashmap for a query.
	 */
	public void reset(){
		parameters.clear();
	}

}