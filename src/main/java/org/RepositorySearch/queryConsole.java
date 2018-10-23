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

import java.util.Scanner;
import java.util.regex.MatchResult;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.kohsuke.github.GitHub;
import org.RepositorySearch.serialize.SGHRepository;
import org.aesh.readline.Readline;
import org.aesh.readline.ReadlineBuilder;
import org.aesh.readline.tty.terminal.TerminalConnection;

public class queryConsole{   
    
    GitHub ghaccount;
    GHSearchResults ghres;
    TerminalConnection tconnect;
    
    static{
	System.loadLibrary("ConsoleWidth");
    }
    
    /**
     * @param args can contain the options {-na} and the search term.
     * The search term contains spaces by default. So first the java engine spilt the search terms and we must concatenate it again.
     */
    public static void main(String[] args){

	RSLogger.getInstance();
	Msg.loadFromDisk();	
	try{
	    String searchTerm = null;
	    queryConsole qu = new queryConsole();
	    boolean addFavorite = false;
	//concatenate the search term
	    for(String term:args){
		if(term.equals("-na")){
		    addFavorite = true;
		}else if(term.equals("-h")){
		    System.out.println(Msg.help.get());
		    return;
		}else{
		    //no option it's part of the search term
		    if(searchTerm != null)
			searchTerm = searchTerm + " " + term;
		    else
			searchTerm = term;
		}
	    }
	
	    if(searchTerm == null){
		System.out.println(Msg.ERROR.get()+Msg.noSearchTerm.get());
		return;
	    }
	    qu.SearchRepos(searchTerm, addFavorite);	
	}catch(Exception e){
	    RSLogger.LogException(e);
	    System.out.println(Msg.ERROR.get()+e.getMessage());
	}
	
    }

    /**
     * Setup connection to hosting providers
     */
    public queryConsole()throws SQLException, IOException{
	CreateDBScheme.forRepository();
	ghaccount = GitHub.connectAnonymously();
	ghres = new GHSearchResults(ghaccount);
 	tconnect = new TerminalConnection();
    }

    /**
     * Serialize a single repository at github
     */
    private void SingleRepoGitHub(String fullName)throws IOException, SQLException{
	SGHRepository ser = new SGHRepository(ghaccount);
	ser.serialize(ghaccount.getRepository(fullName));
	Readline readline = ReadlineBuilder.builder().enableHistory(true).build();
	Console con = new Console();
	read(readline, con, false);
	tconnect.openBlocking();
    }

    private void read(Readline readline, Console con, boolean CanFetch){
	
        readline.readline(tconnect, "sql>", input -> {
		if(input != null && input.equals("exit")) {
		    tconnect.close();
		    try{
			CreateDBScheme.getConnection().close();
		    }catch(SQLException e){
			RSLogger.LogExceptionMessage(e, "want to close database connection");
		    }
		}
		else {
		    ExecuteSQLCommand(con, input, CanFetch);
		    read(readline, con, CanFetch);
		}
	    });
    }    

    /**
     * Executes a single sql command
     * @param CanFetch if the method is called after the search, supplie true
     * then the user can fetch more results from the hosting provider
     */
    private void ExecuteSQLCommand(Console con, String command, boolean CanFetch){

	boolean ret = false;
	try{
	    ret = fetchMoreGitHub(command);
	    if(ret)
		return;
	    ret = con.DisplayResultsAsblock(command);
	    if(ret)
		return;
	    con.DisplayResultsAsTable(command);	
	}catch(IllegalArgumentException e){
	    System.out.println(Msg.ERROR.get() + e.getMessage());
	}catch(SQLException e){
	    System.out.println(Msg.ERROR.get() + e.getMessage());
	}catch(IOException e){
	    RSLogger.LogException(e);
	    System.out.println(Msg.ERROR.get() + e.getMessage());
	}
	
    }   

    private void SearchRepos(String term, boolean AddMyFavorites)throws IOException, SQLException{

	int found = 0;
	
	for(Integer type:Config.getInstance().HostingProvider){
	    switch(type.intValue()){
	    case CONSTANT.TypeGitHub:
		found += SearchReposGitHub(term, AddMyFavorites);
		break;
	    case CONSTANT.TypeGitLab:
		//@not implemented yet
		break;
	    }

	    if(found >= Config.getInstance().maxNoResults){
		break;
	    }
	}
	System.out.println(Msg.foundResults.format(found));
	Readline readline = ReadlineBuilder.builder().enableHistory(true).build();
	Console con = new Console();	
	read(readline, con, true);
	tconnect.openBlocking();

    }

    /**
     * Serialize the repositories resulting from an search query
     */
    private int SearchReposGitHub(String term, boolean AddMyFavorites)throws IOException, SQLException{	
	return ghres.query(term, AddMyFavorites);

    }

    /**
     * First the method SearchReposGitHub loads a specific number of results
     * into the inmemory database. This method fetches more results 
     * from github and attaches them to the inmemory database.
     * @param command Syntax is: <fetch X rows from github>
     * X is an integer.
     */
    private boolean fetchMoreGitHub(String command)throws IOException, SQLException{
	
	try{
	    int count = new Integer(regexForFetch(command).group(1)).intValue();
	    int found = ghres.fetch(count);
	    System.out.println(Msg.foundResults.format(found));
	    return true;
	}catch(IllegalStateException e){
	    return false;
	}
	
    }

    //is a regular expression for fetching rows
    MatchResult regexForFetch(String command)throws IllegalStateException{
	
	if(command == null){
	    throw new IllegalStateException("command == null");
	}
	Scanner scan = new Scanner(command);
	scan.findInLine("fetch (\\d+) rows from github");
	return scan.match();
	
    }

}
