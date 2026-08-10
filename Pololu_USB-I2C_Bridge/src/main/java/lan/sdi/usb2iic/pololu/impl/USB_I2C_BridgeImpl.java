/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : USB_I2C_BridgeImpl.java
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

import com.fazecast.jSerialComm.SerialPort;

import lan.sdi.usb2iic.core.USB_I2C_Exception;
import lan.sdi.usb2iic.pololu.U2iErrorInfo;
import lan.sdi.usb2iic.pololu.U2iResponse;
import lan.sdi.usb2iic.pololu.USB_I2C_Bridge;
import lan.sdi.usb2iic.pololu.model.GPIO_FMP_MODE;
import lan.sdi.usb2iic.pololu.model.I2C_MODE;
import lan.sdi.usb2iic.pololu.model.U2I_ERROR_CODES;
import lan.sdi.usb2iic.pololu.model.VCC_STATE;
import lan.sdi.utility.HexUtils;


/**
 * <b>Responsibilities:</b><br>
 * <ul>
 * <li>Search for the Pololu USB 2 I2C adaptor in the device list of the host system
 * <li>Initialize the Pololu USB to I2C adaptor
 * <li>Do I2C read / write and administrative operations
 * <li>Provide I2C error information
 * <li>Provide access to the jSerialCom status and error information
 * </ul>
 * 
 * <p>
 * <b>Collaborators:</b><br>
 * jSerialCom.SerialPort
 * 
 * <p>
 * <b>Description:</b><br>
 * USB_I2C_BridgeImpl acts as bridge between two worlds: The USB interface on 
 * a PC and the I2C interface with I2C devices connected to it.<br>
 * It provides easy read, write and write/read access to the connected I2C devices.
 * <p>
 * Some operations return a U2iResponse object which contains the status of 
 * the invoked operation. T least, the field <i>errorCode</i> should be checked
 * to verify that the operation was successful.<br>
 * If the errorCode is not 0, the <i>getLastError()</i> should be invoked to obtain
 * error information of the serial underlying interface.
 * <p>
 * For detailed information, please refer to the vendors documentation.
 * 
 * <a href="https://www.pololu.com/docs/0J89/7">Command reference</a>
 * 
 * <p>
 * @author Stefan
 *
 */

public class USB_I2C_BridgeImpl implements USB_I2C_Bridge {		// NOSONAR
	
	private final Logger logger = LoggerFactory.getLogger(USB_I2C_BridgeImpl.class);
	
	/** command codes as defined in vendors documentation */
	public static final byte WRITE_COMMAND				= (byte) 0x0_91;
	public static final byte READ_COMMAND				= (byte) 0x0_92;
	public static final byte SET_MODE_COMMAND			= (byte) 0x0_94;
	public static final byte SET_TIMEOUT_COMMAND		= (byte) 0x0_97;
	public static final byte CLEAR_BUS_COMMAND			= (byte) 0x0_98;
	public static final byte WRRD_COMMAND				= (byte) 0x0_9B;
	public static final byte SET_STM32_TIMING_COMMAND	= (byte) 0x0_A1;
	public static final byte DIGITAL_READ_COMMAND		= (byte) 0x0_A2;
	public static final byte ENABLE_VCC_OUT_COMMAND		= (byte) 0x0_A4;
	public static final byte GET_DEV_INFO_COMMAND		= (byte) 0x0_A7;
	
	/** size of information elements respective buffers */
	public static final int CMD_STATUS_SIZE				= 1;
	public static final int WR_CMD_HEADER_SIZE			= 3;
	public static final int RD_CMD_HEADER_SIZE			= 3;
	public static final int SET_MODE_CMD_HEADER_SIZE	= 2;
	public static final int WRRD_CMD_HEADER_SIZE		= 4;
	
