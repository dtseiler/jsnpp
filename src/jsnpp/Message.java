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
	public static final int SNPP_LEVEL = 1;

	// Message states
	public static final int STATE_UNDEF = -1;
	public static final int STATE_CONN = 0;
	public static final int STATE_CALL = 1;
	public static final int STATE_PAGE = 2;
	public static final int STATE_MESS = 3;
	public static final int STATE_SEND = 4;
	public static final int STATE_QUIT = 5;

	/** Connection to server */
	private Connection conn = null;

	/** Message state */
	private int state = STATE_UNDEF;

	/** Message recipient */
	private String recipient = null;

	/** Message sender */
	private String sender = null;

	/** Message */
	// XXX Check into hard limits on message length
	private String message = null;


	/** Level */
	private int level = SNPP_LEVEL;


	/** Constructor */
	public Message() {
	}

	/** Constructor */
	public Message (String host,
					int port,
					String sender,
					String recipient,
					String message) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;

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
		this.sender = sender;
	}

	/** Sets recipient */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	/** Sets message */
	public void setMessage(String message) {
		this.message = message;
	}

	/** Returns SNPP level of this message */
	public int getLevel() {
		return level;
	}

	/** Sets SNPP level of this message */
	public void setLevel(int level) {
		if (level <= SNPP_LEVEL)
			this.level = level;
		else
			this.level = SNPP_LEVEL;
	}

	//
	// Function to format and send messages
	//

	/** Handles reponse from server */
	private void handleResponse(String response) throws IOException, Exception {
		switch (state) {
			case STATE_CONN:
				if (response.startsWith("220"))
					sendPageCommand();
				else
					errorOut(response);
				break;

			case STATE_PAGE:
				if (response.startsWith("250"))
					sendMessCommand();
				else
					errorOut(response);
				break;

			case STATE_MESS:
				if (response.startsWith("250"))
					sendSendCommand();
				else
					errorOut(response);
				break;

			case STATE_SEND:
				if (response.startsWith("250"))
					sendQuitCommand();
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


	private void sendPageCommand() throws IOException, Exception {
		state = STATE_PAGE;
		String msg = "PAGE " + recipient;
		handleResponse(conn.send(msg));
	}


	private void sendMessCommand() throws IOException, Exception {
		state = STATE_MESS;
		String msg = "MESS " + message;
		handleResponse(conn.send(msg));
	}


	private void sendSendCommand() throws IOException, Exception {
		state = STATE_SEND;
		handleResponse(conn.send("SEND"));
	}


	private void sendQuitCommand() throws IOException, Exception {
		state = STATE_QUIT;
		handleResponse(conn.send("QUIT"));
	}


	private void errorOut(String badResponse) throws Exception {
		throw new Exception ("Unexpected response received during state "
				+ state + ":\n" + badResponse);
	}
}
