package io.abner.vertxyz;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe base para vertices que usa uma estratégia única baseada em variáveis de ambiente para carregamento de
 * configurações do vértice que permite que o artefato gerado seja utilizado em ambiente desenvolvimento,
 * testes, ou deploy nos servidores
 * Utilizando dotenv é possível definir configurações padrões com arquivos .env colocados nas pastas src/main/resources
 * e src/test/resources. Essas configurações padrões nos resources pode ser sobrescrita nos ambientes produtivos ou no
 * de ci através de variáveis de ambiente
 *
 * @author Ábner Oliveira [ abner.oliveira at serpro.gov.br ]
 */
public abstract class AbstractVerticle extends io.vertx.core.AbstractVerticle{

    private JsonObject config;

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    private JsonObject loadEnvConfig() {
        return DotEnvToJson.load(this.getClass().getClassLoader());
    }

    public void loadConfig(Handler<AsyncResult<JsonObject>> loadConfigHandler) {
        loadEnvConfig();

        // lê do dotenv (arquivo .env em resources ou working directory e carrega para um store json
        ConfigStoreOptions envFileStore = new ConfigStoreOptions().setConfig(this.loadEnvConfig()).setType("json");

        // config store que carrega do system ENV
        ConfigStoreOptions envStore = new ConfigStoreOptions().setType("env");

        // ConfigRetriever montado para primeiro ler dos arquivos .env, mas sobrescreve
        // com os valores do ENV caso estejam definidas
        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(envFileStore)
                .addStore(envStore);

        ConfigRetriever configRetriever = ConfigRetriever.create(vertx, options);

        // carrega as configurações
        configRetriever.getConfig(loadConfigHandler);
    }

    public void start(Future<Void> startupFuture) {
        // carrega as configurações e depois processegue com a inicialização do
        // verticle
        loadConfig(h -> {
            if (h.succeeded()) {
                this.config = new JsonObject();
                // normalizes all env vars to string, because dotEnv read numbers as Double but
                // vertx config store of type env does not do the same
                h.result().fieldNames().forEach(fieldName -> {
                  this.config.put(fieldName, h.result().getValue(fieldName).toString().replaceAll("\\.0", ""));
                });
                try {
                    this.onStart(startupFuture);
                } catch(Exception e) {
                    LOGGER.error("failed to startup the verticle " + this.getClass().getSimpleName(), e);
                    startupFuture.fail(e);
                }
            } else {
                startupFuture.fail(new Exception("Could not load config", h.cause()));
            }
        });
    }

    public JsonObject config() {
        return this.config;
    }

    /**
     * Implementations should implements this and should use the Future startupFuture
     * to indicate the verticle initialization is complete or if it failed
     * @param startupFuture the future object: implementations should call .complete() at it
     * to indicate the startup is completed
     */
    protected abstract void onStart(Future<Void> startupFuture);
}
