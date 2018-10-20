/*
Eclipse Public License - v 2.0
Copyright (c) 2018 Johannes Gerbershagen <johannes.gerbershagen@kabelmail.de>

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v2.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v20.html

NO WARRANTY:
EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, AND TO THE EXTENT
PERMITTED BY APPLICABLE LAW, THE PROGRAM IS PROVIDED ON AN "AS IS"
BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS OF
TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR
PURPOSE. Each Recipient is solely responsible for determining the
appropriateness of using and distributing the Program and assumes all
risks associated with its exercise of rights under this Agreement,
including but not limited to the risks and costs of program errors,
compliance with applicable laws, damage to or loss of data, programs
or equipment, and unavailability or interruption of operations.
*/
package org.RepositorySearch;

import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ErrorManager;
import java.util.Locale;
import java.io.IOException;
import java.nio.file.*;

public final class RSLogger{
    /* create attribute lg  only one time
       call java -Djava.util.logging.config.file=log.properties
     */

    private static Logger lg;
    public static IOException InitFailed;

    public static void getInstance(){
	Initialize();
    }

    public static void close(){
	/* write to file system and close the log */
	try{
	    for(Handler h:lg.getHandlers()){
		h.flush();
		h.close();
	    }
	}catch(NullPointerException e){
	    //nothing to do
	}
    }
    
    public static void LogException(Exception e){
	
	if(e.getCause() != null){
	    LogExceptionCause(e);
	}else{
	    LogExceptionMessage(e, "unexpected exception");
	}

    }

    /**
     * logs an exception with a cause
     *@param e e.getCause() != null
     */
    public static void LogExceptionCause(Exception e){


	if(lg == null){
	    /* If log isn't created yet, write to System.err
	     */
	    System.err.println(e.getMessage());
	    System.err.println(e.getCause().getMessage());
	    e.getCause().printStackTrace(); 
	    return;
	}
	
	lg.log(Level.SEVERE, e.getMessage(), e.getCause());

    }

    /**
     * Logs an exception with an message. 
     * @param e e.getCause isn't taken in account
     */
    public static void LogExceptionMessage(Exception e, String msg){

	if(lg == null){
	    System.err.println(msg);
	    System.err.println(e.getMessage());
	    e.printStackTrace();
	    return;
	}

	lg.log(Level.SEVERE, msg, e);

    }

    protected static void Initialize(){

	//create log directory
	Path logDir = FileSystems.getDefault().getPath("log");     
	try{
	    try{
		Files.createDirectory(logDir);
	    }catch(FileAlreadyExistsException e){
		// nothing to do
	    }
	    FileHandler fh = new FileHandler();

	    lg = Logger.getLogger("RepositorySearch");
	    lg.addHandler(fh);
	
	
	    //log the version
	    lg.info("version " + CONSTANT.Version);
	    
	}
	catch(IOException e){
	    InitFailed = new IOException(Msg.initialize_log.format(logDir.toString()), e);
	    LogExceptionCause(InitFailed);
	}catch(SecurityException e){
	    InitFailed = new IOException(Msg.initialize_log.format(logDir.toString()), e);
	    LogExceptionCause(InitFailed);
	}catch(NullPointerException e){
	    // the pattern property is a empty string
	    NullPointerException cause = new NullPointerException("The pattern property is a empty string");
	    InitFailed = new IOException(Msg.initialize_log.format(logDir.toString()), cause);
	    LogExceptionCause(InitFailed);
	}

    }

}
