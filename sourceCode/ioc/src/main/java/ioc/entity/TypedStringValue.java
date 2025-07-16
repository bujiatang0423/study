package ioc.entity;

/**
 * @author bujiatang
 */
public class TypedStringValue {

    /**
     * 属性值
     */
    private String value;

    /**
     * 属性值对应属性
     */
    private Class<?> targetType;


    public TypedStringValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this.targetType = targetType;
    }
}
