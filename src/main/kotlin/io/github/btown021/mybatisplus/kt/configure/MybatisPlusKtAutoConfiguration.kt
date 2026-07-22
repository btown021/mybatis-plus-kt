package io.github.btown021.mybatisplus.kt.configure

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import io.github.btown021.mybatisplus.kt.support.CacheInitializer
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@AutoConfiguration
@ConditionalOnClass(QueryWrapper::class, IService::class)
class MybatisPlusKtAutoConfiguration(
    private val applicationContext: ApplicationContext
) {

    @EventListener(ContextRefreshedEvent::class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun onContextRefreshed() {
        CacheInitializer.init(applicationContext)
    }
}