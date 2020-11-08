package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

public class BooleanRule implements ValidatorRule {
    public String validate(Validator validator, String key, String value) {
        if(value == null)
            return null;
        if(value.equals("true") || value.equals("false"))
            return null;
        return "Not a boolean value";
    }
}
