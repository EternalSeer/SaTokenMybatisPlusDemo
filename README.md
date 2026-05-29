# SaToken MyBatis-Plus Demo

一个用于学习 **Spring Boot 4 + Sa-Token + MyBatis-Plus + PostgreSQL** 的完整示例项目。

项目不只演示普通 CRUD，还刻意覆盖了 MyBatis-Plus 的常用能力：`BaseMapper`、`IService`、分页插件、自动填充、逻辑删除、乐观锁、`LambdaQueryWrapper`、链式更新、批量操作，以及复杂 SQL 的 `mapper.xml` 写法。

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen)
![MyBatis--Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.16-blueviolet)
![Sa--Token](https://img.shields.io/badge/Sa--Token-1.45.0-orange)
![Database](https://img.shields.io/badge/PostgreSQL-supported-336791)
![Build](https://img.shields.io/badge/Build-Gradle-02303A)
![License](https://img.shields.io/badge/License-MIT-green)

## 适合谁

- 想学习 MyBatis-Plus 常用功能的 Java 开发者
- 想看 Sa-Token 登录鉴权最小实践的人
- 想要一个 Spring Boot 4 + Gradle + PostgreSQL 示例项目的人
- 想比较 `LambdaQueryWrapper` 和 `mapper.xml` 使用边界的人

## 功能概览

- 用户登录、退出、当前用户查询
- 用户分页、搜索、创建、批量创建、更新、逻辑删除
- 登录成功/失败日志记录
- 登录日志动态条件查询
- 登录日志按天聚合统计
- PostgreSQL 初始化脚本
- Gradle Wrapper 构建

## 技术栈

| 技术 | 版本/说明 |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.0.6 |
| MyBatis-Plus | 3.5.16 |
| Sa-Token | 1.45.0 |
| PostgreSQL | 默认数据库 |
| Gradle | Wrapper: 8.14.3 |
| Lombok | 1.18.46 |

## 快速启动

先创建 PostgreSQL 数据库：

```sql
CREATE DATABASE user_crud;
```

默认连接配置：

```text
JDBC URL: jdbc:postgresql://localhost:5432/user_crud
Username: postgres
Password: postgres
```

也可以通过环境变量覆盖：

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

启动项目：

```bash
./gradlew bootRun
```

Windows PowerShell：

```powershell
.\gradlew.bat bootRun
```

运行测试：

```bash
./gradlew test
```

应用启动时会执行：

- `src/main/resources/schema.sql`
- `src/main/resources/data.sql`

默认账号：

```text
Username: admin
Password: admin123
```

## 登录示例

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

登录成功后，返回的 `satoken` 可用于后续 `/user/**`、`/login-log/**` 接口请求。

## MyBatis-Plus 学习点

| 学习点 | 代码位置 |
| --- | --- |
| `BaseMapper<T>` | `UserMapper`、`LoginLogMapper` |
| `IService<T>` / `ServiceImpl` | `UserServiceImpl`、`LoginLogServiceImpl` |
| 分页插件 | `MyBatisPlusConfig` |
| 乐观锁插件 | `OptimisticLockerInnerInterceptor` |
| 自动填充 | `MetaObjectHandler`、`@TableField(fill = ...)` |
| 逻辑删除 | `@TableLogic`、`logic-delete-field` |
| 乐观锁字段 | `@Version` |
| 动态条件查询 | `LambdaQueryWrapper` |
| 嵌套条件 | `and(...)`、`or(...)`、`nested(...)` |
| 集合条件 | `in(...)` |
| 子查询条件 | `inSql(...)`、`exists(...)` |
| 字段选择 | `select(...)` |
| 链式更新 | `lambdaUpdate()` |
| 批量保存 | `saveBatch(...)` |
| 批量逻辑删除 | `removeBatchByIds(...)` |
| XML 复杂 SQL | `src/main/resources/mapper/LoginLogMapper.xml` |

## 接口示例

认证接口：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/auth/login` | 登录 |
| `POST` | `/auth/logout` | 退出 |
| `GET` | `/auth/me` | 当前登录用户 |

用户接口：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/user?current=1&size=10&keyword=admin` | 分页搜索 |
| `GET` | `/user/recent?limit=5` | 最近用户 |
| `GET` | `/user/with-login-log?success=true&limit=10` | 有登录日志的用户 |
| `GET` | `/user/count?keyword=admin` | 统计数量 |
| `GET` | `/user/{id}` | 用户详情 |
| `POST` | `/user` | 创建用户 |
| `POST` | `/user/batch` | 批量创建 |
| `PUT` | `/user/{id}` | 更新用户 |
| `PUT` | `/user/{id}/email` | 只更新邮箱 |
| `DELETE` | `/user/{id}` | 逻辑删除 |
| `DELETE` | `/user/batch` | 批量逻辑删除 |

登录日志接口：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/login-log?current=1&size=10&success=false&keyword=admin` | 动态分页搜索 |
| `GET` | `/login-log?startTime=2026-05-01T00:00:00&endTime=2026-05-31T23:59:59` | 时间范围查询 |
| `GET` | `/login-log/latest?userIds=1,2,3&limit=10` | 按用户 ID 集合查询 |
| `GET` | `/login-log/failed?usernames=admin,test&limit=10` | 按用户名集合查询失败日志 |
| `GET` | `/login-log/existing-user?success=true&limit=10` | 子查询过滤已有用户日志 |
| `GET` | `/login-log/count-by-username?usernames=admin,test` | 按用户名统计 |
| `GET` | `/login-log/daily-stats?username=admin&startTime=2026-05-01T00:00:00` | XML 复杂聚合统计 |
| `DELETE` | `/login-log/before?beforeTime=2026-05-01T00:00:00` | 批量逻辑删除旧日志 |

## 项目结构

```text
src/main/java/com/example
├── common
│   ├── ApiResponse.java
│   ├── GlobalExceptionHandler.java
│   └── config
│       ├── MyBatisPlusConfig.java
│       └── SaTokenConfig.java
├── controller
│   ├── AuthController.java
│   ├── LoginLogController.java
│   └── UserController.java
├── domain
│   ├── User.java
│   ├── LoginLog.java
│   └── request/response records
├── mapper
│   ├── UserMapper.java
│   └── LoginLogMapper.java
└── service
    ├── UserServiceImpl.java
    └── LoginLogServiceImpl.java
```

```text
src/main/resources
├── application.yml
├── schema.sql
├── data.sql
└── mapper
    └── LoginLogMapper.xml
```

## Wrapper 和 XML 的取舍

项目中同时保留了两种写法：

- 简单、动态条件明确的查询放在 `LambdaQueryWrapper`
- 聚合、分组、CTE、复杂关联查询放在 `mapper.xml`

例如登录日志分页查询使用 `LambdaQueryWrapper`，而按天统计登录成功/失败次数使用 `LoginLogMapper.xml`。

## 注意事项

- 这是学习项目，默认密码仍是明文保存，不建议直接用于生产。
- `data.sql` 中的默认账号只用于本地学习。
- 公开仓库中没有真实数据库地址和密码，连接信息请使用环境变量覆盖。

## License

This project is licensed under the [MIT License](LICENSE).
