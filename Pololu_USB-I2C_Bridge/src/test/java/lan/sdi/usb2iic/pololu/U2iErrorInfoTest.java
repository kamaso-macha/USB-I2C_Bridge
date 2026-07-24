/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : U2iErrorInfoTest.java
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


package lan.sdi.usb2iic.pololu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lan.sdi.usb2iic.pololu.U2iErrorInfo;

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
// Created at 2026-07-17 14:11:36

class U2iErrorInfoTest {
	
	private static Logger logger = LoggerFactory.getLogger(U2iErrorInfoTest.class.getName());

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
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
	 * Test method for {@link lan.sdi.usb2iic.pololu.U2iErrorInfo#U2iErrorInfo(int, int)}.
	 */
	@Test
	void testU2iErrorInfo() {
		logger.info("testU2iErrorInfo()");
		
		final int ERROR_CODE		= 88;
		final int ERROR_LOCATION	= 123;
		
		U2iErrorInfo cut = new U2iErrorInfo(ERROR_CODE, ERROR_LOCATION);
		
		assertEquals(ERROR_CODE, cut.errorCode);
		assertEquals(ERROR_LOCATION, cut.errorLocation);
		
	} // testU2iErrorInfo()


	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.U2iErrorInfo#toString()}.
	 */
	@Test
	void testToString() {
		logger.info("testToString()");
		
		final int ERROR_CODE		= 0x088;
		final int ERROR_LOCATION	= 123;
		
		U2iErrorInfo cut = new U2iErrorInfo(ERROR_CODE, ERROR_LOCATION);
		
		String result = cut.toString();
		logger.info("result: {}", result);
		
		assertEquals("U2iErrorInfo [errorCode=0x88, errorLocation=123]", result);

	} // testToString()

	
} // ssalc
