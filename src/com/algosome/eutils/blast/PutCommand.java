package com.algosome.eutils.blast;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Adapted from http://users.encs.concordia.ca/~f_kohant/ncbiblast/
 * 
 * @author: Farzad Kohant
 * @author Greg Cope
 */
public class PutCommand extends QBlastCommand{
	
	private static final Logger logger = Logger.getLogger(PutCommand.class);
	
	/**
	 * 
	 */
	public PutCommand(){
		super();
	}
	
    protected String getCommandName(){
        return "Put";
    }

    /**
     * Determines whether to format results automatically.
     *
     * @param value Off, Semiauto, Fullauto
     */
    public void setAutoFormat(String value)
    {
        params.put("AUTO_FORMAT", value);
    }


    /**
     * Database name
     *
     * @param value valid database name
     */
    public void setDatabase(String value)
    {
        params.put("DATABASE", value);
    }

    /**
     * Database genetic code (PROGRAM=tblast[nx] only)
     *
     * @param value integers: 1..16,21,22
     */
    public void setDBGenericCode(int value)
    {
        params.put("DB_GENETIC_CODE", String.valueOf(value));
    }


    /**
     * Number of descriptions
     *
     * @param value the integer value
     */
    public void setDescriptions(int value)
    {
        params.put("DESCRIPTIONS", String.valueOf(value));
    }

    /**
     * Get only alignment endpoints in results (megablast only)
     *
     * @param value yes, no
     */
    public void setEndPoints(boolean value)
    {
        params.put("ENDPOINTS", value ? "yes" : "no");
    }

    /**
     * Do search with tweak parameter set to true
     *
     * @param value yes, no
     */
    public void setCompositionBasedStatistics(boolean value)
    {
        params.put("COMPOSITION_BASED_STATISTICS", value ? "yes" : "no");
    }

    /**
     * Entrez query to limit Blast search
     *
     * @param value Entrez query format
     */
    public void setEntrezQueryFormat(String value)
    {
        params.put("ENTREZ_QUERY", value);
    }

    /**
     * Expect value
     *
     * @param value double type value
     */
    public void setExpectValue(double value)
    {
        params.put("EXPECT", String.valueOf(value));
    }

    /**
     * Sequence filter identifier
     *
     * @param value ''L'' for Low Complexity, ''R'' for Human Repeats, ''m'' for Mask for Lookup
     */
    public void setFilter(String value)
    {
        params.put("FILTER", value);
    }

    /**
     * Gap open and gap extend costs
     *
     * @param value Space separated float values, ''5 2'' for nuc-nuc, ''11 1'' for proteins, non-affine for megablast
     */
    public void setGapCosts(String value)
    {
        params.put("GAPCOSTS", value);
    }

    /**
     * Query genetic code
     *
     * @param value integers: 1..16,21,22
     */
    public void setGeneticCode(int value)
    {
        params.put("GENETIC_CODE", String.valueOf(value));
    }

    /**
     * Number of hits to keep
     *
     * @param value integer value
     */
    public void setHitListSize(int value)
    {
        params.put("HITLIST_SIZE", String.valueOf(value));
    }

    /**
     * Threshold for extending hits (PSI BLAST only)
     *
     * @param value float value
     */
    public void setThreshhold(float value){
        params.put("I_THRESH", String.valueOf(value));
    }

    /**
     * Enable masking of lower case in query
     *
     * @param value yes, no
     */
    public void setLowerCaseMask(boolean value)
    {
        params.put("LCASE_MASK", value ? "yes" : "no");
    }

    /**
     * Matrix name (protein search only)
     *
     * @param value Valid matrix name
     */
    public void setMatrixName(String value)
    {
        params.put("MATRIX_NAME", value);
    }

    /**
     * Penalty for a nucleotide mismatch (blastn only)
     *
     * @param value negative integer value
     */
    public void setNucleotideMismatch(int value)
    {
        params.put("NUCL_PENALTY", String.valueOf(value));
    }

    /**
     * Reward for a nucleotide match (blastn only)
     *
     * @param value integer value
     */
    public void setNucleotideMatch(int value)
    {
        params.put("NUCL_REWARD", String.valueOf(value));
    }

    /**
     * Other advanced options
     *
     * @param value A string combining the options:
     *              -G Cost to open a gap, see GAPCOSTS
     *              -E Cost to extend a gap, see GAPCOSTS
     *              -r Reward for match, see NUCL_REWARD
     *              -q Penalty for mismatch, see NUCL_PENALTY
     *              -e Expectation value (E), see EXPECT
     *              -W Word size, see WORD_SIZE
     *              -y Dropoff (X) for blast extensions in bits (default if zero)
     *              (Integer) default = 20 for nuc-nuc 7 for other programs
     *              Not applicable for megablast
     *              -X X dropoff value for gapped alignment (in bits) (Integer)
     *              default = 30 for nuc-nuc (blastn and megablast), 15 for other programs
     *              -Z Final X dropoff value for gapped alignment (in bits)
     *              (Integer) 50 for nuc-nuc (blastn), 25 for other programs
     *              megablast - not applicable
     *              -P 0 for multiple hits 1-pass, 1 for single hit 1-pass (Integer)
     *              Does not apply to blastn or megablast
     *              -A Multiple Hits window size (zero for single hit algorithm)(Integer)
     *              -I Number of database sequences to save hits for,
     *              see HITLIST_SIZE
     *              -b Number of database sequences to show alignments for,
     *              see ALIGNMENTS
     *              -v Number of database sequences to show one-line descriptions for,
     *              see DESCRIPTIONS
     *              -Y Effective length of the search space, see SEARCHSP_EFF
     *              -z Effective length of the database (use zero for the real size)(Real), default=0
     *              -c Pseudocount constant for PSI-BLAST (Integer), default=7
     *              -F Filtering directives, see FILTER
     */
    public void setAdvanceOptions(String value)
    {
        params.put("OTHER_ADVANCED", value);
    }

