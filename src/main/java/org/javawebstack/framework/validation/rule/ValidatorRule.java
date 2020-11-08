package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

public interface ValidatorRule {

    String validate(Validator validator, String key, String value);

}
