/**
 *
 * **********************************************************************
 * PROJECT       : PI4J-PololuI2C
 * FILENAME      : HexUtils.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/PI4J-PololuI2C
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


package lan.sdi.utility;

/**
 * Responsibilities:<br>
 * Data type conversion.
 * 
 * <p>
 * Collaborators:<br>
 * No specific.
 * 
 * <p>
 * Description:<br>
 * Common usable utility methods.
 * 
 * <p>
 * @author Stefan
 *
 */

public class HexUtils {
	
	private HexUtils() { /* empty */ }

	
	/**
	 * Convert a byte array into a string where each byte of the array is printed as hex number.
	 * 
	 * @param aArray Byte array, containing the data to be converted.
	 * 
	 * @return A string made of the bytes.
	 */
	public static String byteArrayToHex(final byte[] aArray) {
		
		   StringBuilder sb = new StringBuilder(aArray.length * 2);
		   
		   for(byte b: aArray)
		      sb.append(String.format("%02X ", b));
		   
		   return sb.toString().stripTrailing();
		   
		} // byteArrayToHex()

} // ssalc

/************************** Memento mori! **************************/
