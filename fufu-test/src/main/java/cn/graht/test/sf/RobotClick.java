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

            // 定义点击位置为屏幕左下角
            int x = 0;
            int y = screenHeight - 1;
            // 创建Robot对象
            Robot robot = new Robot();
            Random random = new Random();

            // 点击屏幕左下角1000次
            for (int i = 0; i < 1000; i++) {
                // 移动鼠标到指定位置
                robot.mouseMove(x, y);

                // 模拟鼠标按下和释放
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(10);
                // 释放鼠标左键
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                // 模拟按下随机按键
                int keyCode = KeyEvent.getExtendedKeyCodeForChar((char) (random.nextInt(26) + 'a'));
                robot.keyPress(keyCode);
                robot.delay(10); // 按键按下持续时间
                robot.keyRelease(keyCode);

                // 添加随机延迟
                int delay = 100 + random.nextInt(200); // 随机延迟100到300毫秒
                robot.delay(delay);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
