/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : U2iErrorCodesTest.java
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


package lan.sdi.usb2iic.pololu.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lan.sdi.usb2iic.pololu.model.U2I_ERROR_CODES;

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
// Created at 2026-07-17 14:23:55

class U2I_ERROR_CODES_Test {

	private static Logger logger;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
	    System.setProperty("log4j2.configurationFile","./cfg/log4j2.xml");
	    logger = LoggerFactory.getLogger(U2I_ERROR_CODES_Test.class.getName());

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
	 * Test method for {@link lan.sdi.usb2iic.pololu.model.U2I_ERROR_CODES#U2iErrorCodes(int)}.
	 */
	@Test
	void testU2iErrorCodes() {
		logger.info("testU2iErrorCodes()");
		
		assertEquals( 0, U2I_ERROR_CODES.ERROR_NONE.errorCode);
		assertEquals( 1, U2I_ERROR_CODES.ERROR_PROTOCOL.errorCode);
		assertEquals( 2, U2I_ERROR_CODES.ERROR_GAP_2.errorCode);
		assertEquals( 3, U2I_ERROR_CODES.ERROR_TIMEOUT.errorCode);
		assertEquals( 4, U2I_ERROR_CODES.ERROR_ADDRESS_TIMEOUT.errorCode);
		assertEquals( 5, U2I_ERROR_CODES.ERROR_TX_TIMEOUT.errorCode);
		assertEquals( 6, U2I_ERROR_CODES.ERROR_RX_TIMEOUT.errorCode);
		assertEquals( 7, U2I_ERROR_CODES.ERROR_GAP_7.errorCode);
		assertEquals( 8, U2I_ERROR_CODES.ERROR_ADDRESS_NACK.errorCode);
		assertEquals( 9, U2I_ERROR_CODES.ERROR_TX_DATA_NACK.errorCode);
		assertEquals(10, U2I_ERROR_CODES.ERROR_BUS_ERROR.errorCode);
		assertEquals(11, U2I_ERROR_CODES.ERROR_ARBITRATION_LOST.errorCode);
		assertEquals(12, U2I_ERROR_CODES.ERROR_GAP_12.errorCode);
		assertEquals(13, U2I_ERROR_CODES.ERROR_NOT_SUPPORTED.errorCode);
		
	} // testU2iErrorCodes()

	
	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.model.U2I_ERROR_CODES#getName(int)}.
	 */
	@Test
	void testGetName() {
		logger.info("testGetName()");
		
		assertEquals(U2I_ERROR_CODES.ERROR_NONE,				U2I_ERROR_CODES.getName(0));
		assertEquals(U2I_ERROR_CODES.ERROR_PROTOCOL,			U2I_ERROR_CODES.getName(1));
		assertEquals(U2I_ERROR_CODES.ERROR_GAP_2,				U2I_ERROR_CODES.getName(2));
		assertEquals(U2I_ERROR_CODES.ERROR_TIMEOUT,				U2I_ERROR_CODES.getName(3));
		assertEquals(U2I_ERROR_CODES.ERROR_ADDRESS_TIMEOUT,		U2I_ERROR_CODES.getName(4));
		assertEquals(U2I_ERROR_CODES.ERROR_TX_TIMEOUT,			U2I_ERROR_CODES.getName(5));
		assertEquals(U2I_ERROR_CODES.ERROR_RX_TIMEOUT,			U2I_ERROR_CODES.getName(6));
		assertEquals(U2I_ERROR_CODES.ERROR_GAP_7,				U2I_ERROR_CODES.getName(7));
		assertEquals(U2I_ERROR_CODES.ERROR_ADDRESS_NACK,		U2I_ERROR_CODES.getName(8));
		assertEquals(U2I_ERROR_CODES.ERROR_TX_DATA_NACK,		U2I_ERROR_CODES.getName(9));
		assertEquals(U2I_ERROR_CODES.ERROR_BUS_ERROR,			U2I_ERROR_CODES.getName(10));
		assertEquals(U2I_ERROR_CODES.ERROR_ARBITRATION_LOST,	U2I_ERROR_CODES.getName(11));
		assertEquals(U2I_ERROR_CODES.ERROR_GAP_12,				U2I_ERROR_CODES.getName(12));
		assertEquals(U2I_ERROR_CODES.ERROR_NOT_SUPPORTED,		U2I_ERROR_CODES.getName(13));
		
	} // testGetName()

	
} // ssalc
