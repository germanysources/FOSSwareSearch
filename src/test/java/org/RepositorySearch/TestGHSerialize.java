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

import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import java.util.*;
import java.sql.SQLException;
import java.io.File;
import java.nio.file.*;

import org.kohsuke.github.GHTopics;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.RepositorySearch.CreateDBScheme;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.core.JsonParseException;

@Ignore
public class TestGHSerialize{

    //class under test
    private SGHRepository ref;
    private ObjectMapper mapper;
    @Mock
    private GHTopics mockTopics;
    
    private static Path testPath;

    @BeforeClass
    public static void csetup()throws SQLException{
	CreateDBScheme.forRepository();
	testPath = FileSystems.getDefault().getPath("src", "test", "resources", "mockRepository.json");
    }     
    
    @Before
    public void setUp() {
	mapper = new ObjectMapper();
    }


    public TestGHSerialize()throws Exception{
        MockitoAnnotations.initMocks(this);
	ref = new SGHRepository(mockTopics);
    }
        
    //test only for exceptions
    @Test
    public void serialize()throws Exception{
	
	//mock topics
	ArrayList<String> myTopics = new ArrayList<String>();
	myTopics.add("search");
	myTopics.add("filter");
	
	when(mockTopics.getAll("RepositorySearch", "foss")).thenReturn(myTopics);
	
	//create Repository directly
	GHRepository repo = GitHub.connect().getRepository("germanysources/mockup_loader");
	
	ref.serialize(repo);
	
    }

}