	/** timing constants */ 
	protected static final int TIME_OUT_MODE	= SerialPort.TIMEOUT_READ_BLOCKING;
	protected static final int RD_TIME_OUT		= 1000;		// milli seconds
	protected static final int WR_TIME_OUT		= 1000;		// milli seconds
	protected static final int BREAK_LENGTH		= 500;		// milli seconds
	
	/** Scan result constants */
	public static final byte PRESENT = (byte) 0x000;
	public static final byte ABSENT  = (byte) 0x0FF;
	public static final byte ILLEGAL = (byte) 0x0FE;

	/**
	 * A reference to the instance of the serial port to be used.
	 */
	protected SerialPort comPort;

	/**
	 * error code of serial port.
	 * Please refer to jSerialCom documentation for more details. 
	 */
	protected int errorCode;

	/**
	 * error location of serial port
	 * Please refer to jSerialCom documentation for more details. 
	 */
	protected int errorLocation;

	
	/**
	 * Constructor.
	 * 
	 * @param aBridgeName The name of the virtual COM port as enlisted in the 
	 * COM port list of the host.
	 * 
	 * @throws USB_I2C_Exception if aBridgeName is null or empty or the desired
	 * device can't be opened.
	 * 
	 */
	public USB_I2C_BridgeImpl(final String aBridgeName) throws USB_I2C_Exception  { 
		logger.trace("Usb_I2C_Bridge() aBridgeName: {}", aBridgeName);

		if(aBridgeName == null || aBridgeName.isBlank()) throw new IllegalArgumentException("aBridgeName can't be null nor blank!");
		
		init(aBridgeName);
		logger.info("{}", getDeviceInfo().toString());	// NOSONAR

	} // Usb_I2C_Bridge()
	
	
	/**
	 * Checks the error code of the serial port.
	 * The fields <i>errorCode</i> and <i>errorLocation</i> are set up with the
	 * current values.
	 */
	private void checkForErrors() {
//		logger.trace("checkForErrors(...)");
		
		errorCode = comPort.getLastErrorCode();
		errorLocation = comPort.getLastErrorLocation();
		
		if(errorCode == 0) {		
			errorLocation = -1;
		}
		else {
			logger.error(String.format("r/w error, errorcode: %d, errorlocation: %d", errorCode, errorLocation));
		}

	} // checkForErrors(...)
	
	
	/**
	 * Sends the <i>clear bus</i> command to recover a hanging I2C bus.
	 */
	@Override
	public void clearBus() {
		logger.trace("clearBus()");
		
		byte[] buffer = new byte[1];
		
		buffer[0] = (byte) CLEAR_BUS_COMMAND;
		
		comPort.writeBytes(buffer, buffer.length);
		checkForErrors();

	} // clearBus()
	
	
	/**
	 * Scan the bus for existing devices and returns the result in a byte array
	 * of 128 bytes.<br>
	 * Each byte represents the scan state of the related address (index 0 == addr 0, 
	 * index 0x4f == addr 0x4F).<br>
	 * The scan state is one of
	 * <ul>
	 * <li> ILLEGAL for reserved addresses if fullScan is NOT requested,
	 * <li> ABSENT for addresses without assignment,
	 * <li> 0x00 for a present device
	 * <li> a value in the range of 1 .. 14 which indicates a present device with error response.<br>
	 *  These response codes can be translated with U2I_ERROR_CODES from numeric to human readable value.
	 * </ul> 
	 * 
	 * @param aFullScan Boolean flag, indicating whether the reserved addresses shall be scanned too.
	 * 
	 * @return a 128 bytes large byte array containing the scan result.
	 */
	@SuppressWarnings("unused")
	public byte[] scanBus(final boolean aFullScan) {
		logger.trace("scanBus(): aFullScan = {}", aFullScan);
		
		int first = aFullScan ? 0x000 : 0x008;
		int last  = aFullScan ? 0x07F : 0x077;
		
		byte[] resultBuffer = new byte[128];
		byte[] rdBuffer = new byte[1];
		
		byte[] wrBuffer = new byte[] {
			WRITE_COMMAND,
			(byte) 0x000,
			(byte) 0x000,
		};
		
		if(!aFullScan) {
			
			for(int i = 0, j = 0x78; i < 8; i++, j++) {
				resultBuffer[i] = (byte) ILLEGAL;
				resultBuffer[j] = (byte) ILLEGAL;
			}
			
		} // fi
		
		for (; first <= last; first++) {
			
//			logger.debug(String.format("first: 0x%02X, resultBuffer size: 0x%02X", first, resultBuffer.length));

			wrBuffer[1] = (byte) first;

			int wrResult = comPort.writeBytes(wrBuffer, wrBuffer.length);
			checkForErrors();

			int rdResult = comPort.readBytes(rdBuffer, rdBuffer.length);
			checkForErrors();
			
			if(rdBuffer[0] == U2I_ERROR_CODES.ERROR_ADDRESS_NACK.errorCode)
				resultBuffer[first] = ABSENT;
			else 
				resultBuffer[first] = rdBuffer[0];

//			logger.debug(String.format("first: 0x%02X, rdRes: 0x%02X, res: 0x%02X", first, rdBuffer[0], resultBuffer[first]));
			
		} // rof
		
		return resultBuffer;
		
	} // scanBus()
	

