/**
 *
 * **********************************************************************
 * PROJECT       : Pololu_USB-I2C_Bridge
 * FILENAME      : I2cBusScan.java
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


package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lan.sdi.usb2iic.core.USB_I2C_Exception;
import lan.sdi.usb2iic.pololu.USB_I2C_Bridge;
import lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl;
import lan.sdi.utility.HexUtils;

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
// Created at 2026-08-10 13:11:47

public class I2cBusScan {

	private final Logger logger = LoggerFactory.getLogger(I2cBusScan.class.getName());

	
	protected void me() {
		
		try {
			
			USB_I2C_Bridge usb2iic = new USB_I2C_BridgeImpl("COM9");
			
			byte[] scanResult = usb2iic.scanBus(false);
			
			for(int n = 0; n < scanResult.length; n++)
				if(scanResult[n] == (byte) 0x000) scanResult[n] = (byte) n;
			
			byte[] dump = new byte[16];
			String hexDump = "";
			
			System.out.println("    0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F");
			
			for(int n = 0; n < scanResult.length; n += 16) {

				System.arraycopy(scanResult, n, dump, 0, 16);
				
				hexDump = HexUtils.byteArrayToHex(dump);
				hexDump = hexDump.replaceAll("FE", "  ").replaceAll("FF", "--");
			
				System.out.println(String.format("%02X  %s", n, hexDump));
				
			}

		} catch (USB_I2C_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} // me()
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		I2cBusScan run = new I2cBusScan();
		
		run.me();

	} // main

} // ssalc

/************************** Memento mori! **************************/