    /**
     * Percent of identity cut-off threshold (megablast only)
     *
     * @param value integer between 0 (no cut-off) and 100 (exact matches only)
     */
    public void setIdentityPrecision(int value)
    {
        params.put("PERC_IDENT", String.valueOf(value));
    }

    /**
     * Phi Blast pattern
     *
     * @param value string, valid PHI BLAST pattern
     */
    public void setPhiPattern(String value)
    {
        params.put("PHI_PATTERN", value);
    }

    /**
     * Blast program name
     *
     * @param value blastn, blastp, blastx, tblastn, tblastx
     */
    public void setProgram(String value)
    {
        params.put("PROGRAM", value);
    }

    /**
     * Sequence query (queries if MegaBlast page)
     *
     * @param value Accession(s), gi(s), or FASTA sequence(s)
     */
    public void setQuery(String value)
    {
        params.put("QUERY", value);
    }

    /**
     * File with sequence queries (MegaBlast only)
     *
     * @param value Accessions, gis, or FASTA sequences
     */
    public void setQueryFile(String value)
    {
        params.put("QUERY_FILE", value);
    }

    /**
     * Whether to believe defline in FASTA query
     *
     * @param value yes, no
     */
    public void setBelieveDefline(boolean value)
    {
        params.put("QUERY_BELIEVE_DEFLINE", value ? "yes" : "no");
    }

    /**
     * Start of subsequence (one offset)
     *
     * @param value integer value
     */
    public void setQueryFrom(int value)
    {
        params.put("QUERY_FROM", String.valueOf(value));
    }

    /**
     * End of subsequence (one offset)
     *
     * @param value integer value, 0 means not to use subsequence
     */
    public void setQueryTo(int value)
    {
        params.put("QUERY_TO", String.valueOf(value));
    }

    /**
     * Effective length of the search space
     *
     * @param value integer value
     */
    public void setEffectiveLengthOfSearchSpace(int value)
    {
        params.put("SEARCHSP_EFF", String.valueOf(value));
    }

    /**
     * Blast service which needs to be performed
     *
     * @param value plain, psi, phi, rpsblast, megablast
     */
    public void setService(String value)
    {
        params.put("SERVICE", value);
    }

    /**
     * Threshold for extending hits
     *
     * @param value integer value
     */
    public void setThreshold(String value)
    {
        params.put("THRESHOLD", value);
    }

    /**
     * Should the ungapped alignment be performed ?
     *
     * @param value yes, no
     */
    public void setUngappedAlignment(boolean value)
    {
        params.put("UNGAPPED_ALIGNMENT", value ? "yes" : "no");
    }


    /**
     * Word size
     *
     * @param value 3 for proteins, 11 for nuc-nuc, 28 for megablast
     */
    public void setWordSize(int value)
    {
        params.put("WORD_SIZE", String.valueOf(value));
    }

    @Override
    protected void setDefaultParamters(){
        //setAutoFormat("Off");
        setDatabase("nr");
       // setDBGenericCode(1);
        setDescriptions(500);
        //setEndPoints(false);
        //setCompositionBasedStatistics(false);
        setExpectValue(10);
        setWordSize(28);
       // setGeneticCode(1);
        //setHitListSize(500);
       // setThreshhold(0.001f);
       // setLowerCaseMask(false);
       // setMatrixName("BLOSUM62");
        
        //setNucleotideMismatch(-3);
        //setNucleotideMatch(1);
        //setIdentityPrecision(0);
        setProgram("blastn");
        //setBelieveDefline(false);
        //setQueryFrom(0);
        //setQueryTo(0);
        //setEffectiveLengthOfSearchSpace(0);
        setService("plain");
        //setUngappedAlignment(false);
    }

    /**
     * 
     * @return
     */
    public String getEstimatedWaitTime(){
    	return params.get("EST_WAIT_TIME");
    }
    
    private static final Pattern WAIT_TIME_PATTERN = Pattern.compile("([0-9]+) seconds", Pattern.DOTALL);
    /**
     * 
     */
    @Override
    protected void processResult(InputStream in) throws IOException
    {
        BufferedReader response = null;
        try{
        	response = new BufferedReader(new InputStreamReader(in));
	        String line = null;	
	        StringBuilder sb = new StringBuilder();
	        while ((line = response.readLine()) != null){
	        	logger.trace(line);
	        	sb.append(line);
	            if (line.indexOf("RID =") > -1)
	                responseID = getValue(line);
	           // else if (line.indexOf("RTOE =") > -1)
	           //     waitingTime = Integer.parseInt(getValue(line));
	        }
	        Matcher matcher = WAIT_TIME_PATTERN.matcher(sb.toString());
	        if ( matcher.find() ){
	        	params.put("EST_WAIT_TIME", matcher.group(1));
	        }
	        //response.close();
        }catch(IOException e){
        	throw e;
        }finally{
        	if ( response != null ){
        		try{
        			response.close();
        		}catch(Exception e){}
        	}
        }
    }

    
    protected String getValue(String line)
    {
        return line.substring(line.indexOf("=")+1).trim();
    }

    public String getRequestID()
    {
        return responseID;
    }


    public void setResponseID(String responseID)
    {
        this.responseID = responseID;
    }

    public void setWaitingTime(int waitingTime)
    {
        this.waitingTime = waitingTime;
    }

    public int getWaitingTime()
    {
        return waitingTime;
    }

    protected String responseID;
    protected int waitingTime;
}