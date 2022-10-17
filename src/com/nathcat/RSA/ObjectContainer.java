package com.nathcat.RSA;

import java.io.Serializable;

/**
 * @deprecated No longer required with the implementation of ByteChunks
 */
public class ObjectContainer implements Serializable {
    public final Object obj;

    public ObjectContainer(Object obj) {
        this.obj = obj;
    }
}
