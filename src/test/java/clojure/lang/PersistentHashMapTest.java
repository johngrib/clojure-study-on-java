package clojure.lang;

import org.junit.jupiter.api.Test;

public class PersistentHashMapTest {

  @Test
  public void it () {

    Object[] list = new Object[40];
    for (int i = 0; i < 20; i += 2) {
      list[i] = Keyword.intern("Key: " + String.valueOf(i));
      list[i+1] = i;
    }

    ISeq seq = PersistentHashMap.NodeSeq.create(list);

    PersistentHashMap map = PersistentHashMap.create(seq);

    PersistentHashMap.INode node = map.root;
    System.out.println(node);
  }
}
