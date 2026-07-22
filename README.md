# mybatis-plus-kt-spring-boot-starter

MyBatis-Plus Kotlin DSL，通过 Lambda 属性引用（`KProperty1`）构建类型安全的查询条件。

## 简介

`mybatis-plus-kt` 是 MyBatis-Plus 的 Kotlin 扩展库，以 `KProperty1` 属性引用替代字符串/Getter 方法引用，提供类自然语言的类型安全查询 DSL。核心思路是将 `QueryWrapper` 的链式调用封装为 `实体::字段 操作符 值` 的中缀表达式，编译期即可校验字段名和类型，消除字符串拼写错误。内置安全模式让搜索表单场景零 if-else——null 参数自动跳过，空集合自动降级。

## 特性

- **类型安全**：`User::name eq "btown"` 编译期检查，列名自动解析
- **类自然语言**：`User::age ge 18` 读作"用户年龄大于等于 18"，代码即注释
- **零配置**：Spring Boot 自动配置，引入即用
- **双模式**：普通模式与原 `QueryWrapper` 完全一致，安全模式 null 自动降级
- **无侵入**：`QueryWrapper` 直接执行 CRUD，无需注入 Service
- **可扩展**：实现 `ColumnNameProvider` 自定义列名映射，支持动态列名

## 环境要求

- Kotlin 2.2+ / JDK 17+
- MyBatis-Plus 3.5.3+
- Spring Boot 3.0+

## 快速开始

### 依赖

**Gradle（Kotlin DSL）**

```kotlin
dependencies {
    implementation("io.github.btown021:mybatis-plus-kt-spring-boot-starter:1.0.1")
}
```

**Maven**

```xml
<dependency>
    <groupId>io.github.btown021</groupId>
    <artifactId>mybatis-plus-kt-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

### 实体 & Service

```kotlin
@TableName("sys_user")
data class User(
    var name: String = "",
    var age: Int = 0,
    var status: Int = 0
) : BaseAttr()

interface IUserService : IService<User>
class UserServiceImpl : ServiceImpl<UserMapper, User>(), IUserService
```

### 第一个查询

```kotlin
@Service
class UserBizService(private val userService: IUserService) {

    fun search(name: String?, status: Int?, roleList: List<String>?): List<User> {
        return userService.createQueryWrapper()
            .ktWhereSafe {
                User::name like name
                User::status eq status
                User::role in roleList
                User::age between (18 to 60)
            }
            .ktOrder { User::id.orderByDesc }
            .selectList()
    }
}
```

## createQueryWrapper

查询条件构建依赖 `entityClass` 完成属性引用到数据库列名的解析。保证 `QueryWrapper` 已设置 `entityClass` 即可，以下两种方式等价：

```kotlin
// 方式一：createQueryWrapper 自动设置 entityClass
val wrapper = userService.createQueryWrapper<User>()

