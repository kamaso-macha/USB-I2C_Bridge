/**
 *
 * **********************************************************************
 * PROJECT       : JSerialComm
 * FILENAME      : NonblockinrRead.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/JSerialComm
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


package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

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
 * https://github.com/Fazecast/jSerialComm/wiki/Usage-Examples
 * 
 * <p>
 * @author Stefan
 *
 */

// DOC
// Created at 2026-06-19 16:07:52

public class Evaluation {

	private static Logger logger; // = LoggerFactory.getLogger(Example.class.getName());

	protected SerialPort comPort;
	protected String comPortName;
	
	private byte[] readBuffer;
	private byte[] writeBuffer;
		

	public static void main(String[] args ) {	// NOSONAR
		System.out.println("main(...)");

	    System.setProperty("log4j2.configurationFile","./cfg/log4j2.xml");
	    logger = LoggerFactory.getLogger(Evaluation.class.getName());

		Evaluation me = new Evaluation();
		
		me.init();
		me.initDs1621();
		me.checkDone();
		me.readTemp();
		
		me.close();
			
	} // main()


	private void initDs1621() {
		logger.trace("initDs1621()");
		
		writeBuffer = new byte[] {
				
				(byte) 0x91,			// I²C write command
				(byte) 0x48,			// address
				(byte) 0x02,			// no. of bytes to write
				(byte) 0xAC,			// DS1621 Access Config command
				(byte) 0x02,			// DS1621 output polarity active high, continuous conversion
		};
		
		
		logger.debug("Access Config command ...");

		logger.debug("bytesAvailable before write = {} ", comPort.bytesAvailable());
		
		comPort.writeBytes(writeBuffer, writeBuffer.length);
		checkForErrors();
				
		logger.debug("bytesAvailable after write = {}", comPort.bytesAvailable());	// NOSONAR

		writeBuffer = new byte[] {
				
				(byte) 0x91,			// I²C write command
				(byte) 0x48,			// address
				(byte) 0x01,			// no. of bytes to write
				(byte) 0xEE				// DS1621 Start Convert T command
		};
		
		
		logger.debug("Start Convert T command ...");

		logger.debug("bytesAvailable before write = {}", comPort.bytesAvailable());	// NOSONAR
		
		comPort.writeBytes(writeBuffer, writeBuffer.length);
		checkForErrors();
				
		logger.debug("bytesAvailable after write = {}", comPort.bytesAvailable());

	} // initDs1621()
	
	
	private void checkDone() {
		logger.debug("checkDone()");
		
		writeBuffer = new byte[] {
				
				(byte) 0x9B,			// I²C write and read
				(byte) 0x48,			// address
				(byte) 0x01,			// no. of bytes to write
				(byte) 0x01,			// no. of bytes to read
				(byte) 0xAC,			// DS1621 Access Config command
		};
		
		readBuffer = new byte[2];		// result + error code of the bridge

		int result;
		
		logger.debug("writing ...");
		logger.debug("bytesAvailable before write = {}", comPort.bytesAvailable());
		
		result = comPort.writeBytes(writeBuffer, writeBuffer.length);
		logger.debug(String.format("comPort send write command - res: %d", result));
		checkForErrors();
				
		logger.debug("reading ...");
		result = comPort.readBytes(readBuffer, readBuffer.length);
		logger.debug(String.format("comPort send read command - res: %d", result));
		checkForErrors();

		logger.debug("bytesAvailable after write = {}", comPort.bytesAvailable());

		logger.debug(String.format("DA1621: b0: 0x%02X, b1: 0x%02X", readBuffer[0], readBuffer[1]));

	} // checkDone()


	private void readTemp() {
		logger.debug("readTemp()");
		
		// reads the temperature register of a DS1621 @ 0x48
		
		writeBuffer = new byte[] {
				
				(byte) 0x9B,			// I²C write and read
				(byte) 0x48,			// address
				(byte) 0x01,			// no. of bytes to write
				(byte) 0x02,			// no. of bytes to read
				(byte) 0xAA,			// DS1621 Read Temperature command
		};
		
		
		readBuffer = new byte[3];		// result + error code of the bridge

		int result;

		logger.debug("bytesAvailable before write = {}", comPort.bytesAvailable());
		
		logger.debug("writing ...");
		result = comPort.writeBytes(writeBuffer, writeBuffer.length);
		logger.debug(String.format("comPort send write command - res: %d", result));
		checkForErrors();
				
		logger.debug("reading ...");
		result = comPort.readBytes(readBuffer, readBuffer.length);
		logger.debug(String.format("comPort send read command - res: %d", result));
		checkForErrors();

		logger.debug("bytesAvailable after write = {}", comPort.bytesAvailable());

		int rawTemp =  (byte) readBuffer[1]; // NOSONAR
		float temp = rawTemp;

		if ((readBuffer[2] & 0x80) != 0) {
			temp += 0.5f;
		}
		
		logger.debug(String.format("DA1621: b0: 0x%02X, b1: 0x%02X, b2: 0x%02X - rawTemp: 0x%02X, temp: %f", readBuffer[0], readBuffer[1], readBuffer[2], rawTemp, temp));

	} // readTemp()
	
	
	/**
	 * @param result
	 */
	private void checkForErrors() {
//		logger.debug("checkForErrors(...)");
		
		int errorCode;
		int errorLocation;
		
		errorCode = comPort.getLastErrorCode();
		errorLocation = comPort.getLastErrorLocation();
		
		if(errorCode == 0) {		
			errorLocation = -1;
		} // fi
		
		logger.debug(String.format("erc: %d, erl: %d", errorCode, errorLocation));

	} // checkForErrors(...)
	
	
	/**
	 * 
	 */
	protected void close() {
		logger.debug("close()");
		
		// don't leave footsteps!
		logger.debug("closed? " + comPort.closePort());	// NOSONAR
		checkForErrors();
		
	} // close()
	
