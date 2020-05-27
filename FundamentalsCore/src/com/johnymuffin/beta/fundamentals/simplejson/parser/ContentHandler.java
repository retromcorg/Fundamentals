package com.johnymuffin.beta.fundamentals.simplejson.parser;

import java.io.IOException;

/**
 * A simplified and stoppable SAX-like content handler for stream processing of JSON text. 
 * 
 * @see org.xml.sax.ContentHandler
 * @see JSONParser#parse(java.io.Reader, ContentHandler, boolean)
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface ContentHandler {
	/**
	 * Receive notification of the beginning of JSON processing.
	 * The parser will invoke this method only once.
     * 
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
	 * 			- JSONParser will stop and throw the same exception to the caller when receiving this exception.
	 */
	void startJSON() throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException, IOException;
	
	/**
	 * Receive notification of the end of JSON processing.
	 * 
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
	 */
	void endJSON() throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException, IOException;
	
	/**
	 * Receive notification of the beginning of a JSON object.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
     *          - JSONParser will stop and throw the same exception to the caller when receiving this exception.
     * @see #endJSON
	 */
	boolean startObject() throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException, IOException;
	
	/**
	 * Receive notification of the end of a JSON object.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
     * 
     * @see #startObject
	 */
	boolean endObject() throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException, IOException;
	
	/**
	 * Receive notification of the beginning of a JSON object entry.
	 * 
	 * @param key - Key of a JSON object entry. 
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
     * 
     * @see #endObjectEntry
	 */
	boolean startObjectEntry(String key) throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException, IOException;
	
	/**
	 * Receive notification of the end of the value of previous object entry.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
     * 
     * @see #startObjectEntry
	 */
	boolean endObjectEntry() throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException, IOException;
	
	/**
	 * Receive notification of the beginning of a JSON array.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
     * 
     * @see #endArray
	 */
	boolean startArray() throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException, IOException;
	
	/**
	 * Receive notification of the end of a JSON array.
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
     * 
     * @see #startArray
	 */
	boolean endArray() throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException, IOException;
	
	/**
	 * Receive notification of the JSON primitive values:
	 * 	java.lang.String,
	 * 	java.lang.Number,
	 * 	java.lang.Boolean
	 * 	null
	 * 
	 * @param value - Instance of the following:
	 * 			java.lang.String,
	 * 			java.lang.Number,
	 * 			java.lang.Boolean
	 * 			null
	 * 
	 * @return false if the handler wants to stop parsing after return.
	 * @throws com.johnymuffin.beta.fundamentals.simplejson.parser.ParseException
	 */
	boolean primitive(Object value) throws ParseException, IOException;
		
}
