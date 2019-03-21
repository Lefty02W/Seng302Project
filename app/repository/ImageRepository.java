package repository;

import io.ebean.*;
import models.Image;
import models.Image;
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
            try {
                ebeanServer.insert(image);
                System.out.println("SUCCESS. ID:" + image.getImageId());
                System.out.println("Type: " + image.getVisible());

            } catch (Exception e) {
                System.out.print(e);
            }
            return image.getImageId();
        }, executionContext);
    }

}
