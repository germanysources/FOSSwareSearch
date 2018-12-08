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
import java.io.IOException;
import java.sql.SQLException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHSearchBuilder;
import org.kohsuke.github.PagedSearchIterable;
import org.kohsuke.github.PagedIterator;

import org.RepositorySearch.serialize.SGHRepository;

/**
 * Serialize the search results from github into the inmemory database
 */
public class GHSearchResults{
    protected GitHub account;
    protected SGHRepository serializer; 
    protected PagedIterator itRepos, itContent;
    protected Config conf;
    protected ArrayList<Integer> scope;
    
    /**
     * @param scope to search (Repositories means description, name, language ...) and/or content
     */
    public GHSearchResults(GitHub account, ArrayList<Integer> scope)throws IOException, SQLException{
	this.account = account;
	serializer = new SGHRepository(account);
	conf = Config.getInstance();
	this.scope = scope;
    }

    /**
     * Query for the specified string and put the results in the database.
     * The fetch limit (number of results) is read from the configuration parameter maxNoResults.
     * @param term The search term. Syntax see {@link https://help.github.com/articles/searching-for-repositories/} 
     * @param AddMyFavorites Favorite addition to the search can be added (for example programming language ...)
     * @returns the number of found entries
     */
    public int query(String term, boolean AddMyFavorites)throws IOException, SQLException{
	
	String sterm;
	if(AddMyFavorites && conf.FavoriteAdditions != null)
	    sterm = term + " " + conf.FavoriteAdditions;
	else
	    sterm = term;
	
	if(scope.contains(new Integer(CONSTANT.ScopeRepos))){
	    PagedSearchIterable sit = account.searchRepositories().q(sterm).list();
	    itRepos = sit._iterator(conf.maxNoResults);
	}
	if(scope.contains(new Integer(CONSTANT.ScopeContent))){
	    PagedSearchIterable sit = account.searchContent().q(sterm).list();
	    itContent = sit._iterator(conf.maxNoResults);
	}
	return fetch(conf.maxNoResults);

    }
    
    /**
     * This methods fetches the results and attach them to the inmemory database
     * @param count max. numbers of results to fetch
     * @returns found numbers of results
     */
    public int fetch(int count)throws IOException, SQLException{
	
	int size = 0;
	if(scope.contains(new Integer(CONSTANT.ScopeRepos))){
	    while(size < count && itRepos.hasNext()){
	    
		serializer.serialize((GHRepository)itRepos.next());
		size++;
	    }
	}
	if(scope.contains(new Integer(CONSTANT.ScopeContent))){
	    while(size < count && itContent.hasNext()){
		serializer.serialize((GHContent)itContent.next());
	    }
	}
	return size;
	

    }

}
