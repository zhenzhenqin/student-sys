**学生管理系统**
项目简介
学生管理系统是一个基于 Java Swing 开发的桌面应用程序，旨在实现学校学生、教师、课程及成绩的综合管理。系统支持多角色登录（管理员、教师、学生），并根据不同角色提供相应的功能权限，实现了数据的增删改查及业务逻辑管理。
**功能模块**
1. 用户登录模块
   支持管理员、教师、学生三种角色登录
   角色切换功能（支持右键快捷切换）
   输入验证及登录状态管理
   登录成功后根据角色分配权限
2. 个人中心模块
   查看个人基本信息
   编辑个人资料（根据角色显示不同字段）
   密码修改功能
   退出登录功能
3. 学生管理模块（仅管理员）
   查看所有学生信息
   新增学生（包含基本信息录入及验证）
   编辑学生信息
   删除学生记录
   数据刷新功能
4. 教师管理模块（仅管理员）
   教师信息表格展示（教师 ID、用户名、性别、职称、年龄等）
   教师信息管理功能（增删改查）
5. 课程管理模块
   管理员 / 学生：查看所有课程信息
   教师：查看自己的授课课程
   学生：查看已选课程及退课功能
   课程信息维护（名称、学分、课时、描述、授课教师等）
6. 成绩管理模块
   教师查看选课学生列表
   成绩录入与批改功能

**技术栈**
   编程语言：Java
   UI 框架：Swing
   数据库：Apache Derby（嵌入式数据库）
   构建工具：IDE 内置编译工具
   测试框架：JUnit
   环境要求
   JDK 版本：17 及以上
   开发工具：IntelliJ IDEA（推荐）或其他 Java IDE
   操作系统：Windows/macOS/Linux

**项目结构**
student-sys/
├── src/
│   ├── com/mjcshuai/
│   │   ├── view/           # 界面相关类
│   │   ├── util/           # 工具类（数据库连接、用户上下文等）
│   │   ├── dao/            # 数据访问接口及实现
│   │   └── model/          # 实体类
│   └── test/               # 测试类
├── .gitignore              # Git忽略文件配置
└── README.md               # 项目说明文档

开发人员
mjc
其他贡献者

备注
如需使用ai功能，请克隆AutAiMaven部分代码到项目根目录下。

**🎓 Student Management System (智能学生管理系统ai版)**
这是一个基于 Java Swing 开发的现代化学生管理系统，集成了 LangChain4j 智能 AI 助手。
系统支持管理员、教师和学生三种角色的多权限管理，默认使用嵌入式 Derby 数据库，可谓“开箱即用”。

📖 项目简介
该项目采用了经典的 MVC 架构（Model-View-Controller），界面使用 Java Swing 构建，
数据持久层支持嵌入式数据库，极大地简化了部署流程。除了传统的增删改查（CRUD）功能外，
最大的亮点是集成了基于 OpenAI 的 AI 顾问功能，能够为用户提供智能问答服务。

🛠 技术栈
开发语言: Java 21
构建工具: Maven
GUI 框架: Java Swing (FlatLaf / Custom Styling)
数据库:
Apache Derby (默认，嵌入式，无需安装)
MySQL (可选，需配置)
AI 集成框架: LangChain4j (0.35.0)
单元测试: JUnit 5

✨ 功能特性
1. 多角色登录与权限控制
   系统支持三种角色登录，不同角色拥有不同的菜单和操作权限：

   管理员 (Admin): 拥有最高权限，可管理所有学生、教师信息及系统设置。
   教师 (Teacher): 可查看我的授课、管理成绩（部分功能）、查看所有教师列表。
   学生 (Student): 可查看已选课程、查看个人信息。

2. 智能 AI 助手
   集成 ChatGPT (gpt-4o-mini 模型)。
   支持上下文记忆（最近 20 条交互）。
   不同用户（学生/教师/管理员）拥有独立的对话记忆窗口。

3. 数据管理
   课程管理: 查看所有课程、我的授课、已选课程。
   人员管理: 学生与教师的增删改查。
   个人中心: 查看及编辑个人信息。

4. 便捷的 UI 体验
   动态桌面面板背景与水印。
   快捷的 GitHub 仓库访问入口。
   登录界面支持键盘快捷键（右键切换角色，回车登录）。


🚀 快速开始
环境要求
JDK 21 或更高版本
Maven 3.x

运行步骤
克隆项目

Bash

git clone https://github.com/zhenzhenqin/student-sys.git
构建项目 在项目根目录下运行：

Bash

mvn clean install
启动应用 运行 src/main/java/com/mjcshuai/App.java 中的 main 方法即可启动。

默认测试账号
系统内置了以下测试账号（密码均为 1234）： | 角色 | 用户名 | 密码 | | :--- | :--- | :--- | | 管理员 | admin | 1234 | | 教师 | teacher | 1234 | | 学生 | student | 1234 |

⚙️ 配置说明
数据库配置
项目默认使用 Derby 嵌入式数据库，无需安装任何数据库软件即可直接运行。系统会自动在项目目录下创建 student_management_db 文件夹。


AI 模型配置
目前的 AI 功能使用的是 LangChain4j 提供的公共演示 Key (demo)，仅用于测试，速度和稳定性受限。
如需在生产环境使用，请在 AiAssistantManager.java 中替换为你自己的 API Key：

Java

ChatLanguageModel model = OpenAiChatModel.builder()
.baseUrl("https://api.openai.com/v1") // 或你的代理地址
.apiKey("YOUR_OPENAI_API_KEY")        // 你的 API Key
.modelName("gpt-4o-mini")
.build();
📂 项目结构
com.mjcshuai
├── constant    // 常量定义 (数据库配置, SQL脚本路径, 提示词)
├── context     // 上下文管理 (当前登录用户)
├── dao         // 数据访问层接口及实现 (CRUD)
├── model       // 实体类 (Admin, Student, Teacher, Course 等)
├── service     // 业务逻辑层 (AI 服务)
├── util        // 工具类 (数据库连接, 图标加载, 用户上下文)
├── view        // 视图层 (Swing 窗口: LoginFrame, MainFrame 等)
└── App.java    // 程序启动入口
🤝 贡献
欢迎提交 Issue 和 Pull Request！ GitHub 仓库: https://github.com/zhenzhenqin/student-sys

