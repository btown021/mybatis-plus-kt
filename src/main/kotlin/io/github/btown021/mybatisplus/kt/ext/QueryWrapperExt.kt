package io.github.btown021.mybatisplus.kt.ext

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.service.IService
import io.github.btown021.mybatisplus.kt.support.resolveEntityClass
import io.github.btown021.mybatisplus.kt.support.resolveServiceBean

/**
 * QueryWrapper 执行器 — 通过 IService 直接执行 CRUD，无需手动注入 Service Bean
 *
 * ## 设计原理
 * 每个 QueryWrapper 在创建时必须绑定实体类（通过 [createQueryWrapper]），
 * 运行时通过 [ServiceRegistry] 反向查找对应的 IService Bean 执行数据库操作。
 * 这样调用方只需持有 QueryWrapper 即可完成查询，无需持有 Service 引用。
 *
 * ## 快速示例
 * ```
 * // 创建 QueryWrapper（必须通过此方法，会自动绑定实体类）
 * val wrapper = someService.createQueryWrapper<User>()
 *
 * // 构建条件 + 执行查询
 * wrapper.ktWhere { User::status eq 1 }.selectList()
 *
 * // 分页
 * val page = Page<User>(1, 10)
 * wrapper.selectPage(page)
 * ```
 *
 * @author btown
 * @date 2026/7/21
 */

/**
 * 分页查询
 * @param page MyBatis-Plus 分页对象（Page / MyPage 等实现 IPage 的类）
 * @return 分页结果
 */
fun <T, E : IPage<T>> QueryWrapper<T>.selectPage(page: E): IPage<T> = resolveServiceBean(resolveEntityClass(this)).page(page, this)

/**
 * 列表查询
 * @return 查询结果列表，无匹配时返回空列表
 */
fun <T> QueryWrapper<T>.selectList(): List<T> = resolveServiceBean(resolveEntityClass(this)).list(this)

/**
 * 单条查询
 * @return 匹配的实体，无匹配返回 null；多条匹配时可能抛出 TooManyResultsException
 */
fun <T> QueryWrapper<T>.selectOne(): T? = resolveServiceBean(resolveEntityClass(this)).getOne(this)

/**
 * 计数查询
 * @return 匹配行数
 */
fun <T> QueryWrapper<T>.selectCount(): Long = resolveServiceBean(resolveEntityClass(this)).count(this)

/**
 * Map 列表查询 — 每行以 Map<String, Any> 返回，key 为数据库列名
 */
fun <T> QueryWrapper<T>.selectMapList(): List<Map<String, Any>> = resolveServiceBean(resolveEntityClass(this)).listMaps(this)

/**
 * 单条 Map 查询
 * @return 单行 Map，无匹配返回 null
 */
fun <T> QueryWrapper<T>.selectMap(): Map<String, Any> = resolveServiceBean(resolveEntityClass(this)).getMap(this)

/**
 * 按条件删除 — 根据 QueryWrapper 条件删除记录
 * 注意：受 MyBatis-Plus 逻辑删除配置影响，配置了逻辑删除的表会执行 UPDATE 而非 DELETE
 * @return true 表示操作成功
 */
fun <T> QueryWrapper<T>.remove(): Boolean = resolveServiceBean(resolveEntityClass(this)).remove(this)

/**
 * 创建已绑定实体类的 QueryWrapper
 *
 * **强烈推荐**通过此方法创建 QueryWrapper，而非直接 `QueryWrapper<T>()`。
 * 直接构造的 QueryWrapper 未设置 `entityClass`，后续 `resolveColumnName` 将无法解析列名。
 *
 * @param T 实体类型（reified 内联泛型，编译后保留类型信息）
 * @return 已绑定实体类信息（entityClass）的 QueryWrapper
 */
inline fun <reified T> IService<T>.createQueryWrapper(): QueryWrapper<T> {
    val queryWrapper = QueryWrapper<T>()
    queryWrapper.setEntityClass(T::class.java)
    return queryWrapper
}
