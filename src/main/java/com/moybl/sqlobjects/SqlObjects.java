package com.moybl.sqlobjects;

import com.moybl.sidl.*;
import com.moybl.sidl.ast.*;
import com.moybl.sidl.semantics.NameLinker;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;

public class SqlObjects {

  private static final String[] SUPPORTED_DATABASES = {"mysql"};

  public static void main(String[] args) {
    Option inputOption = new Option("i", "input", true, "input directory");
    inputOption.setRequired(true);
    Option outputOption = new Option("o", "output", true, "output directory");
    outputOption.setRequired(false);
    Option dbOption = new Option("db", "db", true, "Target database");
    dbOption.setRequired(true);

    Options options = new Options();
    options.addOption(inputOption);
    options.addOption(outputOption);
    options.addOption(dbOption);

    CommandLineParser cmdParser = new DefaultParser();
    HelpFormatter helpFormatter = new HelpFormatter();
    CommandLine cmd = null;

    try {
      cmd = cmdParser.parse(options, args);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }

    String db = cmd.getOptionValue("db");
    if (!Arrays.asList(SUPPORTED_DATABASES).contains(db)) {
      System.err.printf("Unsupported database: '%s'\n", db);
      System.exit(1);
    }

    File inputDirectory = new File(cmd.getOptionValue("input"));
    List<Definition> definitions = parseDefinitions(inputDirectory);

    if (definitions.isEmpty()) {
      System.err.printf("No '.sidl' files found in '%s'\n", inputDirectory.getAbsolutePath());
      System.exit(1);
    }

    File outputDirectory = new File(cmd
      .getOptionValue("output", inputDirectory + File.separator + "sqlobjects"));

    try {
      if (db.equals("mysql")) {
        MySqlSchemaUtils utils = new MySqlSchemaUtils();
        MySqlWriter.write(definitions, outputDirectory, utils);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static List<Definition> parseDefinitions(File inputDirectory) {
    List<Definition> definitions = new ArrayList<Definition>();

    if (inputDirectory.isFile()) {
      FileInputStream fis = null;

      if (!inputDirectory.getName().endsWith("sidl")) {
        return definitions;
      }

      try {
        fis = new FileInputStream(inputDirectory);
        Lexer lexer = new Lexer(new BufferedInputStream(fis));
        com.moybl.sidl.Parser parser = new com.moybl.sidl.Parser(lexer);

        List<Definition> parsedDefinitions = parser.parse().getDefinitions();
        definitions.addAll(parsedDefinitions);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException e) {
          }
        }
      }
    } else {
      Iterator<File> fileIterator = FileUtils.iterateFiles(inputDirectory, null, true);

      while (fileIterator.hasNext()) {
        File file = fileIterator.next();
        if (!file.getName().endsWith("sidl")) {
          continue;
        }

        FileInputStream fis = null;

        try {
          fis = new FileInputStream(file);
          Lexer lexer = new Lexer(new BufferedInputStream(fis));
          com.moybl.sidl.Parser parser = new com.moybl.sidl.Parser(lexer);

          List<Definition> parsedDefinitions = parser.parse().getDefinitions();
          definitions.addAll(parsedDefinitions);

        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } finally {
          if (fis != null) {
            try {
              fis.close();
            } catch (IOException e) {
            }
          }
        }
      }
    }

    Position p = null;

    if (definitions.size() != 0) {
      Position a = definitions.get(0).getPosition();
      Position b = definitions.get(definitions.size() - 1).getPosition();
      p = Position.expand(a, b);
    }

    Document document = new Document(p, definitions);
    document.accept(new NameLinker());

    for (int i = 0; i < definitions.size(); i++) {
      Definition d = definitions.get(i);

      if (d instanceof NamespaceDefinition) {
        throw new ParserException(d.getPosition(), "Namespaces not allowed");
      } else if (d instanceof InterfaceDefinition) {
        throw new ParserException(d.getPosition(), "Interfaces not allowed");
      } else if (d instanceof StructDefinition) {
        throw new ParserException(d.getPosition(), "Structs not allowed");
      }
    }

    return definitions;
  }

}
