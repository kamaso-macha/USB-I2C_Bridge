/**
 *
 * **********************************************************************
 * PROJECT       : Pi4J-Mock
 * FILENAME      : module-info.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/Pi4J-Mock
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
 * 
 * Core library for interaction with the <i>Pololu Isolated USB-to-I²C Adapter<i/> (Pololu item #: 5396)
 * and <i>Pololu Isolated USB-to-I²C Adapter with Isolated Power<i/> (Pololu item #: 5397).
 * 
 * It provides a API to simplify the interaction with the adaptor and I2C devices. 
 * 
 */

module lan.sdi.usb2iic {

    requires org.slf4j;
    requires org.apache.logging.log4j;
//    requires org.apache.logging.log4j.slf4j2.impl;
	requires com.fazecast.jSerialComm;

	exports lan.sdi.usb2iic.core;
    exports lan.sdi.usb2iic.pololu;
    exports lan.sdi.usb2iic.pololu.impl;
    exports lan.sdi.usb2iic.pololu.model;

    exports lan.sdi.utility;
        
} // eludom


/************************** Memento mori! **************************/
