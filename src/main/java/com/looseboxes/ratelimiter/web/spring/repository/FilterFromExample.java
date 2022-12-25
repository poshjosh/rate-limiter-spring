package com.looseboxes.ratelimiter.web.spring.repository;

import com.looseboxes.ratelimiter.annotations.Experimental;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Example;

import java.beans.PropertyDescriptor;
import java.util.function.Predicate;

/** @Experimental */
@Experimental
public class FilterFromExample<E> implements Predicate<E> {

    private final BeanWrapper probeBeanWrapper;
    private final PropertyDescriptor [] propertyDescriptors;
    private final Object [] values;
    public FilterFromExample(Example<E> example) {
        this.probeBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(example.getProbe());
        this.propertyDescriptors = probeBeanWrapper.getPropertyDescriptors();
        this.values = new Object[propertyDescriptors.length];
        for(int i = 0; i < propertyDescriptors.length; i++) {
            values[i] = probeBeanWrapper.getPropertyValue(propertyDescriptors[i].getName());
        }
    }

    @Override
    public boolean test(E e) {

        //While org.springframework.data.domain.Example requires all fields to be compared, the equals method
        //defined by object subclasses may not require all fields to be equal (e.g where only id is checked)
        //Therefore, this is not valid logic
        //
        //if(probeBeanWrapper.getWrappedInstance().equals(e)) {
        //    return true;
        //}

        BeanWrapper beanWrapper2 = PropertyAccessorFactory.forBeanPropertyAccess(e);

        for(int i= 0; i < values.length; i++) {
            String name = propertyDescriptors[i].getName();
            if ("class".equals(name)) {
                continue;
            }
            Object val1 = values[i];
            Object val2 = beanWrapper2.getPropertyValue(name);
            if(val1 != null) {
                if(val2 != null) {
                    if(!val1.equals(val2)) {
                        return false;
                    }
                }else{
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "FilterFromExample{probeBeanWrapper=" + probeBeanWrapper.getWrappedInstance() + '}';
    }
}
