/*
 * MIT License
 * 
 * Copyright (c) 2018 Fabvalaaah - fabvalaaah@laposte.net
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * DONATION:
 * As I share these sources for commercial use too, maybe you could consider
 * sending me a reward (even a tiny one) to my Ethereum wallet at the address
 * 0x1fEaa1E88203cc13ffE9BAe434385350bBf10868
 * If so, I would be forever grateful to you and motivated to keep up the good
 * work for sure :oD Thanks in advance !
 * 
 * FEEDBACK:
 * You like my work? It helps you? You plan to use/reuse/transform it? You have
 * suggestions or questions about it? Just want to say "hi"? Let me know your
 * feedbacks by mail to the address fabvalaaah@laposte.net
 * 
 * DISCLAIMER:
 * I am not responsible in any way of any consequence of the usage
 * of this piece of software. You are warned, use it at your own risks.
 */

 /* 
 * File:   SerialListener.java
 * Author: Fabvalaaah
 *
 * 01/14/2018
 */
package serialmqttgateway;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import serialmqttgateway.exceptions.MQTTConnectorPublishException;
import serialmqttgateway.exceptions.SerialListenerInitException;

public class SerialListener implements SerialPortEventListener {

    private static final String PORT_NAMES[] = {
        "/dev/tty.usbserial-A9007UX1", // Mac OS X
        "/dev/ttyACM0", // Linux (Debian-based) - Raspberry Pi
        "/dev/ttyUSB0", // Linux (not Debian-based)
        "COM3", // Windows
    };
    private static final String SELECTED_PORT_NAME = "/dev/ttyACM0";
    private static final int OPENING_TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    private final MQTTConnector mc;
    private SerialPort serialPort;
    private BufferedReader input;

    public SerialPort getSerialPort() {
        return this.serialPort;
    }

    public SerialListener(MQTTConnector mc) throws SerialListenerInitException {
        this.mc = mc;

        this.init();
    }

    private CommPortIdentifier getComPort() {
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId
                    = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    return currPortId;
                }
            }
        }

        return null;
    }

    private void init() throws SerialListenerInitException {
        System.setProperty("gnu.io.rxtx.SerialPorts", SELECTED_PORT_NAME);

        CommPortIdentifier comPort = getComPort();

        if (null == comPort) {
            System.err.println("Could not find the serial port");
            throw new serialmqttgateway.exceptions.SerialListenerInitException(
                    new Exception("Could not find the serial port"));
        }

        try {
            serialPort = (SerialPort) comPort.open(this.getClass().getName(),
                    OPENING_TIME_OUT);

            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            input = new BufferedReader(new InputStreamReader(
                    serialPort.getInputStream()));

            serialPort.addEventListener(this);

            serialPort.notifyOnDataAvailable(true);
        } catch (PortInUseException | UnsupportedCommOperationException |
                IOException | TooManyListenersException ex) {
            throw new serialmqttgateway.exceptions.SerialListenerInitException(
                    ex);
        }
    }

    public synchronized void close() {
        if (null != serialPort) {
            serialPort.removeEventListener();
            serialPort.close();
        }

        if (null != input) {
            try {
                input.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                throw new Error(ex.getMessage());
            }
        }
    }

    @Override
    public synchronized void serialEvent(SerialPortEvent spe) {
        if (SerialPortEvent.DATA_AVAILABLE == spe.getEventType()) {
            String inputLine = null;
            try {
                inputLine = input.readLine();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
            try {
                this.mc.publish(inputLine);
            } catch (MQTTConnectorPublishException ex) {
                System.err.println(new StringBuilder("Could not send \"")
                        .append(inputLine).append("\" to MQTT").toString());
            }
        }
    }
}
