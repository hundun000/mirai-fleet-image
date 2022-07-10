package hundun.petpet.share.block.provider;

import hundun.petpet.share.block.PetpetBlockException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FileBaseImageProvider implements IImageProvider {
    public static final String RANDOM_FILE_KEY_START = "RANDOM:";
    public static final String RANDOM_FILE_KEY_SPLIT = "\\|";

    final String baseFolder;
    final Random random = new Random();

    public FileBaseImageProvider(String baseFolder) {
        this.baseFolder = baseFolder;
    }

    @Override
    public BufferedImage apply(String key) throws PetpetBlockException {
        String filePath;
        if (key.startsWith(RANDOM_FILE_KEY_START)) {
            String[] options = key.replace(RANDOM_FILE_KEY_START, "").split(RANDOM_FILE_KEY_SPLIT);
            filePath = baseFolder + File.separator + options[random.nextInt(options.length)];
        } else {
            filePath = baseFolder + File.separator + key;
        }


        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            throw PetpetBlockException.fromIOException(e);
        }
    }
}
