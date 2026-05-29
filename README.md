# Spring Boot 4 User CRUD

Spring Boot 4.x + MyBatis-Plus + Sa-Token 的用户 CRUD 示例项目。

## 版本

- Spring Boot: `4.0.6`
- Java: `21`
- MyBatis-Plus: `3.5.16`
- Sa-Token: `1.45.0`
- Database: PostgreSQL

## 数据库

默认连接本机 PostgreSQL：

- JDBC URL: `jdbc:postgresql://localhost:5432/user_crud`
- Username: `postgres`
- Password: `postgres`

也可以通过环境变量覆盖：

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

启动前请先创建数据库：

```sql
CREATE DATABASE user_crud;
```

应用启动时会执行 `src/main/resources/schema.sql` 和 `src/main/resources/data.sql` 初始化表和默认用户。

默认登录账号：

- Username: `admin`
- Password: `admin123`

## 启动

如果本机已经安装 Maven：

```bash
mvn spring-boot:run
```

也可以使用项目内置 Maven：

```powershell
.\.tools\apache-maven-3.9.11\bin\mvn.cmd spring-boot:run
```

启动后可访问：

- 登录: `POST http://localhost:8080/auth/login`
- 当前登录用户: `GET http://localhost:8080/auth/me`
- 用户 CRUD: `/user`

## MyBatis-Plus 学习点

这个项目刻意保留了一些常用 MyBatis-Plus 写法，方便对照学习：

- 通用 Service: `UserService extends IService<User>`
- 通用 Mapper: `UserMapper extends BaseMapper<User>`
- 分页插件: `MybatisPlusInterceptor + PaginationInnerInterceptor`
- 乐观锁插件: `OptimisticLockerInnerInterceptor`
- 自动填充: `MetaObjectHandler` 自动写入 `createdAt`、`updatedAt`
- 逻辑删除: `@TableLogic`，删除时更新 `deleted = 1`
- 乐观锁字段: `@Version`
- 条件构造器: `LambdaQueryWrapper`
- 动态条件: `eq(condition, ...)`、`ge(condition, ...)`、`le(condition, ...)`
- 嵌套条件: `and(...)`、`or(...)`、`nested(...)`
- 集合条件: `in(...)`
- 子查询条件: `inSql(...)`、`exists(...)`
- 字段选择: `select(...)`
- 自定义 XML SQL: `mapper-locations` + `LoginLogMapper.xml`
- 链式更新: `lambdaUpdate()`
- 批量保存: `saveBatch(...)`
- 批量逻辑删除: `removeBatchByIds(...)`

## 用户接口示例

- 分页搜索: `GET /user?current=1&size=10&keyword=admin`
- 最近用户: `GET /user/recent?limit=5`
- 有登录日志的用户: `GET /user/with-login-log?success=true&limit=10`
- 统计数量: `GET /user/count?keyword=admin`
- 用户详情: `GET /user/{id}`
- 创建用户: `POST /user`
- 批量创建: `POST /user/batch`
- 更新用户: `PUT /user/{id}`
- 只更新邮箱: `PUT /user/{id}/email`
- 逻辑删除: `DELETE /user/{id}`
- 批量逻辑删除: `DELETE /user/batch`

## 登录日志接口示例

登录日志表 `sys_login_log` 用来演示更复杂的 `LambdaQueryWrapper` 场景：

- 动态分页搜索: `GET /login-log?current=1&size=10&success=false&keyword=admin`
- 时间范围查询: `GET /login-log?startTime=2026-05-01T00:00:00&endTime=2026-05-31T23:59:59`
- 按用户 ID 集合查询: `GET /login-log/latest?userIds=1,2,3&limit=10`
- 按用户名集合查询失败日志: `GET /login-log/failed?usernames=admin,test&limit=10`
- 子查询过滤已有用户日志: `GET /login-log/existing-user?success=true&limit=10`
- 分组式统计示例: `GET /login-log/count-by-username?usernames=admin,test`
- XML 复杂聚合统计: `GET /login-log/daily-stats?username=admin&startTime=2026-05-01T00:00:00`
- 批量逻辑删除旧日志: `DELETE /login-log/before?beforeTime=2026-05-01T00:00:00`

## 登录示例

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

登录成功后，返回的 `satoken` 可用于后续 `/user/**` 接口请求。
