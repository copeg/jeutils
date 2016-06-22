package com.algosome.eutils.blast;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapted from http://users.encs.concordia.ca/~f_kohant/ncbiblast/
 * @author: Farzad Kohant
 * @author Greg Cope
 */
public class GetCommand extends QBlastCommand{
	
	 public static final String STATUS_READY = "READY";
	 public static final String STATUS_WAITING = "WAITING";
	 public static final String STATUS_NONE = "NONE"; 
	
	protected String status = STATUS_NONE;
	
	private final BlastParser blastParser;
	
	
	/**
	 * Constructs a new GetCommand with a parser to parse the blast output. 
	 * @param parser
	 */
	public GetCommand(BlastParser parser){
		this.blastParser = parser;
	}
	
	
	
    /**
     * Number of alignments
     *
     * @param value integer value
     */
    public void setAlignments(int value)
    {
        params.put("ALIGNMENTS", String.valueOf(value));
    }

    /**
     * Type of alignment view (FORMAT_OBJECT=Alignment only)
     *
     * @param value Pairwise, QueryAnchored, QueryAnchoredNoIdentities,
     *              FlatQueryAnchored, FlatQueryAnchoredNoIdentities, Tabular
     */
    public void setAlignmentView(String value)
    {
        params.put("ALIGNMENT_VIEW", value);
    }
    /**
     * 
     * @return
     */
    public String getAlignmentView(){
    	 return params.get("ALIGNMENT_VIEW");
    }

    /**
     * Number of descriptions
     *
     * @param value integer value
     */
    public void setDescriptions(int value)
    {
        params.put("DESCRIPTIONS", String.valueOf(value));
    }

    /**
     * Add TARGET to Entrez links in formatted results
     *
     * @param value yes, no
     */
    public void setEntrezTarget(boolean value)
    {
        params.put("ENTREZ_LINKS_NEW_WINDOW", value ? "yes" : "no");
    }

    /**
     * Low expect value threshold for formatting
     *
     * @param value double type value
     */
    public void setLowExpect(double value)
    {
        params.put("EXPECT_LOW", String.valueOf(value));
    }

    /**
     * High expect value threshold for formatting
     *
     * @param value double type value
     */
    public void setHighExpect(double value)
    {
        params.put("EXPECT_HIGH", String.valueOf(value));
    }

    /**
     * Entrez query to limit formatting of Blast results
     *
     * @param value Entrez query format
     */
    public void setEntrezQueryFormat(String value)
    {
        params.put("FORMAT_ENTREZ_QUERY", value);
    }

    /**
     * Specifies object to get
     *
     * @param value Alignment, Neighbors, PSSM
     *              SearchInfo
     *              TaxBlast, TaxblastParent, TaxBlastMultiFrame
     */
    public void setObject(String value)
    {
        params.put("FORMAT_OBJECT", value);
    }

    /**
     * Type of formatting
     *
     * @param value HTML, Text, ASN.1, XML
     */
    public void setFormatType(String value)
    {
        params.put("FORMAT_TYPE", value);
    }

    /**
     * Show NCBI GI
     *
     * @param value yes, no
     */
    public void setShowGI(boolean value)
    {
        params.put("NCBI_GI", value ? "yes" : "no");
    }

    /**
     * Request ID
     *
     * @param value Valid request ID
     */
    public void setRequestID(String value)
    {
        params.put("RID", value);
    }

    /**
     * Allow to download megablast results as a gzip-compressed file
     *
     * @param value yes, no
     */
    public void setResultsFile(boolean value)
    {
        params.put("RESULTS_FILE", value ? "yes" : "no");
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
    
//    /**
//     * 
//     * @param max
//     */
//    public void setMaxNumSequences(int max){
//    	params.put("MAX_NUM_SEQ",Integer.toString(max));
//    }

    /**
     * Show graphical overview
     *
     * @param value yes, no
     */
    public void setShowOverview(boolean value)
    {
        params.put("SHOW_OVERVIEW", value ? "yes" : "no");
    }

    @Override
    protected void setDefaultParamters(){
        setAlignments(500);
        setAlignmentView("pairwise");
        setDescriptions(500);
//        setLowExpect(0);
//        setHighExpect(0);
        setObject("Alignment");
        setFormatType("HTML");
        //setShowGI(false);
        //setResultsFile(false);
        setService("plain");
        //setShowOverview(true);
    }
    
    @Override
    protected String getCommandName(){
        return "Get";
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
     * Processes the result. Should the status signify the result is ready, 
     * the output will be passed to the BlastParser defined in the constructor.
     */
    @Override
    protected void processResult(InputStream in) throws Exception{
    	StringBuilder sb = new StringBuilder();
     
    	BufferedReader br = null;
    	try{
    		br = new BufferedReader(new InputStreamReader(in));
    	
	        String line;
	        status = "";
	
	        while ((line = br.readLine()) != null){
	        	sb.append(line);
	        	sb.append("\n");
	            if (line.indexOf("Status=") != -1){
	                status = getValue(line).toUpperCase();
	            }
	        }
	        
	        if (params.get("FORMAT_TYPE").equals("XML") && status==null){
	            status = STATUS_READY;
	        }
	      
	        if (status != null && status.equals(STATUS_READY)){
	        	if ( blastParser != null ){
	        		blastParser.parseBlastOutput(sb.toString());
	        	}
	        }else{
	        	Matcher matcher = WAIT_TIME_PATTERN.matcher(sb.toString());
	        	if ( matcher.find() ){
	        		params.put("EST_WAIT_TIME", matcher.group(1));
	        	}
	        }
    	}catch(Exception e){
    		throw e;
    	}finally{
    		if ( br != null ){
    			try{br.close();}catch(Exception e){}
    		}
    	}
    }

    

    protected String getValue(String line){
        return line.substring(line.indexOf("=") + 1).trim();
    }

    /**
     * Retrieves the current status.
     * @return
     */
    public String getStatus(){
        return status;
    }

   
}