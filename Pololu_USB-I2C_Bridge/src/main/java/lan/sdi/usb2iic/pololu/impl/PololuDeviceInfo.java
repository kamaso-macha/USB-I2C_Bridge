/**
 *
 * **********************************************************************
 * PROJECT       : Pololu_USB-I2C_Bridge
 * FILENAME      : PololuDeviceInfo.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/USB-I2C_Bridge
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lan.sdi.utility.HexUtils;

/**
 * Responsibilities:<br>
 * DAO to hold Pololu device info.
 * 
 * <p>
 * Collaborators:<br>
 * None
 * 
 * <p>
 * Description:<br>
 * Parses the byte buffer given in the constructor into its parts and
 * set up the information elements inside. 
 * 
 * Refer to <a href="https://www.pololu.com/docs/0J89/7">Command reference of the vendor documentation</a> for more details.
 * 
 * Sample device info data:
 * 
 *   device info buffer: 
 *        0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
 *   00  1C 00 FB 1F 02 25 01 01 2D 00 00 00 00 00 00 00 
 *   10  52 00 76 00 01 50 36 42 48 37 30 20 -- -- -- --
 *   
 *   Ofs	Len		Md			Meaning		  Value
 *   00		 1		byte		len			: 0x1C
 *   01		 1		byte		version		: 0x00
 *   02		 2		LE int		vID			: 0x1FFB
 *   04		 2		LE int		pID			: 0x2502
 *   06		 2		LE BCD		FW-ver		: 01.01
 *   08		 8		byte[]		mod			: 2D 00 00 00 00 00 00 00 
 *   16		12		byte[]		uid			: 52 00 76 00 01 50 36 42 48 37 30 20
 *
 * <p>
 * @author Stefan
 *
 */

public class PololuDeviceInfo {

	private final Logger logger = LoggerFactory.getLogger(PololuDeviceInfo.class.getName());
		
	public final int size;
	public final int versionNbr;
	public final int usbVendorId;
	public final int usbProductId;
	public final int firmwareVersionMajor;
	public final int firmwareVersionMinor;
	public final String specialModifications;
	public final byte[] uniqueDeviceId;
	public final String deviceName;
	

	/**
	 * Constructor.
	 * 
	 * @param aRawDevInfo A byte array containing the raw device info data as
	 * returned by getDeviceInfo command.
	 *  
	 */
	public PololuDeviceInfo(final byte[] aRawDevInfo) {
		logger.trace("PololuDeviceInfo(): aRawDevInfo.length: {}", aRawDevInfo.length);
		
		size = aRawDevInfo[0];
		versionNbr = aRawDevInfo[1]; 
		
		usbVendorId		= getLeIntFrom2Bytes(aRawDevInfo, 2);
		usbProductId	= getLeIntFrom2Bytes(aRawDevInfo, 4);
		
		firmwareVersionMajor = convertPackedByte(aRawDevInfo[7]);
		firmwareVersionMinor = convertPackedByte(aRawDevInfo[6]);
		
		byte[] buffer = new byte[8];
		System.arraycopy(aRawDevInfo, 8, buffer, 0, 8);
		specialModifications = (new String(buffer)).trim();

		buffer = new byte[12];
		System.arraycopy(aRawDevInfo, 16, buffer, 0, 12);
		uniqueDeviceId = buffer;
		
		switch(usbProductId) {
		
		case 0x0_2502:	deviceName = "Pololu Isolated USB-to-I2C Adapter"; break;
		case 0x0_2503:	deviceName = "Pololu Isolated USB-to-I2C Adapter with Isolated Power"; break;
		
		default:		deviceName = "Unknown device";
		
		} // hctiws
		
	} // PololuDeviceInfo(...)
	
	
	/**
	 * Converts two bytes in a buffer into a int using LITTLE END convention.
	 * 
	 * @param aByteBuffer The buffer which holds the data to be converted.
	 * 
	 * @param aOffset A pointer into the array, indicating the LSB of the value to be converted
	 * 
	 * @return An integer representation of the two bytes of the buffer.
	 * 
	 */
	protected static int getLeIntFrom2Bytes(final byte[] aByteBuffer, final int aOffset) {
		
		return (((aByteBuffer[aOffset + 1] << 8) + (aByteBuffer[aOffset] & 0x0FF)) & 0x0_FFFF);
		
	} // getLeIntFrom2Bytes()
	
	
	/**
	 * Converts a packed BCD number into an integer value.
	 * 
	 * @param aBcdByte The packed BCD value to be converted.
	 * 
	 * @return the integer representation of the BCD number.
	 */
	protected static int convertPackedByte(final byte aBcdByte) {
	    
		int upperNibble = (aBcdByte >> 4) & 0x0F; 
	    int lowerNibble = aBcdByte & 0x0F;    
	    
	    if (upperNibble > 9 || lowerNibble > 9) {
	        throw new IllegalArgumentException(
	          String.format("Invalid BCD format: byte 0x%02X contains non-decimal digit.", aBcdByte)
	        );
	    }
	    
	    return upperNibble * 10 + lowerNibble;
	    
	} // convertPackedByte()
	
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("PololuDeviceInfo [");
		sb.append(String.format("size: 0x%02X / %d, ", size, size));
		sb.append(String.format("version: %d, ", versionNbr));
		sb.append(String.format("USB vendor ID: 0x%04X, ", usbVendorId));
		sb.append(String.format("USB product ID: 0x%04X, ", usbProductId));
		sb.append(String.format("FW version: %d.%d, ", firmwareVersionMajor, firmwareVersionMinor ));
		sb.append(String.format("modifications: %s, ", specialModifications));
		sb.append(String.format("uid: %s", HexUtils.byteArrayToHex(uniqueDeviceId)));
		sb.append("]");
		
		return sb.toString();
		
	} // toString()
	

} // ssalc

/************************** Memento mori! **************************/
