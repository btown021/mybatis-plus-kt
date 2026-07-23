package io.github.btown021.mybatisplus.kt.support

import com.baomidou.mybatisplus.extension.repository.IRepository
import java.util.concurrent.ConcurrentHashMap

/**
 * 全局 IService 缓存
 *
 * @author btown
 * @date 2026/7/21
 */
object ServiceRegistry {

    private val entityServiceMap = ConcurrentHashMap<Class<*>, IRepository<*>>()

    fun put(entityClass: Class<*>, service: IRepository<*>) {
        entityServiceMap[entityClass] = service
    }

    fun resolve(entityClass: Class<*>): IRepository<*>? = entityServiceMap[entityClass]
}
