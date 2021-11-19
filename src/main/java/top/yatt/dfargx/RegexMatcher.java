package top.yatt.dfargx;

import java.util.ArrayList;
import java.util.List;
import top.yatt.dfargx.automata.DFA;
import top.yatt.dfargx.automata.NFA;
import top.yatt.dfargx.tree.SyntaxTree;
import top.yatt.dfargx.tree.node.Node;

/**
 * Created on 2015/5/11.
 */
public class RegexMatcher {
    private int[][] transitionTable;
    private int is;
    private int rs;
    private boolean[] fs;

  public int[][] getTransitionTable() {
    return transitionTable;
  }

  public int getIs() {
    return is;
  }

  public int getRs() {
    return rs;
  }

  public boolean[] getFs() {
    return fs;
  }

  public static class TreePrinter {

    /**
     * Print a tree
     *
     * @param root tree root node
     */
    public static void print(Node root) {
      List<List<String>> lines = new ArrayList<List<String>>();

      List<Node> level = new ArrayList<Node>();
      List<Node> next = new ArrayList<Node>();

      level.add(root);
      int nn = 1;

      int widest = 0;

      while (nn != 0) {
        List<String> line = new ArrayList<String>();

        nn = 0;

        for (Node n : level) {
          if (n == null) {
            line.add(null);

            next.add(null);
            next.add(null);
          } else {
            String aa = n.toString();
            line.add(aa);
            if (aa.length() > widest) {
              widest = aa.length();
            }

            next.add(n.left());
            next.add(n.right());

            if (n.left() != null) {
              nn++;
            }
            if (n.right() != null) {
              nn++;
            }
          }
        }

        if (widest % 2 == 1) {
          widest++;
        }

        lines.add(line);

        List<Node> tmp = level;
        level = next;
        next = tmp;
        next.clear();
      }

      int perpiece = lines.get(lines.size() - 1).size() * (widest + 4);
      for (int i = 0; i < lines.size(); i++) {
        List<String> line = lines.get(i);
        int hpw = (int) Math.floor(perpiece / 2f) - 1;

        if (i > 0) {
          for (int j = 0; j < line.size(); j++) {

            // split node
            char c = ' ';
            if (j % 2 == 1) {
              if (line.get(j - 1) != null) {
                c = (line.get(j) != null) ? '┴' : '┘';
              } else {
                if (j < line.size() && line.get(j) != null) {
                  c = '└';
                }
              }
            }
            System.out.print(c);

            // lines and spaces
            if (line.get(j) == null) {
              for (int k = 0; k < perpiece - 1; k++) {
                System.out.print(" ");
              }
            } else {

              for (int k = 0; k < hpw; k++) {
                System.out.print(j % 2 == 0 ? " " : "─");
              }
              System.out.print(j % 2 == 0 ? "┌" : "┐");
              for (int k = 0; k < hpw; k++) {
                System.out.print(j % 2 == 0 ? "─" : " ");
              }
            }
          }
          System.out.println();
        }

        // print line of numbers
        for (int j = 0; j < line.size(); j++) {

          String f = line.get(j);
          if (f == null) {
            f = "";
          }
          int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
          int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

          // a number
          for (int k = 0; k < gap1; k++) {
            System.out.print(" ");
          }
          System.out.print(f);
          for (int k = 0; k < gap2; k++) {
            System.out.print(" ");
          }
        }
        System.out.println();

        perpiece /= 2;
      }
    }
  }

  public RegexMatcher(String regex) {
    compile(regex);
  }

  private void compile(String regex) {
    SyntaxTree syntaxTree = new SyntaxTree(regex);

    NFA nfa = new NFA(syntaxTree.getRoot());
    DFA dfa = new DFA(nfa.asBitmapStateManager());
    transitionTable = dfa.getTransitionTable();
    is = dfa.getInitState();
    fs = dfa.getFinalStates();
    rs = dfa.getRejectedState();
  }

  public boolean match(String str) {
    int s = is;
    for (int i = 0, length = str.length(); i < length; i++) {
      char ch = str.charAt(i);
            s = transitionTable[s][ch];
            if (s == rs) {
                return false; // fast failed using rejected state
            }
        }
        return fs[s];
    }


}
