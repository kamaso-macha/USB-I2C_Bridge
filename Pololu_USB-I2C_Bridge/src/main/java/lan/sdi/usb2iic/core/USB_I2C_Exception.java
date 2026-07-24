/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : USB_I2C_Exception.java
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


package lan.sdi.usb2iic.core;

import lan.sdi.usb2iic.pololu.U2iResponse;

/**
 * Description:<br>
 * USB_I2C specific exception type.
 * 
 * <p>
 * @author Stefan
 *
 */

public class USB_I2C_Exception extends Exception {	// NOSONAR

	private static final long serialVersionUID = 1L;
	
	public final U2iResponse response;
	

	public USB_I2C_Exception(final String aMessage) {
		super(aMessage);
		
		response = null;
		
	} // USB_I2C_Exception()
	
	
} // ssalc

/************************** Memento mori! **************************/