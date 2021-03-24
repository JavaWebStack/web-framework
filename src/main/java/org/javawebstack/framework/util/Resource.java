package org.javawebstack.framework.util;

import java.util.*;
import java.util.stream.Collectors;

public interface Resource<T> {

    void map(T source, Context context);

    static <T> Resource<T> make(Class<? extends Resource<T>> type, T source) {
        return make(type, new Context(), source);
    }

    static <T> List<Resource<T>> make(Class<? extends Resource<T>> type, List<T> source) {
        return make(type, new Context(), source);
    }

    static <T> List<Resource<T>> make(Class<? extends Resource<T>> type, T... source) {
        return make(type, new Context(), source);
    }

    static <T> Resource<T> make(Class<? extends Resource<T>> type, Context context, T source) {
        if (source == null)
            return null;
        try {
            Resource<T> resource = type.newInstance();
            resource.map(source, context);
            return resource;
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    static <T> List<Resource<T>> make(Class<? extends Resource<T>> type, Context context, List<T> source) {
        return source.stream().map(s -> make(type, context, s)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    static <T> List<Resource<T>> make(Class<? extends Resource<T>> type, Context context, T... source) {
        return make(type, context, Arrays.asList(source));
    }

    class Context {
        final Map<String, Object> attribs = new HashMap<>();
        public <T> T attrib(String key) {
            return (T) attribs.get(key);
        }
        public Context attrib(String key, Object value) {
            attribs.put(key, value);
            return this;
        }
    }

}
