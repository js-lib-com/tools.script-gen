package js.tools.script.gen;

/**
 * Script generator builder.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
public final class Builder
{
  /**
   * Create a new script class.
   * 
   * @return newly created script class.
   */
  public static JsClass createJsClass()
  {
    return new JsClassImpl();
  }

  /**
   * Prevent default constructor synthesis.
   */
  private Builder()
  {
  }
}
