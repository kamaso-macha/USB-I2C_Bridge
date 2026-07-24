/**
 *
 * **********************************************************************
 * PROJECT       : Pololu_USB-I2C_Bridge
 * FILENAME      : GPOI_FMP_MODETest.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/Pololu_USB-I2C_Bridge
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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lan.sdi.usb2iic.pololu.model.GPIO_FMP_MODE;

/**
 * Responsibilities:<br>
 * Validates the parameter set for <i>Set STM32 timing.gpio_fmp_mode<i/>
 * 
 * <p>
 * Collaborators:<br>
 * None
 * 
 * <p>
 * @author Stefan
 *
 *
 */

class GPIO_FMP_MODE_Test {

	private final Logger logger = LoggerFactory.getLogger(GPIO_FMP_MODE_Test.class.getName());

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGpioFmpMode() {
		logger.info("testGpioFmpMode()");
		
		assertEquals( 0, GPIO_FMP_MODE.FAST_MODE_DISABLE.value);
		assertEquals( 1, GPIO_FMP_MODE.FAST_MODE_ENABLE.value);
		
	} // testGpioFmpMode()

} // ssalc


/************************** Memento mori! **************************/