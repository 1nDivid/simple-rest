package ru.individ.simplerest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
    public static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String render(Object o) throws Exception {
        if (o == null) {
            return "";
        } else {
            return mapper.writeValueAsString(o);
        }
    }
}
