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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.RepositorySearch.serialize.SGLRepository;

import okhttp3.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 * Execute search at gitlab and put the results into the inmemory database.
 */
public class GLSearch{

    ObjectMapper mapper;
    GLRequester req;
    SGLRepository ser;

    public GLSearch(String ApiUrl)throws IOException, SQLException{
	mapper = new ObjectMapper();
	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	req = new GLRequester(ApiUrl);
	ser = new SGLRepository();
    }

    /**
     * Attach the search results to the inmemory database.
     */
    public int attachSearchResults(String searchTerm)throws IOException, SQLException, JsonParseException, JsonMappingException{
	
	//Replace spaces in the search term with + 
	String HttpSearchTerm = searchTerm.replace(' ', '+');

	try(Response res=req.foundProjects(HttpSearchTerm)){
		GLProject projects[] = mapper.readValue(res.body().charStream(), GLProject[].class);
		for(GLProject p:projects){
		    p.getLanguagesApi(mapper, req);
		    ser.serialize(p); 
		}
		return projects.length;
	    }catch(Exception e){
	    throw e;
	}	

    }

    

}
