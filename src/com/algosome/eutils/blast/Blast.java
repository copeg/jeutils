package com.algosome.eutils.blast;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



/**
 * Performs a BLAST at NCBI based upon a PutCommand and GetCommand parameters. To perform a simple blast, 
 * 1) create a PutCommand and call setQuery with the sequence and setProgram
 * to define the program (blastn, blastp, tblastn, etc...) and any other options
 * 2) Create a GetCommand, passing a BlastParser implementation which defines what to do with the blast result.
 * 3) Use the above references to create a new Blast, and run the blast. When completed, the BlastParser will
 * 	be notified and passed the full html results of the blast. 
 * 
 * Adapted from http://users.encs.concordia.ca/~f_kohant/ncbiblast/
 * @author: Farzad Kohant
 * @author Greg Cope
 */
public class Blast implements Runnable{
	
 public static List<String> PROTEIN_DATABASES = new ArrayList<String>();
	
	static{
		PROTEIN_DATABASES.add("Non-redundant protein sequences (nr)");
		PROTEIN_DATABASES.add("Reference proteins (refseq_protein)");
		PROTEIN_DATABASES.add("UniProtKB/Swiss-Prot (swissprot)");
		PROTEIN_DATABASES.add("Patented protein sequences (pat)");
		PROTEIN_DATABASES.add("Protein Data Bank proteins (pdb)");
	}
	
	public static List<String> DNA_DATABASES = new ArrayList<String>();
	
	static{
		DNA_DATABASES.add("Nucleotide collection (nr)");
		
		DNA_DATABASES.add("Reference RNA sequences (refseq_rna)");
		DNA_DATABASES.add("Reference genomic sequences (refseq_genomic)");
		DNA_DATABASES.add("NCBI Genomes (chromosome)");
		DNA_DATABASES.add("Expressed sequence tags (est)");
		DNA_DATABASES.add("Expressed sequence tags (est)");
		DNA_DATABASES.add("Genomic survey sequences (gss)");
		DNA_DATABASES.add("High throughput genomic sequences (htgs)");
		DNA_DATABASES.add("Patent sequences (pat)");
		DNA_DATABASES.add("Protein Data Bank (pdb)");
		DNA_DATABASES.add("Human ALU repeat elements (alu)");
		DNA_DATABASES.add("Sequence tagged sites (dbsts)");
		DNA_DATABASES.add("Whole-genome shotgun contigs (wgs)");
		DNA_DATABASES.add("Transcript Shotgun Assembly (tsa_nt)");
		//DNA_DATABASES.add("Human genomic plus transcript (Human G+T)");
		//DNA_DATABASES.add("Mouse genomic plus transcript (Mouse G+T)");
		
	}
	
	private static final Logger logger = Logger.getLogger(Blast.class);
	
	private final PutCommand putCommand;
	private final GetCommand getCommand;
	
	private volatile boolean keepGoing = true;
		
	/**
	 * Constructs a new Blast object based upon a get and put operation.
	 * @param putCommand
	 * @param getCommand
	 */
    public Blast(PutCommand putCommand, GetCommand getCommand){
        this.putCommand = putCommand;
        this.getCommand = getCommand;
    }
    
    public void stop(){
    	keepGoing = false;
    }
    
    /**
     * 
     * @return
     */
    public boolean wasCancelled(){
    	return keepGoing;
    }

