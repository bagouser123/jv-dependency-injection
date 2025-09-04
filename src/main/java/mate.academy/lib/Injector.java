package mate.academy.lib;

import mate.academy.service.FileReaderService;
import mate.academy.service.ProductParser;
import mate.academy.service.ProductService;
import mate.academy.service.impl.FileReaderServiceImpl;
import mate.academy.service.impl.ProductParserImpl;
import mate.academy.service.impl.ProductServiceImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Injector {
    private static final Injector injector = new Injector();

    public static Injector getInjector() {
        return injector;
    }
    Map<Class<?>, Object> instances = new HashMap<>();

    public Object getInstance(Class<?> interfaceClazz) {
        Object classImplementationInstance = null;
        Class<?> clazz = findImplementation(interfaceClazz);
        if (clazz.isAnnotationPresent(Component.class)) {
            Field[] declaredField = clazz.getDeclaredFields();
            for (Field field : declaredField) {
                if (field.isAnnotationPresent(Inject.class)) {
                    Object instance = getInstance(field.getType());

                    classImplementationInstance = createInstance(clazz);

                    try {
                        field.setAccessible(true);
                        field.set(classImplementationInstance, instance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (classImplementationInstance == null) {
                classImplementationInstance = createInstance(clazz);
            }
            return classImplementationInstance;
        }
            throw new RuntimeException("There is unsupported class given or not exist constructor!");
    }

    private Object createInstance(Class<?> clazz) {
        if (instances.containsKey(clazz)) {
            return instances.get(clazz);
        }

        try {
            Constructor<?> constructor = clazz.getConstructor();
            Object instance = constructor.newInstance();
            instances.put(clazz, instance);
            return instance;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> findImplementation(Class<?> interfaceClazz) {
        Map<Class<?>, Class<?>> Implement = new HashMap<>();
        Implement.put(ProductParser.class, ProductParserImpl.class);
        Implement.put(ProductService.class, ProductServiceImpl.class);
        Implement.put(FileReaderService.class, FileReaderServiceImpl.class);
        if (interfaceClazz.isInterface()) {
            return Implement.get(interfaceClazz);
        }
        return interfaceClazz;
    }
}
