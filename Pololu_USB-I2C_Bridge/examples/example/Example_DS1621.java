/**
 *
 * **********************************************************************
 * PROJECT       : Pololu_USB-I2C_Bridge
 * FILENAME      : Example_DS1621.java
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
import lan.sdi.usb2iic.pololu.U2iResponse;
import lan.sdi.usb2iic.pololu.USB_I2C_Bridge;
import lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl;
import lan.sdi.usb2iic.pololu.model.I2C_MODE;
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
 *
 */

// DOC
// Created at 2026-07-26 12:00:46

public class Example_DS1621 {

	private final Logger logger = LoggerFactory.getLogger(Example_DS1621.class.getName());
	
	private static Example_DS1621 me;
	private USB_I2C_Bridge bridge;
	private final int i2cAddress;
	
	
	/**
	 * 
	 */
	public Example_DS1621(final int aAddress) {
		logger.info(String.format("Example_DS1621(), aAddress = 0x%02X", aAddress));
		
		i2cAddress = aAddress;
		
	} // Example_DS1621()
	
	
	private void run(final String aComPort) {
		logger.info("Example_DS1621.run(), aComPort = {}", aComPort);
		
		try {
			
			// Create an instance of the USB to I2C bridge.
			bridge = new USB_I2C_BridgeImpl(aComPort);
			
			setI2cBusMode(bridge);
			
			initDS1621();
			
			checkStatus();
			
			readTemperature();
			
		} catch (USB_I2C_Exception e) {
			
			e.printStackTrace();
			
		} finally {
			
			bridge.close();
			
		} // yrt
		
	} // run()


	/**
	 * 
	 */
	private boolean checkStatus() {
		
		byte[] wrBuffer = new byte[] { 
				(byte) 0x0AC,			// 'Access config'
		};
		
		byte[] rdBuffer = new byte[1];		// Status byte of DS1621.
		
		U2iResponse response = bridge.i2cWriteRead(i2cAddress, wrBuffer, rdBuffer);
		
		String successMessage = String.format("Status: 0x%02X", rdBuffer[0]);

		// the expected result of this operation.
		U2iResponse expectation = new U2iResponse(
				USB_I2C_BridgeImpl.WRRD_COMMAND, 
				U2I_ERROR_CODES.ERROR_NONE.errorCode, 
				USB_I2C_BridgeImpl.WRRD_CMD_HEADER_SIZE + wrBuffer.length, 
				USB_I2C_BridgeImpl.CMD_STATUS_SIZE + rdBuffer.length);
		
		checkResponse(response, expectation, successMessage);
		
		return ((byte) (rdBuffer[0] & 0x080) == (byte) 0x080);
		
	} // checkStatus()


	/**
	 * 
	 */
	private void readTemperature() {
		
		byte[] wrBuffer = new byte[] { 
				(byte) 0x0AA,				// 'Read temperature'
		};
		
		byte[] rdBuffer = new byte[2];		// we get two bytes from DS1621.
		
		U2iResponse response = bridge.i2cWriteRead(i2cAddress, wrBuffer, rdBuffer);
		
		float temp = (byte) rdBuffer[0];
		if(rdBuffer[1] != 0x00) 
			temp += 0.5;
		
		String successMessage = String.format("Temperature: 0x%02X 0x%02X / %f", rdBuffer[0], rdBuffer[1], temp);

		// the expected result of this operation.
		U2iResponse expectation = new U2iResponse(
				USB_I2C_BridgeImpl.WRRD_COMMAND, 
				U2I_ERROR_CODES.ERROR_NONE.errorCode, 
				USB_I2C_BridgeImpl.WRRD_CMD_HEADER_SIZE + wrBuffer.length, 
				USB_I2C_BridgeImpl.CMD_STATUS_SIZE + rdBuffer.length);
		
		checkResponse(response, expectation, successMessage);
		
	} // readTemperature()


	/**
	 * 
	 */
	protected void initDS1621() {
		
		byte[] wrBuffer = new byte[] { 
				(byte) 0x0AC,			// 'Access config'
				(byte) 0x002,			// POL = act. H, 1SHOT = continuously 
				(byte) 0x0EE			// 'Start convert T'
		};
		
		U2iResponse response = bridge.i2cWrite(i2cAddress, wrBuffer);
		
		String successMessage = String.format("DS1621 is initialized now.");

		// the expected result of this operation.
		U2iResponse expectation = new U2iResponse(
				USB_I2C_BridgeImpl.WRITE_COMMAND, 
				U2I_ERROR_CODES.ERROR_NONE.errorCode, 
				USB_I2C_BridgeImpl.WR_CMD_HEADER_SIZE + wrBuffer.length, 
				USB_I2C_BridgeImpl.CMD_STATUS_SIZE);
		
		checkResponse(response, expectation, successMessage);
		
	} // initDS1621()


	/**
	 * Set the desired I2C mode and check the result for successful execution.
	 * 
	 * @param bridge The currently instantiated USB_I2C_BridgeImpl object to work on.
	 * 
	 */
	protected void setI2cBusMode(USB_I2C_Bridge bridge) {
		
		U2iResponse response = bridge.setI2cMode(I2C_MODE.STANDARD);
		
		String successMessage = String.format("I2C bus mode is set to %s", I2C_MODE.STANDARD);

		// the expected result of this operation.
		U2iResponse expectation = new U2iResponse(
				USB_I2C_BridgeImpl.SET_MODE_COMMAND, 
				U2I_ERROR_CODES.ERROR_NONE.errorCode, 
				USB_I2C_BridgeImpl.SET_MODE_CMD_HEADER_SIZE, 
				USB_I2C_BridgeImpl.CMD_STATUS_SIZE);
		
		checkResponse(response, expectation, successMessage);
		
	} // setI2cBusMode()


	/**
	 * @param response
	 * @param successMessage
	 */
	protected void checkResponse(U2iResponse response, U2iResponse expectation, String successMessage) {
		
		if(response.commandCode == expectation.commandCode) {
			if(response.errorCode == expectation.errorCode) {
				if(response.bytesWritten == expectation.bytesWritten) {
					if(response.bytesRead == expectation.bytesRead) {
						logger.info(successMessage);
					}
					else {logger.error("Too few bytes read on SET_MODE_COMMAND: {}.", response.bytesRead);
					} // fi 
				}
				else {
					logger.error("Too few bytes written on SET_MODE_COMMAND: {}.", response.bytesWritten);
				} // fi bytesWritten
			}
			else {
				logger.error("Got errorcode {}.", response.errorCode);
			} // fi errorCode
		}
		else {
			logger.error("Wrong response received!");
		} // fi commandCode
	} // run()
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length != 2) {
			System.err.println(
"Usage: \n" + 
"Example_DS1621 COM-port address\n" + 
"  COM-port:  The COM port which is assocciated to the connected USB to I2C bridge (usually COM9)." + 
"  address :  The bus address of a attached DS1621 temperature sensor."
			);
			
		} // fi
		
		
		int address = Integer.parseInt((args[1]).replaceFirst("0[xX]", ""), 16);

		me = new Example_DS1621(address);
		
		me.run(args[0]);

	} // main()

} // ssalc

/************************** Memento mori! **************************/