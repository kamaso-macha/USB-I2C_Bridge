/**
 *
 * **********************************************************************
 * PROJECT       : Pololu_USB-I2C_Bridge
 * FILENAME      : PololuDeviceInfoTest.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/Pololu_USB-I2C_Bridge
 *
 * **********************************************************************
 *
 * Copyright (C)2026 by Stefan Dickel
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


package lan.sdi.usb2iic.pololu.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

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
 *
 */

// DOC
// Created at 2026-07-22 13:35:37

class PololuDeviceInfoTest {

	private final Logger logger = LoggerFactory.getLogger(PololuDeviceInfoTest.class.getName());

	/*
	 * Sample device info data for Pololu Isolated USB-to-I2C Adapter
	 * 
	 *   device info buffer: 
	 *        0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
	 *   00  1C 00 FB 1F 02 25 01 01 2D 00 00 00 00 00 00 00 
	 *   10  52 00 76 00 01 50 36 42 48 37 30 20 -- -- -- --
	 *   
	 *   
	 *   len			: 0x1C
	 *   version		: 0x00
	 *   vID			: 0x1FFB
	 *   pID			: 0x2502
	 *   FW-ver			: 01.01
	 *   mod			: 2D 00 00 00 00 00 00 00 
	 *   uid			: 52 00 76 00 01 50 36 42 48 37 30 20
	 *   
	 */  

	private static byte[] rawDeviceInfo_ndef = new byte[] {
		(byte) 0x0_1C, (byte) 0x0_00, (byte) 0x0_FB, (byte) 0x0_1F, (byte) 0x0_0A, (byte) 0x0_42, (byte) 0x0_02, (byte) 0x0_01, 
		(byte) 0x0_2D, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, 
		(byte) 0x0_52, (byte) 0x0_00, (byte) 0x0_76, (byte) 0x0_00, (byte) 0x0_01, (byte) 0x0_50, (byte) 0x0_36, (byte) 0x0_42, 
		(byte) 0x0_48, (byte) 0x0_37, (byte) 0x0_30, (byte) 0x0_20, 
	};
	

	private static byte[] rawDeviceInfo_5396 = new byte[] {
			(byte) 0x0_1C, (byte) 0x0_00, (byte) 0x0_FB, (byte) 0x0_1F, (byte) 0x0_02, (byte) 0x0_25, (byte) 0x0_02, (byte) 0x0_01, 
			(byte) 0x0_2D, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, 
			(byte) 0x0_52, (byte) 0x0_00, (byte) 0x0_76, (byte) 0x0_00, (byte) 0x0_01, (byte) 0x0_50, (byte) 0x0_36, (byte) 0x0_42, 
			(byte) 0x0_48, (byte) 0x0_37, (byte) 0x0_30, (byte) 0x0_20, 
		};
		

