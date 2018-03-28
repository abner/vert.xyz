package io.abner.vertxyz.example;

import io.abner.vertxyz.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
//import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticleExample extends AbstractVerticle {
    private static Logger LOGGER = LoggerFactory.getLogger(VerticleExample.class);
    private int httpPort;

    public static void main(String[] args) {
        VerticleExample verticle = new VerticleExample();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(verticle, result -> {
            if (result.succeeded()) {

                HttpClient client = vertx.createHttpClient(
                        new HttpClientOptions()
                                .setDefaultPort(verticle.getHttpPort())
                                .setDefaultHost("0.0.0.0")
                );
                client.get("/", h -> {
                    if (h.statusCode() == 200) {
                        h.bodyHandler(body -> {
                            LOGGER.info("Server respondeu com sucesso: " + body.toString());
                            System.exit(0);
                        });
                    }
                }).end();
            } else {
                LOGGER.error("Falhou ao iniciar example verticle!");
                System.exit(225);
            }
        });
    }

    @Override
    public void onStart(Future<Void> startupFuture) {
        try {

//            Router router = Router.router(vertx);
//
//            router.get("/").handler(routingContext -> {
//                routingContext.response().end("OK");
//            });

            HttpServer server = vertx.createHttpServer();

            final int httpPort = Integer.valueOf(config().getString("EXAMPLE_HTTP_PORT"));

            server.requestHandler(req -> {
                req.response().end("SERVER IS OK > Great!!!!");
            }).listen(httpPort, sh -> {
                if (sh.succeeded()) {
                    LOGGER.info("Server started at port: " + sh.result().actualPort());
                    this.httpPort = sh.result().actualPort();
                    startupFuture.complete();
                } else {
                    LOGGER.error("Failed to start vertx at port" + Integer.toString(httpPort));
                    startupFuture.fail(sh.cause());
                }
            });
        } catch (Exception e) {
            startupFuture.fail(e);
        }
    }

    public int getHttpPort() {
        return httpPort;
    }
}