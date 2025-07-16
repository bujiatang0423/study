package ioc.entity;

/**
 * <bean> 标签中 子标签 <property> 的 ref 属性值
 * @author bujiatang
 *
 */
public class RuntimeBeanReference {
    private String ref;



    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public RuntimeBeanReference(String ref) {
        this.ref = ref;
    }
}
