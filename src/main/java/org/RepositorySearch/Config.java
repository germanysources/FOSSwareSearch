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

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonParseException;

/**
 * Retrieve the configuration file (singleton design). 
 * The configuration file is a json object file with the following informations:
 * 1. Hosting provider to search through
 * 2. fetch limit (max. number of results
 * 3. Additions for the search query (for example programming language)
 */
public class Config{

    public int maxNoResults;
    public boolean AddMyFavorites;
    public String FavoriteAdditions;
    public ArrayList<Integer> HostingProvider;

    private static Config inst;

    /**
     * Retrieve the configuration from the json file "config.json" and parse it directly
     * in the singleton instance of this class
     */
    public static Config getInstance()throws IOException, JsonParseException, JsonMappingException{
	if(inst == null){
	    ObjectMapper mapper = new ObjectMapper();
	    inst = mapper.readValue(new File("config.json"), Config.class);
	}
	return inst;
    }


}
