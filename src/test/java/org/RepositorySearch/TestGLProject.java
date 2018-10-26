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
import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.junit.Ignore;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Calendar;

public class TestGLProject{
    
    //class under test
    GLProject ref;

    @Before
    public void setup(){
	ref = new GLProject();
	ref.created_at = "2018-10-20T20:00:00.000Z";
	ref.languages = new HashMap();
	ref.languages.put("C", new Double(60.0));
	ref.languages.put("D", new Double(40.0));
    }

    @Test
    public void getLanguage(){
	//language determination

	Assert.assertEquals("Language not correct", "C", ref.getMainLanguage());

    }

    @Test
    public void getUpdatedAt(){
	//date updated_at is correct
	
	GregorianCalendar ActCal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
	ActCal.clear();
	ActCal.setTime(ref.getCreatedAt());
	GregorianCalendar ExpCal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
	ExpCal.clear();
	ExpCal.set(2018, Calendar.OCTOBER, 20, 20, 00);
	
	//System.out.println(ActCal.getTime().toString() + "\t" + ExpCal.getTime().toString());
	Assert.assertEquals("Date is not correct", ExpCal.getTime(), ref.getCreatedAt());
    }
    
}
