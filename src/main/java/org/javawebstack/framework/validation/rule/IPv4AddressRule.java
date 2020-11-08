package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

public class IPv4AddressRule extends RegexRule {

    public IPv4AddressRule() {
        super("((([01][0-9]{0,2})|(2[0-4][0-9])|(25[0-5])))(\\.(?1)){3}");
    }

    public String validate(Validator validator, String key, String value) {
        return super.validate(validator, key, value) == null ? null : "Not a valid IPv4 Address";
    }
}
