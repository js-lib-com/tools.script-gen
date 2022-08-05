package com.jslib.maven.script.gen.test;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.jslib.util.Strings;
import com.jslib.util.Types;

import junit.framework.TestCase;

public class VelocityUnitTest extends TestCase
{
  private VelocityEngine engine;
  private VelocityContext context;

  @Override
  protected void setUp() throws Exception
  {
    engine = new VelocityEngine();
    engine.init();
    context = new VelocityContext();
  }

  public void testCreateScript() throws Exception
  {
    context.put("class", loadClass());

    File file = new File("fixture/script.vtl");
    Writer writer = new StringWriter();
    Template template = engine.getTemplate(file.getPath());
    template.merge(context, writer);
    System.out.println(writer);
  }

  private static JsClass loadClass() throws Exception
  {
    Class<?> javaClass = Class.forName("com.jslib.maven.script.gen.JsClassImpl");

    JsClass jsClass = new JsClass();
    jsClass.qualifiedName = javaClass.getName();
    jsClass.packageName = javaClass.getPackage().getName();
    jsClass.name = javaClass.getSimpleName();

    for(Method javaMethod : javaClass.getDeclaredMethods()) {
      JsMethod jsMethod = new JsMethod();
      jsMethod.name = javaMethod.getName();
      jsMethod.title = titleCase(jsMethod.name);
      if(!Types.isVoid(javaMethod.getReturnType())) {
        jsMethod.returnType = javaMethod.getReturnType().getName();
      }

      int parameterIndex = 0;
      List<String> parameterNames = new ArrayList<String>();
      for(Class<?> parameterType : javaMethod.getParameterTypes()) {
        String parameterName = "paramNumber" + parameterIndex++;
        parameterNames.add(parameterName);
        jsMethod.parameters.add(new JsParameter(parameterType.getName(), parameterName, titleCase(parameterName)));
      }
      jsMethod.parametersList = Strings.join(parameterNames, ", ");

      jsMethod.exceptionTypes.add("java.io.IOException");

      jsClass.methods.add(jsMethod);
    }

    return jsClass;
  }

  public static class JsClass
  {
    String qualifiedName;
    String packageName;
    String name;
    List<JsMethod> methods = new ArrayList<JsMethod>();

    public String getQualifiedName()
    {
      return qualifiedName;
    }

    public String getPackageName()
    {
      return packageName;
    }

    public String getName()
    {
      return name;
    }

    public List<JsMethod> getMethods()
    {
      return methods;
    }
  }

  public static class JsMethod
  {
    String name;
    String title;
    List<JsParameter> parameters = new ArrayList<JsParameter>();
    String parametersList;
    String returnType;
    List<String> exceptionTypes = new ArrayList<String>();

    public String getName()
    {
      return name;
    }

    public String getTitle()
    {
      return title;
    }

    public List<JsParameter> getParameters()
    {
      return parameters;
    }

    public String getParametersList()
    {
      return parametersList;
    }

    public String getReturnType()
    {
      return returnType;
    }

    public List<String> getExceptionTypes()
    {
      return exceptionTypes;
    }
  }

  public static class JsParameter
  {
    String type;
    String kind;
    String name;
    String description;

    public JsParameter(String type, String name, String description)
    {
      this.type = type;
      this.kind = KINDS.get(type);
      this.name = name;
      this.description = description;
    }

    public String getType()
    {
      return type;
    }

    public String getKind()
    {
      return kind;
    }

    public String getName()
    {
      return name;
    }

    public String getDescription()
    {
      return description;
    }

    private static final Map<String, String> KINDS = new HashMap<String, String>();
    static {
      KINDS.put("java.lang.String", "string");
      KINDS.put("java.lang.CharSequence", "string");
      KINDS.put("java.io.File", "string");
      KINDS.put("java.net.URL", "string");
      KINDS.put("java.lang.Number", "number");
      KINDS.put("java.lang.Byte", "number");
      KINDS.put("byte", "number");
      KINDS.put("java.lang.Short", "number");
      KINDS.put("short", "number");
      KINDS.put("java.lang.Integer", "number");
      KINDS.put("int", "number");
      KINDS.put("java.lang.Long", "number");
      KINDS.put("long", "number");
      KINDS.put("java.lang.Float", "number");
      KINDS.put("float", "number");
      KINDS.put("java.lang.Double", "number");
      KINDS.put("double", "number");
      KINDS.put("java.lang.Boolean", "boolean");
      KINDS.put("boolean", "boolean");
      KINDS.put("java.util.Date", "date");
      KINDS.put("java.sql.Date", "date");
      KINDS.put("java.sql.Time", "date");
      KINDS.put("java.sql.Timestamp", "date");
    }
  }

  private static String titleCase(String camelCase)
  {
    // TODO replace with regex
    int length = camelCase.length();
    StringBuilder sb = new StringBuilder(length);
    sb.append(Character.toUpperCase(camelCase.charAt(0)));
    for(int i = 1; i < length - 1; i++) {
      char ch = camelCase.charAt(i);
      if(Character.isLowerCase(camelCase.charAt(i - 1)) && Character.isUpperCase(ch)) {
        sb.append(' ');
        if(Character.isLowerCase(camelCase.charAt(i + 1))) {
          ch = Character.toLowerCase(ch);
        }
      }
      sb.append(ch);
    }
    sb.append(camelCase.charAt(length - 1));
    return sb.toString();
  }
}
