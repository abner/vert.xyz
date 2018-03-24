package io.abner.vertxyz.base;

import com.github.shyiko.dotenv.DotEnv;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.Map;

public class DotEnvToJson {
    public static JsonObject load(ClassLoader classLoader) {
        Map<String, String> envMap = DotEnv.load(classLoader);
        return new JsonObject(Json.encode(envMap));
    }
}
