package tr.com.hive.smm.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tr.com.hive.smm.mapping.Converter;
import tr.com.hive.smm.mapping.MappingException;

public class MyCustomConverter implements Converter {

  @SuppressWarnings("unchecked")
  @Override
  public Object decode(Object obj) throws MappingException {
    Document document = (Document) obj;

    HashMap<String, ArrayList<Map<String, String>>> tableValues = Maps.newHashMap();

    for (String mapKey : document.keySet()) {

      ArrayList<Map<String, String>> list = Lists.newArrayList();
      List<Document> listValues = (List<Document>) document.get(mapKey);
      for (Document listValue : listValues) {
        Map<String, String> map = Maps.newHashMap();

        for (String key : listValue.keySet()) {
          // key ozel key
//          String decodedKey = key.replaceAll("U\\+FF04", "\\$")
//                                 .replaceAll("U\\+FF0E", "\\.");

          map.put(key + "123", listValue.getString(key));
        }

        list.add(map);
      }

      tableValues.put(mapKey, list);
    }

    return tableValues;
  }

  @SuppressWarnings("unchecked")
  @Override
  public BsonValue encode(Object obj) throws MappingException {
    HashMap<String, ArrayList<Map<String, String>>> map = (HashMap<String, ArrayList<Map<String, String>>>) obj;

    if (map == null) {
      map = Maps.newHashMap();
    }

    if (map.size() == 0) {
      return null;
    }

    BsonDocument bsonDocument = new BsonDocument();

    for (String o : map.keySet()) {
      // table row
      BsonArray bsonArray = new BsonArray();

      ArrayList<Map<String, String>> value = map.get(o);
      for (Map<String, String> valueMap : value) {
        BsonDocument bsonDocumentInner = new BsonDocument();

        for (String key : valueMap.keySet()) {
          // key ozel key
//          String encodedKey = key.replaceAll("\\$", "U\\+FF04")
//                                 .replaceAll("\\.", "U\\+FF0E");

          bsonDocumentInner.put(key + "123", new BsonString(valueMap.get(key)));
        }

        bsonArray.add(bsonDocumentInner);
      }

      bsonDocument.put(o, bsonArray);
    }

    return bsonDocument;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object encodeToDocument(Object obj) throws MappingException {
    HashMap<String, ArrayList<Map<String, String>>> map = (HashMap<String, ArrayList<Map<String, String>>>) obj;

    if (map == null) {
      map = Maps.newHashMap();
    }

    if (map.size() == 0) {
      return null;
    }

    Document document = new Document();

    for (String o : map.keySet()) {
      // table row
      ArrayList<Document> valueDocument = Lists.newArrayList();

      ArrayList<Map<String, String>> value = map.get(o);
      for (Map<String, String> valueMap : value) {
        Document documentInner = new Document();

        for (String key : valueMap.keySet()) {
          // key ozel key
          String encodedKey = key.replaceAll("\\$", "U\\+FF04")
                                 .replaceAll("\\.", "U\\+FF0E");

          documentInner.put(encodedKey, valueMap.get(key));
        }

        valueDocument.add(documentInner);
      }

      document.put(o, valueDocument);
    }

    return document;
  }

}