package io.github.btown021.mybatisplus.kt.configure

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import io.github.btown021.mybatisplus.kt.support.CacheInitializer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.ApplicationListener

/**
 * mybatis-plus-kt 自动配置 — 应用启动完成后触发 [CacheInitializer] 扫描 IService Bean 并建立缓存
 *
 * @author btown
 * @date 2026/7/21
 */
@AutoConfiguration
@ConditionalOnClass(QueryWrapper::class, IService::class)
class MybatisPlusKtAutoConfiguration {

    @Bean
    fun cacheInitializer(applicationContext: ApplicationContext): ApplicationListener<ApplicationReadyEvent> =
        ApplicationListener<ApplicationReadyEvent> {
            CacheInitializer.init(applicationContext)
        }
}
