package io.github.btown021.mybatisplus.kt.query

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper

/**
 * 查询操作符封装 — 每个 object 代表一种 SQL 条件操作
 *
 * [conditionalActuator] 接收 safe 参数：safe=true 时 null 值降级跳过，safe=false 时原样透传
 *
 * @author btown
 * @date 2026/7/21
 */
sealed class QueryOperator<in P>(
    val conditionalActuator: (wrapper: QueryWrapper<*>, column: String, value: P, safe: Boolean) -> Unit
) {
    // null 值时忽略条件，safe 模式专用
    object Eq : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.eq(column, value)
            !safe -> wrapper.eq(column, value)
        }
    })

    // null 值时转为 IS NULL
    object EqOrIsNull : QueryOperator<Any?>({ wrapper, column, value, safe ->
        if (value == null) {
            wrapper.isNull(column)
        } else {
            wrapper.eq(column, value)
        }
    })

    // null 值时忽略条件（！= null 没意义）
    object Ne : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.ne(column, value)
            !safe -> wrapper.ne(column, value)
        }
    })

    // null 值时转为 IS NOT NULL
    object NeOrIsNull : QueryOperator<Any?>({ wrapper, column, value, safe ->
        if (value == null) {
            wrapper.isNotNull(column)
        } else {
            wrapper.ne(column, value)
        }
    })


    object Gt : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.gt(column, value)
            !safe -> wrapper.gt(column, value)
        }
    })

    object Ge : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.ge(column, value)
            !safe -> wrapper.ge(column, value)
        }
    })

    object Lt : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.lt(column, value)
            !safe -> wrapper.lt(column, value)
        }
    })


    object Le : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.le(column, value)
            !safe -> wrapper.le(column, value)
        }
    })

    object Like : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.like(column, value)
            !safe -> wrapper.like(column, value)
        }
    })


    object LikeLeft : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.likeLeft(column, value)
            !safe -> wrapper.likeLeft(column, value)
        }
    })

    object LikeRight : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.likeRight(column, value)
            !safe -> wrapper.likeRight(column, value)
        }
    })


    object NotLike : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.notLike(column, value)
            !safe -> wrapper.notLike(column, value)
        }
    })

    object NotLikeLeft : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.notLikeLeft(column, value)
            !safe -> wrapper.notLikeLeft(column, value)
        }
    })

    object NotLikeRight : QueryOperator<Any?>({ wrapper, column, value, safe ->
        when {
            safe && value != null -> wrapper.notLikeRight(column, value)
            !safe -> wrapper.notLikeRight(column, value)
        }
    })

    object Between : QueryOperator<Pair<Any?, Any?>>({ wrapper, column, value, safe ->
        when {
            !safe ->
                wrapper.between(column, value.first, value.second)

            value.first != null && value.second != null ->
                wrapper.between(column, value.first, value.second)

            value.first != null ->
                wrapper.ge(column, value.first)

            value.second != null ->
                wrapper.le(column, value.second)

        }
    })

    object NotBetween : QueryOperator<Pair<Any?, Any?>>({ wrapper, column, value, safe ->
       when {
           !safe ->
               wrapper.notBetween(column, value.first, value.second)

           value.first != null && value.second != null ->
               wrapper.notBetween(column, value.first, value.second)

           value.first != null ->
               wrapper.lt(column, value.first)

           value.second != null ->
               wrapper.gt(column, value.second)

       }
    })


    object In : QueryOperator<Collection<Any?>?>({ wrapper, column, values, safe ->
        when {
            !safe -> wrapper.`in`(column, values)
            !values.isNullOrEmpty() -> wrapper.`in`(column, values)
        }
    })


    object NotIn : QueryOperator<Collection<Any?>?>({ wrapper, column, values, safe ->
        when {
            !safe -> wrapper.notIn(column, values)
            !values.isNullOrEmpty() -> wrapper.notIn(column, values)
        }
    })


    object IsNull : QueryOperator<Unit>({ wrapper, column, _, safe ->
        wrapper.isNull(column)
    })


    object IsNotNull : QueryOperator<Unit>({ wrapper, column, _, safe ->
        wrapper.isNotNull(column)
    })


    object InSql : QueryOperator<String?>({ wrapper, column, value, safe ->
        when {
            !safe -> wrapper.inSql(column, value)
            !value.isNullOrBlank() -> wrapper.inSql(column, value)
        }
    })


    object NotInSql : QueryOperator<String?>({ wrapper, column, value, safe ->
        when {
            !safe -> wrapper.notInSql(column, value)
            !value.isNullOrBlank() -> wrapper.notInSql(column, value)
        }
    })

}