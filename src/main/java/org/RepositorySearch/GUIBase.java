package org.RepositorySearch;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.DateFormat;

/**
 * Abstract base class for viewing the result.
 * First step is to fill the inmemory database.
 * In this class we can run sql queries on the inmemory database
 */
public abstract class GUIBase{
    
    public static final String ViewBlock = "view block";
    
    private PreparedStatement readTopics;
    
    public GUIBase()throws SQLException{
	readTopics = CreateDBScheme.getConnection().prepareStatement("select topic from RepositoryTopics where html_url = ?");
    }

    /**
     * Display the results of the query in the local memory database.
     * The sql query is the following
     * select (fields) from Repositories where (clause) view block.
     * View Block looks like this:
     * url
     * Description
     * topics
     * properties in a table
     * The syntax view block is not a standard sql syntax.
     * Subqueries can be attached in the where clause to filter in topics
     * table
     * In the fields the columns html_url and description (with this labels) from the table Repositories must be supplied!
     * @returns true if results should be displayed in the block view, false otherwise
     * @throws IllegalArgumentExceptionException: if the fields html_url, description are not supplied or the package java.sql throws the SQLException
     */
    public boolean DisplayResultsAsblock(String rssqlquery)throws SQLException{
	
	int i = rssqlquery.indexOf(ViewBlock);
	if(i == -1){
	    return false;
	}
	
	//this is the sqlquery in the standard sql syntax
	StringBuffer sqlquery = new StringBuffer();
	sqlquery.append(rssqlquery.subSequence(0, i));	
	try{
	    sqlquery.append(rssqlquery.subSequence(i+ViewBlock.length(), rssqlquery.length()));
	}catch(IndexOutOfBoundsException e){}
		
	Statement stmt = CreateDBScheme.getConnection().createStatement();
	
	ResultSet rs = stmt.executeQuery(sqlquery.toString());
	ResultSetMetaData columns = rs.getMetaData();

	try{
	    while(rs.next()){
		
		DisplayRepoHeader(rs.getString("html_url"), rs.getString("description"));
		getTopics(rs.getString("html_url"));
		
		for(int j=1;j<=columns.getColumnCount();j++){
		    if(!columns.getColumnLabel(j).equals("html_url") 
		       && !columns.getColumnLabel(j).equals("description")){
			//return from the timestamp only the date
			String value;
			if(columns.getColumnType(j) == Types.DATE){
			    DateFormat df = DateFormat.getDateInstance();
			    value = df.format(rs.getDate(j));
			}else{
			    value = rs.getString(j);
			}
			DisplayRepoPropertie(columns.getColumnLabel(j), 
					     value);
		    }
		}
		printProperties();
	    }
	    
	}catch(SQLException e){
	    throw new IllegalArgumentException(Msg.MandatoryFields.get());
	}
	return true;
    
    }

    /**
     * Display the results in a tabular view. 
     * @param sqlquery a query in the standard sql syntax of sqlite
     */
    public void DisplayResultsAsTable(String sqlquery)throws SQLException{

	Statement stmt = CreateDBScheme.getConnection().createStatement();
	
	ResultSet rs = stmt.executeQuery(sqlquery.toString());
	ResultSetMetaData columns = rs.getMetaData();
	
	printTableHead(rs, columns);
	while(rs.next()){
	    BeginTableRow();

	    for(int j=1;j<=columns.getColumnCount();j++){
		String value;		
		if(columns.getColumnType(j) == Types.DATE){
			    DateFormat df = DateFormat.getDateInstance();
			    value = df.format(rs.getDate(j));
		}else{
		    value = rs.getString(j);
		}
		addTableColumn(j-1, value);
	    }
	    EndTableRow();
	}
	EndTable();

    }

    private void getTopics(String html_url)throws SQLException{
	
	readTopics.setString(1, html_url);
	ResultSet rs = readTopics.executeQuery();
	while(rs.next()){
	    DisplayTopic(rs.getString(1));
	}

    }
        
    protected abstract void DisplayRepoHeader(String html_url, String description);

    //Display a single topic
    protected abstract void DisplayTopic(String name);

    protected abstract void DisplayRepoPropertie(String label, String value);

    //print at the end of the repository
    protected abstract void printProperties();
    
    //methods for tabular view

    protected abstract void printTableHead(ResultSet rs, ResultSetMetaData col)throws SQLException;

    protected abstract void BeginTableRow();

    protected abstract void addTableColumn(int i, String value);

    protected abstract void EndTableRow();

    protected abstract void EndTable();

}
