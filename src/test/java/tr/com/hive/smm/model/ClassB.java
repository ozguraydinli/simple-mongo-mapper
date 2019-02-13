package tr.com.hive.smm.model;

import com.google.common.collect.Lists;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;

@MongoEntity
public class ClassB {

  @MongoId
  public ObjectId id;

  public String varString;

  public int varInt;

  public Date varDate;

  public MyEnum varEnum;

  public List<String> varListOfString;

  public static Document createDocument(int i, Date date) {
    Document document = new Document();

    document.put("id", new ObjectId());
    document.put("varString", "s" + i);
    document.put("varInt", i);
    document.put("varDate", date);
    document.put("varEnum", MyEnum.En2.name());
    document.put("varListOfString", Lists.newArrayList("b" + i, "bb" + i));

    return document;
  }

  public static ClassB create(int i) {
    ClassB classB = new ClassB();
    classB.varString = "s" + i;
    classB.varInt = i;
    classB.varDate = new Date();
    classB.varEnum = MyEnum.En2;
    classB.varListOfString = Lists.newArrayList("b" + i, "bb" + i);

    return classB;
  }

  public static ClassB create(ObjectId id, int i) {
    ClassB classB = new ClassB();
    classB.id = id;
    classB.varString = "s" + i;
    classB.varInt = i;
    classB.varDate = new Date();
    classB.varEnum = MyEnum.En2;
    classB.varListOfString = Lists.newArrayList("b" + i, "bb" + i);

    return classB;
  }

  public ClassB() {
  }

  public ClassB(ObjectId id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ClassB)) {
      return false;
    }

    ClassB classB = (ClassB) o;

    return id != null ? id.equals(classB.id) : classB.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

}
