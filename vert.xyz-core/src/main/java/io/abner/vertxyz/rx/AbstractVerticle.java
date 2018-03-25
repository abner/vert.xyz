package io.abner.vertxyz.rx;

import io.vertx.core.Context;
import io.vertx.core.Vertx;

public abstract class AbstractVerticle extends io.abner.vertxyz.AbstractVerticle {

    // Shadows the AbstractVerticle#vertx field
    protected io.vertx.rxjava.core.Vertx vertx;

    public AbstractVerticle() {
        super();
    }

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        this.vertx = new io.vertx.rxjava.core.Vertx(vertx);
    }
}
