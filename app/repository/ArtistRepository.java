package repository;

import io.ebean.*;
import models.*;
import play.data.Form;
import play.db.ebean.EbeanConfig;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

import static java.lang.Integer.parseInt;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Database access class for the artist table. Handles all database interactions with this table
 */
public class ArtistRepository {


    private final EbeanServer ebeanServer;
    private final DatabaseExecutionContext executionContext;
    private final PassportCountryRepository passportCountryRepository;
    private final GenreRepository genreRepository;
    private static final int PAGE_SIZE = 10;
    /**
     * Ebeans injector constructor method for Artist repository.
     *
     * @param ebeanConfig The ebeans config which the ebean server will be supplied from
     * @param executionContext the database execution context object for this instance.
     * @param passportCountryRepository passportCountryRepository required for initialisation
     */
    @Inject
    public ArtistRepository(EbeanConfig ebeanConfig, DatabaseExecutionContext executionContext, PassportCountryRepository passportCountryRepository) {

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
            artist.setGenre(new ArrayList<>());
            outputList.add(populateArtistAdmin(artist));
        }
        return outputList;
    }

    /**
     * Get the all of the artists currently registered and verfied does not fill with linking tables
     *
     * @return Artist, list of all Artist
     */
    public List<Artist> getAllVerfiedArtists() {
        return new ArrayList<>(ebeanServer.find(Artist.class)
                .where()
                .eq("soft_delete", 0)
                .eq("verified", 1)
                .findList());
    }

    /**
     * Get a single registered artist
     * @param artistID - The ID of the artists to retrieve
     * @return Artist, list of all Artist
     */
    public Artist getArtistById(Integer artistID) {
        return populateArtistAdmin(ebeanServer.find(Artist.class)
                .where()
                .eq("soft_delete", 0)
                .eq("artist_id", artistID)
                .findOne());
    }

    /**
     * Get all artists for an event
     * @param eventId id of the event
     * @return List of artists linked to the event
     */
    public List<Artist> getEventArtists(int eventId) {
        List<EventArtists> eventArtists = ebeanServer.find(EventArtists.class).where().eq("event_id", eventId).findList();
        List<Artist> artists = new ArrayList<>();
        Optional<Artist> artist;
        for (EventArtists eventArtist : eventArtists) {
            artist = Optional.ofNullable(ebeanServer.find(Artist.class).where().eq("artist_id", eventArtist.getArtistId()).findOne());
            if(artist.isPresent()) {
                artists.add(artist.get());
            }
        }
        return (artists);
    }


    /**
     * Inserts an Artist object into the ebean database server
     * and checks if the selected countries are in the database,
     * if they are not, the country is added to the database and added to
     * the artist country linking table
     *
     * @param artist Artist object to insert into the database
     * @return the new Artist id
     */
    public CompletionStage<Integer> insert(Artist artist) {
        return supplyAsync(() -> {
            ebeanServer.insert(artist);
            return artist.getArtistId();
        }, executionContext);
    }

    /** Checks if the artist to add is a duplicate of an existing artist
     * @param artistName Name of the artist the duplicate check is on
     * @return Completion stage boolean that returns true if there is an artist with the given name in the database
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
     * Method to populate a artist to add admins into artists used for admin page
     * @param artist Artist to be have added linking table data
     * @return Artist that has had genre and country added
     */
    public Artist populateArtistAdmin(Artist artist) {

        artist = populateArtist(artist);
        List<Integer> linkIds = ebeanServer.find(ArtistProfile.class).select("profileId").where().eq("artist_id", artist.getArtistId()).findSingleAttributeList();
        if (!linkIds.isEmpty()) {
            artist.setAdminsList(ebeanServer.find(Profile.class).where().idIn(linkIds).findList());
        } else {
            artist.setAdminsList(new ArrayList<>());
        }
        return artist;
    }

    /**
     * Method to populate a artist with all linking table data eg genre and country
     * @param artist Artist to be have added linking table data
     * @return Artist that has had genre and country added
     */
    public Artist populateArtist(Artist artist) {
     Map<Integer, PassportCountry> countries = new HashMap<>();
     Optional<Map<Integer, PassportCountry>> countryMap = getCountryList(artist.getArtistId());
     if (countryMap.isPresent()) {
       countries = countryMap.get();
     }

     artist.setCountry(countries);
        Optional<List<MusicGenre>> genreList = genreRepository.getArtistGenres(artist.getArtistId());
        if(genreList.isPresent() && !genreList.get().isEmpty()) {
            artist.setGenre(genreList.get());
        }

        artist.setFollowerCount(getNumFollowers(artist.getArtistId()));

        return artist;
    }
    /**
     * Helper function to get country of an artist
     * @param artistId id of the artist to find country for
     * @return country if one is present as an Optional
     */
    private Optional<Map<Integer, PassportCountry>> getCountryList(Integer artistId) {
        String qry = "Select * from artist_country where artist_id = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(qry).setParameter(1, artistId).findList();
        Map<Integer, PassportCountry> country = new TreeMap<>();
        Optional<PassportCountry> countryName;
        for (SqlRow aRowList : rowList) {
            countryName = passportCountryRepository.findById(aRowList.getInteger("country_id"));
            countryName.ifPresent(passportCountry -> country.put(aRowList.getInteger("country_id"), passportCountry));
        }
        return Optional.of(country);
    }


    /**
     * Method to return all of a users artists
     * @param userId, the id of the user
     * @return Artists, an ArrayList of all artists that user is a part of.
     */
    public List<Artist> getAllUserArtists(int userId) {
        List<Integer> artistIds = ebeanServer.find(ArtistProfile.class)
                .select("artistId")
                .where()
                .eq("profile_id", userId)
                .findSingleAttributeList();
        if (artistIds.isEmpty()) {
            return new ArrayList<>();
        }

        return ebeanServer.find(Artist.class)
                .where()
                .eq("soft_delete", 0)
                .idIn(artistIds)
                .findList();
    }


    /**
     * Sets the artists approved flag to 1, this allows the artist to fully access the application
     *
     * @param artistId Id of the artist to approve
     * @return Void completion stage
     */
    public CompletionStage<Void> setArtistAsVerified(int artistId) {
        return supplyAsync(() -> {
            ebeanServer.update(Artist.class).set("verified", 1).where().eq("artist_id", Integer.toString(artistId)).update();
            return null;
        });
    }

    /**
     * Method to get up to 50 artists at a time for lazy loading.
     * @param page starting at 0, the page is how many times we have requested artists.
     * @return A list of artists.
     */
    public List<Artist> getPagedArtists(int page) {

        List <Artist> returnArtistList = new ArrayList<>();
        List<Artist> artistList = ebeanServer.find(Artist.class).where()
                .setFirstRow(page * PAGE_SIZE)
                .setMaxRows(PAGE_SIZE)
                .orderBy("artist_name asc")
                .findPagedList().getList();
        for(Artist artist : artistList) {
            returnArtistList.add(populateArtist(artist));
        }
        return artistList;
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
    public List<Artist> getInvalidArtists() {
        return new ArrayList<>(ebeanServer.find(Artist.class)
                .where().eq("verified", 0).findList());
    }


    /**
     * Method returns all of the users followed artists
     *
     * @param profileId User if of the followed artists to return
     * @return Optional array list of artists followed by the user
     */
    public List<Artist> getFollowedArtists(int profileId) {
        List<Integer> artistIds = ebeanServer.find(FollowArtist.class)
                .select("artistId")
                .where()
                .eq("profile_id", profileId)
                .findSingleAttributeList();
        if (artistIds.isEmpty()){
            return Collections.emptyList();
        }
        return ebeanServer.find(Artist.class).where().idIn(artistIds).findList();
    }

    /**
     * Method returns all followed artist ids from a user
     *
     * @param profileId User id for the user followed artist
     * @return Optional array list of integers of the followed artist ids
     */
    public Optional<ArrayList<Integer>> getFollowedArtistIds(int profileId) {
        String updateQuery = "Select artist_id from follow_artist where profile_id = ?";
        List<SqlRow> rowList = ebeanServer.createSqlQuery(updateQuery).setParameter(1, profileId).findList();
        ArrayList<Integer> artIdList = new ArrayList<>();
        for (SqlRow aRowList : rowList) {
            int id = aRowList.getInteger("artist_id");
            artIdList.add(id);
        }
        return Optional.of(artIdList);
    }

    /**
     * Method to follow a artist for a user
     *
     * @param artId    Id of the entered artist
     * @param profileId Id of the entered profile
     * @return Optional array of integers of the followed artist id
     */
    public CompletionStage<Optional<ArrayList<Integer>>> followArtist(int artId, int profileId) {
        return supplyAsync(() -> {
            String updateQuery = "INSERT into follow_artist(profile_id, artist_id) values (?, ?)";
            SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
            query.setParameter(1, profileId);
            query.setParameter(2, artId);
            query.execute();
            return getFollowedArtistIds(profileId);
        });
    }

    /**
     * Method to allow a user to un-follow a given artist
     *
     * @param artId    Id of the artist to be un-followed
     * @param profileId Id of the user that wants to un-follow a artist
     * @return Optional list of integers for the followed artist ids
     */
    public CompletionStage<Optional<ArrayList<Integer>>> unfollowArtist(int artId, int profileId) {
        return supplyAsync(() -> {
            String updateQuery = "DELETE from follow_artist where profile_id = ? and artist_id =  ?";
            SqlUpdate query = Ebean.createSqlUpdate(updateQuery);
            query.setParameter(1, profileId);
            query.setParameter(2, artId);
            query.execute();
            return getFollowedArtistIds(profileId);
        });
    }

    /**
     * Updates an Artist object in the database by taking in an id of an already existing artist and changing its attributes
     * Also updates the corresponding artist countries and artist genres database tables
     *
     * @param artistId the id of the artist being edited
     * @param newArtist the artist object that will be edited
     * @return the same artist id of the artist object that got edited
     */
    public CompletionStage<Integer> editArtistProfile(Integer artistId, Artist newArtist, Form<Artist> artistForm, Integer currentUserId) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            Artist targetArtist = ebeanServer.find(Artist.class).setId(artistId).findOne();
            if (targetArtist != null) {
                targetArtist.setArtistName(newArtist.getArtistName());
                targetArtist.setBiography(newArtist.getBiography());
                targetArtist.setMembers(newArtist.getMembers());
                targetArtist.setFacebookLink(newArtist.getFacebookLink());
                targetArtist.setSpotifyLink(newArtist.getSpotifyLink());
                targetArtist.setWebsiteLink(newArtist.getWebsiteLink());
                targetArtist.setInstagramLink(newArtist.getInstagramLink());
                targetArtist.setTwitterLink(newArtist.getTwitterLink());
                targetArtist.update();
                txn.commit();

                newArtist.setArtistId(artistId);
                deleteAllSpecifiedEntriesForAnArtist(artistId, "artist_country");
                deleteAllSpecifiedEntriesForAnArtist(artistId, "artist_genre");
                deleteAllSpecifiedEntriesForAnArtist(artistId, "artist_profile");

                saveAdminArtistCountries(newArtist);
                saveAdminArtistGenres(newArtist, artistForm);
                saveAdminArtistAdmins(newArtist, artistForm, currentUserId);
            }

            return artistId;
        });
    }

    /**
     * Database method to query for an artist that match the search parameters
     * @param name name of artist
     * @param genre Genre of artist to be searched
     * @param country Country searched
     * @param followed 1 or 0 if followed or not
     * @return List of artists
     */
    public List<Artist> searchArtist(String name, String genre, String country, int followed, int created, int userId){
        if(name.equals("") && genre.equals("") && country.equals("") && followed == 0 && created == 0) {
            return getAllArtists();
        }
        String queryString = "SELECT DISTINCT artist.artist_id, artist.artist_name, artist.biography, artist.facebook_link, artist.instagram_link, artist.spotify_link, artist.twitter_link, artist.website_link, artist.soft_delete FROM artist " +
                "LEFT OUTER JOIN artist_genre ON artist_genre.artist_id = artist.artist_id " +
                "LEFT OUTER JOIN music_genre ON music_genre.genre_id = artist_genre.genre_id " +
                "LEFT OUTER JOIN artist_country ON artist_country.artist_id = artist.artist_id " +
                "LEFT OUTER JOIN passport_country ON passport_country.passport_country_id = artist_country.country_id " +
                "LEFT OUTER JOIN artist_profile ON artist.artist_id = artist_profile.artist_id " +
                "LEFT OUTER JOIN follow_artist ON artist.artist_id = follow_artist.artist_id ";
        boolean namePresent = false;
        boolean genrePresent = false;
        boolean countryPresent = false;
        if (!name.equals("")){
            queryString += "WHERE artist_name LIKE ? ";
            namePresent = true;
        }
        if (!genre.equals("")){
            if (namePresent){
                queryString += "AND genre = ? ";
                genrePresent = true;
            } else {
                queryString += "WHERE genre = ? ";
                genrePresent = true;
            }
        }
        if (!country.equals("")){
            if(namePresent || genrePresent){
                queryString += "AND passport_name = ? ";
                countryPresent = true;
            } else {
                queryString += "WHERE passport_name = ? ";
                countryPresent = true;
            }
        }

        if (followed == 1){
            if(namePresent || genrePresent || countryPresent){
                queryString += "AND follow_artist.profile_id = ? ";
            } else {
                queryString += "WHERE follow_artist.profile_id = ? ";
            }
        }

        if (created == 1){
            if(namePresent || genrePresent || countryPresent || followed == 1){
                queryString += "AND artist_profile.profile_id = ? ";
            } else {
                queryString += "WHERE artist_profile.profile_id = ? ";
            }
        }
        queryString += "LIMIT 100";

        int numberAdd = 0;

        SqlQuery sqlQuery = ebeanServer.createSqlQuery(queryString);
        if (!name.equals("")){
            sqlQuery.setParameter(numberAdd + 1, "%" + name + "%");
            numberAdd++;
        }
        if (!genre.equals("")){
            sqlQuery.setParameter(numberAdd + 1,genre);
            numberAdd++;
        }
        if (!country.equals("")){
            sqlQuery.setParameter(numberAdd + 1,country);
            numberAdd++;
        }

        if (followed == 1){
            sqlQuery.setParameter(numberAdd + 1,userId);
            numberAdd++;
        }

        if (created == 1){
            sqlQuery.setParameter(numberAdd + 1,userId);
        }

        List<SqlRow> foundRows = sqlQuery.findList();
        List<Artist> foundArtists = new ArrayList<>();
        if (!foundRows.isEmpty()){
            for (SqlRow sqlRow : foundRows){
                foundArtists.add(populateArtist(new Artist(sqlRow.getInteger("artist_id"), sqlRow.getString("artist_name")
                        , sqlRow.getString("biography"), sqlRow.getString("facebook_link")
                        , sqlRow.getString("instagram_link"), sqlRow.getString("spotify_link")
                        , sqlRow.getString("twitter_link"), sqlRow.getString("website_link")
                        , sqlRow.getInteger("soft_delete")
                        , new ArrayList<>())));
            }
        }
        return foundArtists;
    }



    /**
     * sets soft delete for a profile which eather deletes it or
     * undoes the delete
     * @param artistId The ID of the profile to soft delete
     * @param value, the value softDelete is to be set to
     * @return
     */
    public CompletionStage<Integer> setSoftDelete(int artistId, int value) {
        return supplyAsync(() -> {
            try {
                Artist targetArtist = ebeanServer.find(Artist.class).setId(artistId).findOne();
                if (targetArtist != null) {
                    targetArtist.setSoftDelete(value);
                    targetArtist.update();
                    return 1;
                } else {
                    return 0;
                }
            } catch(Exception e) {
                return 0;
            }
        }, executionContext);
    }

    /**
     * Function to get all countries of a given artist
     * @param artistId Id of the artists to get countries for
     * @return Map<Integer, PassportCountry> Map holding the country and key.
     */
    public Map<Integer, PassportCountry> getArtistCounties(int artistId) {
         List<ArtistCountry> artistCountries = ebeanServer.find(ArtistCountry.class)
                .where().eq("artist_id", artistId).findList();

         Map<Integer, PassportCountry> passportCountries = new HashMap<>();
         for (ArtistCountry artistCountry: artistCountries) {
             passportCountries.put(artistCountry.getCountryId(), ebeanServer.find(PassportCountry.class)
             .where().eq("passport_country_id", artistCountry.getCountryId()).findOne());
         }
         return passportCountries;
    }
    /**
     * Method to insert an artists country to the artist_country table
     * @param artistCountry artistCountry object to be added to the database
     * @return void CompletionStage
     */
    public CompletionStage<Void> addCountrytoArtistCountryTable(ArtistCountry artistCountry){
        return supplyAsync(() -> {
            ebeanServer.insert(artistCountry);
            return null;
        });
    }

    /**
     * Helper method that takes in a specified artist table  and artist id and deletes all entries for that particular
     * artist in the given artist table. Used for the admin update artist.
     *
     * @param id the id of the artist
     * @param table the intended artist table that all artist entries are going to be removed from
     * @return void CompletionStage
     */
    private CompletionStage<Void> deleteAllSpecifiedEntriesForAnArtist(int id, String table) {
        return supplyAsync(() -> {
            Transaction txn = ebeanServer.beginTransaction();
            String qry = "DELETE FROM " + table + " WHERE artist_id = ?";
            try {
                SqlUpdate query = Ebean.createSqlUpdate(qry);
                query.setParameter(1, id);
                query.execute();
                txn.commit();
            } finally {
                txn.end();
            }
            return null;
        });
    }

    /**
     * Method to save a new artist object with newly set attributes from the edit artist model
     *
     * @param newArtist the artist object to be edited
     */
    private void saveAdminArtistCountries(Artist newArtist) {
        for (String countryName : newArtist.getCountryList()) {
            Optional<Integer> countryObject = passportCountryRepository.getPassportCountryId(countryName);
            if (countryObject.isPresent()) {
                ArtistCountry artistCountry = new ArtistCountry(newArtist.getArtistId(), countryObject.get());
                addCountrytoArtistCountryTable(artistCountry);
            } else {
                PassportCountry passportCountry = new PassportCountry(countryName);
                passportCountryRepository.insert(passportCountry).thenApplyAsync(id -> {
                    if (id.isPresent()) {
                        ArtistCountry artistCountry = new ArtistCountry(newArtist.getArtistId(), id.get());
                        addCountrytoArtistCountryTable(artistCountry);
                    }
                    return null;
                });
            }
        }
    }

    /**
     * Method used to extract selected genres from a form binding and insert it into the artist_genre linking table
     * for the related artist.
     * @param newArtist an artist object
     * @param artistProfileForm the form containing all newly input the attributes of an artist
     */
    private void saveAdminArtistGenres(Artist newArtist, Form<Artist> artistProfileForm) {
        Optional<String> optionalGenres = artistProfileForm.field("genreForm").value();
        if (optionalGenres.isPresent() && !optionalGenres.get().isEmpty()) {
            for (String genre : optionalGenres.get().split(",")) {
                genreRepository.insertArtistGenre(newArtist.getArtistId(), parseInt(genre));
            }
        }
    }

    /**
     * Method used to extract selected artist profiles (as admins) from a form binding and insert it into the artist_profile linking table
     * for the related artist.
     * @param newArtist an artist object
     * @param artistProfileForm the form containing all newly input the attributes of an artist
     */
    private void saveAdminArtistAdmins(Artist newArtist, Form<Artist> artistProfileForm, Integer currentUserId) {
        Optional<String> optionalProfiles = artistProfileForm.field("adminForm").value();
        if (optionalProfiles.isPresent() && !optionalProfiles.get().isEmpty()) {
            //Insert ArtistProfiles for new Artist.
            for (String profileIdString : optionalProfiles.get().split(",")) {
                Integer profileId = parseInt(profileIdString);
                ArtistProfile artistProfile = new ArtistProfile(newArtist.getArtistId(), profileId);
                insertProfileLink(artistProfile);
            }
        } else {
            insertProfileLink(new ArtistProfile(newArtist.getArtistId(), currentUserId));
        }
    }


    /**
     * Method to retrieve an artist from the database using a passed database id
     * @param artistId the id of the artist to retrieve
     * @return the found artist
     */
    public Optional<Artist> getArtist(int artistId) {
        Artist artist = ebeanServer.find(Artist.class).where().eq("artist_id", artistId).findOne();
        if (artist == null) {
            return Optional.empty();
        } else {
            return Optional.of(populateArtistAdmin(artist));
        }
    }

    /**
     * Method to get the number of invalid artists in the system
     * Used for pagination
     *
     * @return int number of artists found
     */
    public int getNumArtistRequests() {
        return ebeanServer.find(Artist.class).where().eq("verified", 0).eq("soft_delete", 0).findCount();
    }

    /**
     * Method to get the number of valid artist in the system
     * Used for pagination
     *
     * @return int number of artists
     */
    public int getNumArtists() {
        return ebeanServer.find(Artist.class).where().eq("verified", 1).eq("soft_delete", 0).findCount();
    }

    /**
     * Method to get one page worth of artists
     *
     * @param offset offset of artists to find
     * @param pageSize max amount to find
     * @param  verified the value specifying if the artist has been verified or not
     *                  affects what type of artists are returned. In this case, should always be
     *                  '1' (verified)
     * @return List of found artists
     */
    public List<Artist> getPageArtists(Integer offset, int pageSize, int verified) {
        List<Artist> artists = new ArrayList<>();
        List<Artist> foundArtists = ebeanServer.find(Artist.class).setMaxRows(pageSize).setFirstRow(offset)
                .where().eq("verified", verified).eq("soft_delete", 0).findList();
        for (Artist artist : foundArtists) {
            artists.add(populateArtistAdmin(artist));
        }
        return artists;
    }

    /**
     * DB check to see if a given profile is an artist admin of a given artist
     * @param profileId id of the given profile
     * @param artistId id of the given artist
     * @return True if the profile is an admin of the given artist
     */
    public boolean isAdminOfGivenArtist(int profileId, int artistId){
        return ebeanServer.find(ArtistProfile.class)
                .where()
                .eq("profile_id", profileId)
                .eq("artist_id", artistId).exists();
    }

    /**
     * Db check to see if the given artist has be verified by an admin
     * @param artistId id of the artist used to check verification status
     * @return True if artist is verified
     */
    public boolean isVerifiedArtist(int artistId){
        return ebeanServer.find(Artist.class)
                .where()
                .eq("artist_id", artistId)
                .eq("verified", 1)
                .exists();
    }

    /**
     * Checks if the given user's id is an admin of a currently verified artist.
     * This allows them to be able to make events.
     * @param profileId The id of the profile being checked.
     * @return True if the artist is an admin of a currently verified artist, else false.
     */
    public boolean isArtistAdmin(int profileId){
        if(ebeanServer.find(ArtistProfile.class).where().eq("profile_id", profileId).exists()){
            int artistID = ebeanServer.find(ArtistProfile.class).select("artistId").where().eq("profile_id", profileId).findSingleAttribute();
            return ebeanServer.find(Artist.class).where().eq("verified", 1).eq("soft_delete", 0).eq("artist_id", artistID).exists();
        };
            return false;
    }


    /**
     * Get count of followers for an artist
     * @param artistId - ID of the artists to get follower count for
     * @return The number of followers for the given artist
     */
    public int getNumFollowers(int artistId) {
        return ebeanServer.find(FollowArtist.class)
                .select("artistId")
                .where()
                .eq("artistId", artistId)
                .findCount();

    }

}
