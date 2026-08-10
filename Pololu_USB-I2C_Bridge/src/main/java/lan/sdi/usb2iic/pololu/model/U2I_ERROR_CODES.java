/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : U2I_ERROR_CODES.java
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


package lan.sdi.usb2iic.pololu.model;

/**
 * Responsibilities:<br>
 * Defines the error codes used by Pololu USB to I2C adaptor.
 * 
 * <p>
 * Collaborators:<br>
 * None.
 * 
 * <p>
 * Description:<br>
 * Refer to <a href="https://www.pololu.com/docs/0J89/7">Command reference of the vendor documentation</a> for more details.
 * 
 * <p>
 * @author Stefan
 *
 */

public enum U2I_ERROR_CODES {
	
	  ERROR_NONE 				(  0 )
	, ERROR_PROTOCOL 			(  1 )
	, ERROR_GAP_2			 	(  2 )
	, ERROR_TIMEOUT 			(  3 )
	, ERROR_ADDRESS_TIMEOUT 	(  4 )
	, ERROR_TX_TIMEOUT 			(  5 )
	, ERROR_RX_TIMEOUT 			(  6 )
	, ERROR_GAP_7				(  7 )
	, ERROR_ADDRESS_NACK 		(  8 )
	, ERROR_TX_DATA_NACK 		(  9 )
	, ERROR_BUS_ERROR 			( 10 )
	, ERROR_ARBITRATION_LOST 	( 11 )
	, ERROR_GAP_12 				( 12 )
	, ERROR_NOT_SUPPORTED 		( 13 )
	;
	
	public final int errorCode;
	
	U2I_ERROR_CODES(final int aErrorCode) { errorCode = aErrorCode; }
	
	public static U2I_ERROR_CODES getName(int aErrorCode) { 
		
		if(aErrorCode > U2I_ERROR_CODES.values().length)
			throw new IllegalArgumentException(String.format("aErrorCode %d is out of range 0 .. %d!", 
						aErrorCode, U2I_ERROR_CODES.values().length));

		for(U2I_ERROR_CODES ec : U2I_ERROR_CODES.values())
			if(ec.errorCode == aErrorCode)
				return ec;
		
		return null;
		
	} // getName()
	
} // mune

/************************** Memento mori! **************************/
