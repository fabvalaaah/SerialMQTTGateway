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
 * File:   MQTTConnector.java
 * Author: Fabvalaaah
 *
 * 01/14/2018
 */
package serialmqttgateway;

import serialmqttgateway.exceptions.MQTTConnectorInitException;
import serialmqttgateway.exceptions.MQTTConnectorPublishException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTConnector {

    private final String id = "Arduino_to_MQTT"; // MQTT broker "name"
    private final String port = "1883"; // MQTT broker port
    private String ip = "127.0.0.1"; // MQTT broker IP
    private String channel = "arduino/pulsar"; // MQTT broker channel

    private MqttClient client;

    public MQTTConnector() throws MQTTConnectorInitException {
        this.init();
    }

    public MQTTConnector(String ip, String channel)
            throws MQTTConnectorInitException {
        if (null == ip || null == channel || ip.isEmpty()
                || channel.isEmpty()) {
            System.out.println(
                    "Missing argument(s): Switching to default values"
            );
        } else {
            this.ip = ip;
            this.channel = channel;
        }

        this.init();
    }

    private void init() throws MQTTConnectorInitException {
        try {
            client = new MqttClient(new StringBuilder("tcp://").append(this.ip)
                    .append(":").append(this.port).toString(), this.id);
            // Connection test
            client.connect();
            client.disconnect();
            // -------
        } catch (MqttException ex) {
            throw new MQTTConnectorInitException(ex);
        }
    }

    public void publish(String message) throws MQTTConnectorPublishException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        try {
            client.connect();
            client.publish(this.channel, mqttMessage);
            client.disconnect();
        } catch (MqttException ex) {
            throw new MQTTConnectorPublishException(ex);
        }
    }
}
