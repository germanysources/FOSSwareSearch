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
package org.RepositorySearch.serialize;

import java.util.ArrayList;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

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

	InsertRepo = con.prepareStatement("insert into Repositories(html_url, license_key, license_description, description, planguage, homepage, star_count, forks_count, last_activity, created_at, open_issues, score) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
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
		InsertRepo.setNull(2, Types.VARCHAR);
		InsertRepo.setNull(3, Types.VARCHAR);
	    }
	    InsertRepo.setString(4, repo.getDescription());
	    InsertRepo.setString(5, repo.getLanguage());
	    InsertRepo.setString(6, repo.getHomepage());
	    InsertRepo.setInt(7, repo.getStargazersCount());
	    InsertRepo.setInt(8, repo.getForks());
	    InsertRepo.setDate(9, new java.sql.Date(repo.getUpdatedAt().getTime()));
	    InsertRepo.setDate(10, new java.sql.Date(repo.getCreationDate().getTime()));
	    InsertRepo.setInt(11, repo.getOpenIssueCount());
	    InsertRepo.setFloat(12, new Float(repo.getScore()).intValue());
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
