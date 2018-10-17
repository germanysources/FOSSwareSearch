package org.RepositorySearch;

import java.util.Scanner;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.kohsuke.github.GitHub;
import org.RepositorySearch.serialize.SGHRepository;

public class queryConsole{   
    
    GitHub ghaccount;
    

    public static void main(String[] args)throws Exception{
	String searchTerm = "";
	queryConsole qu = new queryConsole();
	//concatenate the search term
	for(String term:args){
	    searchTerm = searchTerm + " " + term;
	}
	qu.SearchRepos(searchTerm, false);
    }

    /**
     * Setup connection to hosting providers
     */
    public queryConsole()throws SQLException, IOException{
	CreateDBScheme.forRepository();
	ghaccount = GitHub.connectAnonymously();	
    }

    /**
     * Serialize a single repository at github
     */
    private void SingleRepoGitHub(String fullName)throws IOException, SQLException{
	SGHRepository ser = new SGHRepository(ghaccount);
	ser.serialize(ghaccount.getRepository(fullName));
	DialogSQLCommands();
    }

    private void DialogSQLCommands()throws SQLException{
	//display input field for sql queries
	Console con = new Console();
	while(1==1){
	    System.out.print(">");
	    //wait for user command 
	    String command = new Scanner(System.in).useDelimiter(";").next();
	    boolean ret = false;
	    try{
		ret = con.DisplayResultsAsblock(command);
		if(ret)
		    return;
		con.DisplayResultsAsTable(command);
	    }catch(IllegalArgumentException e){
		System.out.println(Msg.ERROR.get() + e.getMessage());
	    }catch(SQLException e){
		System.out.println(Msg.ERROR.get() + e.getMessage());
	    }
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
	DialogSQLCommands();


    }

    /**
     * Serialize the repositories resulting from an search query
     */
    private int SearchReposGitHub(String term, boolean AddMyFavorites)throws IOException, SQLException{
	GHSearchResults res = new GHSearchResults(ghaccount);
	return res.query(term, AddMyFavorites);
	

    }

}
