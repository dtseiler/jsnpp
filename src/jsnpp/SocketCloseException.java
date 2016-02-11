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

/**
 * SocketCloseException thrown when closing streams and sockets
 *
 * @author Mike Kellstrand
 * @version $Id$
 */
public class SocketCloseException extends IOException {

    public SocketCloseException(Exception e) {
        super(e);
    }
}
