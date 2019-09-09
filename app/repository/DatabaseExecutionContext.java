package repository;

import akka.actor.ActorSystem;
import play.db.ebean.EbeanDynamicEvolutions;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

/**
 * Custom execution context, so that blocking database operations don't
 * happen on the rendering thread pool.
 *
 * @link https://www.playframework.com/documentation/latest/ThreadPools
 */
public class DatabaseExecutionContext extends CustomExecutionContext {

    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;

    @Inject
    public DatabaseExecutionContext(ActorSystem actorSystem, EbeanDynamicEvolutions ebeanDynamicEvolutions) {
        super(actorSystem, "database.dispatcher");
        this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
    }
}
