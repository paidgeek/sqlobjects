package com.moybl.sqlobjects;

import com.moybl.sidl.ast.Definition;

import org.jtwig.JtwigModel;

import java.io.File;
import java.util.List;

public class MySqlWriter {

  public static void write(List<Definition> definitions, File outputDirectory, MySqlSchemaUtils utils) throws Exception {
    JtwigModel model = JtwigModel.newModel()
      .with("definitions", definitions)
      .with("utils", utils);
    Util.writeTemplateFile("mysql/schema.twig", model, outputDirectory, "generated.sql");
  }

}
