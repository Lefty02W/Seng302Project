package utility;

public class Thumbnail {

    private static Thumbnail INSTANCE;

    /**
     * static method to create instance of Thumbnail class
      */
    public static Thumbnail getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new Thumbnail();

        return INSTANCE;
    }

    private Thumbnail(){}
}
