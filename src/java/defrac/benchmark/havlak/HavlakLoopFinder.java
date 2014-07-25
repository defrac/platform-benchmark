package defrac.benchmark.havlak;

import defrac.lang.Lists;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class HavlakLoopFinder {
  private static final int BB_NONHEADER    = 1; // a regular BB
  private static final int BB_REDUCIBLE    = 2; // reducible loop
  private static final int BB_SELF         = 3; // single BB loop
  private static final int BB_IRREDUCIBLE  = 4; // irreducible loop
  private static final int BB_DEAD         = 5; // a dead BB

  // Marker for uninitialized nodes.
  private static final int UNVISITED = -1;

  // Safeguard against pathologic algorithm behavior.
  private static final int MAXNONBACKPREDS = (32 * 1024);

  @Nonnull
  private final CFG cfg;

  @Nonnull
  private final LSG lsg;

  public HavlakLoopFinder(@Nonnull final CFG cfg, @Nonnull final LSG lsg) {
    this.cfg = cfg;
    this.lsg = lsg;
  }

  //
  // IsAncestor
  //
  // As described in the paper, determine whether a node 'w' is a
  // "true" ancestor for node 'v'.
  //
  // Dominance can be tested quickly using a pre-order trick
  // for depth-first spanning trees. This is why DFS is the first
  // thing we run below.
  //
  private boolean isAncestor(int w, int v, @Nonnull List<Integer> last) {
    return (w <= v) && (v <= last.get(w));
  }

  //
  // DFS - Depth-First-Search
  //
  // DESCRIPTION:
  // Simple depth first traversal along out edges with node numbering.
  //
  int DFS(@Nonnull final BasicBlock currentNode,
          @Nonnull final ArrayList<UnionFindNode> nodes,
          @Nonnull final ArrayList<Integer> number,
          @Nonnull final ArrayList<Integer> last,
          final int current) {
    nodes.get(current).initNode(currentNode, current);
    number.set(currentNode.name, current);

    int lastid = current;
    final int length = currentNode.outEdges.size();
    for (int target = 0; target < length; target++) {
      if (number.get(currentNode.outEdges.get(target).name) == UNVISITED)
        lastid = DFS(currentNode.outEdges.get(target), nodes, number,
            last, lastid + 1);
    }

    last.set(number.get(currentNode.name), lastid);
    return lastid;
  }

  //
  // findLoops
  //
  // Find loops and build loop forest using Havlak's algorithm, which
  // is derived from Tarjan. Variable names and step numbering has
  // been chosen to be identical to the nomenclature in Havlak's
  // paper (which, in turn, is similar to the one used by Tarjan).
  //
  public int findLoops() {
    if (cfg.startNode == null) {
      return 0;
    }

    final int size = cfg.getNumNodes();

    final ArrayList<ArrayList<Integer>> nonBackPreds = Lists.newArrayList(size);
    final ArrayList<ArrayList<Integer>> backPreds = Lists.newArrayList(size);
    final ArrayList<Integer> number = Lists.newArrayList(size);
    final ArrayList<Integer> header = Lists.newArrayList(size);
    final ArrayList<Integer> types = Lists.newArrayList(size);
    final ArrayList<Integer> last = Lists.newArrayList(size);
    final ArrayList<UnionFindNode> nodes = Lists.newArrayList(size);

    for(int i = 0; i < size; ++i) {
      nonBackPreds.add(Lists.<Integer>newArrayList());
      backPreds.add(Lists.<Integer>newArrayList());
      number.add(UNVISITED);
      header.add(0);
      types.add(BB_NONHEADER);
      last.add(0);
      nodes.add(new UnionFindNode());
    }

    // Step a:
    //   - initialize all nodes as unvisited.
    //   - depth-first traversal and numbering.
    //   - unreached BB's are marked as dead.
    //
    DFS(cfg.startNode, nodes, number, last, 0);

    // Step b:
    //   - iterate over all nodes.
    //
    //   A backedge comes from a descendant in the DFS tree, and non-backedges
    //   from non-descendants (following Tarjan).
    //
    //   - check incoming edges 'v' and add them to either
    //     - the list of backedges (backPreds) or
    //     - the list of non-backedges (nonBackPreds)
    //
    for (int w = 0; w < size; ++w) {
      final BasicBlock nodeW = nodes.get(w).bb;
      if (nodeW == null) {
        types.set(w, BB_DEAD);
      } else {
        if (nodeW.getNumPred() > 0) {
          for (int nv = 0; nv < nodeW.inEdges.size(); ++nv) {
            BasicBlock nodeV = nodeW.inEdges.get(nv);
            int v = number.get(nodeV.name);
            if (v != UNVISITED) {
              if (isAncestor(w, v, last)) {
                backPreds.get(w).add(v);
              } else {
                nonBackPreds.get(w).add(v);
              }
            }
          }
        }
      }
    }

    // Step c:
    //
    // The outer loop, unchanged from Tarjan. It does nothing except
    // for those nodes which are the destinations of backedges.
    // For a header node w, we chase backward from the sources of the
    // backedges adding nodes to the set P, representing the body of
    // the loop headed by w.
    //
    // By running through the nodes in reverse of the DFST preorder,
    // we ensure that inner loop headers will be processed before the
    // headers for surrounding loops.
    //
    for (int w = size-1; w >=0; --w) {
      // this is 'P' in Havlak's paper
      final ArrayList<UnionFindNode> nodePool = Lists.newArrayList();

      final BasicBlock nodeW = nodes.get(w).bb;
      if (nodeW == null) {
        continue;
      }

      // Step d:
      for (int vi = 0; vi < backPreds.get(w).size(); ++vi) {
        final int v = backPreds.get(w).get(vi);
        if (v != w) {
          nodePool.add(nodes.get(v).findSet());
        } else {
          types.set(w, BB_SELF);
        }
      }

      // Copy nodePool to workList.
      //
      final LinkedList<UnionFindNode> workList = Lists.newLinkedList();
      final int length = nodePool.size();
      //noinspection ForLoopReplaceableByForEach
      for(int n = 0; n < length; ++n) {
        workList.add(nodePool.get(n));
      }

      if (!nodePool.isEmpty()) {
        types.set(w, BB_REDUCIBLE);
      }
      // work the list...
      //
      while (!workList.isEmpty()) {
        final UnionFindNode x = workList.removeFirst();

        // Step e:
        //
        // Step e represents the main difference from Tarjan's method.
        // Chasing upwards from the sources of a node w's backedges. If
        // there is a node y' that is not a descendant of w, w is marked
        // the header of an irreducible loop, there is another entry
        // into this loop that avoids w.
        //

        // The algorithm has degenerated. Break and
        // return in this case.
        //
        final int nonBackSize = nonBackPreds.get(x.dfsNumber).size();
        if (nonBackSize > MAXNONBACKPREDS) {
          return 0;
        }

        for (int iter=0; iter < nonBackPreds.get(x.dfsNumber).size(); ++iter) {
          final UnionFindNode y = nodes.get(nonBackPreds.get(x.dfsNumber).get(iter));
          final UnionFindNode ydash = y.findSet();

          if (!isAncestor(w, ydash.dfsNumber, last)) {
            types.set(w, BB_IRREDUCIBLE);
            nonBackPreds.get(w).add(ydash.dfsNumber);
          } else {
            if (ydash.dfsNumber != w) {
              if (nodePool.indexOf(ydash) == -1) {
                workList.add(ydash);
                nodePool.add(ydash);
              }
            }
          }
        }
      }

      // Collapse/Unionize nodes in a SCC to a single node
      // For every SCC found, create a loop descriptor and link it in.
      //
      if (!nodePool.isEmpty() || types.get(w) == BB_SELF) {
        final SimpleLoop loop = lsg.createNewLoop();

        loop.setHeader(nodeW);
        loop.isReducible = types.get(w) == BB_IRREDUCIBLE;

        // At this point, one can set attributes to the loop, such as:
        //
        // the bottom node:
        //    iter  = backPreds(w).begin();
        //    loop bottom is: nodes(iter).node;
        //
        // the number of backedges:
        //    backPreds(w).size()
        //
        // whether this loop is reducible:
        //    types(w) != BB_IRREDUCIBLE
        //
        nodes.get(w).loop = loop;

        final int length2 = nodePool.size();
        //noinspection ForLoopReplaceableByForEach
        for(int np = 0; np < length2; ++np) {
          final UnionFindNode node = nodePool.get(np);

          // Add nodes to loop descriptor.
          header.set(node.dfsNumber, w);
          node.union(nodes.get(w));

          // Nested loops are not added, but linked together.
          if (node.loop != null) {
            node.loop.setParent(loop);
          } else {
            loop.addNode(node.bb);
          }
        }
        lsg.addLoop(loop);
      } // nodePool.size()
    } // Step c

    return lsg.getNumLoops();
  } // findLoops
}
