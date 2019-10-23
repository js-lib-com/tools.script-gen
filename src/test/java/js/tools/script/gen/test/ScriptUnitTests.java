package js.tools.script.gen.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import js.tools.commons.util.Classes;
import junit.framework.TestCase;

@SuppressWarnings(
{
    "rawtypes", "unchecked"
})
public class ScriptUnitTests extends TestCase
{
  public void testGenerateVoidMethod() throws Throwable
  {
    Object compilationUnit = Classes.newInstance("js.tools.script.gen.JsClassImpl");
    Classes.setFieldValue(compilationUnit, "packageName", "js.test");
    Classes.setFieldValue(compilationUnit, "qualifiedClassName", "js.test.ClassName");
    Classes.setFieldValue(compilationUnit, "className", "ClassName");

    List parameters = new ArrayList();
    parameters.add(createParameter("java.lang.Double", "param1"));
    parameters.add(createParameter("java.lang.Boolean[]", "param2"));
    Object method = createMethod("methodName", parameters, "void", "Exception1", "Exception2");

    List methods = new ArrayList();
    methods.add(method);
    Classes.setFieldValue(compilationUnit, "methods", methods);

    Class clazz = Class.forName("js.tools.script.gen.Script");
    String s = Classes.invoke(clazz, "generate", compilationUnit);
    System.out.println(s);
  }

  public void testGenerateMethodWithReturn() throws Throwable
  {
    Object compilationUnit = Classes.newInstance("js.tools.script.gen.JsClassImpl");
    Classes.setFieldValue(compilationUnit, "packageName", "js.test");
    Classes.setFieldValue(compilationUnit, "qualifiedClassName", "js.test.ClassName");
    Classes.setFieldValue(compilationUnit, "className", "ClassName");

    List parameters = new ArrayList();
    parameters.add(createParameter("int", "param1"));
    parameters.add(createParameter("java.lang.String", "param2"));
    Object method = createMethod("methodName", parameters, "Object", "Exception1", "Exception2");

    List methods = new ArrayList();
    methods.add(method);
    Classes.setFieldValue(compilationUnit, "methods", methods);

    Class clazz = Class.forName("js.tools.script.gen.Script");
    String s = Classes.invoke(clazz, "generate", compilationUnit);
    System.out.println(s);
  }

  private static Object createMethod(String name, List parameters, String returnType, String... thrownTypes) throws Exception
  {
    Object method = Classes.newInstance("js.tools.script.gen.JsMethodImpl");
    Classes.setFieldValue(method, "name", name);
    Classes.setFieldValue(method, "parameters", parameters);
    Classes.setFieldValue(method, "returnType", returnType);
    Classes.setFieldValue(method, "exceptionTypes", Arrays.asList(thrownTypes));
    return method;
  }

  private static Object createParameter(String type, String name) throws Exception
  {
    return Classes.newInstance("js.tools.script.gen.Parameter", type, name);
  }
}
