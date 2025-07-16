package ioc.test;

import ioc.entity.*;
import ioc.service.UserService;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.sax.SAXResult;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.util.ClassUtils.resolveClassName;

/**
 * 面向过程 IOC
 * @author bujiatang
 */
public class  TestSpringV01 {

    /**
     * 存储解析XML出来的 BeanDefinition 对象
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 存储创建出来的Bean对象
     */
    private Map<String, Object> singletonObject = new HashMap<>();


    @Test
    public void test(){
        UserService userService = (UserService) getBean("userService");
        HashMap<String, Object> params = new HashMap<>();
        params.put("userName","张三");
        assert userService != null;
        List<User> users = userService.queryUser(params);
    }

    /**
     * 用于加载BeanDefinition
     */
    @Before
    public void init(){
        String location = "bean.xml";
        InputStream inputStream = getResource(location);
        Document document = createDocument(inputStream);
        if(document!=null) {
            parseBeanDefinitions(document.getRootElement());
        }
    }

    /**
     * rootElement
     * @param rootElement beans 标签
     */
    private void parseBeanDefinitions(Element rootElement) {
        List<Element> elements = rootElement.elements();
        for (Element beanElement : elements) {
            parseBeanDefinition(beanElement);

        }
    }

    private void parseBeanDefinition(Element beanElement) {
        String className = beanElement.attributeValue("class");
        if(className==null || className.isEmpty()){
            return;
        }
        Class clazzType = resolveClassName(className);
        if(clazzType==null){
            return;
        }
        String beanName = beanElement.attributeValue("id");
        if(beanName == null || beanName.isEmpty()){
            beanName = clazzType.getSimpleName();
        }
        String scope = beanElement.attributeValue("scope");
        if(scope==null|| scope.isEmpty()){
            scope="singleton";
        }

        String initMethod = beanElement.attributeValue("init-method");

        List<Element> propertyList = beanElement.elements("property");


        BeanDefinition beanDefinition = new BeanDefinition(beanName, className);
        beanDefinition.setClazzType(clazzType);
        beanDefinition.setScope(scope);
        beanDefinition.setInitMethod(initMethod);

        for (Element element : propertyList) {
            parsePropElement(element,beanDefinition);
        }
        this.beanDefinitionMap.put(beanName,beanDefinition);
    }

    /**
     * 解析 property标签
     * @param element
     */
    private void parsePropElement(Element element,BeanDefinition beanDefinition) {
        String name = element.attributeValue("name");
        String value = element.attributeValue("value");
        String ref = element.attributeValue("ref");

        if((value == null || value.isEmpty()) && (ref== null||ref.isEmpty())){
            return;
        }
        if(value!=null && !value.isEmpty()){
            TypedStringValue typedStringValue = new TypedStringValue(value);
            Class targetType = resolveType(beanDefinition.getClazzType(), name);
            typedStringValue.setTargetType(targetType);
            PropertyValue propertyValue = new PropertyValue(name, typedStringValue);

            beanDefinition.addPropertyValues(propertyValue);
        }else {
            RuntimeBeanReference beanReference = new RuntimeBeanReference(value);
            PropertyValue propertyValue = new PropertyValue(name, beanReference);
            beanDefinition.addPropertyValues(propertyValue);
        }



    }

    private Class resolveClassName(String className){
        try {
            return Class.forName(className);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Class resolveType(Class clazzType,String name){
        try {
            Field field = clazzType.getField(name);
            field.setAccessible(true);
            return field.getType();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Document createDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            return saxReader.read(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private InputStream getResource(String location) {
        return this.getClass().getClassLoader().getResourceAsStream(location);
    }

    /**
     *
     * @param beanName  唯一id
     * @return bean 实例对象
     *
     * 1.在单例Bean集合中查找指定的BeanName的Bean实例是否存在
     *      1.1 若存在 直接返回
     *      1.2 若不存在 -> 2
     * 2.根据BeanName 从 beanDefinitionMap 获取对应的 BeanDefinition
     * 3.判断该 Bean 是多例 还是 单例
     *      3.1 如果 Bean 是单例，将创建出的Bean 实例放入 单例Bean集合 并返回
     *      3.2 如果 Bean 是多例，则直接返回
     *
     * --创建Bean实例--
     *      1.通过反射创建 对象实例
     *      2.对对象赋值
     *      3.调用初始化方法
     */
    private Object getBean(String beanName){
        Object bean = this.singletonObject.get(beanName);
        if(bean!=null){
            return bean;
        }

        final BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if(bd==null){
            return null;
        }
        if(bd.isSingle()){
            // 创建单例bean
            bean = createBean(bd);
            this.singletonObject.put(beanName,bean);
        }else if(bd.isPrototype()){
            bean = createBean(bd);
        }

        return bean;
    }

    /** --创建Bean实例--
    *      1.通过反射创建 对象实例
    *      2.对对象赋值
    /*      3.调用初始化方法
     */
    public Object createBean(BeanDefinition bd){
        Object bean = createInstance(bd);
        
        populateBean(bean,bd);
        
        initilizingBean(bean,bd);
        
        return bean;
    }

    private void initilizingBean(Object bean, BeanDefinition bd) {
        String initMethod = bd.getInitMethod();
        if(initMethod==null || initMethod.equals("")){
            return;
        }
        invokeMethod(bd.getClazzType(),bean,initMethod);
    }

    private void invokeMethod(Class classType,Object bean, String initMethod) {
        try {
            Method method = classType.getMethod(initMethod);
            method.invoke(bean);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void populateBean(Object bean, BeanDefinition bd) {
        List<PropertyValue> propertyValues = bd.getPropertyValues();
        for (PropertyValue propertyValue : propertyValues) {
            String name = propertyValue.getName();
            Object value = propertyValue.getValue();
            Object valueToUser = parseValue(bd,name,value);
            setProperty(bd,bean,name,valueToUser);
        }
    }

    private Object createInstance(BeanDefinition bd) {
        Class<?> clazzType = bd.getClazzType();
        return newInstance(clazzType);
    }

    private Object newInstance(Class clazzType){
        Object bean =null;
        try {
            Constructor constructor = clazzType.getConstructor();
            bean=constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bean;
    }

    private void setProperty(BeanDefinition bd,Object bean,String name,Object value){
        Class<?> clazzType = bd.getClazzType();
        try {
            Field field = clazzType.getField(name);
            field.setAccessible(true);
            field.set(bean,value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object parseValue(BeanDefinition bd,String name,Object value){
        if(value instanceof TypedStringValue){
            TypedStringValue typedStringValue = (TypedStringValue) value;
            String string = typedStringValue.getValue();
            Class<?> targetType = typedStringValue.getTargetType();
            return handleType(string,targetType);

        }else if(value instanceof RuntimeBeanReference){
            RuntimeBeanReference reference = (RuntimeBeanReference) value;
            String ref = reference.getRef();
            /*
            可能产生循环依赖;
             */
            return getBean(ref);
        }
        return null;
    }

    private Object handleType(String value,Class type){
        if(type == String.class){
            return value;
        } else if (type == Integer.class) {
            return Integer.parseInt(value);
        }

        return null;
    }

}
