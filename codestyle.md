# Code Style

本项目 Java 代码主要参考 Google Java Style Guide，并结合 IntelliJ IDEA 默认 Java 格式化规则。

规则：
- 类名使用 UpperCamelCase。
- 方法、变量使用 lowerCamelCase。
- 常量使用 UPPER_SNAKE_CASE。
- 每个类只承担明确职责，Controller/Service/Repository/Parser 分层。
- 使用 4 空格缩进，不使用 Tab。
- 避免魔法数字，将关键配置提取为常量或配置项。
- 对用户输入进行校验，对可预期异常提供明确错误信息。

参考：https://google.github.io/styleguide/javaguide.html
