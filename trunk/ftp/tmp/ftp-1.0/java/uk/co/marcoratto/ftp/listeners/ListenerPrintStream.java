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

import java.io.File;

import org.apache.log4j.Logger;

public class ListenerPrintStream extends ListenerAbstract {
	
	private static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");
		
	public void onError(Throwable t) throws ListenerException {
		System.err.println(t.getMessage());
		t.printStackTrace(System.err);
	}

	public void onStart() throws ListenerException{
		logger.info("Start.");
	}

	public void onEnd(int returnCode) throws ListenerException{
		logger.info("End with return code " + returnCode);
	}
	
	public void onStartUpload(int counter, int totalFiles, File fromUri, String toUri) throws ListenerException {
		System.out.println("Start upload file " + counter + "/" + totalFiles + " from " + fromUri + " to " + toUri);
	}
	
	public void onEndUpload(int counter, int totalFiles, File fromUri, String toUri) throws ListenerException {
		System.out.println("End upload file " + counter + "/" + totalFiles + " from " + fromUri + " to " + toUri);
	}

	public void onStartDownload(int counter, int totalFiles, String fromUri, String toUri) throws ListenerException{
		System.out.println("Start download from " + fromUri + " to " + toUri);
	}
	
	public void onEndDownload(int counter, int totalFiles, String fromUri, String toUri) throws ListenerException{
		System.out.println("End download from " + fromUri + " to " + toUri);
	}

}
