package io.github.btown021.mybatisplus.kt.support

import com.baomidou.mybatisplus.extension.service.IService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

/**
 * 缓存初始化器 — 启动时扫描 Spring 容器中所有 IService Bean 并填充 [ServiceRegistry] 与 [ColumnMappingRegistry]
 *
 * @author btown
 * @date 2026/7/21
 */
object CacheInitializer {

    private val log = LoggerFactory.getLogger(CacheInitializer::class.java)

    fun init(applicationContext: ApplicationContext) {
        var count = 0
        val serviceBeans = applicationContext.getBeansOfType(IService::class.java)
        serviceBeans.forEach { (beanName, service) ->
            val entityClass = service.entityClass
            if (entityClass != null) {
                ServiceRegistry.put(entityClass, service)
                ColumnMappingRegistry.register(entityClass, service)
                count++
                log.debug("mybatis-plus-kt: registered {} -> {}", entityClass.simpleName, beanName)
            } else {
                log.debug("mybatis-plus-kt: skip {} (entity class not resolved)", beanName)
            }
        }
        log.info("mybatis-plus-kt: initialized {} entity-service mappings", count)
    }
}
