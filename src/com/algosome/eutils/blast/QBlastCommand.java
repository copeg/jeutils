package com.algosome.eutils.blast;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Adapted from http://users.encs.concordia.ca/~f_kohant/ncbiblast/
 * @author: Farzad Kohant
 * @author Greg Cope
 */
public abstract class QBlastCommand implements Cloneable{
	
	private static final Logger logger = Logger.getLogger(QBlastCommand.class);
	
	/*  */
	protected Map<String, String> params = new HashMap<String, String>();
	
	
	
    public QBlastCommand(){
        setDefaultParamters();
    }

    /**
     * Sets the default parameters for the Blast. Called upon construction.
     */
    protected abstract void setDefaultParamters();
    
    /**
     * Retrieves the command name. 
     * @return
     */
    protected abstract String getCommandName();
    
    /**
     * Process the result. Clients can override this function to parse
     * results appropriately. 
     * @param in
     * @throws Exception
     */
    protected abstract void processResult(InputStream in) throws Exception;

    /**
     * 
     */
    @Override
    public String toString(){
        Set<Map.Entry<String,String>> entries = params.entrySet();
        URLParameter parameter = new URLParameter();
        
        try{
            parameter.addParameter("CMD", getCommandName());

            for (Map.Entry<String,String> entry : entries){
                if (entry.getValue() == null || entry.getValue().toString().length() == 0){
                	logger.debug("Null entry = " + entry.getKey());
                    continue;
                }
                parameter.addParameter(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        catch (UnsupportedEncodingException e){
            throw new IllegalStateException("Failed to form the url. nested exception is = " + e);
        }
        String url = parameter.getUrlText();
        logger.debug(url);
        return url;
    }

    
    
}
