package com.algosome.eutils.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamAdapter implements InputStreamParser{
	/**
	*Specifies an implementation to parse the given InputStream. 
	*@param is An input stream from a URL or file connection
	*/
	public void parseInput( InputStream is ) throws IOException{}

	/**
	*Specifies a start location to parse from.
	*@param start The location to start parsing.
	*/
	public void parseFrom(int start){}
	/**
	*Specifies an end location to parse to.
	*@param end The location to stop parsing.
	*/
	public void parseTo(int end){}
}
