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
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import okhttp3.Response;

import org.kohsuke.github.GitHub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GLProject{

    private static final String[] TIME_FORMATS = {"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"};
    
    //public String name, name_with_namespace, path, path_with_namespace, default_branch, ssh_url_to_repo, avatar_url;
    public String http_url_to_repo,
	description,
	web_url;
    public int star_count, forks_count, score;
    public String created_at, last_activity_at;
    public int id;
    
    HashMap languages;       
    
    /**
     * Request the languages with an api call
     */
    public void getLanguagesApi(ObjectMapper mapper, GLRequester requester)throws IOException, JsonParseException, JsonMappingException{
	try(Response res = requester.getLanguage(id)){
		languages = mapper.readValue(res.body().charStream(), HashMap.class);
		//needed for closing the resource
	    }catch(Exception e){
	    throw e;
	}
    }

    /**
     * Get's the main language used in this project.
     * Call the method getLanguagesApi before
     */
    public String getMainLanguage(){
	float maxCodePer = 0;
	String mlang = null;

	Set<String> langs = languages.keySet();
	for(String l:langs){
	    if(((Double)languages.get(l)).floatValue() > maxCodePer){
		mlang = l;
		maxCodePer = ((Double)languages.get(l)).floatValue(); 
	    }
	}
	return mlang;

    }
    
    public java.sql.Date getUpdatedAt(){
	return new java.sql.Date(parseDate(last_activity_at).getTime());
    }

    public java.sql.Date getCreatedAt(){
	return new java.sql.Date(parseDate(created_at).getTime());
    }

    private java.util.Date parseDate(String timestamp){
        if (timestamp==null)    return null;
        for (String f : TIME_FORMATS) {
            try {
                SimpleDateFormat df = new SimpleDateFormat(f);
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                return df.parse(timestamp);
            } catch (ParseException e) {
                // try next
            }
        }
        throw new IllegalStateException("Unable to parse the timestamp: "+timestamp);
    }	
    

}
