package tr.com.hive.smm.model;

public enum MyMoreComplexEnum {
  MoreComplexEn1("complex enum 1") {
    @Override
    public String getValue() {
      return "value 1";
    }
  },
  MoreComplexEn2("complex enum 2") {
    @Override
    public String getValue() {
      return "value 2";
    }
  };

  private String name;

  MyMoreComplexEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public abstract String  getValue();
}