package com.mjcshuai.view;

import com.mjcshuai.service.ai.AiAssistantManager;
import com.mjcshuai.service.ai.ConsultantService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * AI 智能助手聊天窗口
 * 独立 JFrame 界面，支持多线程异步发送消息
 */
public class AiChatWindow extends JFrame {

    private JTextArea chatArea;      // 聊天记录显示区
    private JTextField inputField;   // 文本输入框
    private JButton sendButton;      // 发送按钮
    private final String currentUserId;    // 当前对话的用户ID（用于记忆上下文）

    public AiChatWindow(String userId) {
        this.currentUserId = userId;
        
        setTitle("智能系统顾问 - 正在为 [" + userId + "] 服务");
        setSize(550, 650);
        setLocationRelativeTo(null); // 居中显示
        // 重要：只关闭当前窗口，不退出整个程序
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new BorderLayout());

        initView();
        //初始欢迎语
        appendMessage("System", "你好！我是你的专属 AI 顾问。有关系统的任何问题都可以问我，我会结合上下文为你解答。");
    }

    private void initView() {
        // --- 中间：聊天记录区域 ---
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true); // 按单词换行，更美观
        chatArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chatArea.setMargin(new Insets(10, 15, 10, 15));
        
        // 放入滚动面板
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // --- 底部：输入区域 ---
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

        // 1. UI 立即响应用户输入
        appendMessage("我", content);
        inputField.setText("");
        
        // 锁定界面，防止重复提交
        setSendingState(true);

        // 2. 使用 SwingWorker 在后台线程调用 AI
        // 这是防止界面卡死的核心关键！
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // 调用后端服务，这是一个耗时的网络操作
                ConsultantService service = AiAssistantManager.getService();
                return service.chat(currentUserId, content);
            }

            @Override
            protected void done() {
                try {
                    // 获取后台任务的结果
                    String reply = get();
                    appendMessage("AI 顾问", reply);
                } catch (Exception e) {
                    String errorMsg = "连接异常: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                    appendMessage("System Error", errorMsg);
                    e.printStackTrace();
                } finally {
                    // 恢复界面状态
                    setSendingState(false);
                    inputField.requestFocus();
                }
            }
        }.execute();
    }

    // 切换发送状态（锁定/解锁按钮）
    private void setSendingState(boolean isSending) {
        inputField.setEnabled(!isSending);
        sendButton.setEnabled(!isSending);
        sendButton.setText(isSending ? "思考中..." : "发送");
        sendButton.setBackground(isSending ? new Color(150, 150, 150) : new Color(0, 123, 255));
    }

    // 辅助方法：向聊天框追加文本
    private void appendMessage(String sender, String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(sender + ":\n" + message + "\n");
            chatArea.append("--------------------------------------------------\n");
            // 自动滚动到底部
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
}