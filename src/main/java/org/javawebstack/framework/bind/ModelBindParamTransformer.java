package org.javawebstack.framework.bind;

import org.javawebstack.httpserver.transformer.route.RouteParamTransformer;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;

import java.util.UUID;

public class ModelBindParamTransformer extends RouteParamTransformer {

    public ModelBindParamTransformer(){
        for(Class<? extends Model> model : ORM.getModels()){
            ModelBind[] binds = model.getDeclaredAnnotationsByType(ModelBind.class);
            if(binds.length == 0)
                continue;
            Repo<?> repo = Repo.get(model);
            Class<?> idType = repo.getInfo().getIdType();
            if(idType.equals(String.class))
                extend("string", binds[0].value(), repo::get);
            if(idType.equals(UUID.class))
                extend("uuid", binds[0].value(), repo::get);
            if(idType.equals(Integer.class))
                extend("i+", binds[0].value(), repo::get);
            if(idType.equals(Long.class))
                extend("l+", binds[0].value(), repo::get);
        }
    }



}
