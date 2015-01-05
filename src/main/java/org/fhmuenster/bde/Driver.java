package org.fhmuenster.bde;

import org.apache.hadoop.util.ProgramDriver;
import org.fhmuenster.bde.morphline.MorphlineDemo;
import org.fhmuenster.bde.mr.tfidf.TFIDFJobController;

public class Driver {

  public static void main(String argv[]) {
    int exitCode = -1;
    ProgramDriver pgd = new ProgramDriver();
    try {
      pgd.addClass("tfidf", TFIDFJobController.class,
        "MapReduce program to compute TF-IDF of input text files.");
      pgd.addClass("testmorphline", MorphlineDemo.class, "Run a morphline locally.");
      pgd.driver(argv);
      // Success
      exitCode = 0;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    System.exit(exitCode);
  }
}
