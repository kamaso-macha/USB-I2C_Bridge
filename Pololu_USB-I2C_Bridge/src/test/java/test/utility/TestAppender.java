/**
 *
 * **********************************************************************
 * PROJECT       : FermentationBox
 * FILENAME      : TestAppender.java
 *
 * PURPOSE       : what is it for?
 *
 * This file is part of the FermentationBox project. More information about
 * this project can be found here:  http://_project_web_page_
 * **********************************************************************
 *
 * Copyright (C)2025 by Stefan Dickel, _project_email_address_
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 *
 */

package test.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * Responsibilities:<br>
 * Log4J2 appender implementation for test purpose.<p>
 * This appender captures and stores logging output - if configured in the log2j config file.
 * The captured logging is obtainable via several interface methods.
 * 
 * <p>
 * Collaborators:<br>
 * 
 * 
 * <p>
 * Description:<br>
 * 
 * 
 * <p>
 * 
 * @author Stefan
 *
 */

// DOC
// Created at 2025-12-01 17:31:18

@Plugin(
	name = "TestAppender", 
	category = Core.CATEGORY_NAME, 
	elementType = Appender.ELEMENT_TYPE
)

public class TestAppender extends AbstractAppender {

	private List<String> logList;
	private double random;
	
	private String logSource = null;
	private Level  logLevel  = Level.ALL;
	
	private boolean isDebug = false;
	
	@SuppressWarnings("deprecation")
	public TestAppender(String name, Filter filter) {
        super(name, filter, null);
        
        logList = Collections.synchronizedList(new ArrayList<>());
        
        random = Math.random();
		
        if(isDebug)
			System.err.println("TestAppender(): " + this.toString());

    } // TestAppender()
	

	@PluginFactory
	public static TestAppender createAppender(
			@PluginAttribute("name") String name,
			@PluginElement("Filter") Filter filter) {
		
		return new TestAppender(name, filter);
		
	} // createAppender()

	
	@Override
	public void append(LogEvent event) {
		
		if(isDebug)
			System.err.println("TestAppender.append() - source" + event.getSource() + ", level: " + event.getLevel() + ", message: " + event.getMessage().getFormattedMessage());
		
		if(event.getLevel().compareTo(logLevel) == 0) {
		
			if(logSource != null) {
				
				if(event.getSource().toString().contains(logSource)) {
					
					logList.add(event.getMessage().getFormattedMessage());
					
					if(isDebug)
						System.err.println("TestAppender.append(): accepted conditional");
					
				} // fi source
				
			}
			else {
				
				logList.add(event.getMessage().getFormattedMessage());
				
				if(isDebug)
					System.err.println("TestAppender.append(): accepted unconditional");				
				
			} // fi != null
			
		} // fi level
		
		
		if(isDebug)
			System.err.println("TestAppender.append(): logList.size() = " + logList.size());
		
	} // append()
	
	
	public void clearList() { logList.clear(); }
	
	public void dumpItems() {
		
		System.err.println("TestAppender.dumpItems()");
		
		for(String e : logList) { System.err.println("e - message: " + e); }

		System.err.println("TestAppender.dumpItems() - done");

	} // dumpItems()
	
	public List<String> getLogItems() { return logList; }
	
	public void setDebug(final boolean aDebugState) { isDebug = aDebugState; }

	public void setLogLevel(final Level aLogLevel) { logLevel = aLogLevel; }
	
	public void setLogSource(final String aLogSource) { logSource = aLogSource; }
	
	@Override
	public String toString() {
		return "TestAppender [logList=" + logList + ", random=" + random + ", toString()=" + super.toString() + "]";
	}
	
	
} // ssalc
