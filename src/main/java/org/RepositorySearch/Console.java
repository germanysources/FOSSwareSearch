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

    private int getConsoleWidth(){
	//@TODO
	return 200;
    }

    @Override
    protected void DisplayRepoHeader(String html_url, String description){
	for(int i=0;i<getConsoleWidth();i++){
	    System.out.print("-");
	}
	System.out.println();
	System.out.println(html_url);
	System.out.println();
	System.out.println(description);
	
	properties = new ArrayList<String[]>();
	colWidth = new int[2];
    }

    @Override
    protected void DisplayTopic(String name){	
	//@TODO color
	System.out.print("["+name+"] ");	
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
