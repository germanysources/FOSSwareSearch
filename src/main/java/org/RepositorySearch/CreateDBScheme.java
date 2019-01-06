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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * A static class, which creates the db scheme and holds the connection object
 * to the in memory sqlite database.
 */
public class CreateDBScheme{   
    
    /*Hold it static, because only one thread does the modifcation statements in sqlite
      and only this connection object can interact with the in memory database
     */
    private static Connection con;

    public static Connection getConnection()throws SQLException{
	if(con == null || con.isClosed()){
	    con = DriverManager.getConnection("jdbc:sqlite:file::memory:?cache=shared");	
	}
	return con;

    }

    public static void forRepository()throws SQLException{
	
	Connection con = getConnection();	
	Statement stmt = con.createStatement();
	
	//html url is extern visible url (for example https://github.com/kohsuke/github-api
	stmt.executeUpdate("create table Repositories(html_url string, license_key string, license_description string, description string, planguage string, homepage string, star_count integer, forks_count integer, last_activity datetime, created_at datetime, open_issues integer, score integer)");
	stmt.executeUpdate("create unique index rurl on Repositories(html_url)");

	stmt.executeUpdate("create table RepositoryTopics(html_url string, topic string, foreign key(html_url) references Repositories(html_url))");
	stmt.executeUpdate("create unique index turl on RepositoryTopics(html_url, topic)");	
	
	/*content searching html_url is the html url of the repository i.e. https://github.com/kohsuke/github-api, path the relative file path, content_url the file content viewed in github ui*/
	stmt.executeUpdate("create table RepositoryContent(html_url string, path string, content_url string, foreign key(html_url) references Repositories(html_url))");
	stmt.executeUpdate("create unique index curl on RepositoryContent(html_url, path)");

	stmt.close();

    }

    /**
     * Clears the results from all database tables
     */
    public synchronized static void clearAllTables()throws SQLException{
	
	Connection con = getConnection();	
	Statement stmt = con.createStatement();
	
	stmt.executeUpdate("delete from Repositories");
	stmt.executeUpdate("delete from RepositoryTopics");

	stmt.close();
    }

}
