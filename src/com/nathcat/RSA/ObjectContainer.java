package com.nathcat.RSA;

import java.io.Serializable;

public class ObjectContainer implements Serializable {
    public final Object obj;

    public ObjectContainer(Object obj) {
        this.obj = obj;
    }
}
