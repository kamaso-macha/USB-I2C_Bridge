/**
 *
 * **********************************************************************
 * PROJECT       : Pololu USB - I2C bridge
 * FILENAME      : USB_I2C_BridgeImplTest.java
 *
 * More information about this project can be found on Github
 * http://github.com/kamaso-macha/Pololu USB - I2C bridge
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


package lan.sdi.usb2iic.pololu.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fazecast.jSerialComm.SerialPort;

import lan.sdi.usb2iic.core.USB_I2C_Exception;
import lan.sdi.usb2iic.pololu.U2iErrorInfo;
import lan.sdi.usb2iic.pololu.U2iResponse;
import lan.sdi.usb2iic.pololu.model.GPIO_FMP_MODE;
import lan.sdi.usb2iic.pololu.model.I2C_MODE;
import lan.sdi.usb2iic.pololu.model.VCC_STATE;
import lan.sdi.utility.HexUtils;
import test.utility.TestAppender;

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
// Created at 2026-07-15 10:21:27

class USB_I2C_BridgeImplTest {

	private static Logger logger = LoggerFactory.getLogger(USB_I2C_BridgeImplTest.class);

	private static TestAppender testAppender;
	
	protected static final String BRIDGE_NAME = "COM2";
	protected static final int I2C_ADDRESS = 0x0042;	// 66T
	
	protected static final byte DEV_INFO_SIZE = (byte) 0x010;

	
    private SerialPort serialPortMock1;
    private SerialPort serialPortMock2;
    private SerialPort serialPortMock3;
    
    private SerialPort[] serialPortList;
	
	private USB_I2C_BridgeImpl cut;
	private U2iResponse rsp;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		logger.info("setUpBeforeClass()");
		
	    testAppender = new TestAppender("testAppender", null);
	    testAppender.start();

	    LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
	    Configuration configuration = loggerContext.getConfiguration();
	    
	    LoggerConfig rootLoggerConfig = configuration.getLoggerConfig("");
	    rootLoggerConfig.addAppender(testAppender, Level.ALL, null);
	    
	    loggerContext.updateLoggers();
		
	} // setUpBeforeClass()
	

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
		
		// Create mock SerialPort instance
		serialPortMock1 = mock(SerialPort.class);
        serialPortMock2 = mock(SerialPort.class);
        serialPortMock3 = mock(SerialPort.class);
        
        // Create mock port array for getCommPorts()
        serialPortList = new SerialPort[] { serialPortMock1, serialPortMock2, serialPortMock3 };
        
        // Stub the port identification methods
        when(serialPortMock1.getSystemPortName()).thenReturn("TEST1"); 
        when(serialPortMock1.getDescriptivePortName()).thenReturn("fake mock adaptor 1");
        
        when(serialPortMock2.getSystemPortName()).thenReturn(BRIDGE_NAME); 
        when(serialPortMock2.getDescriptivePortName()).thenReturn("jUnit Test Mock adaptor");
        
        when(serialPortMock3.getSystemPortName()).thenReturn("TEST3"); 
        when(serialPortMock3.getDescriptivePortName()).thenReturn("fake mock adaptor 3");
        
        // default behavior
        when(serialPortMock2.openPort(anyInt())).thenReturn(true);
        when(serialPortMock2.closePort()).thenReturn(true);
        
        when(serialPortMock2.getLastErrorCode()).thenReturn(0);
        when(serialPortMock2.getLastErrorLocation()).thenReturn(0);
        
        // generic answer for all methods but write
		when(serialPortMock2.readBytes(any(byte[].class), anyInt())).thenAnswer(inv -> {
			byte[] buf = inv.getArgument(0);
			if (buf != null) buf[0] = (byte) buf.length;
			logger.trace("readBytes(): buf.length: {}", buf.length);
			
			return inv.getArgument(1);
			
		});

		// specific answer for write methods
		when(serialPortMock2.readBytes(any(byte[].class), eq(1))).thenAnswer(inv -> {
			byte[] buf = inv.getArgument(0);
			if (buf != null) buf[0] = (byte) 0x0_00;
			logger.trace("readBytes(): buf.length: {}", buf.length);
			
			return inv.getArgument(1);
			
		});

		when(serialPortMock2.writeBytes(any(byte[].class), anyInt())).thenAnswer(inv -> {
			return inv.getArgument(1);
		});


	} // setUp()
	

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}
	

	private MockedStatic<SerialPort> mockStaticSerialPorts() {
	
		MockedStatic<SerialPort> mock = mockStatic(SerialPort.class);
	    mock.when(SerialPort::getCommPorts).thenReturn(serialPortList);
	    
	    return mock;
	    
	} // mockSerialPorts()


	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#USB_I2C_BridgeImpl(java.lang.String)}.
	 */
	@Test
	void testUSB_I2C_BridgeImpl() {
		logger.info("testUSB_I2C_BridgeImpl()");
		
		IllegalArgumentException thrownIAE;
		USB_I2C_Exception thrownUIE;
		
        thrownIAE = assertThrows(IllegalArgumentException.class, () -> { new USB_I2C_BridgeImpl(null); });
        assertEquals(thrownIAE.getMessage(), "aBridgeName can't be null nor blank!");
        
        thrownIAE = assertThrows(IllegalArgumentException.class, () -> { new USB_I2C_BridgeImpl(""); });
        assertEquals(thrownIAE.getMessage(), "aBridgeName can't be null nor blank!");
		
        try (
        		
           	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
            	
        ) {
                	
            // Override default behavior
            when(serialPortMock2.openPort(anyInt())).thenReturn(
            	true, 		// successful
            	false		// unsuccessful
            );
            
            // successful
            
            cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);           
            assertNotNull(cut);

            staticMock.verify(() -> SerialPort.getCommPorts(), times(1));

            verify(serialPortMock2, atLeast(2)).getSystemPortName();
            verify(serialPortMock2, times(1)).setComPortTimeouts(any(int.class), any(int.class), any(int.class));
            verify(serialPortMock2, times(1)).openPort(any(int.class));

            // unsuccessful
            
            thrownUIE = assertThrows(USB_I2C_Exception.class, () -> { new USB_I2C_BridgeImpl(BRIDGE_NAME); });
            assertEquals(thrownUIE.getMessage(), "Failed to open port " + BRIDGE_NAME);
           
        } // yrt
        catch (Exception e) {
			
        	logger.error("Ooops, an unexpected Exception flew by ...", e);
			fail("Unexpected Exception");
		}

	} // testUSB_I2C_BridgeImpl()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#clearBus()}.
	 */
	@Test
	void testCorrectClearBusCommand() {
		logger.info("testCorrectClearBusCommand()");
		
		
        try (
        		
           	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
            	
        ) {
            	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            cut.clearBus();
             
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            assertTrue(
            	wrCommand != null &&
            	wrCommand.length == 1 &&
            	wrCommand[0] == (byte) 0x98 
       		);

   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testCorrectClearBusCommand()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#digitalRead()}.
	 */
	@Test
	void testClearBus() {
		logger.info("testClearBus()");
		
		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
			
			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
			cut.clearBus();
        	
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            assertEquals(2, bufferCaptor.getAllValues().size());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of digital read.
            
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            logger.info("cmd buffer: {}", HexUtils.byteArrayToHex(wrCommand));

            assertTrue(
            	wrCommand != null && 
            	wrCommand.length == 1 &&
            	wrCommand[0] == (byte) 0x0_98
       		);

        } 
        catch (Exception e) {
			
        	logger.error("Ooops, an unexpected Exception flew by ...", e);
			fail("Unexpected Exception");
			
		} // yrt
		
	} // testClearBus()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#digitalRead()}.
	 */
	@Test
	void testScanBus() {
		logger.info("testScanBus()");

		// prepare reference
		byte[] predeterminedValues = new byte[128];
		byte[] reference = new byte[128];
		
		int tmp;
		
		for (int i = 0; i < 128; i++) {
		
			if (i % 16 == 0) {
		        predeterminedValues[i] = 0;
		        reference[i] = 0;
		    } 
			else if (i % 32 == 1) {
				
				tmp = (i % 14);
				if(tmp == 8) tmp--; // Any value between 1 .. 14 but not 8
		        predeterminedValues[i] = (byte) tmp;  
		        reference[i] = (byte) tmp;  
		        
		    } 
			else {
		        predeterminedValues[i] = (byte) 0x08;
		        reference[i] = (byte) 0x0FF;
		    } // fi
			
		} // rof
		
		// there is exactly 1 read attempt BEFORE the scan starts. 
		// So we must start by -1 for a correct result buffer alignment.
		AtomicInteger callCounter = new AtomicInteger(-1);
		AtomicInteger offsetValue = new AtomicInteger(0);

		when(serialPortMock2.readBytes(any(byte[].class), eq(1))).thenAnswer(inv -> {
			
		    int count  = callCounter.get();
		    int offset = offsetValue.get();
		    
		    if(count >= 0) {
//				logger.debug(String.format("count = 0x%02X, offset = 0x%02X, idx = 0x%02X, value = 0x%02X", 
//						count, offset, count + offset, predeterminedValues[count + offset]));

				byte[] buf = inv.getArgument(0);
			    
			    if (buf != null && count < predeterminedValues.length) {
			        buf[0] = predeterminedValues[count + offset];
			    }
			    
		    } // fi
		    
		    callCounter.incrementAndGet();
		    
		    return 1;
		    
		});
	
		when(serialPortMock2.writeBytes(any(byte[].class), eq(3)))
		.thenReturn(3);
	

        try (
    		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
            	
			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
			assertNotNull(cut);
			
			byte[] result;
	
			result = cut.scanBus(true);

			logger.info(HexUtils.byteArrayToHex(predeterminedValues));
			logger.info(HexUtils.byteArrayToHex(reference));
			logger.info(HexUtils.byteArrayToHex(result));

			assertEquals(128, result.length);
			assertArrayEquals(reference, result);
	
			callCounter.set(0);
			offsetValue.set(8);
			
			result = cut.scanBus(false);
			
			for(int i = 0, j = 0x078; i < 8; i++, j++) {
				reference[i] = (byte) 0x0FE;
				reference[j] = (byte) 0x0FE;
			}

			logger.info(HexUtils.byteArrayToHex(predeterminedValues));
			logger.info(HexUtils.byteArrayToHex(reference));
			logger.info(HexUtils.byteArrayToHex(result));

			assertEquals(128, result.length);
			assertArrayEquals(reference, result);
			
        }
        catch (Exception e) {
			
        	logger.error("Ooops, an unexpected Exception flew by ...", e);
			fail("Unexpected Exception");
			
		} // yrt
	
	} // testScanBus()
	
		
	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#close()}.
	 */
	@Test
	void testClose() {
		logger.info("testClose()");
		
		
        try (
    		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
            	
        	when(serialPortMock2.closePort()).thenReturn(
        		true,    // successful
        		false    // NOT successful
        	);
        	
			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
			
            // successful
			if(! cut.close()) fail("cut.close() should have been successful");

			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
			
            // NOT successful
			if(cut.close()) fail("cut.close() should have been NOT successful");

	    } 
        catch (Exception e) {
			
        	logger.error("Ooops, an unexpected Exception flew by ...", e);
			fail("Unexpected Exception");
			
		} // yrt
		
	} // testClose()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#digitalRead()}.
	 */
	@Test
	void testDigitalRead() {
		logger.info("testDigitalRead()");
		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			when(serialPortMock2.readBytes(any(byte[].class), eq(1))).thenAnswer(inv -> {
				byte[] buf = inv.getArgument(0);
				if (buf != null) buf[0] = (byte) 0x0_03;
				
				return 1;
				
			});

			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
			
			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
			byte result = cut.digitalRead();
        	
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            assertEquals(2, bufferCaptor.getAllValues().size());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of digital read.
            
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            logger.info("cmd buffer: {}", HexUtils.byteArrayToHex(wrCommand));

            assertTrue(
            	wrCommand != null && 
            	wrCommand.length == 1 &&
            	wrCommand[0] == (byte) 0x0_A2
       		);

            assertEquals((byte) 0x0_03, result);

            
            // error handling -------------------------------------------------
            USB_I2C_Exception thrown;
            
            reset(serialPortMock2);
            
            when(serialPortMock2.writeBytes(any(byte[].class), eq(1))).thenReturn(0);
            
            thrown = assertThrows(USB_I2C_Exception.class, () -> cut.digitalRead());
            assertEquals("Error while writing or reading the I2C bus.", thrown.getMessage());
            
            reset(serialPortMock2);            
            
            when(serialPortMock2.readBytes(any(byte[].class), eq(1))).thenReturn(0);
        
            thrown = assertThrows(USB_I2C_Exception.class, () -> cut.digitalRead());
            assertEquals("Error while writing or reading the I2C bus.", thrown.getMessage());

        } 
        catch (Exception e) {
			
        	logger.error("Ooops, an unexpected Exception flew by ...", e);
			fail("Unexpected Exception");
			
		} // yrt
		
	} // testDigitalRead()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#enableVccOut()}.
	 */
	@Test
	void testEnableVccOut() {
		logger.info("testEnableVccOut()");
		
		final byte COMMAND = (byte) 0x0_A4;
		final VCC_STATE VCC_STATUS = VCC_STATE.VCC_ON;
		
		
        try (
        		MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			when(serialPortMock2.readBytes(any(byte[].class), eq(1))).thenAnswer(inv -> {
				byte[] buf = inv.getArgument(0);
				if (buf != null) buf[0] = VCC_STATUS.value;
				
				return 1;
				
			});

			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
			
			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
			U2iResponse response = cut.enableVccOut(VCC_STATUS);
        	
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            assertEquals(2, bufferCaptor.getAllValues().size());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of digital read.
            
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            logger.info("cmd buffer: {}", HexUtils.byteArrayToHex(wrCommand));

            assertTrue(
            	wrCommand != null && 
            	wrCommand.length == 2 &&
            	wrCommand[0] == COMMAND &&
            	wrCommand[1] == VCC_STATUS.value
       		);

            assertEquals(COMMAND, response.commandCode);
            assertEquals(VCC_STATUS.value, response.errorCode);
            assertEquals(2, response.bytesWritten);
            assertEquals(1, response.bytesRead);
            
        } 
        catch (Exception e) {
			
        	logger.error("Ooops, an unexpected Exception flew by ...", e);
			fail("Unexpected Exception");
			
		} // yrt
		
	} // testEnableVccOut()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#USB_I2C_BridgeImpl#getDeviceInfo()}.
	 */
	@Test
	void testGetDeviceInfo() {
		logger.info("testGetDeviceInfo()");
		
		/*
		 * Sample device info data for Pololu Isolated USB-to-I2C Adapter
		 * 
		 *   device info buffer: 
		 *        0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F
		 *   00  1C 00 FB 1F 02 25 01 01 2D 00 00 00 00 00 00 00 
		 *   10  52 00 76 00 01 50 36 42 48 37 30 20 -- -- -- --
		 *   
		 *   
		 *   len			: 0x1C
		 *   version		: 0x00
		 *   vID			: 0x1FFB
		 *   pID			: 0x2502
		 *   FW-ver			: 01.01
		 *   mod			: 2D 00 00 00 00 00 00 00 
		 *   uid			: 52 00 76 00 01 50 36 42 48 37 30 20
		 *   
		 */  

		String referenceString = 
				  "PololuDeviceInfo ["
				+ "size: 0x1C / 28, "
				+ "version: 0, "
				+ "USB vendor ID: 0x1FFB, "
				+ "USB product ID: 0x2502, "
				+ "FW version: 1.2, "
				+ "modifications: -, "
				+ "uid: 52 00 76 00 01 50 36 42 48 37 30 20"
				+ "]"
				;

		final byte[] rawDeviceInfo_5396 = new byte[] {
				(byte) 0x0_1C, (byte) 0x0_00, (byte) 0x0_FB, (byte) 0x0_1F, (byte) 0x0_02, (byte) 0x0_25, (byte) 0x0_02, (byte) 0x0_01, 
				(byte) 0x0_2D, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, (byte) 0x0_00, 
				(byte) 0x0_52, (byte) 0x0_00, (byte) 0x0_76, (byte) 0x0_00, (byte) 0x0_01, (byte) 0x0_50, (byte) 0x0_36, (byte) 0x0_42, 
				(byte) 0x0_48, (byte) 0x0_37, (byte) 0x0_30, (byte) 0x0_20, 
			};

		byte[] uniqueDeviceId = new byte[] {
				(byte) 0x0_52, (byte) 0x0_00, (byte) 0x0_76, (byte) 0x0_00, (byte) 0x0_01, (byte) 0x0_50, (byte) 0x0_36, (byte) 0x0_42, 
				(byte) 0x0_48, (byte) 0x0_37, (byte) 0x0_30, (byte) 0x0_20, 
			};
		
		byte[] commandByte = new byte[] { (byte) 0xA7 };

		
        try (
        		
           	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
            	
        ) {
            
            when(serialPortMock2.readBytes(any(byte[].class), eq(1))).thenAnswer(inv -> {
            	
                byte[] buf = inv.getArgument(0);
                if (buf != null) buf[0] = rawDeviceInfo_5396[0];
                
        		logger.trace("readBytes(buf, 1) - buf[0]: {}", buf[0]);

        		return 1;
                
            });

            when(serialPortMock2.readBytes(any(byte[].class), anyInt(), eq(1))).thenAnswer(inv -> {
            	
                byte[] buf = inv.getArgument(0);
                int requestedLength = inv.getArgument(1);
                
        		logger.trace("deviceInfo.length: {}, requestedLength: {}", rawDeviceInfo_5396.length, requestedLength);

        		if (buf != null && requestedLength > 0) {
                    System.arraycopy(rawDeviceInfo_5396, 0, buf, 0, rawDeviceInfo_5396.length);
                }
                
        		logger.trace("readBytes(buf, buf[0], 1) - buf[0]: {}, deviceInfo.length: {}", buf[0], rawDeviceInfo_5396.length);

                return rawDeviceInfo_5396.length;
                
            });

            // Capture both calls to readBytes
            ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);
            ArgumentCaptor<Integer> lengthCaptor = ArgumentCaptor.forClass(Integer.class);
            
        	InOrder inOrder = Mockito.inOrder(serialPortMock2);
        	
            testAppender.clearList();
            testAppender.setLogSource("USB_I2C_BridgeImpl.<init>");
            testAppender.setLogLevel(Level.INFO);
//            testAppender.setDebug(true);
            
            cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);

            // Capture ALL readBytes calls
            verify(serialPortMock2, times(1)).readBytes(bufferCaptor.capture(), lengthCaptor.capture());
            verify(serialPortMock2, times(1)).readBytes(bufferCaptor.capture(), lengthCaptor.capture(), eq(1));
            
            List<Integer> lengths = lengthCaptor.getAllValues();
            List<byte[]> buffers  = bufferCaptor.getAllValues();
            
            // First call should read 1 byte
            assertEquals(1, lengths.get(0), "First read should be 1 byte");
            assertEquals(rawDeviceInfo_5396[0], buffers.get(0)[0]);
            
            // Second call should read N bytes (matching first byte from first read)
            assertEquals(rawDeviceInfo_5396[0], lengths.get(1), "Second read should be N bytes");
            
            inOrder.verify(serialPortMock2, times(1)).writeBytes(commandByte, 1);
            inOrder.verify(serialPortMock2, times(1)).readBytes(any(byte[].class), eq(1));
            inOrder.verify(serialPortMock2, times(1)).readBytes(any(byte[].class), eq((int)rawDeviceInfo_5396[0]), eq(1));

            testAppender.dumpItems();
            
    		List<String> items = testAppender.getLogItems();

    		assertEquals(1, items.size());
    		assertEquals(items.get(0), referenceString);
    		
    		PololuDeviceInfo returnedDeviceInfo = cut.getDeviceInfo();
    		assertNotNull(returnedDeviceInfo);

    		assertEquals(0x0_1C,	returnedDeviceInfo.size);
    		assertEquals(0x0_00,	returnedDeviceInfo.versionNbr);
    		assertEquals(0x0_1FFB,	returnedDeviceInfo.usbVendorId);
    		assertEquals(0x0_2502,	returnedDeviceInfo.usbProductId);
    		assertEquals(01,		returnedDeviceInfo.firmwareVersionMajor);
    		assertEquals(02,		returnedDeviceInfo.firmwareVersionMinor);
    		assertEquals("-",		returnedDeviceInfo.specialModifications);
    		
    		assertEquals(0, Arrays.compare(uniqueDeviceId, returnedDeviceInfo.uniqueDeviceId));
    		
    		assertEquals("Pololu Isolated USB-to-I2C Adapter", returnedDeviceInfo.deviceName);

	    } 
        catch (Exception e) {
			
        	logger.error("Ooops, an unexpected Exception flew by ...", e);
			fail("Unexpected Exception");
			
		} // yrt
        
	} // testGetDeviceInfo()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#getLastError()}.
	 */
	@Test
	void testGetLastError() {
		logger.info("testGetLastError()");
		
		final int ERROR_CODE		= 7;
		final int ERROR_LOCATION	= 815;

        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
            	
			when(serialPortMock2.writeBytes(any(byte[].class), anyInt())).thenAnswer(inv -> {
				int n = inv.getArgument(1);
				return n;
			});
			
			
            when(serialPortMock2.getLastErrorCode()).thenReturn(ERROR_CODE);
            
            
            when(serialPortMock2.getLastErrorLocation()).thenReturn(ERROR_LOCATION);
            
            testAppender.clearList();
            testAppender.setLogSource("USB_I2C_BridgeImpl.checkForErrors");
            testAppender.setLogLevel(Level.ERROR);

            cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
		    assertNotNull(cut);
		    
		    U2iErrorInfo errorInfo = cut.getLastError();
		    assertEquals(ERROR_CODE, errorInfo.errorCode);
		    assertEquals(ERROR_LOCATION, errorInfo.errorLocation);
		    
		    testAppender.dumpItems();
		    
    		List<String> items = testAppender.getLogItems();
    		
    		assertEquals(3, items.size());
    		assertEquals(items.get(0), "r/w error, errorcode: 7, errorlocation: 815");

   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testGetLastError()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cRead(int, byte[])}.
	 */
	@Test
	void testCorrectI2cReadCommandHeader() {
		logger.info("testCorrectI2cReadCommandHeader()");
		
		final int DATA_LENGTH   = 1; 

        byte[] readBuffer = new byte[DATA_LENGTH];
		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
            	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cRead(I2C_ADDRESS, readBuffer);
             
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            assertTrue(
            	wrCommand != null && 
            	wrCommand.length == 3 &&
            	wrCommand[0] == (byte) 0x92 && 
            	wrCommand[1] == (byte) I2C_ADDRESS && 
            	wrCommand[2] == DATA_LENGTH
       		);

   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testCorrectI2cReadCommandHeader()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cRead(int, byte[])}.
	 */
	@Test
	void testSuccessfulI2cRead_int_byteArray() {
		logger.info("testSuccessfulI2cRead_int_byteArray()");
		
		final int  READ_CMD_SIZE = 3;
		
		final byte[] data = new byte[] { 
			(byte) 0x042, (byte) 0x033, (byte) 0x05A, (byte) 0x066, 
			(byte) 0x088, (byte) 0x088, (byte) 0x0A5, (byte) 0x0FF
		};
		
        final U2iResponse referenceRsp = new U2iResponse((byte) 0x92, (byte) 0x00, READ_CMD_SIZE, data.length);

        byte[] readBuffer = new byte[data.length];
		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
            	
			when(serialPortMock2.readBytes(any(byte[].class), eq(1)))
				.thenReturn(1);
           
			when(serialPortMock2.readBytes(any(byte[].class), eq(1))).thenAnswer(inv -> {
				byte[] buf = inv.getArgument(0);
				if (buf != null) buf[0] = DEV_INFO_SIZE;
				
				return 1;
				
			});

			when(serialPortMock2.readBytes(any(byte[].class), eq(data.length + 1))).thenAnswer(inv -> {

				byte[] buf = inv.getArgument(0);
				int requestedLength = inv.getArgument(1);
				   
				logger.trace("data.length: {}, requestedLength: {}", data.length, requestedLength);
				
				if (buf != null && requestedLength == data.length + 1) {
					buf[0] = (byte) 0x000;
					System.arraycopy(data, 0, buf, 1, data.length);
				}
				   
				logger.trace("readBytes(buf, length) - buf[0]: {}, data.length: {}", buf[0], data.length);
				
				return data.length;
   
			});
			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cRead(I2C_ADDRESS, readBuffer);
            assertNotNull(rsp);
           
            assertTrue(rsp.equals(referenceRsp));
    		assertTrue(Arrays.equals(data, readBuffer));
            
   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testSuccessfulI2cRead_int_byteArray()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cRead(int, byte[], int, int)}.
	 */
	@Test
	void testSuccessfulI2cRead_int_byteArray_int_int() {
		logger.info("testSuccessfulI2cRead_int_byteArray_int_int()");
		
		final int  READ_CMD_SIZE = 3;
		
		// defines the data read from the i2c device
		final byte[] data = new byte[] { 
				(byte) 0x022, (byte) 0x023, (byte) 0x024, (byte) 0x025, 
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036
		};
		
		// the final buffer content after read operation
		final byte[] reference = new byte[] { 
				(byte) 0x011, (byte) 0x012, (byte) 0x013, (byte) 0x014, 
				(byte) 0x022, (byte) 0x023, (byte) 0x024, (byte) 0x025, 
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036
			};
			
		// working buffer for read operation
		
		final int DATA_OFSET = 4;
		
        byte[] readBuffer = new byte[] { 
    			(byte) 0x011, (byte) 0x012, (byte) 0x013, (byte) 0x014, 
    			(byte) 0x000, (byte) 0x000, (byte) 0x000, (byte) 0x000, 
    			(byte) 0x000, (byte) 0x000, (byte) 0x000, (byte) 0x000, 
    		};

        final U2iResponse referenceRsp = new U2iResponse((byte) 0x92, (byte) 0x00, READ_CMD_SIZE, data.length);

		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			when(serialPortMock2.readBytes(any(byte[].class), eq(data.length + 1))).thenAnswer(inv -> {

				byte[] buf = inv.getArgument(0);
				int requestedLength = inv.getArgument(1);
				   
				logger.trace("data.length: {}, requestedLength: {}", data.length, requestedLength);
				
				if (buf != null && requestedLength == data.length + 1) {
					buf[0] = (byte) 0x000;
					System.arraycopy(data, 0, buf, 1, data.length);
				}
				   
				logger.trace("readBytes(buf, length) - buf[0]: {}, data.length: {}", buf[0], data.length);
				
				return data.length;
   
			});
			
			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cRead(I2C_ADDRESS, readBuffer, DATA_OFSET, data.length);
            assertNotNull(rsp);
            
            logger.info("rsp: {}, ref: {}", rsp, referenceRsp);
            for(byte b : readBuffer) logger.debug(String.format("0x%02X", b));
            
            assertTrue(rsp.equals(referenceRsp));
 
    		assertTrue(Arrays.equals(reference, readBuffer));
            
   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testSuccessfulI2cRead_int_byteArray_int_int()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cWrite(int, byte[])}.
	 */
	@Test
	void testCorrectI2cWriteCommandHeader() {
		logger.info("testCorrectI2cWriteCommandHeader()");
		
		final int DATA_LENGTH   = 1; 

        byte[] readBuffer = new byte[DATA_LENGTH];
		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cWrite(I2C_ADDRESS, readBuffer);
             
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            assertTrue(
            	wrCommand != null && 
               	wrCommand.length >= 3 &&
            	wrCommand[0] == (byte) 0x91 && 
            	wrCommand[1] == (byte) I2C_ADDRESS && 
            	wrCommand[2] == DATA_LENGTH
       		);

   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testCorrectI2cWriteCommandHeader()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cWrite(int, byte[])}.
	 */
	@Test
	void testSuccessfulI2cWrite_int_byteArray() {
		logger.info("testSuccessfulI2cWrite_int_byteArray()");
		
		final int WRITE_CMD_SIZE   = 3;
		final int READ_RESULT_SIZE = 1;
		
		// defines the data written to the i2c device
		final byte[] data = new byte[] { 
				(byte) 0x022, (byte) 0x023, (byte) 0x024, (byte) 0x025, 
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036
		};
		
		final byte[] reference = new byte[] {
				(byte) 0x091, (byte) I2C_ADDRESS, (byte) data.length,		// cmd header
				(byte) 0x022, (byte) 0x023, (byte) 0x024, (byte) 0x025, 	// data
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036				
		};

        final U2iResponse referenceRsp = new U2iResponse((byte) 0x91, (byte) 0x00, data.length + WRITE_CMD_SIZE, READ_RESULT_SIZE);

        try (
    		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cWrite(I2C_ADDRESS, data);
            assertNotNull(rsp);
            
            logger.info("rsp: {}, ref: {}", rsp, referenceRsp);
            
            assertTrue(rsp.equals(referenceRsp));
           
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrBuffer = bufferCaptor.getAllValues().get(1);           
 
    		assertTrue(Arrays.equals(reference, wrBuffer));
        
   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testSuccessfulI2cWrite_int_byteArray()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cWrite(int, byte[], int, int)}.
	 */
	@Test
	void testSuccessfulI2cWrite_int_byteArray_int_int() {
		logger.info("testSuccessfulI2cWrite_int_byteArray_int_int()");
		
		final int WRITE_CMD_SIZE   = 3;
		final int READ_RESULT_SIZE = 1;
		
		// defines the data written to the i2c device
		final byte[] data = new byte[] { 
				(byte) 0x022, (byte) 0x023, (byte) 0x024, (byte) 0x025, 
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036
		};
		
		final int WR_OFFSET  = 4;
		final byte WR_LENGTH = (byte) (data.length - WR_OFFSET);
		
		final byte[] reference = new byte[] {
				(byte) 0x091, (byte) I2C_ADDRESS, WR_LENGTH,				// cmd header
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036		// data				
		};

        final U2iResponse referenceRsp = new U2iResponse((byte) 0x91, (byte) 0x00, data.length + WRITE_CMD_SIZE - WR_OFFSET, READ_RESULT_SIZE);

        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cWrite(I2C_ADDRESS, data, WR_OFFSET, data.length - WR_OFFSET);
            assertNotNull(rsp);
            
            logger.info("rsp: {}, ref: {}", rsp, referenceRsp);
            
            assertTrue(rsp.equals(referenceRsp));
            
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrBuffer = bufferCaptor.getAllValues().get(1);           
 
    		assertTrue(Arrays.equals(reference, wrBuffer));
        
   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testSuccessfulI2cWrite_int_byteArray_int_int()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cWriteRead(int, byte[], int, int, byte[], int, int)}.
	 */
	@Test
	void testCorrectI2cWriteReadCommandHeader() {
		logger.info("testCorrectI2cWriteReadCommandHeader()");
		
		final int WR_DATA_LENGTH   = 7; 
		final int RD_DATA_LENGTH   = 4; 

        byte[] writeBuffer = new byte[WR_DATA_LENGTH];
        byte[] readBuffer  = new byte[RD_DATA_LENGTH];
		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cWriteRead(I2C_ADDRESS, writeBuffer, readBuffer);
             
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            assertTrue(
            	wrCommand != null && 
                wrCommand.length >= 4 &&
            	wrCommand[0] == (byte) 0x9B && 
            	wrCommand[1] == (byte) I2C_ADDRESS && 
            	wrCommand[2] == WR_DATA_LENGTH &&
            	wrCommand[3] == RD_DATA_LENGTH
       		);

   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testCorrectI2cWriteReadCommandHeader()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cWriteRead(int, byte[], byte[])}.
	 */
	@Test
	void testI2cWriteRead_int_byteArray_byteArray() {
		logger.info("testI2cWriteRead_int_byteArray_byteArray()");
		
		// defines the data written to the i2c device
		final byte[] writeData = new byte[] { 
				(byte) 0x022, (byte) 0x023, (byte) 0x024, (byte) 0x025, 
		};
		
		// defines the data to be read from the i2c device
		final byte[] readData = new byte[] { 
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036,
				(byte) 0x044, (byte) 0x045, (byte) 0x046, (byte) 0x047, 
		};
		
		
		byte[] readBuffer =new byte[readData.length];
		
		final byte[] writeReference = new byte[] {
				(byte) 0x09B, (byte) I2C_ADDRESS, (byte) writeData.length, (byte) readBuffer.length,	// cmd header
				(byte) 0x022, (byte) 0x023, (byte) 0x024, (byte) 0x025, 								// data
		};

		final int WRRD_CMD_SIZE    = 4;
		final int READ_RESULT_SIZE = readData.length;
		
        final U2iResponse referenceRsp = new U2iResponse((byte) 0x9B, (byte) 0x00, writeData.length + WRRD_CMD_SIZE, READ_RESULT_SIZE + 1);

        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			when(serialPortMock2.readBytes(any(byte[].class), eq(readData.length + 1))).thenAnswer(inv -> {

				byte[] buf = inv.getArgument(0);
				int requestedLength = inv.getArgument(1);
				   
				logger.trace("data.length: {}, requestedLength: {}", readData.length, requestedLength);
				
				if (buf != null && requestedLength == readData.length + 1) {
					buf[0] = (byte) 0x000;
					System.arraycopy(readData, 0, buf, 1, readData.length);
				}
				   
				logger.trace("readBytes(buf, length) - buf[0]: {}, data.length: {}", buf[0], readData.length);
				
				return readData.length + 1;
   
			});
			
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cWriteRead(I2C_ADDRESS, writeData, readBuffer);
            assertNotNull(rsp);
            
            logger.info("rsp: {}, ref: {}", rsp, referenceRsp);
            
            assertTrue(rsp.equals(referenceRsp));
            
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            verify(serialPortMock2, times(2)).readBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrBuffer = bufferCaptor.getAllValues().get(1);     
 
    		assertTrue(Arrays.equals(writeReference, wrBuffer));		
    		assertTrue(Arrays.equals(readData, readBuffer));
        
   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testI2cWriteRead_int_byteArray_byteArray()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#i2cWriteRead(int, byte[], int, int, byte[], int, int)}.
	 */
	@Test
	void testI2cWriteRead_int_byteArray_int_int_byteArray_int_int() {
		logger.info("testI2cWriteRead_int_byteArray_int_int_byteArray_int_int()");
		
		// defines the data written to the i2c device
		final byte[] writeData = new byte[] { 
				(byte) 0x022, (byte) 0x023, (byte) 0x024, (byte) 0x025, 
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036,
		};
		
		// defines the data to be read from the i2c device
		final byte[] readData = new byte[] { 
				(byte) 0x044, (byte) 0x045, (byte) 0x046, (byte) 0x047, 
				(byte) 0x055, (byte) 0x056, (byte) 0x057, (byte) 0x058,
		};
		
		final int WRRD_CMD_SIZE    = 4;
		
		final int WR_OFFSET = 4;
		final int WR_LENGTH = writeData.length - WR_OFFSET;
		
		final int RD_OFFSET = 4;
		final int RD_LENGTH = readData.length - RD_OFFSET;
		
		
		final byte[] writeReference = new byte[] {
				(byte) 0x09B, (byte) I2C_ADDRESS, (byte) WR_LENGTH, (byte) RD_LENGTH,	// cmd header
				(byte) 0x033, (byte) 0x034, (byte) 0x035, (byte) 0x036,
		};

		final byte[] readReference = new byte[] {
				(byte) 0x000, (byte) 0x000, (byte) 0x000, (byte) 0x000,
				(byte) 0x055, (byte) 0x056, (byte) 0x057, (byte) 0x058,
		};
		
		byte[] readBuffer = new byte[readData.length];
		
        final U2iResponse referenceRsp = new U2iResponse((byte) 0x9B, (byte) 0x00, WR_LENGTH + WRRD_CMD_SIZE, RD_LENGTH + 1);

        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			when(SerialPort.getCommPorts()).thenReturn(serialPortList);
        
			when(serialPortMock2.readBytes(any(byte[].class), eq(RD_LENGTH + 1))).thenAnswer(inv -> {

				byte[] buf = inv.getArgument(0);
				int requestedLength = inv.getArgument(1);
				   
				logger.trace("data.length: {}, requestedLength: {}", readData.length, requestedLength);
				
				if (buf != null && requestedLength == RD_LENGTH + 1) {
					buf[0] = (byte) 0x000;
					System.arraycopy(readData, RD_OFFSET, buf, 1, RD_LENGTH);
				}
				   
				logger.trace("readBytes(buf, length) - buf[0]: {}, data.length: {}", buf[0], readData.length);
				
				return RD_LENGTH + 1;
   
			});
			
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.i2cWriteRead(I2C_ADDRESS, writeData, WR_OFFSET, WR_LENGTH, readBuffer, RD_OFFSET, RD_LENGTH);
            assertNotNull(rsp);
            
            logger.info("rsp: {}, ref: {}", rsp, referenceRsp);
            
            assertTrue(rsp.equals(referenceRsp));
            
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            verify(serialPortMock2, times(2)).readBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] writeBuffer = bufferCaptor.getAllValues().get(1);     
 
    		assertTrue(Arrays.equals(writeReference, writeBuffer));		
    		assertTrue(Arrays.equals(readReference,  readBuffer));
        
   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testI2cWriteRead_int_byteArray_int_int_byteArray_int_int()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#resetSerialPort()}.
	 */
	@Test
	void testResetSerialPortd() {
		logger.info("testResetSerialPortd()");
		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			when(SerialPort.getCommPorts()).thenReturn(serialPortList);
			
			when(serialPortMock2.isOpen()).thenReturn(
				  true
				, false
			);
			

			InOrder sequence = inOrder(serialPortMock2);
			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            // successful test
            cut.resetSerialPort();
            
            sequence.verify(serialPortMock2, times(1)).isOpen();
            sequence.verify(serialPortMock2, times(1)).setBreak();
            sequence.verify(serialPortMock2, times(1)).clearBreak();
            
            reset(serialPortMock2);
            
            testAppender.clearList();
            testAppender.setLogSource("USB_I2C_BridgeImpl.resetSerialPort");
            testAppender.setLogLevel(Level.ERROR);

            
            // error test
            cut.resetSerialPort();
            
            sequence.verify(serialPortMock2, times(1)).isOpen();
            sequence.verify(serialPortMock2, never()).setBreak();
            sequence.verify(serialPortMock2, never()).clearBreak();

            testAppender.dumpItems();
		    
    		List<String> items = testAppender.getLogItems();
    		
    		assertEquals(1, items.size());
    		assertEquals(items.get(0), "Invalid attempt to reset a closed comPort!");
            
   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testResetSerialPortd()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#setI2cMode(I2C_MODE)}.
	 */
	@Test
	void testSetI2cModeCommand() {
		logger.info("testSetI2cModeCommand()");
		
		final I2C_MODE mode = I2C_MODE.FAST_PLUS;
		
        try (
        		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			when(SerialPort.getCommPorts()).thenReturn(serialPortList);
			
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.setI2cMode(mode);
             
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            assertTrue(
            	wrCommand != null && 
            	wrCommand.length == 2 &&
            	wrCommand[0] == (byte) 0x94 && 
            	wrCommand[1] == mode.mode
       		);

   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testSetI2cModeCommand()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#setI2cTimeout(int)}.
	 */
	@Test
	void testSetI2cTimeoutCommand() {
		logger.info("testSetI2cTimeoutCommand()");
		
		final int timeOutValue = 1234;
		final byte[] toLeBytes = new byte[] {
			(byte) (timeOutValue & 0x00FF),
			(byte) ((timeOutValue >> 8) & 0x00FF)
		};
		
        try (
    		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            rsp = cut.setI2cTimeout(timeOutValue);
             
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);           
            assertTrue(
            	wrCommand != null && 
                wrCommand.length == 3 &&
            	wrCommand[0] == (byte) 0x97 && 
            	wrCommand[1] == toLeBytes[0] &&
               	wrCommand[2] == toLeBytes[1]
       		);

   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testSetI2cTimeoutCommand()
	

	/**
	 * Test method for {@link lan.sdi.usb2iic.pololu.impl.USB_I2C_BridgeImpl#setStm32Timing(int)}.
	 */
	@Test
	void testSetStm32Timing() {
		logger.info("testSetStm32Timing()");
		
		final int timingrValue = 12345678;
		final byte[] toLeBytes = new byte[] {
			(byte) (timingrValue & 0x00FF),
			(byte) ((timingrValue >>  8) & 0x00FF),
			(byte) ((timingrValue >> 16) & 0x00FF),
			(byte) ((timingrValue >> 24) & 0x00FF)
		};
		
		final GPIO_FMP_MODE gpioFmpMode = GPIO_FMP_MODE.FAST_MODE_ENABLE;
		
        try (
    		
        	MockedStatic<SerialPort> staticMock = mockStaticSerialPorts();        		
        	
        ) {
        	
			ArgumentCaptor<byte[]> bufferCaptor = ArgumentCaptor.forClass(byte[].class);

			
   			cut = new USB_I2C_BridgeImpl(BRIDGE_NAME);
            assertNotNull(cut);
            
            cut.setStm32Timing(timingrValue, gpioFmpMode);
             
            verify(serialPortMock2, times(2)).writeBytes(bufferCaptor.capture(), anyInt());
            
            assertEquals(2, bufferCaptor.getAllValues().size());
            
            // bufferCaptor.getAllValues().get(0) contains the writeByte buffer of getDeviceInfo.
            byte[] wrCommand = bufferCaptor.getAllValues().get(1);
            
            logger.debug("reference buffer: {}", HexUtils.byteArrayToHex(toLeBytes));
            logger.debug("command   buffer: {}", HexUtils.byteArrayToHex(wrCommand));
            
            assertTrue(
            	wrCommand != null && 
                wrCommand.length == 6 &&
            	wrCommand[0] == (byte) 0xA1 && 
            	wrCommand[1] == toLeBytes[0] &&
               	wrCommand[2] == toLeBytes[1] &&
              	wrCommand[3] == toLeBytes[2] &&
              	wrCommand[4] == toLeBytes[3] &&
              	wrCommand[5] == gpioFmpMode.value
       		);

   	    } 
        catch (Exception e) {
   			
           	logger.error("Ooops, an unexpected Exception flew by ...", e);
   			fail("Unexpected Exception");
   			
   		} // yrt
		
	} // testSetStm32Timing()
	

} // ssalc
