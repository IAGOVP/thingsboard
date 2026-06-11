/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Collections util.
 */
public class CollectionsUtil {
    /**
     * Is empty.
     *
     * @param collection collection ({@link Collection})
     * @return the boolean result
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    /**
     * Is not empty.
     *
     * @param collection collection ({@link Collection})
     * @return the boolean result
     */

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    
    /**
     * Diff sets.
     *
     * @param a a ({@link Set})
     * @param b b ({@link Set})
     * @return {@link Set}
     */

    public static <T> Set<T> diffSets(Set<T> a, Set<T> b) {
        return b.stream().filter(p -> !a.contains(p)).collect(Collectors.toSet());
    }

    
    /**
     * Diff lists.
     *
     * @param a a ({@link List})
     * @param b b ({@link List})
     * @return {@link List}
     */

    public static <T> List<T> diffLists(List<T> a, List<T> b) {
        return b.stream().filter(p -> !a.contains(p)).collect(Collectors.toList());
    }
    /**
     * Contains.
     *
     * @param collection collection ({@link Collection})
     * @param element element ({@link T})
     * @return the boolean result
     */

    public static <T> boolean contains(Collection<T> collection, T element) {
        return isNotEmpty(collection) && collection.contains(element);
    }
    /**
     * Counts non null.
     *
     * @param array array
     * @return the int result
     */

    public static <T> int countNonNull(T[] array) {
        int count = 0;
        for (T t : array) {
            if (t != null) count++;
        }
        return count;
    }
    /**
     * Map of.
     *
     * @param kvs kvs
     * @return {@link Map}
     */

    @SuppressWarnings("unchecked")
    public static <T> Map<T, T> mapOf(T... kvs) {
        if (kvs.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of parameters");
        }
        Map<T, T> map = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            T key = kvs[i];
            T value = kvs[i + 1];
            map.put(key, value);
        }
        return map;
    }
    /**
     * Empty or contains.
     *
     * @param collection collection ({@link Collection})
     * @param element element ({@link V})
     * @return the boolean result
     */

    public static <V> boolean emptyOrContains(Collection<V> collection, V element) {
        return isEmpty(collection) || collection.contains(element);
    }
    /**
     * Concat.
     *
     * @param set1 set1 ({@link Set})
     * @param set2 set2 ({@link Set})
     * @return {@link HashSet}
     */

    public static <V> HashSet<V> concat(Set<V> set1, Set<V> set2) {
        HashSet<V> result = new HashSet<>();
        result.addAll(set1);
        result.addAll(set2);
        return result;
    }
    /**
     * Is one of.
     *
     * @param value value ({@link V})
     * @param others others
     * @return the boolean result
     */

    public static <V> boolean isOneOf(V value, V... others) {
        if (value == null) {
            return false;
        }
        for (V other : others) {
            if (value.equals(other)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Elements equal.
     *
     * @param iterable1 iterable1 ({@link Iterable})
     * @param iterable2 iterable2 ({@link Iterable})
     * @param equalityCheck equality check ({@link BiPredicate})
     * @return the boolean result
     */

    public static <T> boolean elementsEqual(Iterable<T> iterable1, Iterable<T> iterable2, BiPredicate<T, T> equalityCheck) {
        if (iterable1 instanceof Collection<?> collection1 && iterable2 instanceof Collection<?> collection2) {
            if (collection1.size() != collection2.size()) {
                return false;
            }
        }

        Iterator<T> iterator1 = iterable1.iterator();
        Iterator<T> iterator2 = iterable2.iterator();
        while (true) {
            if (iterator1.hasNext()) {
                if (!iterator2.hasNext()) {
                    return false;
                }

                T o1 = iterator1.next();
                T o2 = iterator2.next();
                if (equalityCheck.test(o1, o2)) {
                    continue;
                } else {
                    return false;
                }
            }
            return !iterator2.hasNext();
        }
    }
    /**
     * Add to set.
     *
     * @param existing existing ({@link Set})
     * @param value value ({@link T})
     * @return {@link Set}
     */

    public static <T> Set<T> addToSet(Set<T> existing, T value) {
        if (existing == null || existing.isEmpty()) {
            return Set.of(value);
        }
        if (existing.contains(value)) {
            return existing;
        }
        Set<T> newSet = new HashSet<>(existing.size() + 1);
        newSet.addAll(existing);
        newSet.add(value);
        return (Set<T>) Set.of(newSet.toArray());
    }
    /**
     * Is empty.
     *
     * @param map map ({@link Map})
     * @return the boolean result
     */

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    /**
     * Is not empty.
     *
     * @param map map ({@link Map})
     * @return the boolean result
     */

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

}
