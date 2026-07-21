package io.github.btown021.mybatisplus.kt.support

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.extension.service.IService
import io.github.btown021.mybatisplus.kt.spi.ColumnNameProvider
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory

/**
 * 列名映射全局缓存
 * - 启动时结合 [ColumnNameProvider] 与 MyBatis-Plus TableInfo 构建映射
 * - 支持动态映射（isDynamic = true 时每次查询重新获取）
 * - 未匹配的属性实时驼峰转下划线兜底
 *
 * @author btown
 * @date 2026/7/21
 */
object ColumnMappingRegistry {

    private val log = LoggerFactory.getLogger(ColumnMappingRegistry::class.java)

    private val cache = ConcurrentHashMap<Class<*>, Map<String, String>>()
    private val dynamicClasses = ConcurrentHashMap.newKeySet<Class<*>>()

    fun register(entityClass: Class<*>, service: IService<*>) {
        if (service is ColumnNameProvider && service.isDynamic()) {
            dynamicClasses.add(entityClass)
            cache.remove(entityClass)
            log.debug("mybatis-plus-kt: dynamic column mapping for {}", entityClass.simpleName)
            return
        }
        if (service is ColumnNameProvider) {
            cache[entityClass] = service.resolveFieldColumnMap()
            log.debug("mybatis-plus-kt: ColumnNameProvider mapping for {}", entityClass.simpleName)
            return
        }
        val columnMap = buildColumnMapFromTableInfo(entityClass)
        if (columnMap.isNotEmpty()) {
            cache[entityClass] = columnMap
            log.debug("mybatis-plus-kt: TableInfo mapping for {}", entityClass.simpleName)
        }
    }

    fun getColumnName(entityClass: Class<*>, fieldName: String): String? =
        cache[entityClass]?.get(fieldName)

    fun isDynamic(entityClass: Class<*>): Boolean = dynamicClasses.contains(entityClass)

    private fun buildColumnMapFromTableInfo(entityClass: Class<*>): Map<String, String> {
        val tableInfo = TableInfoHelper.getTableInfo(entityClass) ?: return emptyMap()
        return tableInfo.fieldList.associate { it.property to it.column }
    }
}
