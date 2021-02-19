package org.javawebstack.framework.util;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class I18N {

    private Locale defaultLocale = Locale.ENGLISH;
    private final Map<Locale, Map<String, I18NString>> translations = new HashMap<>();

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void set(Locale locale, String key, Map<String, Object> fields) {
        if (!translations.containsKey(locale))
            translations.put(locale, new HashMap<>());
        Map<String, I18NString> strings = translations.get(locale);
        strings.put(key, new I18NString(fields));
        translations.put(locale, strings);
    }

    public void set(String key, Map<String, Object> fields) {
        set(getDefaultLocale(), key, fields);
    }

    public void add(AbstractArray array) {
        add(getDefaultLocale(), array);
    }

    public void add(Locale locale, AbstractArray array) {
        AbstractObject object = new AbstractObject();
        array.forEach(e -> object.set(e.object().get("key").string(), e));
        add(locale, object);
    }

    public void add(AbstractObject object) {
        add(getDefaultLocale(), object);
    }

    public void add(Locale locale, AbstractObject object) {
        for (String key : object.keys()) {
            Map<String, Object> fields = new HashMap<>();
            object.get(key).object().forEach((k, v) -> fields.put(k, v.isNumber() ? v.number() : v.string()));
            set(locale, key, fields);
        }
    }

    public I18NString getString(Locale locale, String key) {
        if (!translations.containsKey(locale))
            locale = getDefaultLocale();
        if (!translations.containsKey(locale))
            return new I18NString(key);
        Map<String, I18NString> strings = translations.get(locale);
        if (strings.containsKey(key))
            return strings.get(key);
        if (locale.equals(getDefaultLocale()))
            return new I18NString(key);
        strings = translations.get(getDefaultLocale());
        return strings.containsKey(key) ? strings.get(key) : new I18NString(key);
    }

    public I18NString getString(String key) {
        return getString(getDefaultLocale(), key);
    }

    public String translate(Locale locale, String key, Object... params) {
        return getString(locale, key).translate(params);
    }

    public String translate(String key, Object... params) {
        return translate(getDefaultLocale(), key, params);
    }

    public static class I18NString {
        private Map<String, Object> fields;

        private I18NString() {
        }

        public I18NString(String message) {
            this(new HashMap<String, Object>() {{
                put("message", message);
            }});
        }

        public I18NString(Map<String, Object> fields) {
            this.fields = fields;
        }

        public Object getField(String key) {
            return fields.get(key);
        }

        public String getString(String key) {
            if (!fields.containsKey(key))
                return null;
            return getField(key).toString();
        }

        public Integer getInt(String key) {
            if (!fields.containsKey(key))
                return null;
            if (fields.get(key) instanceof Integer)
                return (Integer) fields.get(key);
            return Integer.parseInt(getString(key));
        }

        public String translate(Object... params) {
            String message = getString("message");
            for (int i = 0; i < params.length; i++)
                message = message.replace("{" + i + "}", params[i].toString());
            return message;
        }
    }

}
