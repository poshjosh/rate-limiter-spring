package com.looseboxes.ratelimiter.spring.util;

import com.looseboxes.ratelimiter.util.ClassFilter;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassesInPackageFinderSpring implements ClassesInPackageFinder {

    @Override
    public List<Class<?>> findClasses(String packageName, ClassFilter classFilter) {
        try{
            return Collections.unmodifiableList(getClasses(packageName, classFilter));
        }catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private  List<Class<?>> getClasses(String controllerPackage, ClassFilter classFilter) throws ClassNotFoundException{

        if(StringUtils.hasText(controllerPackage)) {

            final List<Class<?>> controllerClasses = new ArrayList<>();

            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(true);

            scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

            for (BeanDefinition beanDefinition : scanner.findCandidateComponents(controllerPackage)){

                Class clazz = Class.forName(beanDefinition.getBeanClassName());

                if(classFilter.test(clazz)) {

                    controllerClasses.add(clazz);
                }
            }

            return controllerClasses.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(controllerClasses);

        }else{

            return Collections.emptyList();
        }
    }
}
