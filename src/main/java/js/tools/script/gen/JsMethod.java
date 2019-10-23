package js.tools.script.gen;

/**
 * Script class method.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
public interface JsMethod
{
  void setMethodName(String methodName);

  void setReturnType(String returnType);

  void addParameter(String parameterType, String parameterName);

  void addExceptionType(String exceptionType);
}