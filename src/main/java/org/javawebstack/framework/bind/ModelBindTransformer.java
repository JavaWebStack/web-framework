package org.javawebstack.framework.bind;

import org.javawebstack.orm.Repo;

public interface ModelBindTransformer {
    Object transform(Repo<?> repo, String fieldName, Object source);
}
