package org.javawebstack.framework.bind;

import org.javawebstack.httpserver.transformer.route.RouteParamTransformer;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;

import java.util.UUID;

public class ModelBindParamTransformer extends RouteParamTransformer {

    private ModelBindTransformer transformer;

    public ModelBindParamTransformer(){
        this.transformer = (repo, fieldName, source) -> repo.where(fieldName, source).get();
        for(Class<? extends Model> model : ORM.getModels()){
            ModelBind[] binds = model.getDeclaredAnnotationsByType(ModelBind.class);
            if(binds.length == 0)
                continue;
            Repo<?> repo = Repo.get(model);
            String fieldName = binds[0].field().length() > 0 ? binds[0].field() : repo.getInfo().getIdField();
            Class<?> fieldType = repo.getInfo().getField(fieldName).getType();
            String parent = "string";
            if(fieldType.equals(String.class))
                extend("string", binds[0].value(), repo::get);
            if(fieldType.equals(UUID.class))
                parent = "uuid";
            if(fieldType.equals(Integer.class))
                parent = "i+";
            if(fieldType.equals(Long.class))
                parent = "l+";
            extend(parent, binds[0].value(), source -> transformer.transform(repo, fieldName, source));
        }
    }

    public void setTransformer(ModelBindTransformer transformer){
        this.transformer = transformer;
    }

}
