package dev.snowdrop.vertx.mail.converter;

import io.vertx.core.MultiMap;
import org.springframework.util.MultiValueMap;

final class MultiMapConverter {

    public MultiMap fromMultiValueMap(MultiValueMap<String, String> multiValueMap) {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiValueMap.forEach(multiMap::add);

        return multiMap;
    }
}
