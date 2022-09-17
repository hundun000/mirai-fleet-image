package hundun.petpet.share.block;

import com.fasterxml.jackson.core.JsonProcessingException;
import hundun.petpet.share.block.PetpetBlock.ImageMeta;
import hundun.petpet.share.block.dto.ImageProviderType;
import hundun.petpet.share.block.provider.SimpleGeometricImageProvider;

import java.io.IOException;

public class PetpetBlockException extends Exception {

    private static final long serialVersionUID = 7086443849195935476L;

    public PetpetBlockException (String msg) {
        super(msg);
    }

    public static PetpetBlockException fromImageSupplierNotFound(ImageMeta imageMeta) {
        return new PetpetBlockException(String.format("未找到imageMeta=%s的ImageSupplier", imageMeta));
    }

    public static PetpetBlockException fromIOException(IOException e) {
        return new PetpetBlockException(String.format("PetpetBlock执行期间发生IOException：%s", e.getMessage()));
    }

    public static PetpetBlockException fromImageSupplierNotFound(ImageProviderType providerType, String key) {
        return new PetpetBlockException(String.format("未找到 providerType = %s 的ImageSupplier for key = %s", providerType, key));
    }

    public static PetpetBlockException fromImageProvider(SimpleGeometricImageProvider provider, String key, Exception e) {
        return new PetpetBlockException(String.format("ImageProvider %s 处理key=“%s”时，发生Exception：%s", provider.getClass().getName(), key, e.getMessage()));
    }

    public static PetpetBlockException fromImageProvider(SimpleGeometricImageProvider provider, String key) {
        return new PetpetBlockException(String.format("ImageProvider %s 处理key=“%s”时，无法处理", provider.getClass().getName(), key));
    }

    public static PetpetBlockException fromJsonException(JsonProcessingException e) {
        return new PetpetBlockException(String.format("PetpetBlock执行期间发生JsonException：%s", e.getMessage()));
    }
}
