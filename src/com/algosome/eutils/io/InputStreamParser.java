package com.algosome.eutils.io;
/**
InputStreamParser.java

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

import java.io.*;

/**
* Interface to parse an InputStream. Specifies a function to parse an InputStream and functions to set specific locations to start and end parsing. 
*
* @version 1.1
* @author Greg Cope
*/
public interface InputStreamParser{

	/**
	*Specifies an implementation to parse the given InputStream. 
	*@param is An input stream from a URL or file connection
	*/
	public void parseInput( InputStream is ) throws IOException;

	/**
	*Specifies a start location to parse from.
	*@param start The location to start parsing.
	*/
	public void parseFrom(int start);
	/**
	*Specifies an end location to parse to.
	*@param end The location to stop parsing.
	*/
	public void parseTo(int end);
	
}