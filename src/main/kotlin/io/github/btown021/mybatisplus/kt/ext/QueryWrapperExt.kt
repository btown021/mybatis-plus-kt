package io.github.btown021.mybatisplus.kt.ext

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import io.github.btown021.mybatisplus.kt.support.resolveEntityClass
import io.github.btown021.mybatisplus.kt.support.resolveServiceBean

/**
 * 分页查询
 *
 * @param page MyBatis-Plus 分页对象（Page / MyPage 等实现 IPage 的类）
 * @return 分页结果
 */
fun <T, E : IPage<T>> QueryWrapper<T>.selectPage(page: E): IPage<T> =
    resolveServiceBean(resolveEntityClass(this)).page(page, this)

/**
 * 列表查询
 *
 * @return 查询结果列表，无匹配时返回空列表
 */
fun <T> QueryWrapper<T>.selectList(): List<T> =
    resolveServiceBean(resolveEntityClass(this)).list(this)

/**
 * 单条查询
 *
 * @return 匹配的实体，无匹配返回 null；多条匹配时可能抛出 TooManyResultsException
 */
fun <T> QueryWrapper<T>.selectOne(): T? = resolveServiceBean(resolveEntityClass(this)).getOne(this)

/**
 * 计数查询
 *
 * @return 匹配行数
 */
fun <T> QueryWrapper<T>.selectCount(): Long =
    resolveServiceBean(resolveEntityClass(this)).count(this)

/** Map 列表查询 — 每行以 Map<String, Any> 返回，key 为数据库列名 */
fun <T> QueryWrapper<T>.selectMapList(): List<Map<String, Any>> =
    resolveServiceBean(resolveEntityClass(this)).listMaps(this)

/** 是否存在 */
fun <T> QueryWrapper<T>.selectExists(): Boolean =
    resolveServiceBean(resolveEntityClass(this)).exists(this)

/**
 * 单条 Map 查询
 *
 * @return 单行 Map，无匹配返回 null
 */
fun <T> QueryWrapper<T>.selectMap(): Map<String, Any> =
    resolveServiceBean(resolveEntityClass(this)).getMap(this)

/**
 * 按条件删除 — 根据 QueryWrapper 条件删除记录 注意：受 MyBatis-Plus 逻辑删除配置影响，配置了逻辑删除的表会执行 UPDATE 而非 DELETE
 *
 * @return true 表示操作成功
 */
fun <T> QueryWrapper<T>.remove(): Boolean =
    resolveServiceBean(resolveEntityClass(this)).remove(this)
