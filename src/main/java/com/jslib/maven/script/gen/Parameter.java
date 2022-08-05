package com.jslib.maven.script.gen;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Parameter of script class method.
 * 
 * @author Iulian Rotaru
 * @since 1.0
 */
final class Parameter
{
  private String type;
  private String name;

  public Parameter(String type, String name)
  {
    assert type != null;
    this.type = type;
    this.name = name;
  }

  String getType()
  {
    return type;
  }

  String getName()
  {
    return name;
  }

  private static final Collection<String> STRINGS = new ArrayList<String>();
  static {
    STRINGS.add("java.lang.String");
    STRINGS.add("java.lang.CharSequence");
    STRINGS.add("java.io.File");
    STRINGS.add("java.net.URL");
  }

  public boolean isString()
  {
    return STRINGS.contains(type);
  }

  private static final Collection<String> NUMBERS = new ArrayList<String>();
  static {
    NUMBERS.add("java.lang.Number");
    NUMBERS.add("java.lang.Byte");
    NUMBERS.add("byte");
    NUMBERS.add("java.lang.Short");
    NUMBERS.add("short");
    NUMBERS.add("java.lang.Integer");
    NUMBERS.add("int");
    NUMBERS.add("java.lang.Long");
    NUMBERS.add("long");
    NUMBERS.add("java.lang.Float");
    NUMBERS.add("float");
    NUMBERS.add("java.lang.Double");
    NUMBERS.add("double");
  }

  public boolean isNumber()
  {
    return NUMBERS.contains(type);
  }

  private static final Collection<String> BOOLEANS = new ArrayList<String>();
  static {
    BOOLEANS.add("java.lang.Boolean");
    BOOLEANS.add("boolean");
  }

  public boolean isBoolean()
  {
    return BOOLEANS.contains(type);
  }

  private static final Collection<String> DATES = new ArrayList<String>();
  static {
    DATES.add("java.util.Date");
    DATES.add("java.sql.Date");
    DATES.add("java.sql.Time");
    DATES.add("java.sql.Timestamp");
  }

  public boolean isDate()
  {
    return DATES.contains(type);
  }

  public boolean isArray()
  {
    return type.contains("[]");
  }
}
