package com.insyte.ems.utils.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

public class MqttClientVerticle extends AbstractVerticle {

    private static final String MQTT_TOPIC = "/variables/#";
    private static final String MQTT_MESSAGE = "Hello from Vert.x MQTT Client";
    private static final String BROKER_HOST = "localhost";
    private static final int BROKER_PORT = 1883;

    @Override
    public void start() {
        MqttClientOptions options = new MqttClientOptions();
        options.setPassword("pas");
        options.setUsername("log");
        // specify broker host
        //options.setHost("iot.eclipse.org");
        //options.setHostnameVerificationAlgorithm("iot.eclipse.org");
        // specify max size of message in bytes
        options.setMaxMessageSize(100_000_000);

        MqttClient client = MqttClient.create(vertx, options);

        client.connect(BROKER_PORT, BROKER_HOST, ch -> {
            if (ch.succeeded()) {
                System.out.println("Connected to a server");

                client.publish(
                        MQTT_TOPIC,
                        Buffer.buffer(MQTT_MESSAGE),
                        MqttQoS.AT_MOST_ONCE,
                        false,
                        false,
                        s -> client.disconnect(d -> System.out.println("Disconnected from server")));

                client.subscribe("#", 0);
            } else {
                System.out.println("Failed to connect to a server");
                System.out.println(ch.cause());
            }
        });


        /*client.publishHandler(s -> {
            try {
                String message = new String(s.payload().getBytes(), "UTF-8");
                System.out.println(String.format("Receive message with content: \"%s\" from topic \"%s\"", message, s.topicName()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });


        client.connect(BROKER_PORT, BROKER_HOST, s -> {
            // subscribe to all subtopics
            client.subscribe(MQTT_TOPIC, 0);
        });*/
    }
}
