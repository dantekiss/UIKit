package com.angcyo.http.type;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/ikidou/TypeBuilder
 */
public class TypeBuilder {
    private final TypeBuilder parent;
    private final Class raw;
    private final List<Type> args = new ArrayList<>();

    private TypeBuilder(Class raw, TypeBuilder parent) {
        assert raw != null;
        this.raw = raw;
        this.parent = parent;
    }

    /**
     * List
     */
    public static TypeBuilder newInstance(Class raw) {
        return new TypeBuilder(raw, null);
    }

    /**
     * List<String>;
     * Bean<Data>;
     */
    public static Type build(Class raw, Class type) {
        return newInstance(raw).addTypeParam(type).build();
    }

    public static Type build(Class raw) {
        return newInstance(raw).build();
    }

    private static TypeBuilder newInstance(Class raw, TypeBuilder parent) {
        return new TypeBuilder(raw, parent);
    }

    /**
     * Map<String, List<String>>
     */
    public TypeBuilder beginSubType(Class raw) {
        return newInstance(raw, this);
    }

    public TypeBuilder endSubType() {
        if (parent == null) {
            throw new TypeException("expect beginSubType() before endSubType()");
        }

        parent.addTypeParam(getType());

        return parent;
    }

    /**
     * List<String>
     * Map<String, String[]>
     */
    public TypeBuilder addTypeParam(Class clazz) {
        return addTypeParam((Type) clazz);
    }

    public TypeBuilder addTypeParamExtends(Class... classes) {
        if (classes == null) {
            throw new NullPointerException("addTypeParamExtends() expect not null Class");
        }

        WildcardTypeImpl wildcardType = new WildcardTypeImpl(null, classes);

        return addTypeParam(wildcardType);
    }

    /**
     * List<? super String>
     */
    public TypeBuilder addTypeParamSuper(Class... classes) {
        if (classes == null) {
            throw new NullPointerException("addTypeParamSuper() expect not null Class");
        }

        WildcardTypeImpl wildcardType = new WildcardTypeImpl(classes, null);

        return addTypeParam(wildcardType);
    }

    public TypeBuilder addTypeParam(Type type) {
        if (type == null) {
            throw new NullPointerException("addTypeParam expect not null Type");
        }

        args.add(type);

        return this;
    }

    public Type build() {
        if (parent != null) {
            throw new TypeException("expect endSubType() before build()");
        }

        return getType();
    }

    private Type getType() {
        if (args.isEmpty()) {
            return raw;
        }
        return new ParameterizedTypeImpl(raw, args.toArray(new Type[args.size()]), null);
    }
}