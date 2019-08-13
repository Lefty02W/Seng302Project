package utility;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility class that holds functionality for creating a thumbnail
 */
public class Thumbnail {

    private static Thumbnail INSTANCE;
    private static int THUMB_HEIGHT = 100;
    private static int THUMB_WIDTH = 100;

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
     * Uses the SCALE_SMOOTH option for resizing.
     * If thumbnail extraction proves to be slow, this can be swapped to SCALE_FAST.
     * @param sourceImage The Image object to be converted to thumbnail
     * @return The thumbnail as a Image object
     */
    public Image extract(BufferedImage sourceImage) {

       return sourceImage.getScaledInstance(THUMB_WIDTH, THUMB_HEIGHT, Image.SCALE_SMOOTH);

    }

    /**
     * Overloaded extract method for when the default thumbnail size needs to be over-rid.
     * @param sourceImage The image to resize
     * @param newWidth The new width of the image
     * @param newHeight The new height of the image
     * @return The resized image.
     */
    public Image extract(BufferedImage sourceImage, int newWidth, int newHeight) {

        return sourceImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

    }


}
