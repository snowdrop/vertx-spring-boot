package dev.snowdrop.vertx.mail;

import io.vertx.core.MultiMap;
import org.springframework.util.MultiValueMap;

class MultiMapConverter {

    MultiMap fromMultiValueMap(MultiValueMap<String, String> multiValueMap) {
        MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
        multiValueMap.forEach(multiMap::add);

        return multiMap;
    }
}
