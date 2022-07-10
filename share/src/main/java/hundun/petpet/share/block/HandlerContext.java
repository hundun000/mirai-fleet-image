package hundun.petpet.share.block;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import hundun.petpet.share.block.provider.IImageProvider;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class HandlerContext {
    Map<ImageProviderType, IImageProvider> imageProviderMap;
}
