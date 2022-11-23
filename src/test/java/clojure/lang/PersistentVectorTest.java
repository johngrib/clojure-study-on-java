package clojure.lang;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("PersistentVector")
class PersistentVectorTest {

  List<String> rangeItems(int start, int end) {
    return range(start, end)
        .mapToObj(i -> "item" + i)
        .collect(Collectors.toList());
  }

  @Nested
  @DisplayName("create")
  class DescribeCreate {
    @Nested
    @DisplayName("1개 ~ 32개의 아이템이 주어지면")
    class Context_range_1_32_item {
      final List<List<String>> givenItems = IntStream.range(1, 34)
          .mapToObj(num -> rangeItems(1, num))
          .collect(Collectors.toList());

      @Test
      @DisplayName("비어 있는 root 노드와 주어진 아이템들로 이루어진 tail 배열을 갖는 벡터를 생성해 리턴한다")
      void it_returns_vector() {
        for (List<String> items : givenItems) {
          final PersistentVector vector = PersistentVector.create(items);

          assertEquals(PersistentVector.EMPTY_NODE, vector.root,
              "루트 노드는 비어 있어야 합니다.");

          Assertions.assertAll(
              "tail 배열은 주어진 아이템들을 그대로 갖고 있어야 합니다.",
              () -> assertEquals(items.size(), vector.tail.length),
              () -> {
                for (int i = 0; i < vector.tail.length; i++) {
                  assertEquals(items.get(i), vector.tail[i]);
                }
              }
          );
        }
      }
    }

    @Nested
    @DisplayName("90개의 아이템이 주어지면")
    class Context_90_item {
      final List<String> givenItems = rangeItems(1, 91);

      @Test
      @DisplayName("root 아이템 2개와 tail을 갖는 벡터를 생성해 리턴한다")
      void it_has_items_in_tail() {
        final PersistentVector vector = PersistentVector.create(givenItems);

        root_0_check:
        {
          int shift = vector.shift;
          assertTrue(vector.root.array[0] instanceof PersistentVector.Node);
          PersistentVector.Node root_0 = (PersistentVector.Node) vector.root.array[0];

          final Object[] root_0_array = root_0.array;

          assertEquals(32, root_0.array.length, "root 0의 아이템 개수는 32개여야 한다.");
          assertEquals(root_0_array[0], "item1");
          assertEquals(root_0_array[31], "item32");
        }
        root_1_check:
        {
          assertTrue(vector.root.array[1] instanceof PersistentVector.Node);
          PersistentVector.Node root_1 = (PersistentVector.Node) vector.root.array[1];

          final Object[] root_1_array = root_1.array;

          assertEquals(32, root_1.array.length, "root 1의 아이템 개수는 32개여야 한다.");
          assertEquals(root_1_array[0], "item33");
          assertEquals(root_1_array[31], "item64");
        }
        tail_check:
        {
          final Object[] tail = vector.tail;

          assertEquals(26, tail.length, "tail의 아이템 수는 26개여야 한다. (32 + 32 + 26 = 90)");
          assertEquals("item65", tail[0]);
          assertEquals("item90", tail[25]);
        }
      }
    }

    @Nested
    @DisplayName("1057 개의 아이템이 주어지면")
    class Context_1057_item {
      final List<String> givenItems = rangeItems(1, 1057 + 1);

      //            @Test
      @DisplayName("tree의 depth가 2인 root 와 tail을 갖는 벡터를 생성해 리턴한다")
      void it_has_items_in_tail() {
        final PersistentVector vector = PersistentVector.create(givenItems);

        PersistentVector.Node root = vector.root;
        Object[] tail = vector.tail;

        PersistentVector.Node root_0 = (PersistentVector.Node) root.array[0];
        assertEquals(32, root_0.array.length);

        PersistentVector.Node root_0_0 = (PersistentVector.Node) root_0.array[0];
        assertEquals(32, root_0_0.array.length);

        final Object[] root_0_0_array = root_0_0.array;

        assertEquals(32, root_0.array.length, "root 0의 아이템 개수는 32개여야 한다.");
        assertEquals(root_0_0_array[0], "item1");
        assertEquals(root_0_0_array[31], "item32");

        PersistentVector.Node root_31 = (PersistentVector.Node) root.array[30];
        assertEquals(32, root_31.array.length);

      }
    }

    @Nested
    @DisplayName("n 개의 아이템이 주어지면")
    class Context_n_item {
      final List<String> givenItems = rangeItems(1, 33554464 + 1);

      @Test
      @DisplayName("tree의 depth가 2인 root 와 tail을 갖는 벡터를 생성해 리턴한다")
      void it_has_items_in_tail() {
        final PersistentVector vector = PersistentVector.create(givenItems);

        PersistentVector.Node root = vector.root;
        Object[] tail = vector.tail;

        PersistentVector.Node root_0 = (PersistentVector.Node) root.array[0];
        assertEquals(32, root_0.array.length);

      }
    }
  }

  @Nested
  @DisplayName("tailoff")
  class Describe_tailoff {
    // 33개 ~ 200개 아이템을 가진 리스트들의 리스트
    final List<List<String>> givenItems = IntStream.range(34, 201)
        .mapToObj(num -> rangeItems(1, num))
        .collect(Collectors.toList());

    @Test
    @DisplayName("올바른 tailoff 값을 리턴한다")
    void it_returns_vector_with_tail() {
      for (List<String> items : givenItems) {
        final PersistentVector vector = PersistentVector.create(items);

        final int cnt = items.size() - 1;
        final int expectTailoff = cnt - (cnt % 32);
        assertEquals(expectTailoff, vector.tailoff(),
            "tailoff 값은 cnt(아이템의 수 - 1) 미만인 32의 배수 중 최대값이어야 합니다.");
        /*
         * cnt     | tailoff
         * ------- | -------
         *  0 ~ 32 | 0
         * 33 ~ 64 | 32
         * 65 ~ 96 | 64
         */
        assertEquals(vector.tail.length, vector.size() - expectTailoff,
            "tail의 길이는 전체 아이템의 수에서 tailoff를 뺀 수입니다");
      }
    }
  }


  @Nested
  @DisplayName("subvec")
  class DescribeSubvec {

    final List<String> givenItems = rangeItems(1, 9001);

    @Test
    void it() {
      PersistentVector vector = PersistentVector.create(givenItems);

      vector.cons("ITEM.....");

      PersistentVector.Node root_a = vector.root;

      Object[] tail = vector.tail;

      List<Integer> numbers = new ArrayList<>();
      for (int i = 0; i < 32801; i++) {
        numbers.add(i);
      }

    }

  }
}

