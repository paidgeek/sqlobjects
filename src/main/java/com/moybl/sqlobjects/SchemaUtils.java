package com.moybl.sqlobjects;

import com.moybl.sidl.Token;
import com.moybl.sidl.ast.*;

public class SchemaUtils {

  public String toUpperCase(String s) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < s.length(); i++) {
      char ch = s.charAt(i);

      if (i > 0 && Character.isUpperCase(ch)) {
        sb.append("_");
      }

      sb.append(Character.toUpperCase(ch));
    }

    return sb.toString();
  }

  public String toSnakeCase(String s) {
    return toUpperCase(s).toLowerCase();
  }

  public String toCamelCase(String s) {
    return Character.toLowerCase(s.charAt(0)) + s.substring(1);
  }

  public String getClassName(Object obj) {
    return obj.getClass().getSimpleName();
  }

  public boolean isScalarType(Type type) {
    PrimaryType pt = null;

    if (type instanceof PrimaryType) {
      pt = (PrimaryType) type;
    } else if (type instanceof ArrayType) {
      pt = ((ArrayType) type).getType();
    } else {
      return false;
    }

    return pt.getDefinition() instanceof StructDefinition || pt
      .getDefinition() instanceof EnumDefinition || isScalarToken(pt.getToken());
  }

  public boolean isScalarToken(Token token) {
    return token == Token.TYPE_BOOL ||
      token == Token.TYPE_INT8 ||
      token == Token.TYPE_INT16 ||
      token == Token.TYPE_INT32 ||
      token == Token.TYPE_INT64 ||
      token == Token.TYPE_UINT8 ||
      token == Token.TYPE_UINT16 ||
      token == Token.TYPE_UINT32 ||
      token == Token.TYPE_UINT64 ||
      token == Token.TYPE_FLOAT32 ||
      token == Token.TYPE_FLOAT64;
  }

  public boolean hasAttribute(Definition definition, String name) {
    return definition.getAttributes().containsKey(name);
  }

}
