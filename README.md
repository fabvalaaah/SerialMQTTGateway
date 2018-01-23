# SerialMQTTGateway
Serial to MQTT gateway in Java.

This is a Java NetBeans project, compiled with the Open JDK 1.8.0_151 and tested
with Ubuntu Linux 16.04.5 LTS x64, an Arduino Duemilanove plugged on the
computer running the gateway, and a Mosquitto MQTT broker running on a third
party machine. It relies on the RXTX library v2.1-7 to listen to the serial
port, and on the Paho client library v1.1.0 to interact with MQTT.

This piece of software listens to a (USB emulated) serial port and forwards
everything it reads from it to an MQTT broker. In my tests, I put a
potentiometer on the Arduino that produced integer values between 0 and 700. In
case of failure from getting values from the serial port, this program switch to
a simulator mode by sending random integers (on the same range) to the MQTT
broker.

Usage: It takes 2 command arguments at execution. The first one is the IP of the
MQTT broker, the second one is the MQTT channel to write to. There are default
values if the command arguments are missing.

Tips:
- Use "dmesg" on Linux to get the serial port ID.
- If the port identifiers are empty, that's certainly due to a privilege issue.
Just launch the gateway as root to solve the problem.

DONATION:
As I share these sources for commercial use too, maybe you could consider
sending me a reward (even a tiny one) to my Ethereum wallet at the address
0x1fEaa1E88203cc13ffE9BAe434385350bBf10868
If so, I would be forever grateful to you and motivated to keep up the good work
for sure :oD Thanks in advance !

FEEDBACK:
You like my work? It helps you? You plan to use/reuse/transform it? You have
suggestions or questions about it? Just want to say "hi"? Let me know your
feedbacks by mail to the address fabvalaaah@laposte.net

DISCLAIMER:
I am not responsible in any way of any consequence of the usage of this piece of
software. You are warned, use it at your own risks.