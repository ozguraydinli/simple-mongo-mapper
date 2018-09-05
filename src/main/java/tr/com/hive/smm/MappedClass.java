package tr.com.hive.smm;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tr.com.hive.smm.mapping.annotation.MongoEntity;
import tr.com.hive.smm.mapping.annotation.index.Index;
import tr.com.hive.smm.mapping.annotation.index.Indexes;

public class MappedClass {

  private Class<?> clazz;

  private MongoEntity mongoEntity;

  private String collectionName;

  private List<Index> indexes = Lists.newArrayList();

  public MappedClass(Class<?> clazz) {
    this.clazz = clazz;

    mongoEntity = clazz.getAnnotation(MongoEntity.class);

    String mongoEntityValue = mongoEntity.value();
    if (Strings.isNullOrEmpty(mongoEntityValue)) {
      collectionName = clazz.getSimpleName();
    } else {
      collectionName = mongoEntityValue;
    }

    collectIndexes();
  }

  private void collectIndexes() {
    if (!clazz.isAnnotationPresent(Indexes.class)) {
      return;
    }

    Indexes annotationIndexes = clazz.getAnnotation(Indexes.class);

    Index[] annotationIndexesValue = annotationIndexes.value();
    if (annotationIndexesValue.length == 0) {
      return;
    }

    indexes.addAll(Arrays.asList(annotationIndexesValue));
  }

  public List<Index> getIndexes() {
    return Collections.unmodifiableList(indexes);
  }

  public String getCollectionName() {
    return collectionName;
  }

}

