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

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * SNPP Connection Class
 *
 * @author Don Seiler <don@seiler.us>
 * @version $Id$
 */
public class Connection {

	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	private String host = null;
	private int port = 444;

	/** Constructor */
	public Connection(String host, int port) {
		this.host = host;

		if (port > 0)
			this.port = port;
	}


	/**
	 * Connects to host/port of SNPP server.
	 *
	 * This method will create the socket to the SNPP server, and return the
	 * response code.  A successful connection will return a String beginning
	 * with "220", similar to this:
	 *
	 * 220 QuickPage v3.3 SNPP server ready at Tue May 17 11:48:12 2005
	 *
	 * @return Response of SNPP server to connection.
	 */
	public String connect() throws UnknownHostException, IOException {
		return connect(0, 0);
	}


	/**
	 * Connects to host/port of SNPP server, with specified timeout settings.
	 *
	 * This method will create the socket to the SNPP server, and return the
	 * response code.  A successful connection will return a String beginning
	 * with "220", similar to this:
	 *
	 * 220 QuickPage v3.3 SNPP server ready at Tue May 17 11:48:12 2005
	 *
	 * @param socketConnectTimeout	Timeout in milliseconds for connection attempt. A timeout of zero is interpreted as an infinite timeout.
	 * @param socketInputTimeout	Timeout in milliseconds to wait on responses from the SNPP server. A timeout of zero is interpreted as an infinite timeout.
	 * @return Response of SNPP server to connection.
	 */
	public String connect(int socketConnectTimeout, int socketInputTimeout)
			throws UnknownHostException, IOException, SocketTimeoutException {
		
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(host, port), socketConnectTimeout);
		socket.setSoTimeout(socketInputTimeout);

		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// This will contain the immediate response
		return in.readLine();
	}

	/**
	 * Closes connection to SNPP server.
	 * If any Exceptions are thrown while closing the streams or socket,
	 * then the first Exception encountered will be re-thrown.
	 *
	 * @throws SocketCloseException		Exception thrown while closing the socket
     	*/
	public void close() throws SocketCloseException {

		SocketCloseException sce = null;

		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
				if (sce == null) {
					sce = new SocketCloseException(e);
				}
			}
		}

		if (in != null) {
			try {
				in.close();
			} catch (Exception e) {
				if (sce == null) {
					sce = new SocketCloseException(e);
				}
			}
		}

		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				if (sce == null) {
					sce = new SocketCloseException(e);
				}
			}
		}

		if (sce != null) {
			throw sce;
		}
	}

	/** Sends data to SNPP server */
	public String send(String data) throws IOException {
		return send(data, true);
	}
	
	public String send(String data, boolean wait) throws IOException {
		String response = null;
		
		// Send command to server
		out.println(data);
		//System.out.println(data);
		
		// Read response
		if (wait) {
			response = in.readLine();
			//System.out.println(response);
		}
		
		// Return response, or null
		return response;
	}
}
