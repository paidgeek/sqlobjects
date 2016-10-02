package com.moybl.sqlobjects;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.*;

public class Util {

  public static void writeTemplateFile(String templateName, JtwigModel model, File outputDirectory, String fileName) throws Exception {
    JtwigTemplate template = JtwigTemplate.classpathTemplate(templateName);

    File path = new File(outputDirectory
      .getAbsolutePath());
    if (!path.exists()) {
      path.mkdirs();
    }

    File file = new File(path, fileName);

    if (!file.exists()) {
      file.createNewFile();
    }

    System.out.println("Generating: " + file.getAbsolutePath());
    try (FileOutputStream fos = new FileOutputStream(file)) {
      template.render(model, fos);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

}
