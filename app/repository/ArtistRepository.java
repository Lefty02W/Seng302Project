package repository;

import io.ebean.*;
import models.Artist;
import models.ArtistCountry;
import models.ArtistProfile;
import models.PassportCountry;
import play.db.ebean.EbeanConfig;
import play.db.ebean.Transactional;
import utility.Country;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class ArtistRepository {


    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final PassportCountryRepository passportCountryRepository;
    private final GenreRepository genreRepository;

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
        this.passportCountryRepository = new PassportCountryRepository(ebeanConfig, executionContext);
        this.genreRepository = new GenreRepository(ebeanConfig, executionContext);
    }


    /**
     * Get the all of the artists currently registered
     *
     * @return Artist, list of all Artist
     */
    public List<Artist> getAllArtists() {
        List<Artist> artistList = new ArrayList<>(ebeanServer.find(Artist.class)
                .where()
                .eq("soft_delete", 0)
                .findList());
        List<Artist> outputList = new ArrayList<>();
        for( Artist artist : artistList) {
            outputList.add(populateArtist(artist));
        }
        return outputList;
    }



    /**
     * Inserts an Artist object into the ebean database server
     *
     * @param artist Artist object to insert into the database
     * @return the new Artist id
     */
    public CompletionStage<Integer> insert(Artist artist) {
        return supplyAsync(() -> {

            ebeanServer.insert(artist);
            // Adding artist countries to artist_country in the database
            System.out.println("Checking for all the countrys in a list: " + artist.getCountryList());
            for (Integer countryId : artist.getCountryList()) {
                System.out.println("FROM THE INSERT: " + countryId);
                supplyAsync(() -> {
                    ebeanServer.insert(new ArtistCountry(artist.getArtistId(), countryId));
                    return null;
                });
            }

            return artist.getArtistId();
        }, executionContext);
    }

    /** Checks if the artist to add is a duplicate of an existing artist
     *
     */
    public CompletionStage<Boolean> checkDuplicate(String artistName) {
        return supplyAsync(() -> {
            Artist artist = ebeanServer.find(Artist.class)
                    .where().eq("artist_name", artistName)
                    .findOne();
            return artist != null;
        }, executionContext);
    }

    /**
     * Inserts ArtistProfile object into the ebean database server for link table.
     *
     * @param artistProfile ArtistProfile object to insert into the database
     * @return the new Artist id
     */
    public CompletionStage<Integer> insertProfileLink(ArtistProfile artistProfile) {
        return supplyAsync(() -> {

            ebeanServer.insert(artistProfile);
            return artistProfile.getAPArtistId();


        }, executionContext);
    }

    /**
     * Method to populate a artist with all linking table data eg genre and country
     * @param artist Artist to be have added linking table data
     * @return Artist that has had genre and country added
     */
    private Artist populateArtist(Artist artist) {
        Map<Integer, PassportCountry> countries = new HashMap<>();
        Optional<Map<Integer, PassportCountry>> countryMap = getCountryList(artist.getArtistId());
        if (countryMap.isPresent()) {
            countries = countryMap.get();
        }
        artist.setCountry(countries);
        //TODO fix below function
       // artist.setGenre(genreRepository.getArtistGenres(artist.getArtistId()));
        return artist;
    }
    /**
     * Helper function to get country of an artist
     * @param artistId id of the artist to find country for
     */
    private Optional<Map<Integer, PassportCountry>> getCountryList(Integer artistId) {
        String qry = "Select * from artist_country where artist_id = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).setParameter(1, artistId).findList();
        Map<Integer, PassportCountry> country = new TreeMap<>();
        for (SqlRow aRowList : rowList) {
            country.put(aRowList.getInteger("country_id"), passportCountryRepository.findById(aRowList.getInteger("artist_country_id")).get());
        }
        return Optional.of(country);
    }


    /**
     * Method to return all of a users artists
     * @param userId, the id of the user
     * @return Artists, an ArrayList of all artists that user is a part of.
     */
    public List<Artist> getAllUserArtists(int userId) {

        List<ArtistProfile> artistProfiles = new ArrayList<>(ebeanServer.find(ArtistProfile.class)
                .where()
                //.eq("soft_delete", 0)
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


    /**
     * Sets the artists approved flag to 1, this allows the artist to fully access the application
     *
     * @param artistId Id of the artist to approve
     * @return Void completion stage
     */
    public CompletionStage<Void> setArtistAsVerified(int artistId) {
        return supplyAsync(() -> {
            ebeanServer.update(Artist.class).set("verified", 1).where().eq("artist_id", Integer.toString(artistId));
            return null;
        });
    }


    /**
     * Removes an artist entry from the database using a passed artist id
     *
     * @param artistId Id of the artist to delete
     * @return Void completion stage
     */
    public CompletionStage<Void> deleteArtist(int artistId) {
        return supplyAsync(() -> {
            ebeanServer.find(Artist.class).where().eq("artist_id", Integer.toString(artistId)).delete();
            return null;
        });
    }


    /**
     * Inserts a new entry into the artist_profile table linking the profile to the artist
     *
     * @param artistId id of artist to link
     * @param profileId id of profile to link
     * @return Void CompletionStage
     */
    public CompletionStage<Void> addProfileToArtist(int artistId, int profileId) {
        return supplyAsync(() -> {
            ebeanServer.insert(new ArtistProfile(artistId, profileId));
            return null;
        });
    }


    /**
     * Removes an entry from the artist_profile table unlinking a profile from an artist
     *
     * @param artistId id of artist to link
     * @param profileId id of profile to link
     * @return Void CompletionStage
     */
    public CompletionStage<Void> removeProfileFromArtist(int artistId, int profileId) {
        return supplyAsync(() -> {
            ebeanServer.find(ArtistProfile.class)
                    .where()
                    .eq("artist_id", artistId)
                    .eq("profile_id", profileId)
                    .delete();
            return null;
        });
    }

    /**
     * Method to get all artist profiles that have not yet been verified so the admin can either verify or remove the
     * profile
     * @return
     */
    public List<Artist> getInvalidArtists(){
        return new ArrayList<>(ebeanServer.find(Artist.class)
                .where().eq("verified", 0).findList());
    }


    /**
     *
     * @param artistId
     * @param newArtist
     * @return
     */
    public CompletionStage<Integer> editArtistProfile(Integer artistId, Artist newArtist) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Artist targetArtist = ebeanServer.find(Artist.class).setId(artistId).findOne();
            if (targetArtist != null) {
                targetArtist.setArtistName(newArtist.getArtistName());
                targetArtist.setBiography(newArtist.getBiography());
                targetArtist.setFacebookLink(newArtist.getFacebookLink());
                targetArtist.setSpotifyLink(newArtist.getSpotifyLink());
                targetArtist.setWebsiteLink(newArtist.getWebsiteLink());
                targetArtist.setWebsiteLink(newArtist.getWebsiteLink());
                targetArtist.setTwitterLink(newArtist.getTwitterLink());
                targetArtist.update();
                txn.commit();
//                artistCountryRepository.removeAll(artistId);
//                for (String artistCountry : newArtist.getCountryList()) {
//                    artistCountryRepository.insertCountry(new Country(artistCountry), artistId);
//                }
//                genreRepository.removeAll(artistId);
//                for (String genreId : newArtist.getGenreList()) {
//                    genreRepository.insertArtistGenre(artistId, genreId);
//                }
            }
            return artistId;
        });
    }

    /**
     * Database method to query for an artist that match the search parameters
     * @param name
     * @param genre
     * @param country
     * @param followed
     * @return
     */
    public List<Artist> searchArtist(String name, String genre, String country, int followed){
        String queryString = "SELECT * FROM artist " +
                "JOIN artist_genre ON artist_genre.artist_id = artist.artist_id " +
                "JOIN music_genre ON music_genre.genre_id = artist_genre.genre_id " +
                "JOIN artist_country ON artist_country.artist_id = artist.artist_id " +
                "JOIN passport_country ON passport_country.passport_country_id = artist_country.country_id";
        boolean namePresent = false;
        boolean genrePresent = false;
        if (!name.equals("")){
            queryString += "WHERE artist_name = ?";
            namePresent = true;
        }
        if (!genre.equals("")){
            if (namePresent){
                queryString += "AND genre = ?";
            } else {
                queryString += "WHERE genre = ?";
                genrePresent = true;
            }
        }
        if (!country.equals("")){
            if(namePresent || genrePresent){
                queryString += "AND passport_name = ?";
            } else {
                queryString += "WHERE passport_name = ?";
            }
        }
        SqlQuery sqlQuery = ebeanServer.createSqlQuery(queryString);
        if (!name.equals("")){
            sqlQuery.setParameter(1, name);
        }
        if (!genre.equals("")){
            if (namePresent){
                sqlQuery.setParameter(2,genre);
            } else{
                sqlQuery.setParameter(1, genre);
            }
        }
        if (!country.equals("")){
            if (namePresent && genrePresent){
                sqlQuery.setParameter(3,country);
            } else if (namePresent || genrePresent){
                sqlQuery.setParameter(2,country);
            } else {
                sqlQuery.setParameter(1,country);
            }
        }

        // TODO: 8/08/19 turn into another function
        List<SqlRow> foundRows = sqlQuery.findList();
        List<Artist> foundArtists = new ArrayList<>();
        if (!foundRows.isEmpty()){
            for (SqlRow sqlRow : foundRows){
                foundArtists.add(populateArtist(new Artist(sqlRow.getInteger("artist_id"), sqlRow.getString("artist_name")
                        , sqlRow.getString("biography"), sqlRow.getString("facebook_link")
                        , sqlRow.getString("instagram_link"), sqlRow.getString("spotify_link")
                        , sqlRow.getString("twitter_link"), sqlRow.getString("website_link")
                        , sqlRow.getInteger("soft_delete"))));
            }
        }

        return foundArtists;
    }
}
