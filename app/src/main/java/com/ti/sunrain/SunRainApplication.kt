package com.ti.sunrain

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:这个类的作用是提供全局的context
 */
class SunRainApplication:Application() {

    /**
     * 静态获取全局上下文的单例类
     */
    companion object{
        //确保注解通过 as 的静态审查，不带这个会报错 2020-12-01
        @Suppress("StaticFieldLeak")
        lateinit var context:Context
        lateinit var settingsPreference:SharedPreferences

        //彩云天气令牌值
        const val TOKEN = "QTeX2n8AQjAhR9BV"
    }

    /**
     * 全局调用 context
     */
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        settingsPreference = getSharedPreferences("settings",0)
        
    }
}