package tr.com.hive.smm.model;

public enum MyComplexEnum {
  ComplexEn1("complex enum 1", "value 1"), ComplexEn2("complex enum 2", "value 2");

  private String name;
  private String value;

  MyComplexEnum(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }
}