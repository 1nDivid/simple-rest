package ru.individ.simplerest.data;

import ru.individ.simplerest.entities.AbstractEntity;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public abstract class AbstractDao<T extends AbstractEntity> {
    // in-memory store
    final ConcurrentMap<Long, T> data;
    // track last entity id
    private Long lastId;

    public AbstractDao() {
        data = new ConcurrentHashMap<>();
        lastId = 0L;
    }

    /**
     * Find entity by id
     * @param id unique id of entity
     * @return matching entity or null if nothing found
     */
    public T findOne(Long id) {
        return data.get(id);
    }

    /**
     * Get list of all entities
     * @return list of entities
     */
    public List<T> findAll() {
        return data.values()
                .stream()
                .sorted(Comparator.comparing((T t) -> t.id))
                .collect(Collectors.toList());
    }

    /**
     * Create and store new entities
     * @param entity entity to store
     * @return newly created transaction
     */
    public T create(T entity) {
        lastId++;
        entity.id = lastId;
        data.put(lastId, entity);
        return entity;
    }

    /**
     * Update existing entity, completely rewrites old one
     * @param entity updated entity
     * @return updated entity or null if nothing to update
     */
    public T update(T entity) {
        if (data.replace(entity.id, entity) == null) {
            return null;
        }
        return entity;
    }

    /**
     * Delete existing entity
     * @param id unique id of entity
     * @return true if entity has been deleted, false if not
     */
    public boolean delete(Long id) {
        return data.remove(id) != null;
    }
}
