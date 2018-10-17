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
