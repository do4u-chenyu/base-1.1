package base.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by cl on 2017/7/5.
 */
public class ObjectUtils {

    private final static Log logger = LogFactory.getLog(ObjectUtils.class);

    /* 忽略的类型：不需要转换成Map的属性 */
    private final static Class<?>[] IGNORE_TYPES = new Class<?>[]{String.class, Date.class, Timestamp.class, BigDecimal.class};

    /* 优化性能，将类的属性全部缓存起来 */
    private static Map<Class<?>, List<Field>> fieldsMap = new HashMap<Class<?>, List<Field>>();

    private ObjectUtils() {
    }

    /**
     * 获取对象的属性Field列表
     */
    public static List<Field> getFields(Object obj) {
        Class<?> clazz = obj.getClass();
        return getFields(clazz);
    }

    /**
     * 获取类的属性Field列表
     */
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> list = fieldsMap.get(clazz);
        if (null == list) {
            synchronized (fieldsMap) {
                list = fieldsMap.get(clazz);
                if (null == list) {
                    list = new ArrayList<Field>();
                    while (null != clazz) {
                        for (Field f : clazz.getDeclaredFields()) {
                            if ((!Modifier.isStatic(f.getModifiers())) && (!Modifier.isFinal(f.getModifiers()))) {
                                list.add(f);
                            }
                        }
                        clazz = clazz.getSuperclass();
                    }
                    fieldsMap.put(clazz, list);
                }
            }
        }

        return list;
    }

    /**
     * 获取对象的属性
     */
    public static Object getProperty(Object obj, String propertyName) {
        Class<?> clazz = obj.getClass();
        String methodName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        try {
            Method method = clazz.getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 判断是否为包装类型
     */
    public static boolean isWrapClass(Class<?> type) {
        try {
            return ((Class<?>) type.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否为基本类型
     */
    public static boolean isPrimitive(Class<?> type) {
        try {
            return type.isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 对象转Map
     */
    public static Map<String, Object> object2Map(Object obj) {
        return (Map<String, Object>) convertToMap(obj, null);
    }

    /**
     * 对象转Map
     */
    public static Map<String, Object> object2Map(Object obj, Object2MapHandler handler) {
        return (Map<String, Object>) convertToMap(obj, handler);
    }

    /**
     * 对象转Map
     */
    private static Object convertToMap(Object obj, Object2MapHandler handler) {
        if (null == obj) {
            return null;
        } else if (obj instanceof Collection || obj.getClass().isArray()) {
            List<Object> targetList = new ArrayList<Object>();

            Collection<?> list = (Collection<?>) obj;
            for (Object o : list) {
                Object target = convertToMap(o, handler);
                targetList.add(target);
            }

            return targetList;
        } else if (obj instanceof Map) {
            Map<Object, Object> targetMap = new LinkedHashMap<Object, Object>();

            Map<?, ?> map = (Map<?, ?>) obj;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                Object o = convertToMap(value, handler);
                targetMap.put(key, o);
            }

            return targetMap;
        } else if (needConvertToMap(obj.getClass())) {
            Map<String, Object> map = new LinkedHashMap<String, Object>();

            for (Field field : getFields(obj.getClass())) {
                try {
                    String name = field.getName();
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    value = convertToMap(value, handler);
                    map.put(name, value);
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage(), e);
                }
            }

            return map;
        } else {
            if (null != handler) {
                obj = handler.handle(obj);
            }
            return obj;
        }
    }

    /**
     * 判断是否需要转成Map
     */
    private static boolean needConvertToMap(Class<?> type) {
        // 基本类型和包装类型无需转换
        if (isPrimitive(type) || isWrapClass(type)) {
            return false;
        }

        // 常用的数据类型（String、Date、BigDecimal等）无需转换
        for (Class<?> clazz : IGNORE_TYPES) {
            if (type.isAssignableFrom(clazz)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 对象转Map过程中的属性处理器
     */
    public static interface Object2MapHandler {

        Object handle(Object value);

    }

}
