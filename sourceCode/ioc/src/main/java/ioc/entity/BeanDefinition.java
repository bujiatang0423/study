package ioc.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bujiatang
 */
public class BeanDefinition {
    //bean 标签的 class 属性
    private String clazzName;

    //bean标签的 class属性对应的 Class 对象
    private Class<?> clazzType;

    private String beanName;

    private String initMethod;

    private String scope;

    private List<PropertyValue> propertyValues = new ArrayList<>();

    private static final String SCOPE_SINGLETON = "singleton";

    private static final String SCOPE_PROTOTYPE = "p rototype";

    public BeanDefinition(String clazzName, String beanName) {
        this.clazzName = clazzName;
        this.beanName = beanName;
        this.clazzType = resolveClassName(clazzName);
    }

    private Class<?> resolveClassName(String clazzName){
        try {
            return Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean isSingle(){
        return this.scope.equals(SCOPE_SINGLETON);
    }

    public Boolean isPrototype(){
        return this.scope.equals(SCOPE_PROTOTYPE);
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public Class<?> getClazzType() {
        return clazzType;
    }

    public void setClazzType(Class<?> clazzType) {
        this.clazzType = clazzType;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public void setPropertyValues(List<PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    public void addPropertyValues(PropertyValue propertyValue){
        this.propertyValues.add(propertyValue);
    }
}
