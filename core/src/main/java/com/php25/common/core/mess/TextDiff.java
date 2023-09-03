package com.php25.common.core.mess;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2023/4/5 23:51
 */
public class TextDiff {
    public static void main(String[] args) {
        String text1 = "The quick brown fox jumps over the lazy dog.";
        String text2 = "The quick red fox jumps over the lazy cat.";

        List<Diff> diffs = diffText(text1, text2);

        for (Diff diff : diffs) {
            System.out.println(diff.toString());
        }
    }

    public static List<Diff> diffText(String text1, String text2) {
        List<Diff> diffs = new ArrayList<>();
        StringBuilder sb1 = new StringBuilder(text1);
        StringBuilder sb2 = new StringBuilder(text2);
        int index1 = 0, index2 = 0;

        while (index1 < sb1.length() || index2 < sb2.length()) {
            if (index1 >= sb1.length()) {
                diffs.add(new Diff(Diff.Operation.INSERT, index2, sb2.substring(index2)));
                index2 = sb2.length();
            } else if (index2 >= sb2.length()) {
                diffs.add(new Diff(Diff.Operation.DELETE, index1, sb1.substring(index1)));
                index1 = sb1.length();
            } else if (sb1.charAt(index1) == sb2.charAt(index2)) {
                index1++;
                index2++;
            } else {
                int deleteEnd = findRegionEnd(sb1, index1, sb2, index2);
                int insertEnd = findRegionEnd(sb2, index2, sb1, index1);

                if (deleteEnd - index1 >= insertEnd - index2) {
                    diffs.add(new Diff(Diff.Operation.DELETE, index1, sb1.substring(index1, deleteEnd)));
                    index1 = deleteEnd;
                } else {
                    diffs.add(new Diff(Diff.Operation.INSERT, index2, sb2.substring(index2, insertEnd)));
                    index2 = insertEnd;
                }
            }
        }

        return diffs;
    }

    private static int findRegionEnd(StringBuilder sb1, int index1, StringBuilder sb2, int index2) {
        int start = Math.max(0, index1 - index2), end = sb1.length();

        while (start < end) {
            int mid = (start + end) / 2;

            if (sb1.charAt(mid) == sb2.charAt(index2 + mid - index1)) {
                start = mid + 1;
            } else {
                end = mid;
            }
        }

        return end;
    }

    public static class Diff {
        private final Operation operation;
        private final int position;
        private final String text;
        public Diff(Operation operation, int position, String text) {
            this.operation = operation;
            this.position = position;
            this.text = text;
        }

        public Operation getOperation() {
            return operation;
        }

        public int getPosition() {
            return position;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return operation.name() + " at " + position + ": '" + text + "'";
        }

        public enum Operation {
            INSERT,
            DELETE
        }
    }
}
