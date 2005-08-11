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
import junit.framework.TestCase;

/**
 * The SNPP Level 1 test case.
 *
 * @author Don Seiler <don@NOSPAM.seiler.us>
 * @version $Revision$
 */
public class Level1Test extends TestCase {
	Message m = null;
	
	public Level1Test (String n) {
		super(n);
	}

	protected void setUp() {
		m = new Message("test.mysnppserver.com", 444, "don", "The system is DOWN.");
	}

	protected void tearDown() {
	}

	public void testSendPage() {
		try {
			m.send();
			System.out.println("Response was: " + m.getSENDResponse());
			System.out.println("Level 1 page sent.");
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IO error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error sending message: " + e.getMessage());
		}
	}
}
