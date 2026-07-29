/**
 *
 * **********************************************************************
 * PROJECT       : Pololu_USB-I2C_Bridge
 * FILENAME      : module-info.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/Pololu_USB-I2C_Bridge
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
 */

// DOC
// Created at 2025-12-27 16:03:48

module lan.sdi.usb2i2c {

    requires org.slf4j;
	requires com.fazecast.jSerialComm;

    exports lan.sdi.usb2iic;
    exports lan.sdi.usb2iic.core;
    exports lan.sdi.usb2iic.pololu;
    exports lan.sdi.usb2iic.pololu.impl;
    exports lan.sdi.usb2iic.pololu.model;

}
