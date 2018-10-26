package org.RepositorySearch;

import java.util.regex.MatchResult;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.BeforeClass;

public class TestConsole{
   
    static queryConsole qu;

    @BeforeClass
    public static void setup()throws Exception{
	qu = new queryConsole();
    }

    /**
     * The regular expression for fetching more rows.
     */
    @Test
    public void regexFetchRows()throws Exception{
	
	//don't match
	String dont = "fetch abc from github";
	try{
	    MatchResult mr = qu.regexForFetch(dont);
	    Assert.fail(dont+" souldn't match");
	}catch(IllegalStateException e){}

	String does = "fetch 3 rows from github";
	MatchResult mr = qu.regexForFetch(does);
	Assert.assertTrue(does + " should match", mr.groupCount()>0);
	Assert.assertEquals("row count", new Integer(3), new Integer(mr.group(1)));

    }

    @Test
    public void StartNewSearch()throws Exception{

	//different command
	Assert.assertFalse("different command", qu.StartNewSearch("foss"));
	//only new search without the query term
	Assert.assertFalse("no search term supplied", qu.StartNewSearch("new search"));
	
	//new search with query term
	Assert.assertTrue("should start a new search", qu.StartNewSearch("new search foss search"));
	Assert.assertEquals("search term", "foss search", queryConsole.searchTerm);

    }

}
