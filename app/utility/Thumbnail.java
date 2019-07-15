package utility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

    /**
     * This method will take in a buffered image at full size and return
     * a 100x100 pixel thumbnail version of it.
     * @param sourceImage The BufferedImage object to be converted to thumbnail
     * @return The thumbnail as a BufferedImage object
     */
    public BufferedImage extract(BufferedImage sourceImage) {

        // check x and y lengths and throw IllegalArgumentException
        // resize buffered image using:
            //https://www.techcoil.com/blog/how-to-create-a-thumbnail-of-an-image-in-java-without-using-external-libraries/
       return null;

    }


}
