package io.github.btown021.mybatisplus.kt.query

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import io.github.btown021.mybatisplus.kt.support.resolveColumnName
import io.github.btown021.mybatisplus.kt.support.resolveEntityClass
import kotlin.reflect.KProperty1

/**
 * 查询条件构建器 — 提供 Kotlin DSL 语法构建 QueryWrapper 条件
 *
 * 通过 [ktWhere] / [ktWhereSafe] 入口创建，safe 模式下 null 值自动降级跳过
 *
 * @author btown
 * @date 2026/7/21
 */
class QueryBuilder<T>(
    @PublishedApi internal val wrapper: QueryWrapper<T>,
    @PublishedApi internal val safe: Boolean
) {

    /**
     * 将 Lambda 属性引用解析为数据库列名后执行操作符条件
     */
    @PublishedApi
    internal fun <P> buildCondition(
        operator: QueryOperator<P>,
        property: KProperty1<T, *>,
        value: P
    ) {
        operator.conditionalActuator(wrapper, property.column(), value, safe)
    }

    inline fun KProperty1<T, *>.column() : String = resolveColumnName(this, resolveEntityClass(wrapper))


    inline fun and(crossinline block: QueryBuilder<T>.() -> Unit) {
        wrapper.and { subWrapper ->
            QueryBuilder(subWrapper, safe).block()
        }
    }

    inline fun or(crossinline block: QueryBuilder<T>.() -> Unit) {
        wrapper.or { subWrapper ->
            QueryBuilder(subWrapper, safe).block()
        }
    }

    /**
     * OR 分组：对同一个字段匹配多个候选值，如 WHERE (col = 'A' OR col = 'B')
     */
    inline fun <P> anyOf(params: Collection<P>, crossinline operation: QueryBuilder<T>.(P) -> Unit) {
        if (params.isNotEmpty()) {
            wrapper.and { queryWrapper ->
                for (param in params) {
                    queryWrapper.or {
                        // it 是 QueryWrapper<T>，需要转换成 QueryBuilder<T>
                        QueryBuilder<T>(it, safe).operation(param)
                    }
                }
            }
        }
    }

    fun apply(sql: String, vararg args: Any?) {
        wrapper.apply(sql, *args)
    }


    // ==================== 条件操作符 ====================
    // infix + KProperty1 实现自然 DSL 语法：User::name eq "btown"

  infix fun KProperty1<T, *>.eq(value: Any?) = buildCondition(QueryOperator.Eq, this, value)

  infix fun KProperty1<T, *>.eqOrIsNull(value: Any?) = buildCondition(QueryOperator.EqOrIsNull, this, value)

  // ne 的语义是"null 也视为不等" → NeOrIsNull（null → IS NOT NULL）
  infix fun KProperty1<T, *>.ne(value: Any?) = buildCondition(QueryOperator.NeOrIsNull, this, value)

  // neOrNotNull 的语义是"不为 null 时才不等于" → Ne（null → 忽略）
  infix fun KProperty1<T, *>.neOrNotNull(value: Any?) = buildCondition(QueryOperator.Ne, this, value)

  infix fun KProperty1<T, *>.gt(value: Any?) = buildCondition(QueryOperator.Gt, this, value)

  infix fun KProperty1<T, *>.ge(value: Any?) = buildCondition(QueryOperator.Ge, this, value)

  infix fun KProperty1<T, *>.lt(value: Any?) = buildCondition(QueryOperator.Lt, this, value)

  infix fun KProperty1<T, *>.le(value: Any?) = buildCondition(QueryOperator.Le, this, value)

  infix fun KProperty1<T, *>.like(value: Any?) = buildCondition(QueryOperator.Like, this, value)

  infix fun KProperty1<T, *>.likeLeft(value: Any?) = buildCondition(QueryOperator.LikeLeft, this, value)

  infix fun KProperty1<T, *>.likeRight(value: Any?) = buildCondition(QueryOperator.LikeRight, this, value)

  infix fun KProperty1<T, *>.notLike(value: Any?) = buildCondition(QueryOperator.NotLike, this, value)

  infix fun KProperty1<T, *>.notLikeLeft(value: Any?) = buildCondition(QueryOperator.NotLikeLeft, this, value)

  infix fun KProperty1<T, *>.notLikeRight(value: Any?) = buildCondition(QueryOperator.NotLikeRight, this, value)

  infix fun KProperty1<T, *>.between(value: Pair<Any?, Any?>) = buildCondition(QueryOperator.Between, this, value)

  infix fun KProperty1<T, *>.notBetween(value: Pair<Any?, Any?>) = buildCondition(QueryOperator.NotBetween, this, value)

  infix fun KProperty1<T, *>.`in`(value: Collection<Any?>) = buildCondition(QueryOperator.In, this, value)

  infix fun KProperty1<T, *>.notIn(value: Collection<Any?>) = buildCondition(QueryOperator.NotIn, this, value)

  infix fun KProperty1<T, *>.inSql(value: String) = buildCondition(QueryOperator.InSql, this, value)

  infix fun KProperty1<T, *>.notInSql(value: String) = buildCondition(QueryOperator.NotInSql, this, value)


  val KProperty1<T, *>.isNotNull: Unit get() = buildCondition(QueryOperator.IsNotNull, this, Unit)

  val KProperty1<T, *>.isNull: Unit get() = buildCondition(QueryOperator.IsNull, this, Unit)

}
