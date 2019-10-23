package js.tools.script.gen;

import java.util.ArrayList;
import java.util.List;

/**
 * Script class method implementation.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
final class JsMethodImpl implements JsMethod
{
  private String name;
  private List<Parameter> parameters = new ArrayList<Parameter>();
  private String returnType;
  private List<String> exceptionTypes = new ArrayList<String>();

  @Override
  public void setMethodName(String methodName)
  {
    this.name = methodName;
  }

  @Override
  public void setReturnType(String returnType)
  {
    this.returnType = returnType;
  }

  @Override
  public void addParameter(String parameterType, String parameterName)
  {
    this.parameters.add(new Parameter(parameterType, parameterName));
  }

  @Override
  public void addExceptionType(String exceptionType)
  {
    this.exceptionTypes.add(exceptionType);
  }

  String getMethodName()
  {
    return name;
  }

  boolean hasParameters()
  {
    return !this.parameters.isEmpty();
  }

  List<Parameter> getParameters()
  {
    return parameters;
  }

  String getReturnType()
  {
    return returnType;
  }

  List<String> getExceptionTypes()
  {
    return exceptionTypes;
  }
}
