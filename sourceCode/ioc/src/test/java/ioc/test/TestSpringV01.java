package ioc.test;

import ioc.entity.BeanDefinition;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 面向过程 IOC
 * @author bujiatang
 */
public class TestSpringV01 {

    /**
     * 存储解析XML出来的 BeanDefinition 对象
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**
     * 存储创建出来的Bean对象
     */
    private Map<String, Object> singletonObject = new HashMap<>();


    /**
     * 用于加载BeanDefinition
     */
    @Before
    public void init(){

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
            bean = createBean(beanName);
            this.singletonObject.put(beanName,bean);
        }else if(bd.isPrototype()){
            bean = createBean(beanName);
        }

        return bean;
    }

    /** --创建Bean实例--
    *      1.通过反射创建 对象实例
    *      2.对对象赋值
    /*      3.调用初始化方法
     */
    public Object createBean(String beanName){
        Object bean = createInstance(beanName);
        
        populateBean(bean,beanName);
        
        initilizingBean(bean,beanName);
        
        return bean;
    }

    private void initilizingBean(Object bean, String beanName) {
    }

    private void populateBean(Object bean, String beanName) {
        
    }

    private Object createInstance(String beanName) {
        return null;
    }


}
