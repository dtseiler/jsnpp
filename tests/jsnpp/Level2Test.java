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
 * The SNPP Level 2 test case.
 *
 * @author Don Seiler <don@NOSPAM.seiler.us>
 * @version $Revision$
 */
public class Level2Test extends TestCase {
	Message m = null;
	String[] data = { "This", "is being", "sent on separate", "lines." };
	
	public Level2Test (String n) {
		super(n);
	}

	protected void setUp() {
		m = new Message("test.mysnppserver.com", 444, "don");
		//m.setLevel(2);
		m.setSubject("The Subject");
		//m.setCoverageArea(2);
		m.setAlertOverride(true);
		m.setData(data);
	}

	protected void tearDown() {
	}

	public void testSendPage() {
		try {
			m.send();
			System.out.println("Level 2 page sent.");
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IO error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error sending message: " + e.getMessage());
		}
	}
}

