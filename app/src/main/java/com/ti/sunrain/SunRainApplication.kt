package com.ti.sunrain

import android.app.Application
import android.content.Context

/**
 * @author: tihon
 * @date: 2020/12/3
 * @description:
 */
class SunRainApplication:Application() {

    /**
     * 静态获取全局上下文的单例类
     */
    companion object{
        @Suppress("StaticFieldLeak")
        lateinit var context:Context

        //彩云天气令牌值
        const val TOKEN = "QTeX2n8AQjAhR9BV"
    }

    /**
     * 全局调用 context
     */
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}