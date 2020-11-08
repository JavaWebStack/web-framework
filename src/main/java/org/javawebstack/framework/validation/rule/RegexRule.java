package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

import java.util.regex.Pattern;

public class RegexRule implements ValidatorRule {
    private final String regex;
    private final Pattern pattern;
    public RegexRule(String regex){
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }
    public String validate(Validator validator, String key, String value) {
        if(value == null)
            return null;
        return pattern.matcher(value).matches() ? null : "Doesn't match the expected pattern";
    }

    public String getRegex() {
        return regex;
    }

    public Pattern getPattern() {
        return pattern;
    }
}