	/**
	 * Perform a <i>close</i> operation on the underlying serial port.
	 * <p>
	 * This command <b>must</b> be send to the USB to I2C bridge <b>before</b> 
	 * the host application terminates.<br>
	 * Otherwise, the related COM port stays in a undefined state and can't be
	 * re-opened.
	 */
	@Override
	public boolean close() {
		logger.trace("close()");
		
		return comPort.closePort();
		
	} // close()
	
	
	/**
	 * This command retrieves digital readings of the adapter’s internal SCL 
	 * and SDA signals, which can be useful for troubleshooting. 
	 * <p>
	 * Normally the bus will be idle when the adapter executes this command, 
	 * so both readings will be high (1). <br>
	 * If the SDA reading is low (0), the Clear bus command might restore your 
	 * bus to a working state.
	 * 
	 * @throws USB_I2C_Exception If either writing the command byte or reading 
	 * the bus status is impossible.
	 * <p>
	 * Call the getLastError() method to obtain detailed informations about
	 * the error.
	 * 
	 * @return A byte: bit 0 represents SCL, bit 1 represents SDA state.
	 * 
	 * @throws USB_I2C_Exception If either the write command or read data command fails.
	 * 
	 */
	@Override
	public byte digitalRead() throws USB_I2C_Exception {
		logger.trace("digitalRead()");

		byte[] wrBuffer = new byte[1];
		
		wrBuffer[0] = (byte) DIGITAL_READ_COMMAND;
		
		int wrResult = comPort.writeBytes(wrBuffer, wrBuffer.length);
		checkForErrors();

		byte[] rdBuffer = new byte[1];

		int rdResult = comPort.readBytes(rdBuffer, rdBuffer.length);
		checkForErrors();
		
		if(wrResult != 1
		|| rdResult != 1) throw new USB_I2C_Exception("Error while writing or reading the I2C bus.");
		
		return rdBuffer[0];
		
	}// digitalRead()


	/**
	 * <b>Only applicable for <i>Pololu Isolated USB-to-I2C Adapter with Isolated Power</i></b>
	 * <p> 
	 * Switches the power supply of the adaptor on or off.
	 * <p>
	 * <b>Note:</b><br>
	 * The value of the output power (3.3V / 5.0V) is set by an on-board HW switch!
	 * 
	 * @param aVccState Desired power status, eighter VCC_STATE.VCC_OFF or VCC_STATE.VCC_ON.
	 * 
	 * @return A U2iResponse representing the status of this operation.
	 * 
	 * @see lan.sdi.usb2iic.pololu.U2iResponse {@link lan.sdi.usb2iic.pololu.U2iResponse}
	 */
	@Override
	public U2iResponse enableVccOut(final VCC_STATE aVccState) {
		logger.trace("enableVccOut()");

		byte command = ENABLE_VCC_OUT_COMMAND;
		byte[] wrBuffer = new byte[2];
		
		wrBuffer[0] = command;
		wrBuffer[1] = aVccState.value;
		
		int wrResult = comPort.writeBytes(wrBuffer, wrBuffer.length);
		checkForErrors();

		byte[] rdBuffer = new byte[1];

		int rdResult = comPort.readBytes(rdBuffer, rdBuffer.length);
		checkForErrors();
		
		return new U2iResponse(command, rdBuffer[0], wrResult, rdResult);
		
	} // enableVccOut()