	private static byte[] rawDeviceInfo_5397 = new byte[] {
			(byte) 0x0_1C, (byte) 0x0_00, (byte) 0x0_FB, (byte) 0x0_1F, (byte) 0x0_03, (byte) 0x0_25, (byte) 0x0_02, (byte) 0x0_01, 
			(byte) 0x0_2D, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, 
			(byte) 0x0_52, (byte) 0x0_00, (byte) 0x0_76, (byte) 0x0_00, (byte) 0x0_01, (byte) 0x0_50, (byte) 0x0_36, (byte) 0x0_42, 
			(byte) 0x0_48, (byte) 0x0_37, (byte) 0x0_30, (byte) 0x0_20, 
		};
		

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
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.PololuDeviceInfo#PololuDeviceInfo(byte[])}.
	 */
	@Test
	void testPololuDeviceInfo() {
		logger.info("testPololuDeviceInfo()");
		
		byte[] uniqueDeviceId = new byte[] {
				(byte) 0x0_52, (byte) 0x0_00, (byte) 0x0_76, (byte) 0x0_00, (byte) 0x0_01, (byte) 0x0_50, (byte) 0x0_36, (byte) 0x0_42, 
				(byte) 0x0_48, (byte) 0x0_37, (byte) 0x0_30, (byte) 0x0_20, 
			};
		
		PololuDeviceInfo cut = new PololuDeviceInfo(rawDeviceInfo_5396);		
		assertNotNull(cut);
		
		assertEquals(0x0_1C,  cut.size);
		assertEquals(0x0_00, cut.versionNbr);
		assertEquals(0x0_1FFB, cut.usbVendorId);
		assertEquals(0x0_2502, cut.usbProductId);
		assertEquals(01, cut.firmwareVersionMajor);
		assertEquals(02, cut.firmwareVersionMinor);
		assertEquals("-", cut.specialModifications);
		assertEquals(0, Arrays.compare(uniqueDeviceId, cut.uniqueDeviceId));
		assertEquals("Pololu Isolated USB-to-I2C Adapter", cut.deviceName);
		
		
		cut = new PololuDeviceInfo(rawDeviceInfo_5397);
		assertNotNull(cut);

		assertEquals(0x0_2503, cut.usbProductId);
		assertEquals("Pololu Isolated USB-to-I2C Adapter with Isolated Power", cut.deviceName);

		
		cut = new PololuDeviceInfo(rawDeviceInfo_ndef);
		assertNotNull(cut);

		assertEquals(0x0_420A, cut.usbProductId);
		assertEquals("Unknown device", cut.deviceName);

	} // testPololuDeviceInfo()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.PololuDeviceInfo#getLeIntFrom2Bytes(byte[], int)}.
	 */
	@Test
	void testGetLeIntFrom2Bytes() {
		logger.info("testGetLeIntFrom2Bytes()");

		byte[] buffer = new byte[] { (byte) 0x0_42, (byte) 0x0_88, (byte) 0x0_69, (byte) 0x0_A5, (byte) 0x0_5A };
		
		int result_0 = 0x0_88_42;
		int result_1 = 0x0_69_88;
		int result_2 = 0x0_A5_69;
		int result_3 = 0x0_5A_A5;
		
		assertEquals(result_0, PololuDeviceInfo.getLeIntFrom2Bytes(buffer, 0));
		assertEquals(result_1, PololuDeviceInfo.getLeIntFrom2Bytes(buffer, 1));
		assertEquals(result_2, PololuDeviceInfo.getLeIntFrom2Bytes(buffer, 2));
		assertEquals(result_3, PololuDeviceInfo.getLeIntFrom2Bytes(buffer, 3));

	} // testGetLeIntFrom2Bytes()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.PololuDeviceInfo#convertPackedByte(byte)}.
	 */
	@Test
	void testConvertPackedByte() {
		logger.info("testConvertPackedByte()");

		assertEquals(00, PololuDeviceInfo.convertPackedByte((byte) 0x000));
		assertEquals(42, PololuDeviceInfo.convertPackedByte((byte) 0x042));
		assertEquals(99, PololuDeviceInfo.convertPackedByte((byte) 0x099));
		
		IllegalArgumentException thrown;
		thrown = assertThrows(IllegalArgumentException.class, () -> PololuDeviceInfo.convertPackedByte((byte) 0x0AB));
		assertEquals("Invalid BCD format: byte 0xAB contains non-decimal digit.", thrown.getMessage());
		
	} // testConvertPackedByte()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.PololuDeviceInfo#toString()}.
	 */
	@Test
	void testToString() {
		logger.info("testToString()");

		String reference = 
				  "PololuDeviceInfo ["
				+ "size: 0x1C / 28, "
				+ "version: 0, "
				+ "USB vendor ID: 0x1FFB, "
				+ "USB product ID: 0x2502, "
				+ "FW version: 1.2, "
				+ "modifications: -, "
				+ "uid: 52 00 76 00 01 50 36 42 48 37 30 20"
				+ "]"
				;

		PololuDeviceInfo cut = new PololuDeviceInfo(rawDeviceInfo_5396);		
		assertNotNull(cut);
			
		logger.info("toString(): {}", cut.toString());

		assertEquals(reference, cut.toString());
		
	} // testToString()
	

} // ssalc


/************************** Memento mori! **************************/