package org.RepositorySearch.serialize;

import java.util.ArrayList;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTopics;
import org.kohsuke.github.GitHub;

import org.RepositorySearch.CreateDBScheme;

/**
 * Serialize a github repository into the database scheme.
 */
public class SGHRepository{
    
    private Connection con;
    private PreparedStatement InsertRepo, InsertTopic;
    private GHTopics readerTopics;

    /**
     * Create the prepared statements for insert operations in the constructor. For productiv use.
     */
    public SGHRepository(GitHub account)throws SQLException{	
	readerTopics = new GHTopics(account);
	con = CreateDBScheme.getConnection();
	prepareStatements();
    }

    /**
     * Create the prepared statements for insert operations in the constructor. For use in unit-test.
     */
    public SGHRepository(GHTopics tops)throws SQLException{
	readerTopics = tops;
	con = CreateDBScheme.getConnection();
	prepareStatements();
    }

    private void prepareStatements()throws SQLException{

	InsertRepo = con.prepareStatement("insert into Repositories(html_url, license_key, license_description, description, planguage, homepage, star_count, forks_count, last_activity, created_at, open_issues) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
	InsertTopic = con.prepareStatement("insert into RepositoryTopics values(?, ?)");
	
    }

    private void closeStatements()throws SQLException{
	
	InsertRepo.close();
	InsertTopic.close();

    }
   
    /**
     * Serialize (means execute the the insert statements for this repository)
     * @throws IOException caused by the github api
     */
    public synchronized void serialize(GHRepository repo)throws SQLException, IOException{
	try{
	    InsertRepo.setString(1, repo.getHtmlUrlString());
	    try{
		InsertRepo.setString(2, repo.getLicenseKey().getKey());
		InsertRepo.setString(3, repo.getLicenseKey().getName());
	    }catch(NullPointerException e){
		//if license is unknown
	    }
	    InsertRepo.setString(4, repo.getDescription());
	    InsertRepo.setString(5, repo.getLanguage());
	    InsertRepo.setString(6, repo.getHomepage());
	    InsertRepo.setInt(7, repo.getStargazersCount());
	    InsertRepo.setInt(8, repo.getForks());
	    InsertRepo.setDate(9, new java.sql.Date(repo.getUpdatedAt().getTime()));
	    InsertRepo.setDate(10, new java.sql.Date(repo.getCreationDate().getTime()));
	    InsertRepo.setInt(11, repo.getOpenIssueCount());
	    InsertRepo.execute();
	    
	    //get the topics
	    String on[] = repo.getFullName().split("/");
	    for(String topic:readerTopics.getAll(on[0], on[1])){
		InsertTopic.setString(1, repo.getHtmlUrlString());
		InsertTopic.setString(2, topic);
		InsertTopic.execute();
	    }
	}catch(SQLException e){
	    //close the statements and open them again, otherwise SQLException would throw
	    //in the next run
	    closeStatements();
	    prepareStatements();

	    throw e;
	}

    }

    

}
