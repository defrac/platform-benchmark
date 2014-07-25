package defrac.benchmark.havlak;

import defrac.lang.Lists;

import java.util.ArrayList;

import static defrac.benchmark.Havlak.mix;

class SimpleLoop {
  private final ArrayList<BasicBlock> basicBlocks = Lists.newArrayList();
  private final ArrayList<SimpleLoop> children = Lists.newArrayList();
  private final int counter;

  private BasicBlock header;

  private boolean isRoot = false;
  boolean isReducible = true;
  private int nestingLevel = 0;

  SimpleLoop(final int counter) {
    this.counter = counter;
  }

  void addNode(BasicBlock bb) { basicBlocks.add(bb); }
  void addChildLoop(SimpleLoop loop) { children.add(loop); }

  void setParent(SimpleLoop p) {
    p.addChildLoop(this);
  }

  void setHeader(BasicBlock bb) {
    basicBlocks.add(bb);
    header = bb;
  }

  void setNestingLevel(final int level) {
    nestingLevel = level;
    if (level == 0) {
      isRoot = true;
    }
  }

  int checksum() {
    int result = counter;
    result = mix(result, isRoot ? 1 : 0);
    result = mix(result, isReducible ? 1 : 0);
    result = mix(result, nestingLevel);
    result = mix(result, 0);//depthlevel
    if (header != null) result = mix(result, header.name);
    for(final BasicBlock e : basicBlocks) {
      result = mix(result, e.name);
    }
    for(final SimpleLoop e : children) {
      result = mix(result, e.checksum());
    }
    return result;
  }
}
