package io.github.btown021.mybatisplus.kt.query

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import io.github.btown021.mybatisplus.kt.support.resolveColumnName
import io.github.btown021.mybatisplus.kt.support.resolveEntityClass
import kotlin.reflect.KProperty1

/**
 * 排序构建器 — 通过 [ktOrder] 入口使用
 *
 * @author btown
 * @date 2026/7/21
 */
class OrderBuilder<T>(private val wrapper: QueryWrapper<T>) {

    @PublishedApi internal  fun buildOrder(
        property: KProperty1<T, *>,
        isAsc: Boolean
    ) {
        val column = resolveColumnName(property, resolveEntityClass(wrapper))
        if (isAsc) wrapper.orderByAsc(column) else wrapper.orderByDesc(column)
    }

    /**
     * 排序：ORDER BY 字段, ... ASC
     * 用法：User::id.orderByAsc
     */
    inline val KProperty1<T, *>.orderByAsc: Unit get() = buildOrder(this, true)

    /**
     * 排序：ORDER BY 字段, ... DESC
     * 用法：User::id.orderByDesc
     */
    inline val KProperty1<T, *>.orderByDesc: Unit get() = buildOrder(this, false)
}
