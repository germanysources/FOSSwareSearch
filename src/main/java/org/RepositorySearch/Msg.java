/*******************************************************************************
 * Copyright (c) 2010, 2011 Tran Nam Quang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tran Nam Quang - initial API and implementation
 *    Johannes Gerbershagen - changed Messages for repository search application
 *******************************************************************************/

/* NO WARRANTY
EXCEPT AS EXPRESSLY SET FORTH IN THIS AGREEMENT, THE PROGRAM IS PROVIDED ON AN "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR IMPLIED INCLUDING, WITHOUT LIMITATION, 
ANY WARRANTIES OR CONDITIONS OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. 
Each Recipient is solely responsible for determining the appropriateness of using and distributing the Program 
and assumes all risks associated with its exercise of rights under this Agreement , including but not 
limited to the risks and costs of program errors, compliance with applicable laws, damage to or loss 
of data, programs or equipment, and unavailability or interruption of operations.
*/
package org.RepositorySearch;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;

import org.RepositorySearch.ClassPathHack;
import org.RepositorySearch.annotations.NotNull;

public enum Msg {
    
    initialize_log("Cannot create the directory {0} for the log files."),
    MandatoryFields("The fields html_url and description are mandatory in this select statement.Please supplie both.\n If you use a as clause, please choose html_url and description as the label."),
    foundInCode("Found in the following files:"),
    ERROR("[ERROR] "),
    foundResults("Found {0} repositories. Filter or order them with your sql statement."),
    resultEmpty("No results found"),
    help("Searchs with apis through open source software provider (github, gitlab).\n Usage:\n python runMain.py [Options] search term \n In the search term you can use spaces.\n You cannot use the options (-h,-na,-hd-c) here, because they are always interpreted as options.\n Options:\n -h print this help\n -na In the configuration file you can add your favorite search extensions\n (for example language:java if you only want to search for java repositories.\n With this option this search extensions is disabled.\n -t GitLab Personal Access Token.\n Options github search scope:\n -h in the header data (name,description ...)\n -c in the file contents\n Default Value: -h header data\n Project Homepage https://github.com/germanysources/FOSSwareSearch\n "),
    noSearchTerm("The search term is missing."),
    support("If you need help, consider to open an issue on https://github.com/germanysources/FOSSwareSearch/issues"),
    NoGitLabToken("For searching through gitlab you need a personal access token.\n Supplie it either with the option -t or write it into the config file.")
    ;
    
    private static boolean checkEnabled = true;
    private String value;
    private final String comment;
	
	Msg(@NotNull String defaultValue) {
		this(defaultValue, "");
	}
	
	Msg(@NotNull String defaultValue, @NotNull String comment) {
	    if(defaultValue == null || comment == null){
		throw new IllegalArgumentException();
	    }
	    this.value = defaultValue;
	    this.comment = comment;
	}
	
	@NotNull
	public String get() {
		assert !checkEnabled || !value.contains("{0}");
		return value;
	}
	
	@NotNull
	public String getComment() {
		return comment;
	}
	
	/**
	 * Returns a string created from a <tt>java.text.MessageFormat</tt>
	 * with the given argument(s).
	 */
	public String format(Object... args) {
		String val = value.replace("'", "''"); // See bug #4293229 in Java bug database
		return MessageFormat.format(val, args);
	}
	
	public static void setCheckEnabled(boolean checkEnabled) {
		Msg.checkEnabled = checkEnabled;
	}
	
	public static void loadFromDisk() {
		try {
			final File langDir;						
			langDir = new File("lang");
			ClassPathHack.addFile(langDir);
			
			/*
			 * Notes: (1) The translated strings must be trimmed, because
			 * translators sometimes accidentally add newlines. (2) Replacing
			 * the character \\u00BB with tabs is necessary because those tabs
			 * somehow get replaced with \\u00BB after going through
			 * transifex.com.
			 */
			ResourceBundle bundle = ResourceBundle.getBundle("Resource");
			for (Msg msg : Msg.values())
				if (bundle.containsKey(msg.name()))
					msg.value = bundle.getString(msg.name()).trim().replace('\u00BB', '\t');
		} catch (MissingResourceException e) {
			/*
			 * The English language strings are hard-coded, so there's no
			 * English bundle, which causes this exception.
			 */
		}catch(IOException e){
		    RSLogger.LogExceptionMessage(e, "try to read the language resource");
		}
	}
}	
