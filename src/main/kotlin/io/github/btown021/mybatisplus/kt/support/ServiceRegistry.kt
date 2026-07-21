package io.github.btown021.mybatisplus.kt.support

import com.baomidou.mybatisplus.extension.service.IService
import java.util.concurrent.ConcurrentHashMap

/**
 * 全局 IService 缓存
 *
 * @author btown
 * @date 2026/7/21
 */
object ServiceRegistry {

    private val entityServiceMap = ConcurrentHashMap<Class<*>, IService<*>>()

    fun put(entityClass: Class<*>, service: IService<*>) {
        entityServiceMap[entityClass] = service
    }

    fun resolve(entityClass: Class<*>): IService<*>? = entityServiceMap[entityClass]
}