	/**
	 * Reads the device info data from the USB to I2C bridge and store them 
	 * in a PololuDeviceInfo object.
	 * 
	 * @return PololuDeviceInfo set up with the current device info data.
	 * 
	 */
	@Override
	public PololuDeviceInfo getDeviceInfo() {
		logger.trace("getDeviceInfo()");
		
		// write command byte 'Get device info'
		byte[] writeBuffer = new byte[] {(byte) 0xA7};
		
		comPort.writeBytes(writeBuffer, writeBuffer.length);
		checkForErrors();

		byte[] readBuffer = new byte[64];
		
		comPort.readBytes(readBuffer, 1);
		checkForErrors();
		
		logger.trace("readBuffer[0]: {}", readBuffer[0]);
		comPort.readBytes(readBuffer, readBuffer[0], 1);
		checkForErrors();
		
		if(logger.isDebugEnabled())
			logger.debug("bytesAvailable after init: {}", comPort.bytesAvailable());
	
		return new PololuDeviceInfo(readBuffer);
		
	} // getDeviceInfo()
	
	
	/**
	 * Returns the error information of recently invoked method.<br>
	 * These error information is related to the last method invoked.
	 * 
	 * @return U2iErrorInfo object filled with the current available error information.
	 * 
	 */
	public U2iErrorInfo getLastError() { return new U2iErrorInfo(errorCode, errorLocation); }
	
	
	/**
	 * Read <i>aBuffer.length</i> bytes from the I2C device with address <i>aAddress</i>.<br>
	 * The data read are placed in the given buffer. Furthermore a U2iResponse
	 * object is set up with the status informations of the command execution.
	 * <p>
	 * The U2iResponse object must be checked to determine whether the operation 
	 * was successful or not.
	 * 
	 * @param aAddress The 7-bit I2C address of the device to read from.
	 * 
	 * @param aBuffer A byte array large enough to hold the data to be read.<br>
	 * 
	 * @return U2iResponse holding the status information of this operation.
	 * 
	 */
	@Override
	public U2iResponse i2cRead(final int aAddress, byte[] aBuffer) {
		logger.trace(String.format("i2cRead(): aAddress: 0x%02X", aAddress));

		return i2cRead(aAddress, aBuffer, 0, aBuffer.length);
		
	} // i2cRead(...)
	
	
	/**
	 * Read <i>aLength</i> bytes from the I2C device with address <i>aAddress</i> and stores 
	 * them in the buffer, placing the first byte at <i>aOffset</i>.<br>
	 * Furthermore a U2iResponse object is set up with the status informations 
	 * of the command execution.
	 * <p>
	 * The U2iResponse object must be checked to determine whether the operation 
	 * was successful or not.
	 * 
	 * @param aAddress The 7-bit I2C address of the device to read from.
	 * 
	 * @param aBuffer A byte array large enough to hold the data to be read.<br>
	 * 
	 * @param aOffset The start offset in the buffer where the bytes read are be placed.
	 * 
	 * @param aLength The number of bytes to be read.
	 * 
	 * @return U2iResponse holding the status information of this operation.
	 * 
	 */
	@Override
	public U2iResponse i2cRead(final int aAddress, byte[] aBuffer, final int aOffset, final int aLength) {
		logger.trace(String.format("i2cRead(): aAddress: 0x%02X, aBuffer.length: %d, aOffset: %d, aLength: %d"
			, aAddress, aBuffer.length
			, aOffset, aLength
		));
		
		byte[] buffer = new byte[3];
		
		buffer[0] = (byte) READ_COMMAND;
		buffer[1] = (byte) aAddress;
		buffer[2] = (byte) aBuffer.length;
		
		int wrResult = comPort.writeBytes(buffer, buffer.length);
		checkForErrors();

		buffer = new byte[aBuffer.length + 1];
		
		int rdResult = comPort.readBytes(buffer, aLength + 1);
		checkForErrors();
		
		System.arraycopy(buffer, 1, aBuffer, aOffset, aLength);
		
		return new U2iResponse((byte) READ_COMMAND, buffer[0], wrResult, rdResult);
		
	} // i2cRead(...)
	
	
	/**
	 * Writes <i>aBuffer.length</i> bytes to the I2C device with address <i>aAddress</i>.<br>
	 * Furthermore a U2iResponse object is created and set up with the status of
	 * the operation execution.
	 * 
	 * @param aAddress The I2C device address to write to
	 * 
	 * @param aBuffer byte array containing the data to write.
	 * 
	 * @return U2iResponse the status of this operation.
	 * 
	 */
	@Override
	public U2iResponse i2cWrite(final int aAddress, byte[] aBuffer) {
		logger.trace(String.format("i2cWrite(): aAddress: 0x%02X", aAddress));

		return i2cWrite(aAddress, aBuffer, 0, aBuffer.length);
		
	} // i2cWrite(...)
	
	
	/**
	 * Writes <i>aLength</i> bytes from <i>aBuffer</i>, starting at location 
	 * <i>aOffset</i> to the I2C device with address <i>aAddress</i>. 
	 * .<br>
	 * Furthermore a U2iResponse object is set up with the status informations 
	 * of the command execution.
	 * <p>
	 * 
	 * @param aAddress The 7-bit I2C address of the device to write to.
	 * 
	 * @param aBuffer A byte array holding the data to be written.<br>
	 * 
	 * @param aOffset The start offset in the buffer.
	 * 
	 * @param aLength The number of bytes to be written.
	 * 
	 * @return U2iResponse holding the status information of this operation.
	 * 
	 */
	@Override
	public U2iResponse i2cWrite(final int aAddress, final byte[] aBuffer, final int aOffset, final int aLength) {
		logger.trace(String.format("i2cWrite(): aAddress: 0x%02X, aBuffer.length: %d, aOffset: %d, aLength: %d"
			, aAddress, aLength, aOffset, aLength));
		
		byte[] buffer = new byte[3 + aLength];
		
		buffer[0] = (byte) WRITE_COMMAND;
		buffer[1] = (byte) aAddress;
		buffer[2] = (byte) aLength;
		
		System.arraycopy(aBuffer, aOffset, buffer, 3, aLength);
		
		if(logger.isDebugEnabled())
			logger.debug("buffer for write: {}", HexUtils.byteArrayToHex(buffer));	
		
		int wrResult = comPort.writeBytes(buffer, buffer.length);
		checkForErrors();
		
		buffer = new byte[1];
		int rdResult = comPort.readBytes(buffer, 1);
		checkForErrors();
		
		if(logger.isDebugEnabled())
			logger.debug("buffer of read: {}", HexUtils.byteArrayToHex(buffer));	
				
		return new U2iResponse((byte) WRITE_COMMAND, buffer[0], wrResult, rdResult);
		
	} // i2cWrite(...)
	

