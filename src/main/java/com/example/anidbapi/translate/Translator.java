package com.example.anidbapi.translate;

import lombok.NonNull;

import java.lang.reflect.Field;

public abstract class Translator {

    public abstract String translate(String text, Language from, Language to) throws TranslateException;

    @SuppressWarnings("unchecked")
    public <T> T translateObject(@NonNull T object, @NonNull Language from, @NonNull Language to) throws TranslateException {
        final Class<?> clazz = object.getClass();
        final Class<T> t;
        try {
            t = (Class<T>) Class.forName(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new TranslateException("Could not find class " + clazz.getName(), e);
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType() != String.class) // only strings obviously
                continue;
            if (field.getDeclaredAnnotation(TranslateField.class) == null)
                continue;
            field.setAccessible(true);
            try {
                final String str = field.get(object).toString();
                field.set(object, translate(str, from, to));
            } catch (IllegalAccessException e) {
                throw new TranslateException("Could not access field " + field.getName() + " in class " + clazz.getName(), e);
            } finally {
                field.setAccessible(false);
            }
        }

        return t.cast(object);
    }

}