package com.ti.sunrain.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:
 */

/**
 * url构建器，retrofit，单例类以确保全局唯一性
 */
object ServiceCreator {

    private const val BASE_URL = "https://api.caiyunapp.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
            //Google 的 GSON 转换库
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Retrofit 2.1, retrofit.create(), serviceClass是动态代理对象, 也就是可以使用对应接口的所有方法
    /**
     * 动态代理（Dynamic Proxy）的机制：可以在运行期动态创建某个interface的实例。
     *
     * kotlin 继承了 java 的机制，不实例化接口，而是直接调用接口对象
     *
     * 2020-12-04 来自 kotlin 中文网
     */
    fun <T> create(serviceClass: Class<T>):T = retrofit.create(serviceClass)

    //inline fun <reified T> create() : T= create(T::class.java)
}