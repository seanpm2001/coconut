package org.coconut.management.defaults;

import static org.junit.Assert.assertEquals;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;

import org.coconut.management.defaults.stubs.OperationStub;
import org.junit.Before;
import org.junit.Test;

public class DefaultManagedOperationTest {

    Map<OperationKey, AbstractManagedOperation> attr;

    OperationStub stub;

    @Before
    public void setup() throws Exception {
        BeanInfo bi = Introspector.getBeanInfo(OperationStub.class);
        stub = new OperationStub();
        attr = DefaultManagedOperation.fromPropertyDescriptors(bi.getMethodDescriptors(), stub);
        assertEquals(10, attr.size());
    }

    private final static Method DUMMY = DefaultManagedOperationTest.class.getMethods()[0];

    @Test(expected = NullPointerException.class)
    public void constructorNPEMethod() {
        new DefaultManagedOperation("foo", null, "desc", "foo");
    }

    @Test(expected = NullPointerException.class)
    public void constructorNPEObject() {
        new DefaultManagedOperation(null, DUMMY, "desc", "foo");
    }

    @Test(expected = NullPointerException.class)
    public void constructorNPEName() {
        new DefaultManagedOperation("foo", DUMMY, null, "foo");
    }

    @Test(expected = NullPointerException.class)
    public void constructorNPEDescription() {
        new DefaultManagedOperation("foo", DUMMY, "desc", null);
    }

    public void getNameDescription() {
        DefaultManagedOperation o = new DefaultManagedOperation("foo", DUMMY, "name", "desc");
        assertEquals("name", o.getName());
        assertEquals("desc", o.getDescription());
    }

    @Test
    public void method1() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("method1"));

        MBeanOperationInfo info = opr.getInfo();
        assertEquals("method1", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("void", info.getReturnType());
        assertEquals(0, info.getSignature().length);

        opr.invoke();
        assertEquals(1, stub.invokeCount);
    }

    @Test
    public void method2() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("mymethod"));

        MBeanOperationInfo info = opr.getInfo();
        assertEquals("mymethod", info.getName());
        assertEquals("desc", info.getDescription());
        assertEquals("void", info.getReturnType());
        assertEquals(0, info.getSignature().length);

        opr.invoke();
        assertEquals(2, stub.invokeCount);
    }

    @Test
    public void method3String() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("method3", "java.lang.String"));

        MBeanOperationInfo info = opr.getInfo();
        assertEquals("method3", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("void", info.getReturnType());
        assertEquals(1, info.getSignature().length);
        assertEquals("java.lang.String", info.getSignature()[0].getType());
        opr.invoke("foo");
        assertEquals(4, stub.invokeCount);
    }

    @Test
    public void method3boolean() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("method3", "boolean"));

        MBeanOperationInfo info = opr.getInfo();
        assertEquals("method3", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("void", info.getReturnType());
        assertEquals(1, info.getSignature().length);
        assertEquals("boolean", info.getSignature()[0].getType());
        opr.invoke(false);
        assertEquals(8, stub.invokeCount);
    }

    @Test
    public void method3Boolean() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("method3", "java.lang.Boolean"));

        MBeanOperationInfo info = opr.getInfo();
        assertEquals("method3", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("void", info.getReturnType());
        assertEquals(1, info.getSignature().length);
        assertEquals("java.lang.Boolean", info.getSignature()[0].getType());
        opr.invoke(Boolean.FALSE);
        assertEquals(16, stub.invokeCount);
    }

    @Test
    public void method4() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("method4"));

        MBeanOperationInfo info = opr.getInfo();
        assertEquals("method4", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("java.lang.String", info.getReturnType());
        assertEquals("32", opr.invoke());
    }

    @Test
    public void method5() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("method5"));

        MBeanOperationInfo info = opr.getInfo();
        assertEquals("method5", info.getName());
        assertEquals("", info.getDescription());
        assertEquals("int", info.getReturnType());
        assertEquals(64, opr.invoke());
    }

    @Test(expected = LinkageError.class)
    public void throwError() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("throwError"));
        opr.invoke();
    }

    @Test(expected = IOException.class)
    public void throwException() throws Throwable {
        AbstractManagedOperation opr = attr.get(new OperationKey("throwException"));
        try {
            opr.invoke();
            throw new AssertionError("should fail");
        } catch (ReflectionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void throwRuntimeError() throws Exception {
        AbstractManagedOperation opr = attr.get(new OperationKey("throwRuntimeException"));
        opr.invoke();
    }
}
