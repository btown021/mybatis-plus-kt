package io.github.btown021.mybatisplus.kt.ext

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.repository.IRepository
import io.github.btown021.mybatisplus.kt.support.resolveColumnName
import kotlin.reflect.KProperty1

/**
 * 创建已绑定实体类的 QueryWrapper
 *
 *
 * **强烈推荐**通过此方法创建 QueryWrapper，而非直接 `QueryWrapper()`。 直接构造的 QueryWrapper 未设置 `entityClass`，后续
 * `resolveColumnName` 将无法解析列名。
 *
 * @return 已绑定实体类信息（entityClass）的 QueryWrapper
 */
fun <T> IRepository<T>.createQueryWrapper(): QueryWrapper<T> =
    this.entityClass.let { clazz ->
        QueryWrapper<T>().apply {
            entityClass = clazz
        }
    }

/**
 * 获取对应的数据库列名。
 */
fun <T> IRepository<T>.columnOf(property: KProperty1<T, *>): String =
    resolveColumnName(property, this.entityClass)
