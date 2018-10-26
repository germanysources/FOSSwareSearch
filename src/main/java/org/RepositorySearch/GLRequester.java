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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.Headers;

import org.kohsuke.github.HttpException;

/**
 * Requester for gitlab api calls (Github Api v4). Singleton class.
 */
public class GLRequester{
    
    OkHttpClient HttpClient;
    MediaType jsonMediaType;
    //the shared url for all calls
    String ApiUrl;
    
    private static GLRequester mySelf;

    public static GLRequester getInstance(String ApiUrl)throws IOException{
	if(mySelf == null)
	    mySelf = new GLRequester(ApiUrl);
	return mySelf;
    }

    public GLRequester(String ApiUrl)throws IOException{
	jsonMediaType = MediaType.parse("application/json; charset=utf-8");
	HttpClient = new OkHttpClient();
	this.ApiUrl = ApiUrl;
    }

    private Response get(String url)throws IOException{
	
	if(Config.getInstance().GitLabToken==null){
	    throw new IOException(Msg.NoGitLabToken.get());
	}
	Request request = new Request.Builder()	    
		.url(url)
		.addHeader("PRIVATE-TOKEN", Config.getInstance().GitLabToken)	    
		.get()
		.build();

	Response res = HttpClient.newCall(request).execute();
	if(!res.isSuccessful()){
	    String error = res.body().string();
	    throw new HttpException(error, res.code(), error, url);
	}
	return res;
    }

    /**
     * List languages for a project
     * @param id the id of the project
     */
    public Response getLanguage(int id)throws IOException{
	
	String url = ApiUrl + "/projects/" + id + "/languages";
	return get(url);
    }
    
    /**
     * Search for projects
     * @returns the found Projects
     */
    public Response foundProjects(String searchTerm)throws IOException{

	String url = ApiUrl + "/search?scope=projects&search=" + searchTerm;
	return get(url);

    }

}
