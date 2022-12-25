package com.ti.sunrain.ui.about

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.ti.sunrain.BuildConfig
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import com.ti.sunrain.logic.ActivitySet
import java.net.URISyntaxException
import android.graphics.Color
import com.ti.sunrain.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {
    lateinit var viewModel: AboutViewModel
    private lateinit var activityAboutBinding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //使用viewBinding代替ktx插件
        activityAboutBinding = ActivityAboutBinding.inflate(layoutInflater)
        val view = activityAboutBinding.root
        setContentView(view)

        //set
        ActivitySet.addActivity(this)

        //黑夜模式
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        viewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)

        if(isDarkTheme(this)){
            activityAboutBinding.emailIcon.setImageResource(R.drawable.baseline_email_white_24dp)
            activityAboutBinding.githubIcon.setImageResource(R.drawable.ic_github_white)
            activityAboutBinding.marketIcon.setImageResource(R.drawable.baseline_store_white_24dp)
            activityAboutBinding.updateIcon.setImageResource(R.drawable.baseline_open_in_browser_white_24dp)

            activityAboutBinding.wechatIcon.setImageResource(R.drawable.ic_wechat_white)
            activityAboutBinding.alipayIcon.setImageResource(R.drawable.ic_alipay_white)
            activityAboutBinding.adIcon.setImageResource(R.drawable.baseline_attach_money_white_24dp)

            activityAboutBinding.privacyIcon.setImageResource(R.drawable.baseline_menu_book_white_24dp)

            activityAboutBinding.githubIconSunnyWeather.setImageResource(R.drawable.ic_github_white)
            activityAboutBinding.githubIconPermissionX.setImageResource(R.drawable.ic_github_white)
            activityAboutBinding.githubIconRetrofit.setImageResource(R.drawable.ic_github_white)
            activityAboutBinding.githubIconmPAndroidChart.setImageResource(R.drawable.ic_github_white)
            activityAboutBinding.githubIconWeatherBg.setImageResource(R.drawable.ic_github_white)

            activityAboutBinding.aboutFAB.setImageResource(R.drawable.rainy_weather_100px)
            activityAboutBinding.questionIcon.setImageResource(R.drawable.baseline_help_outline_white_24dp)
            activityAboutBinding.freedomIcon.setImageResource(R.drawable.baseline_eco_white_24dp)
        }

        setSupportActionBar(activityAboutBinding.aboutToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }
        activityAboutBinding.aboutToolbar.overflowIcon = ContextCompat.getDrawable(this,R.drawable.baseline_more_vert_white_24dp)

        activityAboutBinding.aboutCollapsingToolbarLayout.apply {
            if(isDarkTheme(this@AboutActivity)){
                setExpandedTitleColor(ContextCompat.getColor(context,R.color.indigo500))
            }else{
                setExpandedTitleColor(ContextCompat.getColor(context,R.color.orange500))
            }
            setCollapsedTitleTextColor(Color.WHITE)
        }

        //触发彩蛋
        activityAboutBinding.aboutBackgroundImage.setOnClickListener {
            getRandomForEgg(this)
        }

        activityAboutBinding.aboutFAB.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("关于")
                .setMessage("晴雨：一个开源的 Android 天气预报 APP\n\n"+
                            "V ${BuildConfig.VERSION_NAME}\n\n"+
                            "本APP/项目所使用的开源协议不涉及此处背景图片的二次授权。图片版权" +
                            "仍归原作者绘恋Galgame制作组所有。")
                .setPositiveButton(R.string.ok,null)
                .show()
        }

        activityAboutBinding.emailMeAbout.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:tihongsheng@foxmail.com")
                putExtra(Intent.EXTRA_SUBJECT,"SunRain反馈")
            }

            //这是Android 11 之前的旧写法，绕开了package权限，却能读取package
