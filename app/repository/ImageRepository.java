package repository;

import io.ebean.*;
import models.Image;
import models.Profile;
import play.db.ebean.EbeanConfig;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;


public class ImageRepository {

    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public ImageRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext){
        this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
        this.executionContext = executionContext;
    }

    public CompletionStage<Integer> insert(Image image){
        return supplyAsync(() -> {
            ebeanServer.insert(image);
            return image.getImageId();
        }, executionContext);
    }

}
