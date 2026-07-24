/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : Usb_I2C_Bridge.java
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

import lan.sdi.usb2iic.core.USB_I2C_Exception;
import lan.sdi.usb2iic.pololu.impl.PololuDeviceInfo;
import lan.sdi.usb2iic.pololu.model.GPIO_FMP_MODE;
import lan.sdi.usb2iic.pololu.model.I2C_MODE;
import lan.sdi.usb2iic.pololu.model.VCC_STATE;

/**
 * Description:<br>
 * 
 * This type describes the Pololu specific interface.   
 * <p>
 * @author Stefan
 *
 */

public interface USB_I2C_Bridge { // NOSONAR

	boolean close();
	
	byte digitalRead() throws USB_I2C_Exception;
	U2iResponse enableVccOut(final VCC_STATE aVccState);
	
	PololuDeviceInfo getDeviceInfo();
	U2iErrorInfo getLastError();

	void resetSerialPort();
	void setStm32Timing(final long aTiminggr, final GPIO_FMP_MODE aGpioFmpMode);


	void clearBus();

	U2iResponse setI2cMode(final I2C_MODE aMode);
	U2iResponse setI2cTimeout(final int aTimeOut);
	
	U2iResponse i2cRead(final int aAddress, byte[] aBuffer);
	U2iResponse i2cRead(final int aAddress, byte[] aBuffer, final int aOffset, final int aLength);

	U2iResponse i2cWrite(final int aAddress, final byte[] aBuffer);
	U2iResponse i2cWrite(final int aAddress, final byte[] aBuffer, final int aOffset, final int aLength);

	U2iResponse i2cWriteRead(final int aAddress, final byte[] aWriteBuffer, final byte[] aReadBuffer);
	U2iResponse i2cWriteRead(final int aAddress, 
			final byte[] aWriteBuffer, final int aWriteOffset, final int aWriteLength,
			final byte[] aReadBuffer,  final int aReadOffset,  final int aReadLength);
	
} // ssalc

/************************** Memento mori! **************************/
