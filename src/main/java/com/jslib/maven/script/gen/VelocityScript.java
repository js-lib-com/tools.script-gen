package com.jslib.maven.script.gen;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.jslib.util.Strings;

public class VelocityScript
{
  // velocity 1.7 bug
  // in a context with multiple class loaders is possible to fail velocity logger initialization

  // org.apache.velocity.runtime.log.LogManager throws org.apache.velocity.exception.VelocityException:
  // The specified logger class org.apache.velocity.runtime.log.CommonsLogLogChute does not implement the
  // org.apache.velocity.runtime.log.LogChute interface.

  // LogManager attempts to dynamically load logger implementation using class name
  // utility that load class by name uses thread context loader or system class loader
  // LogChute interface that should be implemented by all loggers are loaded by one of these class loaders

  // it can happen to have interface and implementation loaded by different class loaders and instanceof fails
  // as a consequence LogManager throw exception assuming bad implementation, that is, implementation not implementing
  // logger interface

  public static CharSequence generate(JsClassImpl jsClass)
  {
    VClass vClass = new VClass();
    vClass.qualifiedName = jsClass.getQualifiedClassName();
    vClass.packageName = jsClass.getPackageName();
    vClass.name = jsClass.getClassName();

    for(JsMethodImpl jsMethod : jsClass.getMethods()) {
      VMethod vMethod = new VMethod();
      vMethod.name = jsMethod.getMethodName();
      vMethod.title = titleCase(vMethod.name);
      if(!"void".equals(jsMethod.getReturnType())) {
        vMethod.returnType = jsMethod.getReturnType();
      }

      List<String> parameterNames = new ArrayList<String>();
      for(Parameter parameter : jsMethod.getParameters()) {
        String parameterName = parameter.getName();
        parameterNames.add(parameterName);
        vMethod.parameters.add(new VParameter(parameter.getType(), parameterName, titleCase(parameterName)));
      }
      vMethod.parametersList = Strings.join(parameterNames, ", ");

      vMethod.exceptionTypes.add("java.io.IOException");

      vClass.methods.add(vMethod);
    }

    VelocityEngine engine = new VelocityEngine();
    engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    engine.init();

    VelocityContext context = new VelocityContext();
    context.put("class", vClass);

    Writer writer = new StringWriter();
    Template template = engine.getTemplate("js/tools/script/gen/template.vtl");
    template.merge(context, writer);

    return writer.toString();
  }

  public static class VClass
  {
    String qualifiedName;
    String packageName;
    String name;
    List<VMethod> methods = new ArrayList<VMethod>();

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

    public List<VMethod> getMethods()
    {
      return methods;
    }
  }

  public static class VMethod
  {
    String name;
    String title;
    List<VParameter> parameters = new ArrayList<VParameter>();
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

    public List<VParameter> getParameters()
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

  public static class VParameter
  {
    String type;
    String kind;
    String name;
    String description;

    public VParameter(String type, String name, String description)
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