    public void run(){
    	long waitTime = 1000;
        try{
            try{
            	Thread.sleep(waitTime);
            }catch(Exception e){}
            logger.debug("Putting blast request");
            QBlastRequest.runCommand(putCommand);
            logger.info("Blast request ID = " + putCommand.getRequestID());
            String wt = putCommand.getEstimatedWaitTime();
            if (wt != null ){
            	waitTime = Integer.parseInt(wt) * 1000;
            }
            do{
            	if ( !keepGoing ){
            		return;
            	}
            	if ( wt == null ){
            		waitTime = getWaitTime((int)waitTime);
            	}
            	//logger.info("Wait time " + waitTime);
            	try{
            		if ( logger.isDebugEnabled() ){
            			logger.debug("Waiting " + wt);
            		}
            		
                	Thread.sleep(waitTime);
                }catch(Exception e){}

                
                getCommand.setRequestID(putCommand.getRequestID());
                logger.debug("Checking NCBI for status");
                try{
                	QBlastRequest.runCommand(getCommand);
                }
                catch (Exception e){
                	logger.error("An exception happened during connecting to ncbi site." + "Command is: " + getCommand  + "  Trying again...");
                	logger.error(e.getMessage(), e);
                	continue;
                }
                logger.debug(getCommand.getStatus());
                wt = getCommand.getEstimatedWaitTime();
                if ( wt != null ){
                	waitTime = Integer.parseInt(wt) * 1000;
                }
                
            }
            while (!getCommand.getStatus().contains(GetCommand.STATUS_READY)) ;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves a defined wait time. Simply starts at 1000, then goes to 3000, then 5000.
     * @param waitTime
     * @return
     */
    private long getWaitTime(int waitTime){
    	switch(waitTime){
	    	case 1000:
	    		return 3000;
	    	case 3000:
	    		return 5000;
	    	default:
	    		return 1000;
    	}
    }

    
    public GetCommand getGetCommand(){
        return getCommand;
    }

    public PutCommand getPutCommand(){
        return putCommand;
    }
    
    public static void main(String[] args) throws Exception{
    	BasicConfigurator.configure();
    	//PropertyConfigurator.configure(Blast.class.getResource("/log4j.prop"));
    	logger.info("Blast utility test");
    	PutCommand put = new PutCommand();
//    	put.setQuery("MTCLDELAHSLESKSGTTNSKTRNSKIKTIDLYQQNELSGQHSQDQDKFYRLPAMDPIARDKKPWKQDVN"+
//"YFNKCYISSLALMKMCTHAQTGGSIEIMGMLVGKISGHSIIVMDTYRLPVEGTETRVNAQNEAYTYMVEY"+
//"LTERQQLSNGKNEENIVGWYHRHPRYGCWLKGIDVSTQSLNQGLQDPYLAIVVDPVKTLKQGKVEIGAFR"+
//"NVS");
    	put.setQuery("agaaattaaagctacttacaacaacggtctactacaaattaaggtgcctaaaattgtcaatgacactgaaaagccgaagccaaaaaagaggatcgccattgaggaaatacccgacgaagaattggagtttgaagaaaatcccaaccctacggtagaaaattgaatatcgtatctgtttatacacacatacatacatttatatttataataagcgttaaaatttcggcagaatatctgtcaaccacacaaaaatcatacaacgaatggtatatgcttcatttctttgtttcgcattagctgcgctatttgactcaaattattattttttactaagacgacgcgtcacagtgttcgagtctgtgtcatttcttttgtaattctcttaaaccacttcataaagttgtgaagttcatagcaaaattcttccgcaaaaagatgaatcttagttctcagcccaccaaaagaggtacatgctaagatcatacagaagttattgtcacttcttaccttgctcttaaatgtacattacaaccgggtattatatcttacatcatcgtataatatgatctttctttatggagaaaatttttttttcactcgaccaaagctcccattgcttctgaagagtgtagtgtatattggtacatcttctcttgaaagactccattgtactgtaacaaaaagcggtttcttcatcgacttgctcggaataacatctatatctgcccactagcaacaatgtcggattcaaaccaaggcaacaatcagcaaaactaccagcaatacagccagaacggtaaccaacaacaaggtaacaacagataccaaggttatcaagcttacaatgctcaagcccaacctgcaggtgggtactaccaaaattaccaaggttattctgggtaccaacaaggtggctatcaacagtacaatcccgacgccggttaccagcaacagtataatcctcaaggaggctatcaacagtacaatcctcaaggcggttatcagcagcaattcaatccacaaggtggccgtggaaattacaaaaacttcaactacaataacaatttgcaaggatatcaagctggtttccaaccacagtctcaaggtatgtctttgaacgactttcaaaagcaacaaaagcaggccgctcccaaaccaaagaagactttgaagcttgtctccagttccggtatcaagttggccaatgctaccaagaaggttggcacaaaacctgccgaatctgataagaaagaggaagagaagtctgctgaaaccaaagaaccaactaaagagccaacaaaggtcgaagaaccagttaaaaaggaggagaaaccagtccagactgaagaaaagacggaggaaaaatcggaacttccaaaggtagaagaccttaaaatctctgaatcaacacataataccaacaatgccaatgttaccagtgctgatgccttgatcaaggaacaggaagaagaagtggatgacgaagttgttaacgatatgtttggtggtaaagatcacgtttctttaattttcatgggtcatgttgatgccggtaaatctactatgggtggtaatctactatacttgactggctctgtggataagagaactattgagaaatatgaaagagaagccaaggatgcaggcagacaaggttggtacttgtcatgggtcatggataccaacaaagaagaaagaaatgatggtaagactatcgaagttggtaaggcctactttgaaactgaaaaaaggcgttataccatattggatgctcctggtcataaaatgtacgtttccgagatgatcggtggtgcttctcaagctgatgttggtgttttggtcatttccgccagaaagggtgagtacgaaaccggttttgagagaggtggtcaaactcgtgaacacgccctattggccaagacccaaggtgttaataagatggttgtcgtcgtaaataagatggatgacccaaccgttaactggtctaaggaacgttacgaccaatgtgtgagtaatgtcagcaatttcttgagagcaattggttacaacattaagacagacgttgtatttatgccagtatccggctacagtggtgcaaatttgaaagatcacgtagatccaaaagaatgcccatggtacaccggcccaactctgttagaatatctggatacaatgaaccacgtcgaccgtcacatcaatgctccattcatgttgcctattgccgctaagatgaaggatctaggtaccatcgttgaaggtaaaattgaatccggtcatatcaaaaagggtcaatccaccctactgatgcctaacaaaaccgctgtggaaattcaaaatatttacaacgaaactgaaaatgaagttgatatggctatgtgtggtgagcaagttaaactaagaatcaaaggtgttgaagaagaagacatttcaccaggttttgtactaacatcgccaaagaaccctatcaagagtgttaccaagtttgtagctcaaattgctattgtagaattaaaatctatcatagcagccggtttttcatgtgttatgcatgttcatacagcaattgaagaggtacatattgttaagttattgcacaaattagaaaagggtaccaaccgtaagtcaaagaaaccacctgcttttgctaagaagggtatgaaggtcatcgctgttttagaaactgaagctccagtttgtgtggaaacttaccaagattaccctcaattaggtagattcactttgagagatcaaggtaccacaatagcaattggtaaaattgttaaaattgccgagtaaatttcttgcaaacataagtaaatgcaaacacaataataccgatcataaagcattttcttctatattaaaaaacaaggtttaataaagctgttatatatatatatatatatatagacgtataattagtttagttctttttgtaccatataccataaacaaggtaaacttcacctctcaatatatctagaatttcataaaaatatctagcaaggtttcaactccttcaatcacgttttcatcataacccttccccggcgttatttcagaatgtgcaaaatctattagtgacatggaactcaaagaaccagttgtttttttgtcctttggtccttcgctgcttccctcggcatcatcatcatcatcatcatcattatcatcatcgtcgtcatcatcgtctataaaatcatctcgcataagtttgtcaacatcatttagtaattcccatcgctccgggtctccttcgtaaataaacaaaagactacttgatatcattctaacttcttcttctagcatagtattataaaa");
    	put.setProgram("blastn");
    	put.setDatabase("nr");
    	
    	GetCommand get = new GetCommand(new BlastParser(){

			@Override
			public void parseBlastOutput(String output) {
				System.out.println(output);
			}
    		
    	});
    	get.setFormatType("Text");
    	logger.info("Blasting");
    	Blast blast = new Blast(put, get);
    	blast.run();
    	
    }
}