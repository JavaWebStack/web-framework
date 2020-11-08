package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

public class IntegerRule implements ValidatorRule {

    private final int min;
    private final int max;

    public IntegerRule(int min, int max){
        this.min = min;
        this.max = max;
    }

    public IntegerRule(){
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public String validate(Validator validator, String key, String value) {
        if(value == null)
            return null;
        try {
            int v = Integer.parseInt(value);
            if(v < min)
                return String.format("Smaller than the minimum value (%d < %d)", v, min);
            if(v > max)
                return String.format("Greater than the maximum value (%d > %d)", v, max);
            return null;
        }catch (NumberFormatException ex){
            return "Not an integer value";
        }
    }
}
