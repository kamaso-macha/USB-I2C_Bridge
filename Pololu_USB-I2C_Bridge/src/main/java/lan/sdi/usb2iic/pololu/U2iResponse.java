/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : U2iResponse.java
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
 * Store the execution status of a invoked command. 
 * 
 * <p>
 * Collaborators:<br>
 * None.
 * 
 * <p>
 * Description:<br>
 * This type is initialized at the end of a command execution and carries the
 * status of the operation. It's meant to be analyzed by the user after reception
 * to verify that the invoked operation was successful.
 * 
 * <p>
 * @author Stefan
 *
 */

public final class U2iResponse {

	public final byte commandCode; 
	public final int errorCode;
	public final int bytesWritten;
	public final int bytesRead;
	
	
	/**
	 * DAO to transport the result of an operation to the caller.
	 * 
	 * Please consult <a href="https://www.pololu.com/docs/0J89/7">Command reference</a> 
	 * and <a href="https://www.pololu.com/docs/0J89/8">Error codes</a> for detailed information. 
	 * 
	 * @param aCommand The ID of the command which was executed as the U2iResponse was created.
	 * 
	 * @param aErrorCode The Pololu I2C error code. See <a href="https://www.pololu.com/docs/0J89/8">Error codes</a> for more details.
	 *  
	 * @param aBytesWritten The number of bytes written by the issued command. <br>
	 * This number should be 1 for every non write command. For write commands, it must be nbrOfBytesToWrite + 1.
	 *  
	 * @param aBytesRead The number of bytes read. <br>
	 * This number should be 1 for every non read command. For read commands, it must be nbrOfBytesToRead + 1.<br>
	 * <b>Note: </b><br>
	 * Some commands do NOT respond with a error information! For those kind, the value is zero.
	 * 
	 */
	public U2iResponse(final byte aCommand, final int aErrorCode, final int aBytesWritten, final int aBytesRead) {
		
		commandCode		= aCommand;
		errorCode		= aErrorCode;
		bytesWritten	= aBytesWritten;
		bytesRead		= aBytesRead;
		
	} // Response()
		
	
	@Override
	public boolean equals(final Object o) {		// NOSONAR
		
		if(this == o) return true;
		if(o == null) return false;
		
		if(!(o instanceof U2iResponse)) return false;
		
		final U2iResponse other = (U2iResponse) o;
		
		if(this.commandCode  == other.commandCode		// NOSONAR
		&& this.errorCode    == other.errorCode
		&& this.bytesWritten == other.bytesWritten
		&& this.bytesRead    == other.bytesRead)
			return true;
		
		return false;
		
	} // equals()


	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("U2iResponse [");
		builder.append("commandCode=");
		builder.append(String.format("0x%02X", commandCode));
		builder.append(", errorCode=");
		builder.append(String.format("0x%02X", errorCode));
		builder.append(", bytesWritten=");
		builder.append(bytesWritten);
		builder.append(", bytesRead=");
		builder.append(bytesRead);
		builder.append("]");
		
		return builder.toString();
		
	} // toStrin()
	

} // ssalc

/************************** Memento mori! **************************/
