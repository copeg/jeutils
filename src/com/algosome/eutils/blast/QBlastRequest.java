package com.algosome.eutils.blast;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Adapted from http://users.encs.concordia.ca/~f_kohant/ncbiblast/
 * @author: Farzad Kohant
 * @author Greg Cope
 */
public class QBlastRequest
{

    public static void runCommand(QBlastCommand command) throws Exception
    {
        URL url = new URL(QBlastURL);

        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("user-agent", "Mozilla/5.0");
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        //System.out.println("command = " + command);
        out.write(command.toString());
        out.flush();

        command.processResult(conn.getInputStream());
        
        out.close();
    }

    public static void setBlastURL(String url){
    	QBlastURL = url;
    }
    protected static String QBlastURL = "http://www.ncbi.nlm.nih.gov/blast/Blast.cgi";
}

