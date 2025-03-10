package cn.graht.test.sf.stack;

/**
 * @author GRAHT
 */
//括号匹配
public class StackTest {
    public static boolean isOk(String str) {
         MyStack<Character> brackets = new ArrayStack<Character>(10);
        char[] charArray = str.toCharArray();
        Character top;
        for (char c : charArray) {
            switch (c) {
                case '{', '(', '[':
                    brackets.push(c);
                    break;
                case ']':
                    top = brackets.pop();
                    if (top == null) return false;
                    if (top == '['){
                        break;
                    }else {
                        return false;
                    }
                case ')':
                    top = brackets.pop();
                    if (top == null) return false;
                    if (top == '('){
                        break;
                    }else {
                        return false;
                    }
                case '}':
                    top = brackets.pop();
                    if (top == null) return false;
                    if (top == '{'){
                        break;
                    }else {
                        return false;
                    }
                default:
                    break;
            }
        }
        return brackets.isEmpty();
    }

    public static void main(String[] args) {
        String str = "{[()]}";
        System.out.println(isOk(str));
    }
}
