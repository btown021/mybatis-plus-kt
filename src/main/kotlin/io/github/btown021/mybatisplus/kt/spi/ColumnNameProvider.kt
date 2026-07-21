package io.github.btown021.mybatisplus.kt.spi

/**
 * 列名映射 SPI — 实体 Service 可实现此接口自定义字段→列名映射
 *
 * 优先级：ColumnNameProvider > MyBatis-Plus TableInfo > camelToUnderline 兜底
 *
 * @author btown
 * @date 2026/7/21
 */
interface ColumnNameProvider {
    fun resolveFieldColumnMap(): Map<String, String>

    fun isDynamic(): Boolean = false
}