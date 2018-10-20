package org.RepositorySearch;

import java.util.regex.MatchResult;
import java.sql.SQLException;
import org.junit.Test;
import org.junit.Assert;

public class TestConsole{

    /**
     * The regular expression for fetching more rows.
     */
    @Test
    public void regexFetchRows()throws Exception{
	
	queryConsole qu = new queryConsole();
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

}