	/**
	 * Performs a w/r operation on the I2C bus with a repeated start condition.
	 * <p>
	 * First, <i>aWriteBuffer.length</i> bytes are read from <i>aWriteBuffer</i> 
	 * and written to the I2C device with the address <i>aAddress</i>.<br>
	 * Then, a read operation is performed to read <i>aReadBuffer.length</i> 
	 * bytes from the addresses device. The bytes read are placed in <i>aReadBuffer</i>.
	 * 
	 * @param aAddress The 7-bit I2C address of the device to write to.

	 * @param aWriteBuffer A byte array holding the data to be written.<br>
	 * 
	 * @param aReadBuffer A byte array large enough to hold the data to be read.<br>
	 * 
	 */
	@Override
	public U2iResponse i2cWriteRead(final int aAddress, final byte[] aWriteBuffer, final byte[] aReadBuffer) {
		logger.trace(String.format("i2cWriteRead(): aAddress: 0x%02X", aAddress));
		
		return i2cWriteRead(aAddress, aWriteBuffer, 0, aWriteBuffer.length, aReadBuffer, 0, aReadBuffer.length);
		
	} // i2cWriteRead(...)
	
	
	/**
	 * Performs a w/r operation on the I2C bus with a repeated start condition.
	 * <p>
	 * First, <i>aWriteLength</i> bytes are read from <i>aWriteBuffer</i>, 
	 * starting at <i>aWriteOffset</i> and written to the I2C device with the 
	 * address <i>aAddress</i>.<br>
	 * Then, a read operation is performed to read <i>aReadLength</i> bytes from 
	 * the addresses device. The bytes read are placed in <i>aReadBuffer</i> 
	 * starting at <i>aReadOffset</i>.
	 * 
	 * @param aAddress The 7-bit I2C address of the device to write to.

	 * @param aWriteBuffer A byte array holding the data to be written.<br>
	 * 
	 * @param aWriteOffset The location where the first byte is read from the buffer.
	 * 
	 * @param aWriteLength Number of bytes to read from buffer and write to the 
	 * addressed device.
	 * 
	 * @param aReadBuffer A byte array large enough to hold the data to be read.<br>
	 * 
	 * @param aReadOffset Location, where the first byte read is placed in the buffer.
	 * 
	 *  @param aReadLength Number of bytes to read from addressed device.
	 * 
	 */
	@Override
	public U2iResponse i2cWriteRead(final int aAddress, 
			final byte[] aWriteBuffer, final int aWriteOffset, final int aWriteLength,
			final byte[] aReadBuffer,  final int aReadOffset,  final int aReadLength) {
		
		logger.trace(String.format("i2cWriteRead(): aAddress: 0x%02X, aWriteBuffer.length: %d, aWriteOffset: %d aWriteLength: %d, aReadBuffer.length: %d, aReadOffset: %d aReadLength: %d", 
				aAddress, aWriteBuffer.length, aWriteOffset, aWriteLength, aReadBuffer.length, aReadOffset, aReadLength));
		
		if(logger.isDebugEnabled())
			logger.debug("bytesAvailable before i2cWriteRead = "+ comPort.bytesAvailable());		// NOSONAR

		byte[] buffer = new byte[4 + aWriteLength];
		
		buffer[0] = (byte) WRRD_COMMAND;
		buffer[1] = (byte) aAddress;
		buffer[2] = (byte) aWriteLength;		// bytes to write
		buffer[3] = (byte) aReadLength;		// bytesa to read
		
		System.arraycopy(aWriteBuffer, aWriteOffset, buffer, 4, aWriteLength);
		
		if(logger.isDebugEnabled())
			logger.debug("buffer for write: {}", HexUtils.byteArrayToHex(buffer));	
		
		int wrResult = comPort.writeBytes(buffer, buffer.length);
		checkForErrors();
		
		buffer = new byte[aReadLength + 1];
		
		int rdResult = comPort.readBytes(buffer, aReadLength + 1);
		checkForErrors();
		
		if(logger.isDebugEnabled())
			logger.debug("buffer of read: {}", HexUtils.byteArrayToHex(buffer));	
		
		System.arraycopy(buffer, 1, aReadBuffer, aReadOffset, aReadLength);
		
		if(logger.isDebugEnabled())
			logger.debug("aReadBuffer: {}", HexUtils.byteArrayToHex(aReadBuffer));	
		
		if(logger.isDebugEnabled())
			logger.debug("bytesAvailable after i2cWriteRead = "+ comPort.bytesAvailable());		// NOSONAR

		return new U2iResponse((byte) WRRD_COMMAND, buffer[0], wrResult, rdResult);

	} // i2cWriteRead(...)
	

