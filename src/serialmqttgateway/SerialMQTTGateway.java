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
 * File:   SerialMQTTGateway.java
 * Author: Fabvalaaah
 *
 * 01/14/2018
 */
package serialmqttgateway;

import java.util.Random;
import serialmqttgateway.exceptions.MQTTConnectorInitException;
import serialmqttgateway.exceptions.MQTTConnectorPublishException;
import serialmqttgateway.exceptions.SerialListenerInitException;

public class SerialMQTTGateway {

    private static final int LISTENING_THREAD_KEEPALIVE = 3600000; // ms
    private static final int TEMPO_RND = 10000; // ms
    private static final int MAX_RND = 401; // 401 excluded

    public static void main(String[] args) {
        MQTTConnector mc = null;
        try {
            mc = new MQTTConnector(args[0], args[1]);
        } catch (MQTTConnectorInitException ex) {
            ex.printStackTrace(System.err);
            throw new Error(ex);
        }

        try {
            SerialListener arduinoListener = new SerialListener(mc);

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(LISTENING_THREAD_KEEPALIVE);
                    } catch (InterruptedException ex1) {
                        arduinoListener.close();
                    }
                }
            };
            t.start();

            System.out.println(new StringBuilder(
                    "Started listening serial on port ")
                    .append(arduinoListener.getSerialPort().toString())
                    .toString());
        } catch (SerialListenerInitException ex) {
            System.out.println(new StringBuilder(
                    "Started sending random data between O and ")
                    .append(MAX_RND - 1).toString());

            long startTime = System.currentTimeMillis();
            Random rand = new Random();
            while (System.currentTimeMillis() <= startTime
                    + LISTENING_THREAD_KEEPALIVE) {
                // Pseudo-random integer pick up between 0 and 400
                int rndValue = rand.nextInt(MAX_RND);
                try {
                    mc.publish("" + rndValue);
                } catch (MQTTConnectorPublishException ex2) {
                    System.err.println(new StringBuilder("Could not send \"")
                            .append(rndValue).append("\" to MQTT").toString());
                }
                // -------
                try {
                    Thread.sleep(TEMPO_RND);
                } catch (InterruptedException ex3) {
                    System.exit(0);
                }
            }
        }
    }
}
