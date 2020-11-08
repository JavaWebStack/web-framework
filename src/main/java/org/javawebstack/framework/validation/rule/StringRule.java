package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

public class StringRule implements ValidatorRule {

    private final int min;
    private final int max;

    public StringRule(int min, int max){
        this.min = min;
        this.max = max;
    }

    public String validate(Validator validator, String key, String value) {
        if(value == null)
            return null;
        if(value.length() < min)
            return String.format("Shorter than minimum length (%d < %d)", value.length(), min);
        if(value.length() > max)
            return String.format("Longer than maximum length (%d > %d)", value.length(), max);
        return null;
    }
}
