// App.java 修改后的版本
package com.mjcshuai;

import com.mjcshuai.util.AppIconUtil;
import com.mjcshuai.view.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {

        // 预加载图标
        Image appIcon = AppIconUtil.loadAppIcon();

        // 设置默认图标
        if (appIcon != null) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            // Toolkit.getDefaultToolkit().setDynamicLayout(true);
        }

        LoginFrame loginFrame = new LoginFrame(); //拉取登录窗口
        AppIconUtil.setWindowIcon(loginFrame);  // 设置当前窗口图标
        loginFrame.setVisible(true);
    }
}
