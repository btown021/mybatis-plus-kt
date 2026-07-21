package io.github.btown021.mybatisplus.kt.support

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline
import com.baomidou.mybatisplus.extension.service.IService
import io.github.btown021.mybatisplus.kt.spi.ColumnNameProvider
import kotlin.reflect.KProperty1

/**
 * QueryWrapper DSL 内部支持函数 — 列名解析 & Service 查找
 *
 * @author btown
 * @date 2026/7/21
 */

private fun <T> resolveEntityClass(wrapper: QueryWrapper<T>): Class<T> =
    wrapper.entityClass
        ?: throw IllegalArgumentException(
            "QueryWrapper 未设置实体类型，请使用 IService.createQueryWrapper() 创建"
        )

/**
 * KProperty1 → 数据库列名解析
 * 优先级：ColumnNameProvider（动态类实时查）→ ColumnMappingRegistry 缓存 → TableInfo 懒加载 → camelToUnderline 兜底
 */
@PublishedApi
internal fun <T> resolveColumnName(
    property: KProperty1<T, *>,
    wrapper: QueryWrapper<T>,
): String {
    val entityClass = resolveEntityClass(wrapper)
    val fieldName = property.name

    if (ColumnMappingRegistry.isDynamic(entityClass)) {
        val service = ServiceRegistry.resolve(entityClass)
        if (service is ColumnNameProvider) {
            return service.resolveFieldColumnMap()[fieldName]
                ?: camelToUnderline(fieldName) ?: fieldName
        }
        return camelToUnderline(fieldName) ?: fieldName
    }

    val cached = ColumnMappingRegistry.getColumnName(entityClass, fieldName)
    if (cached != null) return cached

    val tableInfo = TableInfoHelper.getTableInfo(entityClass)
    if (tableInfo != null) {
        val column = tableInfo.fieldList.find { it.property == fieldName }?.column
        if (column != null) return column
    }

    return camelToUnderline(fieldName) ?: fieldName
}

/** QueryWrapper → IService 查找，用于执行 selectPage / selectList 等操作 */
fun <T> resolveServiceBean(wrapper: QueryWrapper<T>): IService<T> {
    val entityClass = resolveEntityClass(wrapper)
    @Suppress("UNCHECKED_CAST")
    return ServiceRegistry.resolve(entityClass) as? IService<T>
        ?: throw IllegalStateException(
            "未找到 ${entityClass.simpleName} 对应的 IService Bean，请确保 Spring 容器中存在该 Service"
        )
}
