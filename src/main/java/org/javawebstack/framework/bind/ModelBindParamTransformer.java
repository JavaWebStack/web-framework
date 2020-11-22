package org.javawebstack.framework.bind;

import org.javawebstack.httpserver.transformer.route.RouteParamTransformer;
import org.javawebstack.orm.Model;
import org.javawebstack.orm.ORM;
import org.javawebstack.orm.Repo;

import java.util.UUID;

public class ModelBindParamTransformer extends RouteParamTransformer {

    private ModelBindTransformer transformer;
    private String accessorAttribName;

    public ModelBindParamTransformer(){
        this.transformer = (exchange, repo, fieldName, source) -> repo.accessible(accessorAttribName == null ? null : exchange.attrib(accessorAttribName)).where(fieldName, source).get();
        for(Class<? extends Model> model : ORM.getModels()){
            ModelBind[] binds = model.getDeclaredAnnotationsByType(ModelBind.class);
            if(binds.length == 0)
                continue;
            Repo<?> repo = Repo.get(model);
            String fieldName = binds[0].field().length() > 0 ? binds[0].field() : repo.getInfo().getIdField();
            Class<?> fieldType = repo.getInfo().getField(fieldName).getType();
            String parent = "string";
            if(fieldType.equals(UUID.class))
                parent = "uuid";
            if(fieldType.equals(Integer.class))
                parent = "i+";
            if(fieldType.equals(Long.class))
                parent = "l+";
            extend(parent, binds[0].value(), (exchange, source) -> transformer.transform(exchange, repo, fieldName, source));
        }
    }

    public void setTransformer(ModelBindTransformer transformer){
        this.transformer = transformer;
    }

    public void setAccessorAttribName(String accessorAttribName) {
        this.accessorAttribName = accessorAttribName;
    }
}