	/**
	 * 
	 */
	public Evaluation() {
		logger.debug("Example()");
		
		for (SerialPort p : SerialPort.getCommPorts()) {
		
			
//			System.out.print(p.getSystemPortName());
//		    logger.debug("\t" + p.getDescriptivePortName());
		    
			String portDescriptor = p.getSystemPortName();
			portDescriptor = "\\\\.\\" + portDescriptor.substring(portDescriptor.lastIndexOf('\\')+1);
			
//			logger.debug("Port descriptor: " + portDescriptor);

			comPortName = p.getSystemPortName();
			
			if(p.getDescriptivePortName().startsWith("Pololu Isolated USB-to-I2C Adapter"))
				break;
			
		} // rof
		
	} // Example()
	
	
	private void init() {		
		logger.debug("init()");
		
		// T:\pololu USB 2 I2C\pololu-usb-i2c-adapter-master\firmware\src\main.c
		//
		// When the USB Host sends "Send Break" request with a non-zero
		// duration, reset the serial port state.  This is useful for the
		// user to do after opening the port, so the next byte is guaranteed
		// to be interpreted as a command.

		/*
		 * ChatGPT:
		 
		 	import com.fazecast.jSerialComm.SerialPort;

			SerialPort port = SerialPort.getCommPort("COM5");  // or /dev/ttyACM0 on Linux
			
			port.setComPortParameters(
			    9600,  // arbitrary
			    8,
			    SerialPort.ONE_STOP_BIT,
			    SerialPort.NO_PARITY
			);
			
			port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
			
			port.setComPortTimeouts(
			    SerialPort.TIMEOUT_READ_BLOCKING,
			    1000,
			    1000
			);
			
			if (!port.openPort()) {
			    throw new RuntimeException("Failed to open port");
			}
			
			USB CDC stands for USB Communications Device Class. It's a standard 
			USB device class that allows a USB device to present itself as a 
			communication device, often a virtual serial (COM) port.

			For the Pololu adapter, USB CDC is why Windows shows it as COMx, 
			Linux as /dev/ttyACM0, and macOS as a serial device, even though 
			there is no actual UART hardware involved.

			Why serial settings don't matter

			With a real UART, settings such as:
			
			9600 baud
			115200 baud
			8 data bits
			no parity
			1 stop bit
			
			must match on both ends.
			
			With a USB CDC device like the Pololu adapter:
			
			The host OS still exposes a COM port API.
			jSerialComm still asks for baud rate, parity, etc.
			The Pololu firmware typically ignores those values.
			

			
		 */
		
		// seizure and open com port
		comPort = SerialPort.getCommPort(comPortName);
		
		logger.debug(comPort.getSystemPortName());
	    logger.debug("\t" + comPort.getDescriptivePortName());	// NOSONAR

//		comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
//		comPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 1000);

		if (!comPort.openPort(500)) {
		    throw new RuntimeException("Failed to open port");
		}

		
		// write command byte 'Get device info'
		writeBuffer = new byte[] {(byte) 0xA7};
		comPort.writeBytes(writeBuffer, writeBuffer.length);
		checkForErrors();

		readBuffer = new byte[64];
		
		comPort.readBytes(readBuffer, 1);
		checkForErrors();
		
		comPort.readBytes(readBuffer, readBuffer[0], 1);
		checkForErrors();
		
		String bufferContend = HexUtils.byteArrayToHex(readBuffer);
		logger.info("device info buffer: {}", bufferContend);
		
		// print device info
		logger.debug(String.format(
			"len: 0x%02X, version: 0x%02X, vID: 0x%02X%02X, pID: 0x%02X%02X, FW-ver: %02X.%02X, "
			+ "mod: %02X %02X %02X %02X %02X %02X %02X %02X "
			+ "uid: %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X %02X" 
			, readBuffer[0]
			, readBuffer[1] 
			, readBuffer[3], readBuffer[2] 
			, readBuffer[5], readBuffer[4]
			, readBuffer[7], readBuffer[6]
			, readBuffer[8], readBuffer[9], readBuffer[10], readBuffer[11] ,readBuffer[12], readBuffer[13] ,readBuffer[14], readBuffer[15]  
			, readBuffer[16], readBuffer[17], readBuffer[18], readBuffer[19], readBuffer[20], readBuffer[21], readBuffer[22], readBuffer[23], readBuffer[24], readBuffer[25], readBuffer[26], readBuffer[27]  
		));
		
	} // init()
	
	
} // ssalc
