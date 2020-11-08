package org.javawebstack.framework.validation.rule;

import org.javawebstack.framework.validation.Validator;

public class IPv6AddressRule extends RegexRule {
    public IPv6AddressRule() {
        super("([0-9a-fA-F]{1,4})(:(?1)){7}");
    }

    public String validate(Validator validator, String key, String value) {
        return super.validate(validator, key, value) == null ? null : "Not a valid IPv6 Address";
    }
}
