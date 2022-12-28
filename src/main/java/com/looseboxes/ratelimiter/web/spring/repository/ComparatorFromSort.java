package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.annotations.Experimental;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/** Experimental */
@Experimental
public class ComparatorFromSort<E> implements Comparator<E> {

    private final Sort sort;

    private final Map<E, BeanWrapper> beanWrapperMap;

    public ComparatorFromSort(Sort sort) {
        this(sort, 16, 0.75f);
    }

    public ComparatorFromSort(Sort sort, int initialCapacity, float loadFactor) {
        this.sort = sort;
        this.beanWrapperMap = new WeakHashMap<>(initialCapacity, loadFactor);
    }

    public BeanWrapper getBeanWrapper(E e) {
        return beanWrapperMap.compute(e, (obj, beanWrapper) -> beanWrapper == null ?
                PropertyAccessorFactory.forBeanPropertyAccess(obj) : beanWrapper);
    }

    @Override
    public int compare(E o1, E o2) {
        final int result;
        if(sort == null || sort.isUnsorted() || sort.isEmpty()) {
            result = 0;
        }else{
            BeanWrapper accessor1 = getBeanWrapper(o1);
            BeanWrapper accessor2 = getBeanWrapper(o2);
            final Iterator<Sort.Order> iter = sort.iterator();
            int n = 0;
            while(n == 0 && iter.hasNext()) {
                n = compare(accessor1, accessor2, iter.next());
            }
            result = n;
        }
        return result;
    }

    private int compare(BeanWrapper bean1, BeanWrapper bean2, Sort.Order order) {
        final String name = order.getProperty();
        final Object val1 = bean1.getPropertyValue(name);
        final Object val2 = bean2.getPropertyValue(name);
        final int result;
        if(val1 == null && val2 == null) {
            result = 0;
        }else if(val1 == null) {
            result = order.getNullHandling() == Sort.NullHandling.NULLS_FIRST ? 1 : -1;
        }else if(val2 == null) {
            result = order.getNullHandling() == Sort.NullHandling.NULLS_FIRST ? -1 : 1;
        }else if(order.isAscending() && val1 instanceof  Comparable) {
            result = ((Comparable)val1).compareTo(val2);
        }else if(order.isDescending() && val2 instanceof  Comparable) {
            result = ((Comparable)val2).compareTo(val1);
        }else{
            result = 0;
        }
        //System.out.printf("result: %d, order: %s, lhs: %s, rhs: %s\n", result, order, val1, val2);
        return result;
    }

    @Override
    public String toString() {
        return "ComparatorFromSort{sort=" + sort + '}';
    }
}
