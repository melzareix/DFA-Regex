package top.yatt.dfargx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

  public static void generateCppCheckerCode(RegexMatcher matcher, String filename)
    throws IOException {
    File file = new File("./out/" + filename + ".cpp");

    if (!file.exists()) {
      file.createNewFile();
    }

    PrintWriter pw = new PrintWriter(new FileWriter(file));

    pw.println("// #######################################################################");
    pw.println("// AUTOGENERATED FILE DON'T MODIFY.");
    pw.println("// FILE IS AUTOGENERATED FROM Main.java TO RECOGNIZE THE REGEX " + filename + ".");
    pw.println("// #######################################################################");
    pw.println("#include <iostream>");
    pw.println("#include <string>");
    pw.println();

    pw.println("int main(int argc, char* argv[]) {");
    pw.println("  int is = " + matcher.getIs() + ";");
    pw.println("  int rs = " + matcher.getRs() + ";");
    pw.print("  bool fs[" + matcher.getFs().length + "] = {");
    for (boolean v : matcher.getFs()) {
      pw.print(v + ",");
    }
    pw.println("};");

    pw.println("  int tt[" + matcher.getTransitionTable().length + "]["
      + matcher.getTransitionTable()[0].length + "] = {");
    for (int[] row : matcher.getTransitionTable()) {
      pw.print("    {");
      for (int e : row) {
        pw.print(e + ",");
      }
      pw.println("},");
    }
    pw.println("  };");

    pw.println("  std::string str(argv[1]);");

    // full matcher code
//    pw.println("int s = is;");
//    pw.println("for (int i = 0; i < str.size(); i++) {");
//    pw.println("s = tt[s][(int)(str[i])];");
//
//    pw.println("if (s == rs) {");
//    pw.println("std::cout << \"Failed\" << std::endl;");
//    pw.println("return 0;");
//    pw.println("}");
//
//    pw.println("}");
//
//    pw.println("std::cout << (fs[s] == 1 ? \"Matched\" : \"Failed\") << std::endl;");
//
//    pw.println("return 0;\n");
//    pw.println("}");

    // check all parts of string
    pw.println("  int start_pos = 0;");

    // start while
    pw.println("  while (start_pos < str.size()) {");
    pw.println("    int s = is;");

    // start for
    pw.println("    for (int i = start_pos; i < str.size(); i++) {");
    pw.println("      s = tt[s][(int)(str[i])];");

    // start if
    pw.println("      if (s == rs) {");
    pw.println("        break;");
    pw.println("      } else if (fs[s]) {");
    pw.println("        std::cout << \"Success!\" << std::endl;");
    pw.println("        return 0;");
    // end if
    pw.println("      }");

    // end for
    pw.println("    }");
    pw.println("    start_pos++;");

    // end while
    pw.println("  }");

    pw.println("  std::cout << \"Failed\" << std::endl;");
    pw.println("  return 0;");
    pw.println("  }");

    pw.flush();
  }

  private static void generateHeaders(PrintWriter pw, String regex) {
    pw.println("// #######################################################################");
    pw.println("// AUTOGENERATED FILE DON'T MODIFY.");
    pw.println("// FILE IS AUTOGENERATED FROM Main.java TO RECOGNIZE THE REGEX " + regex + ".");
    pw.println("// #######################################################################");
    pw.println("#include <iostream>");
    pw.println("#include <string>");
    pw.println();

    pw.println("int main(int argc, char* argv[]) {");
    pw.println("  int result = -1;");
    pw.println("  int idx = 0;");
    pw.println("  int c;");
    pw.println("  std::string str(argv[1]);");
  }

  private static void generateFooter(PrintWriter pw) {
    pw.println("}"); // end main
    pw.flush();
  }

  private static void groupByValue(Map<Integer, Integer> originalMap) {
    Map<Integer, List<Integer>> resultMap = originalMap.entrySet().stream()
      .collect(Collectors.groupingBy(Map.Entry::getValue,
        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));
    resultMap.forEach((k, v) -> System.out.println(k + ":" + v.size()));
  }

  private static void genRange(PrintWriter pw, int startRange, int endRange, int v) {
    if (endRange - startRange == 0) {
      pw.print("    if (c == " + startRange + ") ");
      pw.println("goto state" + v + ";");
    } else {
      // actual range
      pw.print("    if (c >= " + startRange + " && c <= " + endRange + ") ");
      pw.println("goto state" + v + ";");
    }
  }

  private static void generateState(Map<Integer, Integer> stateMap, int state, boolean[] fs,
    int rs, PrintWriter pw) {
    pw.println("  state" + state + ":");

    if (fs[state]) {
      pw.println("    // final state");
      pw.println("    result = idx;");
      pw.println("    std::cout << \"Found ending at:\" << result << std::endl;");
      pw.println("    return 0;");
      return;
    }

    if (state == rs) {
      pw.println("    // reject state");
      pw.println("    std::cout << \"Failed to match:\" << result << std::endl;");
      pw.println("    return 0;");
      return;
    }
    // boundary check
    pw.println("    if (str.size() <= idx) {");
    pw.println("       std::cout << \"Input ended before a match is found.\" << std::endl;");
    pw.println("       return 0;");
    pw.println("    }");

    pw.println("    c = (int)(str[idx++]);");

    int prev = -1;
    int startRange = -1;
    int endRange = -1;

    Integer[] keys = stateMap.keySet().toArray(new Integer[0]);
    Arrays.sort(keys);
    System.out.println(state);
    for (int i = 0; i < keys.length; i++) {
      if (i == 0) {
        startRange = endRange = keys[i];
        prev = stateMap.get(startRange);
        continue;
      }
      int key = keys[i];
      int v = stateMap.get(key);
      if (v == prev && (key == keys[i - 1] + 1)) {
        endRange = key;
      } else {
        // new range print old range
        System.out.println("RANGE DONE: " + startRange + " <> " + endRange + "-> GOTO: " + prev);
        // single range
        genRange(pw, startRange, endRange, prev);
        startRange = endRange = key;
      }
      prev = v;
    }

    System.out.println("FINAL RANGE DONE: " + startRange + " <> " + endRange + "-> GOTO: " + prev);
    genRange(pw, startRange, endRange, prev);

//    for (Map.Entry<Integer, Integer> entry : stateMap.entrySet()) {
//      int v = entry.getKey();
//      pw.println("    if (c == " + v + ") {");
//      pw.println("      goto state" + entry.getValue() + ";");
//      pw.println("    }");
//    }

    // no other condition matched
    pw.println("    goto state" + rs + ";");
  }

  private static PrintWriter getWriter(String filename) throws IOException {
    String s = filename.replace("\\", "");
    System.out.println(s);

    File file = new File("./out/" + s + ".cpp");

    if (!file.exists()) {
      file.createNewFile();
    }

    return new PrintWriter(new FileWriter(file));
  }

  private static void generateCode(RegexMatcher matcher, String regex) throws IOException {
    // convert transition table to hashmap
    // [state_id] -> {"char": to_state_id}
    HashMap<Integer, Map<Integer, Integer>> transitionMap = new HashMap<>();
    for (int i = 0; i < matcher.getTransitionTable().length; i++) {
      int[] stateTransition = matcher.getTransitionTable()[i];
      HashMap<Integer, Integer> stateTransitions = new HashMap<>();
      for (int j = 0; j < stateTransition.length; j++) {
        if (stateTransition[j] != matcher.getRs()) {
          stateTransitions.put(j, stateTransition[j]);
        }
      }

      transitionMap.put(i, stateTransitions);
    }

    System.out.println(transitionMap);

    // create file writer
    PrintWriter pw = getWriter(regex);

    // headers
    generateHeaders(pw, regex);
    // generate initial state
    int is = matcher.getIs();
    boolean[] fs = matcher.getFs();
    int rs = matcher.getRs();

    generateState(transitionMap.get(is), is, fs, matcher.getRs(), pw);

    for (int i = 0; i < transitionMap.size(); i++) {
      if (i == is || i == rs) {
        continue;
      }
      generateState(transitionMap.get(i), i, fs, rs, pw);
    }

    generateState(transitionMap.get(rs), rs, fs, rs, pw);

    generateFooter(pw);
  }

  public static void main(String[] args) throws IOException {
    Scanner sc = new Scanner(System.in);
//    String regex = sc.nextLine();
    String regex = "a+x";
    // allow fast partial matching
    // would be changed if we support lineStart and lineEnd.
    boolean matchPartial = false;
//    boolean matchPartial = true;
    if (matchPartial) {
      regex = ".*(" + regex + ").*";
    }
    RegexMatcher matcher = new RegexMatcher(regex);
    generateCode(matcher, regex);


  }
}
