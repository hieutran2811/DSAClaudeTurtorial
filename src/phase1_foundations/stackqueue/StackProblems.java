package phase1_foundations.stackqueue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/**
 * PHASE 1.4 — Stack
 *
 * Nguyên tắc: LIFO — Last In First Out
 * Dùng khi: cần "nhớ" trạng thái trước, xử lý ngược chiều, match cặp
 *
 * Java: Dùng Deque<Integer> stack = new ArrayDeque<>() (không dùng Stack class — legacy)
 *   push  → addFirst / push
 *   pop   → removeFirst / pop
 *   peek  → peekFirst / peek
 */
public class StackProblems {

    // =========================================================
    // Stack tự cài từ đầu (để hiểu internals)
    // =========================================================
    static class MyStack {
        private int[] data;
        private int top = -1;

        MyStack(int capacity) { data = new int[capacity]; }

        void push(int val) {
            if (top == data.length - 1) throw new RuntimeException("Stack full");
            data[++top] = val;
        }

        int pop() {
            if (isEmpty()) throw new RuntimeException("Stack empty");
            return data[top--];
        }

        int peek() {
            if (isEmpty()) throw new RuntimeException("Stack empty");
            return data[top];
        }

        boolean isEmpty() { return top == -1; }
        int size()        { return top + 1; }
    }

