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

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.RepositorySearch.CreateDBScheme;

public class TestGHSerialize{

    //class under test
    private SGHRepository cut;

    @BeforeClass
    public static void csetup()throws SQLException{
	CreateDBScheme.forRepository();
    }

    @Before
    public void setup()throws Exception{
	cut = new SGHRepository();
    }

    @Test
    public void serialize()throws Exception{
	
	GHRepository repo = GitHub.connect().getRepository("germanysources/FOSSWareSearch");
	
	cut.serialize(repo);

	Connection con = CreateDBScheme.getConnection();
        ResultSet resultSet = con.createStatement().executeQuery("select html_url from Repositories");
        resultSet.first();
        assertTrue(resultSet.isLast());
        assertEquals("https://github.com/germanysources/FOSSWareSearch", resultSet.getString(1));
    }

}