// 方式二：手动设置
val wrapper = QueryWrapper<User>().apply { setEntityClass(User::class.java) }
```

推荐使用 `createQueryWrapper`，代码更简洁。

## 条件查询

### ktWhere / ktWhereSafe

语法：`实体::字段 操作符 值`

| 模式 | 入口 | 行为 |
|------|------|------|
| 普通 | `ktWhere { }` | 与原生 `QueryWrapper` 行为一致，不做任何降级处理 |
| 安全 | `ktWhereSafe { }` | null / 空集合自动跳过，适合搜索表单 |

### 操作符列表

| 操作符 | 用法 | 对应 SQL | safe 降级 |
|--------|------|---------|----------|
| `eq` | `User::name eq "btown"` | `name = 'btown'` | null→跳过 |
| `eqOrIsNull` | `User::name eqOrIsNull value` | null→`IS NULL`，有值→`= value` | 不降级 |
| `ne` | `User::name ne "btown"` | null→`IS NOT NULL`，有值→`<> value` | 不降级 |
| `neOrNotNull` | `User::name neOrNotNull value` | null→跳过，有值→`<> value` | null→跳过 |
| `gt` / `ge` / `lt` / `le` | `User::age gt 18` | `>`, `>=`, `<`, `<=` | null→跳过 |
| `like` | `User::name like "btown"` | `LIKE '%btown%'` | null→跳过 |
| `likeLeft` | `User::name likeLeft "btown"` | `LIKE '%btown'` | null→跳过 |
| `likeRight` | `User::name likeRight "btown"` | `LIKE 'btown%'` | null→跳过 |
| `notLike` / `notLikeLeft` / `notLikeRight` | 同 `like` 系列 | `NOT LIKE '%btown%'` / `'%btown'` / `'btown%'` | null→跳过 |
| `between` | `User::age between (18 to 60)` | `BETWEEN 18 AND 60` | 单端 null→降级为 ge/le，双端 null→跳过 |
| `notBetween` | `User::age notBetween (18 to 60)` | `NOT BETWEEN 18 AND 60` | 单端 null→降级为 lt/gt，双端 null→跳过 |
| `in` | `User::role in listOf("A","B")` | `IN ('A','B')` | null→跳过，空集合→`1<>1`（无匹配） |
| `notIn` | `User::role notIn listOf("X")` | `NOT IN ('X')` | 空集合→跳过 |
| `isNull` | `User::name.isNull` | `IS NULL` | 不降级 |
| `isNotNull` | `User::name.isNotNull` | `IS NOT NULL` | 不降级 |
| `inSql` | `User::id inSql "SELECT id FROM t"` | `IN (子查询)` | 空字符串→跳过 |
| `notInSql` | `User::id notInSql "..."` | `NOT IN (子查询)` | 空字符串→跳过 |

> `isNull` / `isNotNull` 无参数，以属性方式呈现（`User::name.isNull`），其他操作符均为 infix 中缀调用。
>
> **注意 `ne` 与 `neOrNotNull` 的区别**：`ne` 语义为"null 也视为不相等"（`value == null` 时生成 `IS NOT NULL`），适合排他筛选；`neOrNotNull` 语义为"不为 null 时才排除"（`value == null` 时跳过），适合可选筛选项。
>
> **注意 `in` 空集合的处理**：操作符在 safe 模式下 null 值直接跳过（不添加条件），但 `in` 的`空集合`不能简单跳过——`WHERE col IN ()` 是非法 SQL，跳过则会查出全部数据。因此空集合在 safe 模式下生成 `1<>1`，强制返回空结果集，更符合"空列表匹配不到任何数据"的业务直觉。NotIn 不受影响，空集合直接跳过。

### vs 原生 QueryWrapper

搜索表单场景——参数可能为 null，需要在业务代码中大量 if-else 判空：

```kotlin
// 原生写法：每个字段手动判空
val wrapper = QueryWrapper<User>()
if (name != null) {
    wrapper.lambda().like(User::getName, name)
}
if (status != null) {
    wrapper.lambda().eq(User::getStatus, status)
}
if (ageMin != null) {
    wrapper.lambda().ge(User::getAge, ageMin)
}
if (ageMax != null) {
    wrapper.lambda().le(User::getAge, ageMax)
}
if (!roleList.isNullOrEmpty()) {
    wrapper.lambda().`in`(User::getRole, roleList)
}
```

```kotlin
// ktWhereSafe：null 自动降级，零 if-else
userService.createQueryWrapper()
    .ktWhereSafe {
        User::name like name
        User::status eq status
        User::age between (ageMin to ageMax)
        User::role in roleList
    }
    .selectList()
```

**普通模式** — 与原生行为一致，不做任何 null 处理：

```kotlin
// 原生
wrapper.lambda().eq(User::getName, "btown").ge(User::getAge, 18)

// ktWhere — 等价写法
userService.createQueryWrapper()
    .ktWhere {
        User::name eq "btown"
        User::age ge 18
    }
    .selectList()
```

## 逻辑组合

### and / or

```kotlin
ktWhere {
    User::status eq 1
    and {
        User::role eq "admin"
        or {
            User::level ge 5
            User::vip eq true
        }
    }
}
```

```sql
WHERE status = 1
  AND (role = 'admin' OR level >= 5 OR vip = TRUE)
```

### anyOf

对同一个或多个字段的候选值做 OR 匹配，生成 `AND (col = 'a' OR col = 'b' OR col = 'c')` 结构：

```kotlin
// 单字段多值模糊匹配
ktWhere {
    anyOf(listOf("a", "b", "c")) {
        User::name like it
    }
}
// SQL: WHERE (name LIKE '%a%' OR name LIKE '%b%' OR name LIKE '%c%')

