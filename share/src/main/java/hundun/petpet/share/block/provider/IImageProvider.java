package hundun.petpet.share.block.provider;

import hundun.petpet.share.block.PetpetBlockException;

import java.awt.image.BufferedImage;

public interface IImageProvider {
    BufferedImage apply(String key) throws PetpetBlockException;
}
