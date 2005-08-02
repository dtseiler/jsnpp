/**
 *	JSNPP - Java SNPP API.
 *	Copyright (C) 2005  Don Seiler <don@seiler.us>
 *	
 *	This library is free software; you can redistribute it and/or
 *	modify it under the terms of the GNU Lesser General Public
 *	License as published by the Free Software Foundation; either
 *	version 2.1 of the License, or (at your option) any later version.
 *	
 *	This library is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *	Lesser General Public License for more details.
 *	
 *	You should have received a copy of the GNU Lesser General Public
 *	License along with this library; if not, write to the Free Software
 *	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package jsnpp;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * SNPP Message Class
 *
 * @author Don Seiler <don@seiler.us>
 * @version $Id$
 */
public class Message {

	// Maximum SNPP Level Supported
	public static final int MAX_SNPP_LEVEL = 1;

	// Message states
	public static final int STATE_UNDEF = -1;
	public static final int STATE_CONN = 0;
	public static final int STATE_PAGE = 11;
	public static final int STATE_MESS = 12;
	public static final int STATE_SEND = 13;
	public static final int STATE_QUIT = 14;
	public static final int STATE_DATA = 21;
	public static final int STATE_LOGI = 22;
	//public static final int STATE_LEVE = 23;
	public static final int STATE_COVE = 24;
	public static final int STATE_SUBJ = 25;
	public static final int STATE_HOLD = 26;
	public static final int STATE_CALL = 27;
	public static final int STATE_ALER = 28;

	/** Connection to server */
	private Connection conn = null;

	/** Message state */
	private int state = STATE_UNDEF;

	/** Pager (recipient) */
	private String pager = null;

	/** CallerIdentifier */
	private String callerIdentifier = null;

	/** Message */
	private String message = null;
	
	/** 
	 * Data 
	 *
	 * A message cannot send both MESS and DATA.
	 */
	private String[] data = null;
	
	/** Subject */
	private String subject = null;
	
	/** Login */
	private String login = null;
	
	/** Service Level */
	private int serviceLevel = -1;
	


	/** Level */
	private int level = MAX_SNPP_LEVEL;


	/** Constructor */
	public Message() {
	}
	
	/** Constructor */
	public Message (String host,
					int port,
					String recipient,
					String message) {
		setRecipient(recipient);
		setMessage(message);
		
		conn = new Connection(host, port);
		state = STATE_CONN;
	}

	/** Constructor */
	public Message (String host,
					int port,
					String sender,
					String recipient,
					String message) {
		setSender(sender);
		setRecipient(recipient);
		setMessage(message);

		conn = new Connection(host, port);
		state = STATE_CONN;
	}

	/**
	 * Initiates the process of sending the message, starting with creating
	 * the connection.
	 */
	public void send() throws UnknownHostException, IOException, Exception {
		handleResponse(conn.connect());
	}

	/** Returns state */
	public int getState () {
		return state;
	}

	/** Returns sender */
	public String getSender () {
		return sender;
	}

	/** Returns recipient */
	public String getRecipient () {
		return recipient;
	}

	/** Returns message */
	public String getMessage () {
		return message;
	}


	/** Sets connection info */
	public void setConnectionInfo(String host, int port) {
		conn = new Connection(host, port);
		state = STATE_CONN;
	}

	/** Sets state */
	public void setState(int state) {
		this.state = state;
	}

	/** Sets sender */
	public void setSender(String sender) {
		this.callerIdentifier = sender;
	}

	/** Sets recipient */
	public void setRecipient(String recipient) {
		this.pager = recipient;
	}

	/** Sets message */
	public void setMessage(String message) {
		// Check for \n chars
		this.message = message;
	}

	/** Returns SNPP level of this message */
	public int getLevel() {
		return level;
	}

	/** Sets SNPP level of this message */
	public void setLevel(int level) {
		if (level <= MAX_SNPP_LEVEL)
			this.level = level;
		else
			this.level = MAX_SNPP_LEVEL;
	}
	
	/** Sets subject */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/** Sets data */
	public void setData(String[] data) {
		this.data = data;
	}

	//
	// Function to format and send messages
	//

	/** Handles reponse from server */
	private void handleResponse(String response) throws IOException, Exception {
		
		// XXX Probably safe to ignore all "500 Command not implemented" messages
		// XXX PAGE should be sent last before SEND
		switch (state) {
			case STATE_CONN:
				if (response.startsWith("220"))
					sendPAGECommand();
				else
					errorOut(response);
				break;

			case STATE_PAGE:
				if (response.startsWith("250")) {
					if ((level >= 2) && (callerIdentifier != null))
						sendCALLCommand();
					else
						sendMESSCommand();
				} else
					errorOut(response);
				break;

			case STATE_MESS:
				if (response.startsWith("250"))
					sendSENDCommand();
				else
					errorOut(response);
				break;

			case STATE_SEND:
				if (response.startsWith("250")
						|| response.startsWith("860") 
						|| response.startsWith("960"))
					sendQUITCommand();
				else
					errorOut(response);
				break;

			case STATE_QUIT:
				if (response.startsWith("221"))
					conn.close();
				else
					errorOut(response);
				break;
		}

	}


	private void sendPAGECommand() throws IOException, Exception {
		state = STATE_PAGE;
		String msg = "PAGE " + pager;
		handleResponse(conn.send(msg));
	}
	
	
	private void sendCALLCommand() throws IOException, Exception {
		state = STATE_CALL;
		String msg = "CALL " + callerIdentifier;
		handleResponse(conn.send(msg));
	}
	
	
	private void sendSUBJCommand() throws IOException, Exception {
		state = STATE_SUBJ;
		String msg = "SUBJ " + subject;
		handleResponse(conn.send(msg));
	}


	private void sendMESSCommand() throws IOException, Exception {
		state = STATE_MESS;
		String msg = "MESS " + message;
		handleResponse(conn.send(msg));
	}


	private void sendSENDCommand() throws IOException, Exception {
		state = STATE_SEND;
		handleResponse(conn.send("SEND"));
	}


	private void sendQUITCommand() throws IOException, Exception {
		state = STATE_QUIT;
		handleResponse(conn.send("QUIT"));
	}


	private void errorOut(String badResponse) throws Exception {
		throw new Exception ("Unexpected response received during state "
				+ state + ":\n" + badResponse);
	}
}
