package com.mjcshuai.util;

import com.mjcshuai.App;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * 窗口图标相关工具类
 * 提供加载应用图标和设置窗口图标功能
 * author: mjc
 * createTime: 2025-11-30
 */
@Slf4j
public class AppIconUtil {

    //全局图标
    private static Image appIcon;

    /**
     * 加载应用图标
     */
    public static Image loadAppIcon() {
        try {
            URL iconUrl = AppIconUtil.class.getResource("/Icon/icon.png");
            if (iconUrl != null) {
                appIcon = new ImageIcon(iconUrl).getImage();
            }
        } catch (Exception e) {
            log.error("Unable to load the application icon: " + e.getMessage());
        }
        return appIcon;
    }

    /**
     * 给指定窗口设置图标
     *
     * @param window 要设置图标的窗口对象
     */
    public static void setWindowIcon(Window window) {
        if (appIcon != null && window != null) {
            window.setIconImage(appIcon);
        }
    }
}
