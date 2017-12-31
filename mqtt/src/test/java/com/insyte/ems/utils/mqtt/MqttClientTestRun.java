package com.insyte.ems.utils.mqtt;

import io.vertx.core.Launcher;

public class MqttClientTestRun {
    public static void main(final String[] args) {
        Launcher.executeCommand("run", MqttClientVerticle.class.getName());
    }
}
