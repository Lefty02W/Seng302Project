package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Artist;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class ArtistRepository {


    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;


    /**
     * Ebeans injector constructor method for Artist repository.
     *
     * @param ebeanConfig The ebeans config which the ebean server will be supplied from
     * @param executionContext the database execution context object for this instance.
     */
    @Inject
    public ArtistRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext) {

        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;


    }

    /**
     * Inserts an Artist object into the ebean database server
     *
     * @param artist Artist object to insert into the database
     * @return the Artist id
     */
    public CompletionStage<Integer> insert(Artist artist) {
        return supplyAsync(() -> {
            ebeanServer.insert(artist);
            return artist.getArtistId();
        }, executionContext);
    }


}
