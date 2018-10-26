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

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import org.RepositorySearch.GLProject;
import org.RepositorySearch.CreateDBScheme;

/**
 * Serialize a gitlab Repository
 */
public class SGLRepository{

    private Connection con;
    private PreparedStatement InsertRepo;

    public SGLRepository()throws SQLException{
	
	con = CreateDBScheme.getConnection();
	prepareStatements();
    }


    private void prepareStatements()throws SQLException{

	InsertRepo = con.prepareStatement("insert into Repositories(html_url, description, homepage, planguage, star_count, forks_count, last_activity, created_at, score) values(?, ?, ?, ?, ?, ?, ?, ?, ?) ");

    }

        /**
     * Serialize (means execute the the insert statements for this repository)
     */
    public synchronized void serialize(GLProject repo)throws SQLException{

	try{
	    InsertRepo.setString(1, repo.http_url_to_repo);
	    InsertRepo.setString(2, repo.description);
	    InsertRepo.setString(3, repo.web_url);
	    InsertRepo.setString(4, repo.getMainLanguage());
	    InsertRepo.setInt(5, repo.star_count);
	    InsertRepo.setInt(6, repo.forks_count);
	    try{
		InsertRepo.setDate(7, repo.getUpdatedAt());
	    }catch(NullPointerException e){
		InsertRepo.setNull(7, Types.DATE);
	    }
	    try{
		InsertRepo.setDate(8, repo.getCreatedAt());
	    }catch(NullPointerException e){
		InsertRepo.setNull(8, Types.DATE);
	    }
	    InsertRepo.setInt(9, repo.score);

	    InsertRepo.execute();

	}catch(SQLException e){
	    //close the statements and open them again, otherwise SQLException would throw
	    //in the next run
	    InsertRepo.close();
	    prepareStatements();
	    throw e;
	}

    }

}
