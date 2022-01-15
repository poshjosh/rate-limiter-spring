package com.looseboxes.ratelimiter.web.spring;

import com.looseboxes.ratelimiter.util.ClassFilter;
import com.looseboxes.ratelimiter.util.ClassesInPackageFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassesInPackageFinderSpring implements ClassesInPackageFinder {

    private static final Logger LOG = LoggerFactory.getLogger(ClassesInPackageFinderSpring.class);

    @Override
    public List<Class<?>> findClasses(String packageName, ClassFilter classFilter) {
        try{
            List<Class<?>> classes = Collections.unmodifiableList(getClasses(packageName, classFilter));
            LOG.debug("Package: {}, classes: {}", packageName, classes);
            return classes;
        }catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private  List<Class<?>> getClasses(String controllerPackage, ClassFilter classFilter) throws ClassNotFoundException{

        if(StringUtils.hasText(controllerPackage)) {

            final List<Class<?>> controllerClasses = new ArrayList<>();

            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(true);

//            scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));

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
