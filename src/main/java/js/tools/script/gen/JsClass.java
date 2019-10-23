package js.tools.script.gen;

import java.io.IOException;
import java.io.Writer;

/**
 * Script class.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
public interface JsClass
{
  JsMethod createMethod();

  void setQualifiedClassName(String qualifiedClassName);

  void addMethod(JsMethod method);

  boolean hasMethods();

  String getPackageName();

  String getClassName();

  void serialize(Writer writer) throws IOException;
}
