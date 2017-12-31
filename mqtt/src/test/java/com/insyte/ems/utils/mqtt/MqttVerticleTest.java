package com.insyte.ems.utils.mqtt;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Launcher;

public class MqttVerticleTest extends AbstractVerticle {
    public static void main(final String[] args) {
        Launcher.executeCommand("run", MqttVerticleTest.class.getName());
    }

    @Override
    public void start() throws Exception {
        deployVerticle(new MqttVerticle());
    }

    private void deployVerticle(AbstractVerticle verticle) {
        Future<String> deployment = Future.future();
        vertx.deployVerticle(verticle, deployment.completer());
    }
}
