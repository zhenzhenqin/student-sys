package com.mjcshuai;

import com.mjcshuai.view.LoginFrame;

import javax.swing.*;
import java.net.URL;


//启动入口 拉取gui登录界面
public class App {
    public static void main(String[] args) {
        LoginFrame loginFrame = new LoginFrame();

        // 设置任务栏图标
        try {
            // 从资源路径加载图标
            URL iconUrl = App.class.getResource("resource/icon.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                loginFrame.setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            System.err.println("无法加载应用程序图标");
        }

        loginFrame.setVisible(true);
    }
}
