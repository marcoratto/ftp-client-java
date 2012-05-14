/*
 * Copyright (C) 2011 Marco Ratto
 *
 * This file is part of the project scp-java-client.
 *
 * scp-java-client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * scp-java-client is free software; you can redistribute it and/or modify
 * it under the terms of the the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.co.marcoratto.ftp.listeners;

import org.apache.log4j.Logger;

import uk.co.marcoratto.util.PropertiesManager;
import uk.co.marcoratto.util.PropertiesManagerException;

public class ListenerFactory {

	private static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");
	
    private static ListenerFactory instance = null;

    private ListenerFactory() {
      logger.debug("ListenerFactory.ListenerFactory();");
    }

    public static ListenerFactory getInstance() {
      if (instance == null) {
        instance = new ListenerFactory();
      } 
      return instance;
    }
    
    public void reset() {
    	instance = null;
    }
    
    public Listener getListener() throws ListenerFactoryException {
    	Listener listener = null;
		try {
			String factory = PropertiesManager.getInstance().getProperty("ftpsListener", null);
			if (factory != null) {
		    	logger.info("factoryString=" + factory);
		    	listener = (Listener) Class.forName(factory).newInstance();
			} else {
				throw new ListenerFactoryException("Why 'ftpsListener' is null ?");
			}
		} catch (PropertiesManagerException e) {
			throw new ListenerFactoryException(e);
		} catch (ClassNotFoundException e) {
			throw new ListenerFactoryException(e);
		} catch (InstantiationException e) {
			throw new ListenerFactoryException(e);
		} catch (IllegalAccessException e) {
			throw new ListenerFactoryException(e);
		}    
    	return listener;
    }

}