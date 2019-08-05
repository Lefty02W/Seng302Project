package repository;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.Artist;
import models.ArtistProfile;
import models.Destination;
import models.TreasureHunt;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
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
     * @return the new Artist id
     */
    public CompletionStage<Integer> insert(Artist artist, List<ArtistProfile> artistProfiles) {
        return supplyAsync(() -> {

            ebeanServer.insert(artist);

            //Manually applying linkage to profiles.
            for(ArtistProfile artistProfile : artistProfiles) {
                ebeanServer.insert(artistProfile);
            }
            return artist.getArtistId();
        }, executionContext);
    }

    /**
     * Method to return all of a users artists
     * @param userId, the id of the user
     * @return Artists, an ArrayList of all artists that user is a part of.
     */
    public List<Artist> getAllUserArtists(int userId) {

        List<ArtistProfile> artistProfiles = new ArrayList<>(ebeanServer.find(ArtistProfile.class)
                .where()
                .eq("soft_delete", 0)
                .eq("profile_id", userId)
                .findList());

        List<Artist> artists = new ArrayList<>();
        for(ArtistProfile artistProfile : artistProfiles) {
            artists.add(ebeanServer.find(Artist.class)
                    .where()
                    .eq("soft_delete", 0)
                    .eq("artist_id", artistProfile.getAPArtistId())
                    .findOne());
        }
        return artists;
    }


}