    // =========================================================
    // 1. Valid Parentheses (LeetCode #20) — bài kinh điển
    // =========================================================
    /**
     * "()" → true | "()[]{}" → true | "(]" → false | "([)]" → false
     *
     * Logic: gặp mở → push, gặp đóng → pop và kiểm tra match
     */
    static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) return false;
                char top = stack.pop();
                if (c == ')' && top != '(') return false;
                if (c == ']' && top != '[') return false;
                if (c == '}' && top != '{') return false;
            }
        }
        return stack.isEmpty();
    }

    // =========================================================
    // 2. Min Stack (LeetCode #155)
    // =========================================================
    /**
     * Stack hỗ trợ getMin() trong O(1)
     *
     * Trick: dùng 2 stack song song — 1 stack data, 1 stack min
     * minStack luôn lưu giá trị min TẠI THỜI ĐIỂM đó
     */
    static class MinStack {
        private Deque<Integer> stack    = new ArrayDeque<>();
        private Deque<Integer> minStack = new ArrayDeque<>();

        void push(int val) {
            stack.push(val);
            int curMin = minStack.isEmpty() ? val : Math.min(val, minStack.peek());
            minStack.push(curMin);
        }

        void pop() {
            stack.pop();
            minStack.pop(); // luôn pop cả 2 cùng nhau
        }

        int top()    { return stack.peek(); }
        int getMin() { return minStack.peek(); } // O(1)
    }

    // =========================================================
    // 3. Evaluate Reverse Polish Notation (LeetCode #150)
    // =========================================================
    /**
     * ["2","1","+","3","*"] → (2+1)*3 = 9
     * ["4","13","5","/","+"] → 4 + (13/5) = 6
     *
     * Logic: số → push, operator → pop 2 số, tính, push kết quả
     */
    static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String token : tokens) {
            switch (token) {
                case "+" -> stack.push(stack.pop() + stack.pop());
                case "-" -> { int b = stack.pop(), a = stack.pop(); stack.push(a - b); }
                case "*" -> stack.push(stack.pop() * stack.pop());
                case "/" -> { int b = stack.pop(), a = stack.pop(); stack.push(a / b); }
                default  -> stack.push(Integer.parseInt(token));
            }
        }
        return stack.pop();
    }

    // =========================================================
    // 4. Daily Temperatures (LeetCode #739) — Monotonic Stack
    // =========================================================
    /**
     * [73,74,75,71,69,72,76,73] → [1,1,4,2,1,1,0,0]
     * Với mỗi ngày, tìm số ngày phải chờ đến khi ấm hơn
     *
     * Brute force: O(n²)
     * Monotonic Stack: O(n) — stack lưu INDEX, duy trì thứ tự giảm dần
     *
     * Khi gặp nhiệt độ mới:
     *   Nếu > stack.peek() → pop và tính khoảng cách (đây là "next greater")
     *   Push index hiện tại vào stack
     */
    static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>(); // lưu INDEX

        for (int i = 0; i < n; i++) {
            // Pop tất cả index có nhiệt độ nhỏ hơn temperatures[i]
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int idx = stack.pop();
                result[idx] = i - idx; // số ngày chờ
            }
            stack.push(i);
        }
        return result; // các index còn trong stack → result = 0 (mặc định)
    }

    // =========================================================
    // 5. Largest Rectangle in Histogram (LeetCode #84) — Hard
    // =========================================================
    /**
     * [2,1,5,6,2,3] → 10 (cột 5 và 6 tạo hình chữ nhật 2×5=10)
     *
     * Monotonic Stack (tăng dần):
     *   Khi gặp cột thấp hơn → pop và tính diện tích với cột bị pop làm chiều cao
     *   width = i - stack.peek() - 1  (hoặc i nếu stack rỗng)
     */
    static int largestRectangle(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int maxArea = 0;
        int n = heights.length;

        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i]; // thêm sentinel 0 ở cuối
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width  = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }

    // =========================================================
    // 6. Decode String (LeetCode #394)
    // =========================================================
    /**
     * "3[a2[c]]" → "accaccacc"
     * "2[abc]3[cd]ef" → "abcabccdcdcdef"
     *
     * 2 stack: countStack (số lần) + stringStack (chuỗi đang build)
     */
    static String decodeString(String s) {
        Deque<Integer>       countStack  = new ArrayDeque<>();
        Deque<StringBuilder> stringStack = new ArrayDeque<>();
        StringBuilder current = new StringBuilder();
        int k = 0;

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0'); // số có thể nhiều chữ số
            } else if (c == '[') {
                countStack.push(k);
                stringStack.push(current);
                current = new StringBuilder();
                k = 0;
            } else if (c == ']') {
                int repeat = countStack.pop();
                StringBuilder prev = stringStack.pop();
                for (int i = 0; i < repeat; i++) prev.append(current);
                current = prev;
            } else {
                current.append(c);
            }
        }
        return current.toString();
    }

    public static void main(String[] args) {
        System.out.println("=== Stack Problems ===\n");

        // MyStack
        MyStack ms = new MyStack(5);
        ms.push(1); ms.push(2); ms.push(3);
        System.out.println("MyStack peek: " + ms.peek() + " | pop: " + ms.pop() + " | size: " + ms.size());

        // Valid Parentheses
        System.out.println("\n--- Valid Parentheses ---");
        System.out.println("'()[]{}' → " + isValid("()[]{}")); // true
        System.out.println("'(]'    → " + isValid("(]"));      // false
        System.out.println("'([)]'  → " + isValid("([)]"));    // false
        System.out.println("'{[]}'  → " + isValid("{[]}"));    // true

        // Min Stack
        System.out.println("\n--- Min Stack ---");
        MinStack minStack = new MinStack();
        minStack.push(-2); minStack.push(0); minStack.push(-3);
        System.out.println("getMin: " + minStack.getMin()); // -3
        minStack.pop();
        System.out.println("top: " + minStack.top());       // 0
        System.out.println("getMin: " + minStack.getMin()); // -2

        // Evaluate RPN
        System.out.println("\n--- Evaluate RPN ---");
        System.out.println("['2','1','+','3','*'] = " + evalRPN(new String[]{"2","1","+","3","*"})); // 9
        System.out.println("['4','13','5','/','+']=  " + evalRPN(new String[]{"4","13","5","/","+"})); // 6

        // Daily Temperatures
        System.out.println("\n--- Daily Temperatures ---");
        int[] temps = {73,74,75,71,69,72,76,73};
        int[] days = dailyTemperatures(temps);
        System.out.print("Result: ");
        for (int d : days) System.out.print(d + " "); // 1 1 4 2 1 1 0 0
        System.out.println();

        // Largest Rectangle
        System.out.println("\n--- Largest Rectangle ---");
        System.out.println("[2,1,5,6,2,3] → " + largestRectangle(new int[]{2,1,5,6,2,3})); // 10
        System.out.println("[2,4]         → " + largestRectangle(new int[]{2,4}));          // 4

        // Decode String
        System.out.println("\n--- Decode String ---");
        System.out.println("'3[a2[c]]'      → " + decodeString("3[a2[c]]"));      // accaccacc
        System.out.println("'2[abc]3[cd]ef' → " + decodeString("2[abc]3[cd]ef")); // abcabccdcdcdef
    }
}
