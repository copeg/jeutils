package com.algosome.eutils.blast;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * @author: Farzad Kohant
 * @author Greg Cope
 */
public class URLParameter{

	StringBuffer urlText = new StringBuffer();
    String encoding;
	
    /**
     * 
     */
	public URLParameter(){
        this("UTF-8");
    }

    public URLParameter(String encoding){
        this.encoding = encoding;
    }

    /**
     * 
     * @param name
     * @param value
     * @throws UnsupportedEncodingException
     */
    public void addParameter(String name, String value) throws UnsupportedEncodingException{
        if (urlText.length() != 0)
            urlText.append("&");

        urlText.append(URLEncoder.encode(name, encoding)).append("=");
        urlText.append(URLEncoder.encode(value, encoding));
    }

    public String getUrlText(){
        return urlText.toString();
    }

    public String getEncoding(){
        return encoding;
    }

    /**
     * 
     * @param encoding
     */
    public void setEncoding(String encoding){
        this.encoding = encoding;
    }

    
}
