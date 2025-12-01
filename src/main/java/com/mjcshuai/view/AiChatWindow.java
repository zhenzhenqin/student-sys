package com.mjcshuai.view;

import com.mjcshuai.service.ai.AiAssistantManager;
import com.mjcshuai.service.ai.ConsultantService;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * AI 智能助手聊天窗口\
 * author:Gemini
 * date:2025-12-1
 */
public class AiChatWindow extends JFrame {

    private JTextPane chatPane;      // 改用 JTextPane 支持样式颜色
    private StyledDocument doc;      // 文本模型，用于管理样式插入
    private JTextField inputField;   // 文本输入框
    private JButton sendButton;      // 发送按钮
    private final String currentUserId; // 当前对话的用户ID

    // --- 定义文字样式 ---
    private SimpleAttributeSet userStyle;   // 用户说话样式（蓝色）
    private SimpleAttributeSet aiStyle;     // AI说话样式（黑色）
    private SimpleAttributeSet systemStyle; // 系统消息样式（灰色）

    public AiChatWindow(String userId) {
        this.currentUserId = userId;

        setTitle("智能系统顾问 - 正在为 [" + userId + "] 服务");
        setSize(550, 650);
        setLocationRelativeTo(null); // 居中显示
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initStyles(); // 1. 初始化颜色样式
        initView();   // 2. 初始化界面

        // 初始欢迎语
        appendMessage("System", "你好！我是你的专属 AUT专属AI 顾问。有关系统的任何问题都可以问我，我会结合上下文为你解答。", systemStyle);
    }

    // 初始化样式配置
    private void initStyles() {
        // 用户样式：深蓝色，加粗
        userStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(userStyle, new Color(30, 144, 255)); // DodgerBlue
        StyleConstants.setBold(userStyle, true);
        StyleConstants.setFontSize(userStyle, 14);

        // AI 样式：深灰色，常规
        aiStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(aiStyle, new Color(51, 51, 51));
        StyleConstants.setFontSize(aiStyle, 14);

        // 系统/错误样式：灰色，斜体，较小
        systemStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(systemStyle, Color.GRAY);
        StyleConstants.setItalic(systemStyle, true);
        StyleConstants.setFontSize(systemStyle, 12);
    }

    private void initView() {
        // --- 中间：聊天记录区域 (改为 JTextPane) ---
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setBackground(Color.WHITE);
        chatPane.setMargin(new Insets(10, 15, 10, 15));

        // 获取文档模型
        doc = chatPane.getStyledDocument();

        // 放入滚动面板
        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // --- 底部：输入区域 (保持不变) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(240, 242, 245));

        inputField = new JTextField();
        inputField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // 绑定回车键发送
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && sendButton.isEnabled()) {
                    performSend();
                }
            }
        });

        sendButton = new JButton("发送");
        sendButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        sendButton.setBackground(new Color(0, 123, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sendButton.addActionListener(e -> performSend());

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 执行发送逻辑
    private void performSend() {
        String content = inputField.getText().trim();
        if (content.isEmpty()) return;

        // 1. UI 立即响应用户输入 (使用用户样式)
        appendMessage(currentUserId, content, userStyle);
        //appendMessage("我", content, userStyle);
        inputField.setText("");

        // 锁定界面
        setSendingState(true);

        // 2. 后台线程调用 AI
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // 依然使用原本的 Manager 获取服务
                ConsultantService service = AiAssistantManager.getService();
                return service.chat(currentUserId, content);
            }

            @Override
            protected void done() {
                try {
                    String reply = get();
                    // AI 回复 (使用 AI 样式)
                    appendMessage("AUT 顾问", reply, aiStyle);
                } catch (Exception e) {
                    String errorMsg = "连接异常: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                    // 错误信息 (使用系统样式)
                    appendMessage("System Error", errorMsg, systemStyle);
                    e.printStackTrace();
                } finally {
                    setSendingState(false);
                    inputField.requestFocus();
                }
            }
        }.execute();
    }

    // 切换发送状态
    private void setSendingState(boolean isSending) {
        inputField.setEnabled(!isSending);
        sendButton.setEnabled(!isSending);
        sendButton.setText(isSending ? "思考中..." : "发送");
        sendButton.setBackground(isSending ? new Color(150, 150, 150) : new Color(0, 123, 255));
    }

    /**
     * 核心辅助方法：向聊天框追加带样式的文本
     * @param sender 发送者名字
     * @param message 消息内容
     * @param style 文本样式 (颜色/字体等)
     */
    private void appendMessage(String sender, String message, AttributeSet style) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. 插入发送者名字和内容
                String textToInsert = sender + ":\n" + message + "\n";
                doc.insertString(doc.getLength(), textToInsert, style);

                // 2. 插入分割线 (使用系统样式，比较淡)
                doc.insertString(doc.getLength(), "--------------------------------------------------\n", systemStyle);

                // 3. 自动滚动到底部
                chatPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }
}