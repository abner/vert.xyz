package io.abner.vertxyz;

import com.google.common.base.CaseFormat;
import io.abner.vertxyz.AbstractVerticle;
import io.abner.vertxyz.annotations.HttpServer;
import io.vertx.core.Future;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpServerRequest;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Classe para ser usada como base para criar verticles que irão servir simples requisições
 * @author Abner Oliveira [ abner.oliveira at serpro.gov.br ]
 */
public abstract class AbstractHttpServerVerticle extends AbstractVerticle {

    public final static String ENV_HTTP_PORT_FORMAT = "%s_HTTP_PORT";

    private HttpServer httpServerAnnotation;

    protected int httpPort;

    private Logger logger;

    protected Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(this.getClass());
        }
        return logger;
    }

    /**
     * @return the number of the port effectively binded to the http server created
     */
    protected int getHttpPort() {
        return this.httpPort;
    }

    @Override
    protected void onStart(Future<Void> startupFuture) {
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();

        server
                .connectionHandler(this::connectionHandler)
                .requestHandler(this::requestHandler)
                .listen(getConfiguredHttpPort(), sh -> {
                    if (sh.succeeded()) {
                        this.httpPort = sh.result().actualPort();
                        getLogger().info("Server  started at port: " + sh.result().actualPort());
                        startupFuture.complete();
                    } else {
                        getLogger().error("Failed to start vertx at port " + Integer.toString(getConfiguredHttpPort()), sh.cause());
                        startupFuture.fail(sh.cause());
                    }
                });
    }


    private void connectionHandler(HttpConnection connection) {
        this.onConnection(connection);
    }

    private void requestHandler(HttpServerRequest request) {
        this.onRequest(request);
    }

    /***
     * Called when a request is issued to this http server
     * @param request the request objecct
     */
    protected abstract void onRequest(HttpServerRequest request);

    // would be override if you want

    /**
     * Called when connection occurs
     *
     * @param connection the http connection which was just being open
     */
    protected abstract void onConnection(HttpConnection connection);

    /**
     * Default implementation returns Http port from either [SERVER_NAME in caps]
     * defined using servername in annotation HttpServer, or the defaultPort defined at the same annotation
     *
     * @return http port
     */
    protected int getConfiguredHttpPort() {
        String httpPortEnvVarName = CaseFormat.UPPER_CAMEL.to(
                CaseFormat.UPPER_UNDERSCORE, this.getServerName()
        );

        httpPortEnvVarName = String.format(ENV_HTTP_PORT_FORMAT, httpPortEnvVarName);

        int httpPort = httpServerAnnotation.defaultPort();

        if (this.config().containsKey(httpPortEnvVarName)) {
            httpPort = NumberUtils.toInt(this.config().getString(httpPortEnvVarName), 0);
        }
        return httpPort;
    }

    /**
     * Returns the HttpServer annotation decorated in the implementation class
     *
     * @return @link HttpServer
     */
    private HttpServer getHttpServerAnnotation() {
        if (httpServerAnnotation == null) {
            if (this.getClass().isAnnotationPresent(HttpServer.class)) {
                httpServerAnnotation = this.getClass().getAnnotation(HttpServer.class);
            }
        }
        return httpServerAnnotation;
    }

    /***
     *
     * @return the server name assigned by the HttpServer annotation
     */
    protected String getServerName() {
        if (getHttpServerAnnotation() != null) {
            return httpServerAnnotation.value();
        } else {
            return this.getClass().getSimpleName();
        }
    }
}
