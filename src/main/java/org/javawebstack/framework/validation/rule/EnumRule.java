package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumRule implements ValidatorRule {
    private final List<String> values;
    public EnumRule(List<String> values){
        this.values = values;
    }
    public EnumRule(String... values){
        this(Arrays.asList(values));
    }
    public EnumRule(Class<? extends Enum<?>> enumType){
        this(Arrays.stream(enumType.getEnumConstants()).map(Enum::name).collect(Collectors.toList()));
    }
    public String validate(Validator validator, String key, String value) {
        if(value == null)
            return null;
        return values.contains(value) ? null : String.format("Not an element of [%s]", String.join(",", values));
    }
}
