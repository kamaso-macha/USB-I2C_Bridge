/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : U2iErrorInfo.java
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


package lan.sdi.usb2iic.pololu;


/**
 * Responsibilities:<br>
 * Store the jSerialComm error information of a invoked command.
 * 
 * <p>
 * Collaborators:<br>
 * jSerialComm SerialPort
 * 
 * <p>
 * Description:<br>
 * This type is used to hold the error code and error location information
 * provided by the jSerialComm framework. It is obtained via the 
 * USB_I2C_Bridge.getLastError() method and is meant to be analyzed by the 
 * user after reception to verify that the invoked operation was successful.
 *
 * <p>
 * @author Stefan
 *
 */

public class U2iErrorInfo {

	public final int errorCode;
	public final int errorLocation;
	
	public U2iErrorInfo(final int aErrorCode, final int aErrorLocation) {
		
		errorCode = aErrorCode;
		errorLocation = aErrorLocation;
		
	} // U2iErrorInfo(..)
	
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("U2iErrorInfo [");
		builder.append("errorCode=");
		builder.append(String.format("0x%02X", errorCode));
		builder.append(", errorLocation=");
		builder.append(String.format("%d", errorLocation));
		builder.append("]");
		
		return builder.toString();
	
	} // toString()
	

} // salc

/************************** Memento mori! **************************/
