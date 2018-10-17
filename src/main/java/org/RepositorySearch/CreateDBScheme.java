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
	stmt.executeUpdate("create table Repositories(html_url string, license_key string, license_description string, description string, planguage string, homepage string, star_count integer, forks_count integer, last_activity datetime, created_at datetime, open_issues integer)");

	stmt.executeUpdate("create table RepositoryTopics(html_url string, topic string, foreign key(html_url) references Repositories(html_url))");
	stmt.executeUpdate("create unique index turl on RepositoryTopics(html_url, topic)");	

	stmt.close();

    }

}
