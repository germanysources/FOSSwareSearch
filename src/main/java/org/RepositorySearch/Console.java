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

import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Console output of the repositories, after the inmemory database is filled
 */
public class Console extends GUIBase{
    
    //properties for block view
    ArrayList<String[]> properties;
    //results in tabular view
    ArrayList<String[]> results;
    //the minimal column length in the output table
    int ColLength[];
    
    public Console()throws SQLException{
	super();
	properties = new ArrayList<String[]>();
    }

    int colWidth[];

    private native void PrintDelimiter();
    /** @returns true if bash shell color escape codes are supported */
    private native boolean ShellColor();
    /** get the terminal size. If not know, returns -1 */
    private native int getSize();

    @Override
    protected void DisplayRepoHeader(String html_url, String description){
	PrintDelimiter();
	System.out.println();
	System.out.println(html_url);
	System.out.println();
	int csize = getSize();
	if(csize == -1){
	    //shell can display endles strings
	    System.out.println(description);
	}else{
	    //Split the string at end of the line
	    ShowLongString(csize, description);
	}
	
	properties = new ArrayList<String[]>();
	colWidth = new int[2];
    }
    
    //show long string, line break after size no. of chars
    void ShowLongString(int size, String st){
	int off = 0;
	while(off<st.length()){
	    int noff = off+size;
	    if(noff>st.length())
		noff=st.length();
	    System.out.println(st.substring(off, noff));
	    off = noff;
	}
    }

    @Override
    protected void DisplayTopic(String name){	
	if(ShellColor()){
	    //with bash shell color style	
	    System.out.print(CONSTANT.BashColorBlue+"["+name+"] "+CONSTANT.BashColorEnd);	
	}else{
	    System.out.print("["+name+"]");
	}
    }

    //Display the properties in rows
    @Override
    protected void DisplayRepoPropertie(String label, String value){
	
	if(label.length()>colWidth[0]){
	    colWidth[0] = label.length();
	}
	if(value != null && value.length()>colWidth[1]){
	    colWidth[1] = value.length();
	}
	
	String prop[] = {label, value};
	properties.add(prop);

    }

    @Override
    protected void printProperties(){
	System.out.println();
	for(String props[]:properties){
	    StringBuilder label = new StringBuilder(colWidth[0]);
	    label.setLength(colWidth[0]);
	    label.replace(0, props[0].length(), props[0]);
	    for(int j=props[0].length();j<label.length(); j++){
		label.setCharAt(j, ' ');
	    }
	    System.out.print(label.subSequence(0, label.length()) + "\t");
	    System.out.println(props[1]);
	}
    }

    /**
     * print the labels
     */
    @Override
    protected void printTableHead(ResultSet rs, ResultSetMetaData col)throws SQLException{
	results = new ArrayList<String[]>();

	String labels[] = new String[col.getColumnCount()];
	ColLength = new int[col.getColumnCount()];
	for(int j=1;j<=col.getColumnCount();j++){
	    labels[j-1] = col.getColumnLabel(j);
	    ColLength[j-1] = labels[j-1].length();
	}
	results.add(labels);
    }
    
    /**
     * Buffer the results first in an ArrayList to determine later the optimize string length and display it properly
     */
    @Override
    protected void BeginTableRow(){
	results.add(new String[ColLength.length]);
    }

    @Override
    protected void addTableColumn(int i, String value){
	
	if(value == null){
	    return;
	}
	String values[] = results.get(results.size()-1);
	values[i] = value;
	if(value.length() > ColLength[i]){
	    ColLength[i] = value.length();
	}
	results.set(results.size()-1, values);
    }

    @Override
    protected void EndTableRow(){}

    //print now to stdout with the minimal column length
    @Override
    protected void EndTable(){
	
	System.out.println();
	for(String[] values:results){
	    for(int j=0;j<values.length;j++){
		if(values[j] != null){
		    StringBuilder sb = new StringBuilder(ColLength[j]);
		    sb.setLength(ColLength[j]);
		    sb.replace(0, values[j].length(), values[j]);
		    for(int k=values[j].length();k<ColLength[j]; k++)
			sb.setCharAt(k, ' ');

		    System.out.print(sb.subSequence(0, sb.length()) + " ");
		}
	    }
	    System.out.println();
	}

    }

}
