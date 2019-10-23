package js.tools.script.gen;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Script class implementation.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
final class JsClassImpl implements JsClass
{
  private String qualifiedClassName;
  private String packageName;
  private String className;
  private List<JsMethodImpl> methods = new ArrayList<JsMethodImpl>();

  @Override
  public JsMethod createMethod()
  {
    return new JsMethodImpl();
  }

  @Override
  public void setQualifiedClassName(String qualifiedClassName)
  {
    this.qualifiedClassName = qualifiedClassName;
    int classNameSeparatorIndex = qualifiedClassName.lastIndexOf('.');
    if(classNameSeparatorIndex == -1) {
      throw new IllegalArgumentException("Invalid qualified class name: " + qualifiedClassName);
    }
    this.packageName = qualifiedClassName.substring(0, classNameSeparatorIndex);
    this.className = qualifiedClassName.substring(classNameSeparatorIndex + 1);
  }

  @Override
  public void addMethod(JsMethod method)
  {
    this.methods.add((JsMethodImpl)method);
  }

  @Override
  public boolean hasMethods()
  {
    return !this.methods.isEmpty();
  }

  @Override
  public String getPackageName()
  {
    return packageName;
  }

  @Override
  public String getClassName()
  {
    return className;
  }

  @Override
  public void serialize(Writer writer) throws IOException
  {
    // Velocity script has a bug and for now cannot be integrated in production
    writer.append(Script.generate(this));
  }

  String getQualifiedClassName()
  {
    return qualifiedClassName;
  }

  List<JsMethodImpl> getMethods()
  {
    return methods;
  }
}
