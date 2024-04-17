package tr.com.hive.smm.model;

import com.google.common.collect.Lists;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.MongoId;
import tr.com.hive.smm.mapping.annotation.MongoTransient;

@MongoEntity
public class ClassBRef {

  @MongoId
  public ObjectId id;

  public String varString;

  public int varInt;

  public Date varDate;

  public MyEnum varEnum;

  public List<String> varListOfString;

  public HashMap<Integer, MyEnum> varMapOfIntToEnum;

  @MongoTransient
  public List<Integer> varTransientList;

  public static Document createDocument(int i, Date date) {
    Document document = new Document();

    document.put("id", new ObjectId());
    document.put("varString", "s" + i);
    document.put("varInt", i);
    document.put("varDate", date);
    document.put("varEnum", MyEnum.En2.name());
    document.put("varListOfString", Lists.newArrayList("b" + i, "bb" + i));

    Document documentMap = new Document();
    documentMap.put("1", MyEnum.En1.name());
    documentMap.put("2", MyEnum.En2.name());

    document.put("varMapOfIntToEnum", documentMap);

    return document;
  }

  public static ClassBRef create(int i) {
    ClassBRef classB = new ClassBRef();
    classB.varString = "s" + i;
    classB.varInt = i;
    classB.varDate = new Date();
    classB.varEnum = MyEnum.En2;
    classB.varListOfString = Lists.newArrayList("b" + i, "bb" + i);

    return classB;
  }

  public static ClassBRef create(ObjectId id, int i) {
    ClassBRef classB = new ClassBRef();
    classB.id = id;
    classB.varString = "s" + i;
    classB.varInt = i;
    classB.varDate = new Date();
    classB.varEnum = MyEnum.En2;
    classB.varListOfString = Lists.newArrayList("b" + i, "bb" + i);
    classB.varTransientList = List.of(1, 2, 3);

    return classB;
  }

  public ClassBRef() {
  }

  public ClassBRef(ObjectId id) {
    this.id = id;
  }

}
