/*
 * Copyright (C) 2011 Marco Ratto
 *
 * This file is part of the project ftp-client-java.
 *
 * ftp-client-java is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * ftp-client-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ftp-client-java; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package uk.co.marcoratto.net;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.log4j.Logger;

public class PrintCommandListener implements ProtocolCommandListener {
	
	private final static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");

	public void protocolCommandSent(ProtocolCommandEvent arg0) {
		logger.info(arg0.getCommand());
	}

	public void protocolReplyReceived(ProtocolCommandEvent arg0) {
		logger.info(arg0.getCommand());
	}

}
