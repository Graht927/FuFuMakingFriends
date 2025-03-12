package cn.graht.test.sf;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * @author GRAHT
 */

public class RobotClick {
    public static void main(String[] args) {
        try {
            // 获取屏幕尺寸
            int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
            int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

            int x = 0;
            int y = screenHeight - 1;
            // 创建Robot对象
            Robot robot = new Robot();
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            Random random = new Random();
            // 模拟鼠标按下和释放的持续时间
            try {
                int pressDuration = 10 + random.nextInt(20); // 随机按下持续时间10到30毫秒
                Thread.sleep(pressDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 释放鼠标左键
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            // 定义要按下的按键
            int keyCode = KeyEvent.VK_ENTER; // 例如，按下Enter键

            // 连续按下键盘按键
            continuousKeyPress(robot, keyCode, 10); // 按下10次
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连续按下指定键盘按键的方法
     * @param robot Robot对象
     * @param keyCode 按键码
     * @param count 按下次数
     */
    public static void continuousKeyPress(Robot robot, int keyCode, int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            // 按下键盘按键
            robot.keyPress(keyCode);

            // 模拟按键按下和释放的持续时间
            try {
                int pressDuration = 10 + random.nextInt(20); // 随机按下持续时间10到30毫秒
                Thread.sleep(pressDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 释放键盘按键
            robot.keyRelease(keyCode);

            // 添加随机延迟
            try {
                int delay = 100 + random.nextInt(200); // 随机延迟100到300毫秒
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
