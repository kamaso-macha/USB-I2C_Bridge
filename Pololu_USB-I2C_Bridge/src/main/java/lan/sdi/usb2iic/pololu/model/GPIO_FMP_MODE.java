/**
 *
 * **********************************************************************
 * PROJECT       : Pololu_USB-I2C_Bridge
 * FILENAME      : GPOI_FMP_MODE.java
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
 * Defines the valid parameter set for the setStm32Timing() method.
 * 
 * <p>
 * Collaborators:<br>
 * USB_I2C_BridgeImpl()
 * 
 * <p>
 * Description:<br>
 * Refer to <a href="https://www.pololu.com/docs/0J89/7">Command reference of the vendor documentation</a> for more details.
 * 
 * <p>
 * @author Stefan
 *
 *
 */

public enum GPIO_FMP_MODE {

	  FAST_MODE_DISABLE		(0)
	, FAST_MODE_ENABLE		(1)
	;
	
	public final byte value;
	
	private GPIO_FMP_MODE(final int aValue) { value = (byte) aValue; }
	
	
} // mune

/************************** Memento mori! **************************/