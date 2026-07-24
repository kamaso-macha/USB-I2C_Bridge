/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : U2iResponseTest.java
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


package lan.sdi.usb2iic.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lan.sdi.usb2iic.pololu.U2iErrorInfo;
import lan.sdi.usb2iic.pololu.U2iResponse;
import lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl;


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
// Created at 2026-07-17 13:23:54

class U2iResponseTest {

	private static Logger logger;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		System.err.println(System.getProperty("user.dir"));
		
	    System.setProperty("log4j2.configurationFile","./test-cfg/log4j2.xml");
	    logger = LoggerFactory.getLogger(U2iResponseTest.class.getName());

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
	 * Test method for {@link lan.sdi.usb2iic.pololu.U2iResponse#U2iResponse(byte, int, int, int)}.
	 */
	@Test
	void testU2iResponse() {
		logger.info("testU2iResponse()");
		
		final byte COMMAND_CODE = (byte) 0x11;
		final int ERROR_CODE	= 0x22;
		final int BYTES_WRITTEN	= 0x33;
		final int BYTES_READ	= 0x44;
		
		U2iResponse cut = new U2iResponse(COMMAND_CODE, ERROR_CODE, BYTES_WRITTEN, BYTES_READ);
		
		assertEquals(COMMAND_CODE, cut.commandCode);
		assertEquals(ERROR_CODE, cut.errorCode);
		assertEquals(BYTES_WRITTEN, cut.bytesWritten);
		assertEquals(BYTES_READ, cut.bytesRead);
		
	} // testU2iResponse()

	
	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.U2iResponse#equals(java.lang.Object)}.
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	void testEqualsObject() {
		logger.info("testEqualsObject()");

		int NO_ERROR = (byte) 0x00;
		int ERROR	 = (byte) 0x88;

		U2iResponse cut				= new U2iResponse(USB_I2C_BridgeImpl.READ_COMMAND, NO_ERROR, 5, 8);

		U2iResponse copy = cut;

		U2iResponse commandCode		= new U2iResponse(USB_I2C_BridgeImpl.WRITE_COMMAND, NO_ERROR, 5, 8);
		U2iResponse errorCode		= new U2iResponse(USB_I2C_BridgeImpl.READ_COMMAND, ERROR,    5, 8);
		U2iResponse bytesWritten	= new U2iResponse(USB_I2C_BridgeImpl.READ_COMMAND, NO_ERROR, 1, 8);
		U2iResponse bytesRead		= new U2iResponse(USB_I2C_BridgeImpl.READ_COMMAND, NO_ERROR, 5, 1);
		
		assertTrue(cut.equals(copy));
		
		assertFalse(cut.equals(null));
		assertFalse(cut.equals(new U2iErrorInfo(0, 0)));
		
		assertFalse(cut.equals(commandCode));
		assertFalse(cut.equals(errorCode));
		assertFalse(cut.equals(bytesWritten));
		assertFalse(cut.equals(bytesRead));
		
	} // testEqualsObject()


	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.U2iResponse#toString()}.
	 */
	@Test
	void testToString() {
		logger.info("testToString()");

		int NO_ERROR = (byte) 0x00;

		U2iResponse cut = new U2iResponse(USB_I2C_BridgeImpl.READ_COMMAND, NO_ERROR, 5, 8);
		
		String result = cut.toString();
		logger.info("result: {}", result);
		
		assertEquals("U2iResponse [commandCode=0x92, errorCode=0x00, bytesWritten=5, bytesRead=8]", result);
		
	} // testToString()


} // ssalc
