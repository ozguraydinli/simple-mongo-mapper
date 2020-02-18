# simple-mongo-mapper v0.5

A simple Java object mapper for MongoDB documents.

```java
@MongoEntity
public class MyClass {

  @MongoId
  private ObjectId id;

  .
  .
  .

}

MyClass myClass =  new SimpleMongoMapper().fromDocument(document, MyClass.class);

```
```java
MyClass myClass = new MyClass();
Document document =  new SimpleMongoMapper().toDocument(myClass);
```
```java
MyClass myClass = new MyClass();
BsonValue bsonValue = new SimpleMongoMapper().toBsonValue(myClass);
```
    
#### Annotations

```java
@MongoEntity // Marks a class as an entity

@MongoEntity("anotherName") // Use this version if you want to map from a specific name
```

```java
@MongoId // Marks the id field
```

```java
@MongoRef // Maps from a DBRef field, only populates the id field of the reference field
```

```java
@MongoTransient // ignores the field when mapping
```