	/**
	 * Initializes the USB to I2C bridge.
	 * <p>
	 * It searches for a COM port with the name <i>aBridgeName</i> in the serial
	 * port enumeration of the host and, if it is found, initializes and opens it
	 * for operation.
	 * 
	 *  @param aBridgeName The name of the device to be searched for.<br>
	 *  Usually, the Pololu USB to I2C adaptor is registered as <i>COM9</i>.<br>
	 *  If in doubt, use the tools from OS of the host to figure out the correct name.
	 */
	private void init(final String aBridgeName) throws USB_I2C_Exception {		
		logger.trace("init()");
		
		// seek for com port name
		
		SerialPort[] portList = SerialPort.getCommPorts();

		logger.debug("Searching for comPort named {} in a list of {} available ports", aBridgeName, portList.length);

		for (SerialPort p : portList) {
			
			if(logger.isDebugEnabled()) {
				logger.debug("found system port name: {}, descriptive port name: {}", p.getSystemPortName(), p.getDescriptivePortName());
			}
		    
			if(p.getSystemPortName().equals(aBridgeName)) {
				comPort = p;
				break;
			}
			
		} // rof
		
		logger.info("using system port name: {}, descriptive port name: {}", comPort.getSystemPortName(), comPort.getDescriptivePortName());
		
		// NO need to initialize serial interface
		
		comPort.setComPortTimeouts(TIME_OUT_MODE, RD_TIME_OUT, WR_TIME_OUT);

		if (!comPort.openPort(500)) {
		    throw new USB_I2C_Exception("Failed to open port " + aBridgeName);
		}

	} // init()
	
	
	
