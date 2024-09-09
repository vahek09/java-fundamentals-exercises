package com.bobocode.basics;

import com.bobocode.basics.util.BaseEntity;
import com.bobocode.util.ExerciseNotCompletedException;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;

/**
 * {@link CrazyGenerics} is an exercise class. It consists of classes, interfaces, and methods that should be updated
 * using generics.
 * <p>
 * TODO: go step by step from top to bottom. Read the java doc, write code, and run CrazyGenericsTest to verify your implementation.
 */
public class CrazyGenerics<T> {
    /**
     * {@link Sourced} is a container class that allows storing any object along with the source of that data.
     * The value type can be specified by a type parameter "T".
     *
     * @param <T> – value type
     */
    @Data
    public static class Sourced<T> {
        private T value;
        private String source;
    }

    /**
     * {@link Limited} is a container class that allows storing an actual value along with possible min and max values.
     * It is a special form of triple. All three values have a generic type that should be a subclass of {@link Number}.
     *
     * @param <T> – actual, min and max type
     */
    @Data
    public static class Limited<T extends Number> {
        private final T actual;
        private final T min;
        private final T max;
    }

    /**
     * {@link Converter} interface declares a typical contract of a converter. It works with two independent generic types.
     * It defines a convert method which accepts one parameter of one type and returns a converted result of another type.
     *
     * @param <T> – source object type
     * @param <R> - converted result type
     */
    public interface Converter<T, R> {
        R convert(T type);
    }

    /**
     * {@link StrictProcessor} defines a contract of a processor that can process only objects that are {@link Serializable}
     * and {@link Comparable}.
     *
     * @param <T> – the type of objects that can be processed
     */
    interface StrictProcessor<T extends Serializable & Comparable<? super T>> {
        void process(T obj);
    }

    /**
     * {@link CollectionRepository} defines a contract of a runtime store for entities based on any {@link Collection}.
     * It has methods that allow saving a new entity and getting the whole collection.
     *
     * @param <T> – a type of the entity that should be a subclass of {@link BaseEntity}
     * @param <C> – a type of any collection
     */
    interface CollectionRepository<T extends BaseEntity, C extends Collection<T>> {
        void save(T entity);

        C getEntityCollection();
    }

    /**
     * {@link ListRepository} extends {@link CollectionRepository} but specifies the underlying collection as
     * {@link java.util.List}.
     *
     * @param <T> – a type of the entity that should be a subclass of {@link BaseEntity}
     */
    interface ListRepository<T extends BaseEntity> extends CollectionRepository<T, List<T>> {
    }

    /**
     * {@link ComparableCollection} is a {@link Collection} that can be compared by size. It extends a {@link Collection}
     * interface and {@link Comparable} interface, and provides a default implementation of a compareTo method that
     * compares collections sizes.
     * <p>
     * Please note that size does not depend on the elements type, so it is allowed to compare collections of different
     * element types.
     *
     * @param <E> a type of collection elements
     */
    interface ComparableCollection<E> extends Collection<E>, Comparable<Collection<?>> {

        @Override
        default int compareTo(Collection<?> other) {
            return Integer.compare(this.size(), other.size());
        }
    }

    /**
     * {@link CollectionUtil} is a utility class that provides various generic helper methods.
     */
    public static class CollectionUtil {
        static final Comparator<BaseEntity> CREATED_ON_COMPARATOR = Comparator.comparing(BaseEntity::getCreatedOn);

        /**
         * A utility method that allows printing a dashed list of elements.
         *
         * @param list List of elements to print.
         */
        public static void print(List<?> list) {
            list.forEach(element -> System.out.println(" – " + element));
        }

        /**
         * Utility method that checks if the provided collection has new entities. An entity is any object
         * that extends {@link BaseEntity}. A new entity is an entity that does not have an id assigned.
         * (In other words, whose id value equals null).
         *
         * @param entities provided collection of entities
         * @return true if at least one of the elements has null id
         */
        public static boolean hasNewEntities(Collection<? extends BaseEntity> entities) {
            return entities.stream().anyMatch(entity -> entity.getUuid() == null);
        }

        /**
         * Utility method that checks if a provided collection of entities is valid. An entity is any subclass of
         * a {@link BaseEntity}. A validation criterion can be different for different cases, so it is passed
         * as a second parameter.
         *
         * @param entities            provided collection of entities
         * @param validationPredicate criteria for validation
         * @return true if all entities fit validation criteria
         */
        public static boolean isValidCollection(Collection<? extends BaseEntity> entities, Predicate<? super BaseEntity> validationPredicate) {
            return entities.stream().allMatch(validationPredicate);
        }


        /**
         * hasDuplicates is a generic utility method that checks if a list of entities contains a target entity more than once.
         * In other words, it checks if the target entity has duplicates in the provided list. A duplicate is an entity that
         * has the same UUID.
         *
         * @param entities     given list of entities
         * @param targetEntity a target entity
         * @param <T>          entity type
         * @return true if entities list contains target entity more than once
         */
        public static <T extends BaseEntity> boolean hasDuplicates(List<T> entities, T targetEntity) {
            if (targetEntity == null || targetEntity.getUuid() == null) {
                return false;
            }

            return entities.stream()
                    .filter(entity -> entity != null && entity.getUuid() != null)
                    .filter(entity -> entity.getUuid().equals(targetEntity.getUuid()))
                    .count() > 1;
        }

        /**
         * findMax is a generic utility method that accepts an {@link Iterable} and {@link Comparator} and returns an
         * optional object, that has the maximum "value" based on the given comparator.
         *
         * @param elements   provided iterable of elements
         * @param comparator an object that will be used to compare elements
         * @param <T>        type of elements
         * @return optional max value
         */
        public static <T> Optional<T> findMax(Iterable<T> elements, Comparator<? super T> comparator) {
            T maxElement = null;
            boolean foundAny = false;

            for (T element : elements) {
                if (!foundAny || comparator.compare(element, maxElement) > 0) {
                    maxElement = element;
                    foundAny = true;
                }
            }

            return foundAny ? Optional.of(maxElement) : Optional.empty();
        }


        /**
         * findMostRecentlyCreatedEntity is a generic utility method that accepts a collection of entities and returns the
         * one that is the most recently created. If the collection is empty,
         * it throws {@link java.util.NoSuchElementException}.
         * <p>
         * This method reuses findMax method and passes entities along with the prepared comparator instance,
         * that is stored as a constant CREATED_ON_COMPARATOR.
         *
         * @param entities provided collection of entities
         * @param <T>      entity type
         * @return an entity from the given collection that has the max createdOn value
         */
        public static <T extends BaseEntity> T findMostRecentlyCreatedEntity(Collection<T> entities) {
            return findMax(entities, CREATED_ON_COMPARATOR)
                    .orElseThrow(NoSuchElementException::new);
        }

        /**
         * A utility method that allows swapping two elements of any list. It changes the list so the element with index
         * i will be located on index j, and the element with index j will be located at index i.
         * Please note that in order to make it convenient and simple, it DOES NOT declare any type parameter.
         *
         * @param elements a list of any given type
         * @param i        index of the element to swap
         * @param j        index of the other element to swap
         */
        public static void swap(List<?> elements, int i, int j) {
            Objects.checkIndex(i, elements.size());
            Objects.checkIndex(j, elements.size());

            swapHelper(elements, i, j);
        }

        /** Helper*/
        private static <T> void swapHelper(List<T> list, int i, int j) {
            T temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }
}
