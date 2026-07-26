# USB-I2C_Bridge
This repository contains libraries for USB to I2C bridges which give the ability to connect I2C devices to a PC or Laptop. Thereby it is possible to cross develop I2C device software for target  systems like Raspberry Pi, Banana Pi, Odroid, e.t.c.

Currently there exists a library for only one vendor, but there are some others on the market and maybe, will come later.

## Pololu_USB-I2C_Bridge
Supports two of Pololus products:
1. [Pololu Isolated USB-to-I²C Adapter (item #: 5396)](https://www.pololu.com/product/5396)
2. [Pololu Isolated USB-to-I²C Adapter with Isolated Power (item #: 5397)](https://www.pololu.com/product/5397)

For a deep understanding of this library, please read the [Pololu Isolated USB-to-I²C Adapter User’s Guide](https://www.pololu.com/docs/0J89) for the relevant product and the [command reference](https://www.pololu.com/docs/0J89/7).

Furthermore, it's recommended to consult [jSerialCom documentation](https://fazecast.github.io/jSerialComm/) for a good understanding of the serial port behaviour.

### Usage

The API exposes several methods which should be (hopefully) self explantory:

```
	public USB_I2C_BridgeImpl(final String aBridgeName) throws USB_I2C_Exception
	
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
```

Please have a look to the file __Example_DS1621__ in the __./examples/example__ folder to get an idea how to use this library.