	/**
	 * Queries and returns the jSerial com port state. 
	 * 
	 * @return The current state of the com port
	 * 
	 */
	
	public boolean isOpen() { return comPort.isOpen(); }


	/**
	 * If an communication error has occurred on the underlying serial port,
	 * which prevents further operation, a reset of the serial port might be 
	 * helpful.
	 * <p>
	 * With this command, a <i>Break condition</i> is send on the serial interface,
	 * causing the adaptor to reset and reinitialize it's serial port. 
	 */
	@Override
	public void resetSerialPort() {
		logger.trace("resetSerialPort()");
		
		// T:\pololu USB 2 I2C\pololu-usb-i2c-adapter-master\firmware\src\main.c
		//
		// When the USB Host sends "Send Break" request with a non-zero
		// duration, reset the serial port state.  This is useful for the
		// user to do after opening the port, so the next byte is guaranteed
		// to be interpreted as a command.

        if (comPort.isOpen()) {

            comPort.setBreak();
            
            try { Thread.sleep(BREAK_LENGTH); } 
            catch (InterruptedException e) {
            	logger.error("Arrrrrrrrgh! Who darrrrres to disturrrrrrrrrrrb my sleep?", e);
            }
            finally {
            	
                comPort.clearBreak();

            }// yrt
            
        }
        else {
        	
        	logger.error("Invalid attempt to reset a closed comPort!");

        } // fi
		
	} // resetSerialPort()
	
	
	/**
	 * Set the operation mode for the I2C interface.
	 * <p>
	 * Valid modes are (as defined in I2C_MODE):
	 * <ul>
	 * <li>I2C_MODE.STANDARD
	 * <li>I2C_MODE.FAST
	 * <li>I2C_MODE.FAST_PLUS
	 * <li>I2C_MODE.SLOW_10_KHZ (Pololu defined slow mode).
	 * </ul>
	 * Detailed information about the I2C bus modes can be obtained from
	 * <a href="https://www.nxp.com/docs/en/user-guide/UM10204.pdf">I²C-bus specification and user manual</a>
	 * <p>
	 * @param aMode The desired operation mode.<br>
	 * <b>Please read the data sheets of the connected devices along with the I2C bus specification to choose the correct bus operation mode!</b>
	 */
	@Override
	public U2iResponse setI2cMode(final I2C_MODE aMode) {
		logger.trace("setI2cMode(): aMode: {}", aMode);
		
		byte[] buffer = new byte[2];
		
		buffer[0] = (byte) SET_MODE_COMMAND;
		buffer[1] = (byte) aMode.mode;
		
		int wrResult = comPort.writeBytes(buffer, buffer.length);
		checkForErrors();

		buffer = new byte[1];
		
		int rdResult = comPort.readBytes(buffer, 1);
		checkForErrors();
		
		return new U2iResponse((byte) SET_MODE_COMMAND, buffer[0], wrResult, rdResult);
		
	} // setI2cMode()
	

