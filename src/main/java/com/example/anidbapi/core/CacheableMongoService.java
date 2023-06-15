package com.example.anidbapi.core;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public abstract class CacheableMongoService<T extends CacheableMongoDocument<T>, ID> {

    private final MongoRepository<T, ID> repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public CacheableMongoService(MongoRepository<T, ID> repository) {
        this.repository = repository;
    }

    private void createTTLIndex(@NonNull Class<?> clazz) {
        final Optional<Field> first = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(TTLField.class))
                .findFirst();

        if (first.isEmpty())
            throw new RuntimeException("No TTLField annotation found in class " + clazz.getName());

        final Field ttlField = first.get();

        // Ensure that the field type is Date
        if (ttlField.getType() != Date.class)
            throw new RuntimeException("TTLField annotation must be on a field of type Date");

        final TTLField ttlFieldAnnotation = ttlField.getAnnotation(TTLField.class);

        mongoTemplate.indexOps(clazz).ensureIndex(new Index(ttlField.getName(), Sort.Direction.ASC)
                .named(ttlFieldAnnotation.name())
                .expire(ttlFieldAnnotation.expireAfterSeconds()));
    }

    final public List<T> saveAll(@NonNull List<T> documents) {

        if (documents.isEmpty())
            throw new RuntimeException("Cannot save empty list");

        // only need to get the first document since the class is the same for all
        final T document = documents.get(0);

        createTTLIndex(document.getClass());

        return repository.saveAll(documents);
    }

    final public T save(@NonNull T document) {
        createTTLIndex(document.getClass());
        return repository.save(document);
    }

}