//            if(emailIntent.resolveActivity(packageManager)!=null){
//                startActivity(emailIntent)
//            }else{
//                Snackbar.make(parentLiner,resources.getString(R.string.email_not_found),Snackbar.LENGTH_SHORT).show()
//            }

            try{
                startActivity(emailIntent)
            }catch (exception:ActivityNotFoundException){
                Snackbar.make(activityAboutBinding.parentLiner,resources.getString(R.string.email_not_found),Snackbar.LENGTH_SHORT).show()
            }
        }

        activityAboutBinding.githubIssuesAbout.setOnClickListener {
            val githubIntent = Intent(Intent.ACTION_VIEW)
            githubIntent.data = Uri.parse("https://github.com/bluebonnet27/SunRain/issues")
            startActivity(githubIntent)
        }

        activityAboutBinding.marketAbout.setOnClickListener {
            val marketIntent = Intent(Intent.ACTION_VIEW)
            marketIntent.data = Uri.parse("https://www.coolapk.com/apk/com.ti.sunrain")
            startActivity(marketIntent)
        }

        activityAboutBinding.updateAbout.setOnClickListener {
            Toast.makeText(this, "密码是 c7t9", Toast.LENGTH_SHORT).show()

            val updateIntent = Intent(Intent.ACTION_VIEW)
            updateIntent.data = Uri.parse("https://wwa.lanzoux.com/b0dwdaush")
            startActivity(updateIntent)
        }

        activityAboutBinding.alipayAbout.setOnClickListener {
            openAliPay()
        }

        activityAboutBinding.wechatPayAbout.setOnClickListener {
            val wechatQrView = ImageView(this)
            wechatQrView.setImageResource(R.drawable.wechatqrcode)
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.cut_screen))
                .setView(wechatQrView)
                .show()
        }

        activityAboutBinding.adAbout.setOnClickListener {
            Snackbar.make(view,"目前还没接到广告...",Snackbar.LENGTH_SHORT).show()
        }

        activityAboutBinding.icons8About.setOnClickListener {
            val icons8Intent = Intent(Intent.ACTION_VIEW)
            icons8Intent.data = Uri.parse("https://icons8.com/")
            startActivity(icons8Intent)
        }

        activityAboutBinding.caiyunAbout.setOnClickListener {
            val caiyunIntent = Intent(Intent.ACTION_VIEW)
            caiyunIntent.data = Uri.parse("http://caiyunapp.com/")
            startActivity(caiyunIntent)
        }

        activityAboutBinding.baiduMapAbout.setOnClickListener {
            val baidumapIntent = Intent(Intent.ACTION_VIEW)
            baidumapIntent.data = Uri.parse("https://map.baidu.com")
            startActivity(baidumapIntent)
        }

        activityAboutBinding.sunnyweatherAbout.setOnClickListener {
            val sunnyweatherIntent = Intent(Intent.ACTION_VIEW)
            sunnyweatherIntent.data = Uri.parse("https://github.com/guolindev/SunnyWeather")
            startActivity(sunnyweatherIntent)
        }

        activityAboutBinding.permissionXAbout.setOnClickListener {
            val permissionXIntent = Intent(Intent.ACTION_VIEW)
            permissionXIntent.data = Uri.parse("https://github.com/guolindev/PermissionX")
            startActivity(permissionXIntent)
        }

        activityAboutBinding.retrofitAbout.setOnClickListener {
            val retrofitIntent = Intent(Intent.ACTION_VIEW)
            retrofitIntent.data = Uri.parse("https://github.com/square/retrofit")
            startActivity(retrofitIntent)
        }

        activityAboutBinding.mPAndroidChartAbout.setOnClickListener {
            val mpAndroidChartIntent = Intent(Intent.ACTION_VIEW)
            mpAndroidChartIntent.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart")
            startActivity(mpAndroidChartIntent)
        }

        activityAboutBinding.weatherAnimationAbout.setOnClickListener {
            val weatherAnimationIntent = Intent(Intent.ACTION_VIEW)
            weatherAnimationIntent.data = Uri.parse("https://github.com/Rainvvy/Weather_Bg")
            startActivity(weatherAnimationIntent)
        }

        activityAboutBinding.privacyAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("隐私政策")
                .setMessage("1. 当您使用晴雨时需要提供网络权限，用于将您输入的城市信息使用彩云天气的" +
                        "第三方接口搜索天气。晴雨会将上一次搜索的记录保存在本地。通过清除软件数据，可" +
                        "以将已保存的数据清除。晴雨不需要其他任何权限。\n\n"+"2. 当您选择使用手机定位功能" +
                        "获取天气时，晴雨需要将从手机获取的定位数据发送至百度地图获取反解析地址。定位并非" +
                        "唯一获取天气信息的方式，拒绝定位权限不会影响其他获取天气信息的功能\n\n"+
                        "3. 如果您对您的隐私有任何疑问或者需要解释的，请通过产品中的反馈方式与开发者" +
                        "取得联系。 如您不同意本协议或其中的任何条款的，您应停止使用晴雨。\n\n"+
                        "4. 最后更新版本 V${BuildConfig.VERSION_NAME}")
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton("更多"){ _, _ -> run {
                        val morePrivacyIntent = Intent(Intent.ACTION_VIEW)
                        morePrivacyIntent.data = Uri.parse("https://bluebonnet27.gitee.io/userAssignment.html")
                        startActivity(morePrivacyIntent)
                    }
                }.show()
        }

        activityAboutBinding.questionAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("注意事项")
                .setMessage("Q1：为什么有时定位查询不到天气？\nA1：晴雨使用原生接口查询天气，没有使" +
                        "用三方SDK。由于原生接口不完善，以及开发者本人能力所限制，可能出现定位不到天气的情" +
                        "况。")
                .setPositiveButton(getString(R.string.ok), null)
                .show()
        }

        activityAboutBinding.freedomAbout.setOnClickListener {
            val freedomIntent = Intent(Intent.ACTION_VIEW)
            freedomIntent.data = Uri.parse("https://www.gnu.org/philosophy/free-software-even-more-important.html")
            startActivity(freedomIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        //set
        ActivitySet.removeActivity(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.about_menu,menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        //更换黑暗模式的图标
        if(isDarkTheme(this)){
            val coolapkOption = menu?.getItem(1)
            coolapkOption?.icon = ContextCompat
                .getDrawable(this,R.drawable.baseline_person_search_white_24dp)
            val blogOption = menu?.getItem(2)
            blogOption?.icon = ContextCompat
                .getDrawable(this,R.drawable.baseline_computer_white_24dp)
            val messageOption = menu?.getItem(3)
            messageOption?.icon = ContextCompat
                .getDrawable(this,R.drawable.baseline_message_white_24dp)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()

            R.id.mainPageAbout -> {
                val mainPageIntent = Intent(Intent.ACTION_VIEW)
                mainPageIntent.data = Uri.parse("https://bluebonnet27.gitee.io")
                startActivity(mainPageIntent)
            }

            R.id.coolapkContact -> {
                val coolapkIntent = Intent(Intent.ACTION_VIEW)
                coolapkIntent.data = Uri.parse("https://www.coolapk.com/u/1756645?from=qr")
                startActivity(coolapkIntent)
            }

            R.id.blogContact -> {
                val blogIntent = Intent(Intent.ACTION_VIEW)
                blogIntent.data = Uri.parse("https://bluebonnet27.github.io")
                startActivity(blogIntent)
            }

            R.id.moreSay -> {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(getString(R.string.things_to_say))
                    .setMessage(getString(R.string.finalTextLine1)+"\n\n"
                                +getString(R.string.finalTextLine2)+"\n\n"
                                +getString(R.string.finalTextLine3)+"\n\n"
                                +getString(R.string.finalTextLine4))
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        Toast.makeText(this, "谢谢你的阅读", Toast.LENGTH_SHORT)
                            .show()
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        return true
    }



    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if(SunRainApplication.settingsPreference.getBoolean("others_icon_menu",true)){
            if(menu.javaClass.simpleName.equals("MenuBuilder",false)){
                try {
                    val method = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible",
                        Boolean::class.java)
                    method.isAccessible = true
                    method.invoke(menu,true)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    /**
     * 触发彩蛋
     *
     * @param activity
     */
    private fun getRandomForEgg(activity: Activity){
        when((0..100).random()){
            0 -> {
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(getString(R.string.egg_title))
                    .setMessage(getString(R.string.sex_pic))
                    .setPositiveButton(getString(R.string.ok),null)
                    .show()
            }
            else -> Toast.makeText(activity, resources.getString(R.string.no_egg), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 打开支付宝
     *
     */
    private fun openAliPay() {
        val url = "intent://platformapi/startapp?saId=10000007&" +
                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Ffkx11810qxcirwaicgedwc6%3F_s" +
                "%3Dweb-other&_t=1472443966571#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
        if(hasInstalledAlipayClient()){
            try {
                Toast.makeText(SunRainApplication.context, "感谢您的捐赠！", Toast.LENGTH_SHORT).show()
                val intent = Intent.parseUri(url,Intent.URI_INTENT_SCHEME)
                //safe rules
                intent.addCategory("android.intent.category.BROWSABLE")
                intent.component = null
                intent.selector = null
                startActivity(intent)
            }catch (e:URISyntaxException){
                e.printStackTrace()
                Snackbar.make(activityAboutBinding.parentLiner,"啊哦，似乎出错了",Snackbar.LENGTH_SHORT).show()
            }
        }else{
            Snackbar.make(activityAboutBinding.parentLiner,"您似乎没有安装支付宝",Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * 是否安装了支付宝
     *
     * @return true 安装了支付宝
     */
    private fun hasInstalledAlipayClient():Boolean{
        val alipayPackageName = "com.eg.android.AlipayGphone"
        val packageManager = SunRainApplication.context.packageManager
        return try {
            val info = packageManager.getPackageInfo(alipayPackageName,0)
            info != null
        }catch (e:PackageManager.NameNotFoundException){
            e.printStackTrace()
            false
        }
    }

    /**
     * 检查是否是暗黑模式
     *
     * @param context
     * @return true 是暗黑模式
     */
    private fun isDarkTheme(context: Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }
}