	/**
	 * This command sets the maximum time allowed for I²C write and I²C read 
	 * commands. 
	 * <p>
	 * If the total time taken by I²C communication in one of those 
	 * commands takes longer than the specified time, the adapter aborts the 
	 * command and returns the timeout error code (3).
	 * 
	 * @param aTimeOut A number of milliseconds from 1 to 65535.<br>
	 * The default I²C timeout is 50 ms.
	 * 
	 */
	@Override
	public U2iResponse setI2cTimeout(final int aTimeOut) {
		logger.trace("setI2cTimeout(): aTimeOut: {}", aTimeOut);
		
		byte[] buffer = new byte[3];
		
		buffer[0] = (byte) SET_TIMEOUT_COMMAND;
		buffer[1] = (byte) (aTimeOut & 0x00FF);
		buffer[2] = (byte) ((aTimeOut >> 8) & 0x00FF);
		
		int wrResult = comPort.writeBytes(buffer, buffer.length);
		checkForErrors();

		buffer = new byte[1];
		
		int rdResult = comPort.readBytes(buffer, 1);
		checkForErrors();
		
		return new U2iResponse((byte) SET_TIMEOUT_COMMAND, buffer[0], wrResult, rdResult);
		
	} // setI2cTimeout()
	

	/**
	 * This command provides a different way to set the I2C mode and allows a 
	 * fine control over the I2C clock speed.
	 * 
	 * @param aTiminggr A 32-bit value for the STM32’s TIMINGR register.
	 * 
	 * @param aGpioFmpMode One of the values defined in GPOI_FMP_MODE:
	 * <ul>
	 * <li>GPOI_FMP_MODE.FAST_MODE_DISABLE
	 * <li>GPOI_FMP_MODE.FAST_MODE_ENABLE
	 * </ul>
	 * <p>
	 * <b>Note:</b><br>
	 * Pleas read the Pololu documentation carefully before using this command!
	 */
	@Override
	public void setStm32Timing(final long aTiminggr, final GPIO_FMP_MODE aGpioFmpMode) {
		logger.trace("setStm32Timing(): aTiminggr: {}, aGpioFmpMode: {}", aTiminggr, aGpioFmpMode);
		
		byte[] buffer = new byte[6];
		
		buffer[0] = (byte) SET_STM32_TIMING_COMMAND;
		
		buffer[1] = (byte) (aTiminggr & 0xFF);
		buffer[2] = (byte) ((aTiminggr >> 8) & 0xFF);
		buffer[3] = (byte) ((aTiminggr >> 16) & 0xFF);
		buffer[4] = (byte) ((aTiminggr >> 24) & 0xFF);
		
		buffer[5] = aGpioFmpMode.value;
		
		comPort.writeBytes(buffer, buffer.length);
		checkForErrors();

	} // setStm32Timing()


} // ssalc

/************************** Memento mori! **************************/
