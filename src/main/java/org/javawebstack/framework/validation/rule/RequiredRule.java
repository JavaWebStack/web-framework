package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

public class RequiredRule implements ValidatorRule {
    public String validate(Validator validator, String key, String value) {
        return value != null ? null : "Missing required field";
    }
}
