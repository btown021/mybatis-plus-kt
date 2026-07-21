package io.github.btown021.mybatisplus.kt.query

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import io.github.btown021.mybatisplus.kt.support.resolveColumnName
import kotlin.reflect.KProperty1

/**
 * QueryWrapper Kotlin DSL 入口 — 类型安全的查询条件构建
 *
 * ## 快速示例
 * ```
 * // 查询
 * service.createQueryWrapper()
 *     .ktWhere { User::name eq "btown"; User::age ge 18 }
 *     .selectList()
 *
 * // 排序
 * service.createQueryWrapper()
 *     .ktOrder { User::id.orderByDesc }
 *     .selectList()
 *
 * // 安全模式 — null 值自动跳过
 * val name: String? = null
 * service.createQueryWrapper()
 *     .ktWhereSafe { User::name eq name }  // name 为 null 时不生成条件
 *     .selectList()
 * ```
 *
 * @see QueryBuilder DSL 条件操作符
 * @see OrderBuilder 排序操作符
 * @author btown
 * @date 2026/7/21
 */

/**
 * 普通模式查询条件构建
 *
 * 通过 Lambda 属性引用（KProperty1）自动解析列名，提供 infix 中缀 DSL 语法。
 * **null 值会透传到 SQL**，可能生成 `WHERE col = NULL`（始终 false）。
 * 如需 null 自动降级跳过，使用 [ktWhereSafe]。
 *
 * @param block 条件 DSL，接收 [QueryBuilder]，内部使用 `实体::字段 infix 操作符 值` 语法
 * @return 当前 QueryWrapper，可链式调用 selectList / selectPage 等执行查询
 */
inline fun <T> QueryWrapper<T>.ktWhere(block: QueryBuilder<T>.() -> Unit): QueryWrapper<T> {
  QueryBuilder(this, safe = false).block()
  return this
}

/**
 * 安全模式查询条件构建
 *
 * 与 [ktWhere] 相同语法，但 null 值自动降级跳过，不生成该条件。
 * 适用于搜索表单等场景——用户未填写的字段不会产生 `WHERE col = NULL`。
 *
 * @param block 条件 DSL，接收 [QueryBuilder]
 * @return 当前 QueryWrapper，可链式调用执行查询
 */
inline fun <T> QueryWrapper<T>.ktWhereSafe(block: QueryBuilder<T>.() -> Unit): QueryWrapper<T> {
  QueryBuilder(this, safe = true).block()
  return this
}

/**
 * 排序构建
 *
 * 通过 Lambda 属性引用自动解析列名，支持 `orderByAsc` / `orderByDesc`。
 *
 * ## 示例
 * ```
 * // 单字段
 * ktOrder { User::id.orderByDesc }
 *
 * // 多字段（按调用顺序）
 * ktOrder { User::status.orderByAsc; User::id.orderByDesc }
 * ```
 *
 * @param block 排序 DSL，接收 [OrderBuilder]
 * @return 当前 QueryWrapper
 */
inline fun <T> QueryWrapper<T>.ktOrder(block: OrderBuilder<T>.() -> Unit): QueryWrapper<T> {
  OrderBuilder(this).block()
  return this
}

/**
 * 指定 SELECT 列
 *
 * 通过 Lambda 属性引用自动解析列名，替代手写字符串。
 *
 * ## 示例
 * ```
 * .ktSelect(User::id, User::name)  // → SELECT id, name
 * ```
 *
 * @param properties 要查询的属性引用列表
 * @return 当前 QueryWrapper
 */
fun <T> QueryWrapper<T>.ktSelect(vararg properties: KProperty1<T, *>): QueryWrapper<T> {
  val columns = properties.map { property -> resolveColumnName(property, this) }
  this.select(columns)
  return this
}
