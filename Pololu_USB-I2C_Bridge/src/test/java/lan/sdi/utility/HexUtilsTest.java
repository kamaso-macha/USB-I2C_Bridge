/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : HexUtilsTest.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/Pololu USB - I2C bridge
 *
 * **********************************************************************
 *
 * Copyright (C)2025 by Stefan Dickel
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


package lan.sdi.utility;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsibilities:<br>
 * 
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
 * @author Stefan
 *
 */

// DOC
// Created at 2026-07-17 13:17:31

class HexUtilsTest {

	private static Logger logger;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	
		System.setProperty("log4j2.configurationFile","./cfg/log4j2.xml");
	    logger = LoggerFactory.getLogger(HexUtilsTest.class.getName());

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link lan.sdi.utility.HexUtils#byteArrayToHex(byte[])}.
	 */
	@Test
	void testByteArrayToHex() {
		logger.info("testByteArrayToHex()");
		
		byte[] data = new byte[] { 
				(byte) 0x042, (byte) 0x033, (byte) 0x05A, (byte) 0x066, 
				(byte) 0x088, (byte) 0x088, (byte) 0x0A5, (byte) 0x0FF
			};
		
		String result = HexUtils.byteArrayToHex(data);

		logger.info("result: {}", result);
		
		assertEquals("42 33 5A 66 88 88 A5 FF", result);

	} // testByteArrayToHex()

} // ssalc
