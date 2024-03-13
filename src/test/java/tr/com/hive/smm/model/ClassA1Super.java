package tr.com.hive.smm.model;

import tr.com.hive.smm.mapping.annotation.MongoEntity;

public class ClassA1Super {

  public String varStringSuper;

  protected int varIntSuper;

  public void setVarIntSuper(int varIntSuper) {
    this.varIntSuper = varIntSuper;
  }


  public int getVarIntSuper() {
    return varIntSuper;
  }

}