// 携带额外固定条件
ktWhere {
    User::status eq 1
    anyOf(listOf(18, 20, 25)) {
        User::age eq it
    }
}
// SQL: WHERE status = 1 AND (age = 18 OR age = 20 OR age = 25)
```

### apply — 手写 SQL 片段

与原生 `QueryWrapper.apply()` 一致，一般不推荐使用，优先使用类型安全的操作符。

```kotlin
ktWhere {
    apply("DATE(create_time) = CURDATE()")
    apply("col1 = {0} AND col2 = {1}", 1, 2)  // 参数化，防 SQL 注入
}
```

## 排序

```kotlin
ktOrder {
    User::status.orderByAsc     -- ORDER BY status ASC
    User::id.orderByDesc        -- ORDER BY status ASC, id DESC
}
```

## 指定返回列

```kotlin
.ktSelect(User::id, User::name)     -- SELECT id, name
```

## 执行方法

所有方法通过 `QueryWrapper` 直接调用，无需注入对应 Service：

```kotlin
val wrapper = userService.createQueryWrapper<User>()

// 列表
val users = wrapper.ktWhere { User::status eq 1 }.selectList()

// 分页
val page = wrapper.ktWhere { User::age ge 18 }.selectPage(Page<User>(1, 10))

// 单条
val user = wrapper.ktWhere { User::id eq 100L }.selectOne()

// 计数
val count = wrapper.ktWhere { User::status eq 1 }.selectCount()

// Map 列表（仅查部分列）
val maps = wrapper
    .ktSelect(User::id, User::name)
    .ktWhere { User::status eq 1 }
    .selectMapList()

// 删除
val ok = wrapper.ktWhere { User::status eq -1 }.remove()
```

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `selectList()` | `List<T>` | 列表查询，无匹配返回空列表 |
| `selectOne()` | `T?` | 单条查询，多条时抛 `TooManyResultsException` |
| `selectCount()` | `Long` | 计数 |
| `selectPage(page)` | `IPage<T>` | 分页查询 |
| `selectMapList()` | `List<Map<String,Any>>` | Map 列表，key 为数据库列名 |
| `selectMap()` | `Map<String,Any>?` | 单条 Map |
| `remove()` | `Boolean` | 按条件删除（受逻辑删除配置影响） |

## 自定义列名映射

绝大多数场景无需额外配置——MyBatis-Plus 的 `@TableField` 注解已声明字段到列名的映射，框架自动从 `TableInfo` 读取。仅当列名依赖运行时上下文（如分表、多租户）或需要覆盖 MP 默认映射时才需实现 `ColumnNameProvider`。

### 解析优先级

```
ColumnNameProvider  >  MP TableInfo（@TableField）  >  camelToUnderline 兜底
```

### 静态映射

列名固定不变，启动时缓存，每次查询直接读缓存：

```kotlin
@Service
class UserServiceImpl : ServiceImpl<UserMapper, User>(), IUserService, ColumnNameProvider {

    override fun resolveFieldColumnMap(): Map<String, String> = mapOf(
        "name" to "user_name",
        "age" to "user_age"
    )

    override fun isDynamic(): Boolean = false  // 默认值，可不写
}
```

### 动态映射

分表、多租户等场景下列名可能动态变化，`isDynamic() = true` 时不走缓存，每次查询实时调用 `resolveFieldColumnMap()` 获取最新映射：

```kotlin
@Service
class OrderServiceImpl : ServiceImpl<OrderMapper, Order>(), IOrderService, ColumnNameProvider {

    override fun resolveFieldColumnMap(): Map<String, String> {
        val suffix = TenantContext.getSuffix()  // 运行时获取分表后缀
        return mapOf(
            "amount" to "amount_$suffix",
            "status" to "status_$suffix"
        )
    }

    override fun isDynamic(): Boolean = true
}
```

> 动态映射有实时调用的性能开销，仅在列名确实依赖运行时上下文时才开启。静态场景关闭 `isDynamic` 让框架走缓存。
