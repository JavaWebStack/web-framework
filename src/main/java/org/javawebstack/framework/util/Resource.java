package org.javawebstack.framework.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Resource<T> {

    void map(T source);

    static <T> Resource<T> make(Class<? extends Resource<T>> type, T source){
        if(source == null)
            return null;
        try {
            Resource<T> resource = type.newInstance();
            resource.map(source);
            return resource;
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    static <T> List<Resource<T>> make(Class<? extends Resource<T>> type, List<T> source){
        return source.stream().map(s -> make(type, s)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    static <T> List<Resource<T>> make(Class<? extends Resource<T>> type, T... source){
        return make(type, Arrays.asList(source));
    }

}
