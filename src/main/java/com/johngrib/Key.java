package com.johngrib;

public class Key {
  private final int i;

  public Key(int i) {
    this.i = i;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Key key = (Key) o;
    return i == key.i;
  }

  @Override
  public String toString() {
    return "Key:" + i;
  }

  @Override
  public int hashCode() {
    return i % 3;
  }
}
