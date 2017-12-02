package com.insyte.ems.utils.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MqttVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttVerticle.class);

    @Override
    public void start() throws Exception {
        startMqttServer();
    }

    private void startMqttServer(){
        MqttServer mqttServer = MqttServer.create(vertx);
        mqttServer.endpointHandler(endpoint -> {
            // shows main connect info
            System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());

            if (endpoint.auth() != null) {
                System.out.println("[username = " + endpoint.auth().userName() + ", password = " + endpoint.auth().password() + "]");
            }
            if (endpoint.will() != null) {
                System.out.println("[will topic = " + endpoint.will().willTopic() + " msg = " + endpoint.will().willMessage() +
                        " QoS = " + endpoint.will().willQos() + " isRetain = " + endpoint.will().isWillRetain() + "]");
            }
            addHandlers(endpoint);

            System.out.println("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");
            // accept connection from the remote client
            endpoint.accept(false);

        })
            .listen(ar -> {
                if (ar.succeeded()) {
                    System.out.println("MQTT server is listening on port " + ar.result().actualPort());
                } else {
                    System.out.println("Error on starting the server");
                    ar.cause().printStackTrace();
                }
            });
    }

    private void addHandlers(MqttEndpoint endpoint){
        endpoint.disconnectHandler(v -> {
            System.out.println("Received disconnect from client");
        });

        endpoint.publishHandler(message -> {
            System.out.println("Just received message [" + message.payload().toString(Charset.defaultCharset()) + "] with QoS [" + message.qosLevel() + "]");
            if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                endpoint.publishAcknowledge(message.messageId());
            } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                endpoint.publishRelease(message.messageId());
            }

        }).publishReleaseHandler(messageId -> {
            endpoint.publishComplete(messageId);
        });

        endpoint.subscribeHandler(subscribe -> {
            List<MqttQoS> grantedQosLevels = new ArrayList<>();
            for (MqttTopicSubscription s: subscribe.topicSubscriptions()) {
                System.out.println("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
                grantedQosLevels.add(s.qualityOfService());
            }
            // ack the subscriptions request
            endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
        });

        endpoint.unsubscribeHandler(unsubscribe -> {

            for (String t: unsubscribe.topics()) {
                System.out.println("Unsubscription for " + t);
            }
            // ack the subscriptions request
            endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
        });

        endpoint.pingHandler(v -> {
            System.out.println("Ping received from client");
        });
    }
}
