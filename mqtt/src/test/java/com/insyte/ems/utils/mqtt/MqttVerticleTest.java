package com.insyte.ems.utils.mqtt;

import io.vertx.core.Launcher;

public class MqttVerticleTest {
    public static void main(final String[] args) {
        Launcher.executeCommand("run", MqttVerticle.class.getName());
    }
}
