# Calculator Backend

Java 25 + Spring Boot 4.1.1 + SQLite 的前后端分离计算器后端。

## 功能
- POST `/api/calculate`：计算表达式并保存历史记录
- GET `/api/history`：查询历史记录
- DELETE `/api/history/{id}`：删除指定历史记录
- DELETE `/api/history`：清空历史记录（扩展功能）
- 支持 `+ - * /`、括号、小数、一元正负号
- 不使用 `eval/exec`，采用递归下降解析器

## 运行
1. 安装 JDK 25
2. 用 IntelliJ IDEA 打开本目录
3. 等待 Maven 下载依赖
4. 运行 `CalculatorApplication`
5. 后端地址：`http://localhost:8080`

SQLite 数据库文件 `calculator.db` 会在首次运行后自动生成。
