package com.ti.sunrain.ui.about

import android.app.Activity
import android.app.AlertDialog
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
import kotlinx.android.synthetic.main.activity_about.*
import java.net.URISyntaxException
import com.ti.sunrain.MainActivity

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue


class AboutActivity : AppCompatActivity() {

    lateinit var viewModel: AboutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        //set
        ActivitySet.addActivity(this)

        //darkmode
        when(SunRainApplication.settingsPreference.getString("others_darkmode_list","0")){
            "0" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            "1" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            "2" -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }

        viewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)

        if(isDarkTheme(this)){
            emailIcon.setImageResource(R.drawable.baseline_email_white_24dp)
            githubIcon.setImageResource(R.drawable.ic_github_white)
            marketIcon.setImageResource(R.drawable.baseline_shop_white_24dp)
            updateIcon.setImageResource(R.drawable.baseline_find_in_page_white_24dp)

            wechatIcon.setImageResource(R.drawable.ic_wechat_white)
            alipayIcon.setImageResource(R.drawable.ic_alipay_white)
            adIcon.setImageResource(R.drawable.baseline_attach_money_white_24dp)

            privacyIcon.setImageResource(R.drawable.baseline_menu_book_white_24dp)
        }

        setSupportActionBar(aboutToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }
        aboutToolbar.overflowIcon = ContextCompat.getDrawable(this,R.drawable.baseline_more_vert_white_24dp)

        aboutCollapsingToolbarLayout.apply {
            if(isDarkTheme(this@AboutActivity)){
                setExpandedTitleColor(ContextCompat.getColor(context,R.color.indigo500))
            }else{
                setExpandedTitleColor(ContextCompat.getColor(context,R.color.orange500))
            }
            setCollapsedTitleTextColor(Color.WHITE)
        }
        //0315 考虑稳定性，这条暂时去掉
//        iconImageAbout.setOnClickListener {
//            getRandomForEgg(this)
//        }

        aboutFAB.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("关于")
                .setMessage("晴雨：一个开源的 Android 天气预报 APP\n\n"+
                            "V ${BuildConfig.VERSION_NAME}\n\n"+
                            "本APP/项目所使用的开源协议不涉及此处背景图片的二次授权。图片版权" +
                            "仍归原作者绘恋Galgame制作组所有。")
                .setPositiveButton(R.string.ok,null)
                .show()
        }

        //versionTitleAbout.text = "V" + BuildConfig.VERSION_NAME

        emailMeAbout.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:tihongsheng@foxmail.com")
                putExtra(Intent.EXTRA_SUBJECT,"SunRain反馈")
            }

            if(emailIntent.resolveActivity(packageManager)!=null){
                startActivity(emailIntent)
            }else{
                Snackbar.make(parentLiner,resources.getString(R.string.email_not_found),Snackbar.LENGTH_SHORT).show()
            }
        }

        githubIssuesAbout.setOnClickListener {
            val githubIntent = Intent(Intent.ACTION_VIEW)
            githubIntent.data = Uri.parse("https://github.com/bluebonnet27/SunRain/issues")
            startActivity(githubIntent)
        }

        marketAbout.setOnClickListener {
            val marketIntent = Intent(Intent.ACTION_VIEW)
            marketIntent.data = Uri.parse("https://www.coolapk.com/apk/com.ti.sunrain")
            startActivity(marketIntent)
        }

        updateAbout.setOnClickListener {
            Toast.makeText(this, "密码是 c7t9", Toast.LENGTH_SHORT).show()

            val updateIntent = Intent(Intent.ACTION_VIEW)
            updateIntent.data = Uri.parse("https://wwa.lanzous.com/b0dwdaush")
            startActivity(updateIntent)
        }

        alipayAbout.setOnClickListener {
            openAliPay(this)
        }

        wechatPayAbout.setOnClickListener {
            val wechatqrview = ImageView(this)
            wechatqrview.setImageResource(R.drawable.wechatqrcode)
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.cut_screen))
                .setView(wechatqrview)
                .show()
        }

        adAbout.setOnClickListener { view ->
            Snackbar.make(view,"目前还没接到广告...",Snackbar.LENGTH_SHORT).show()
        }

        icons8About.setOnClickListener {
            val icons8Intent = Intent(Intent.ACTION_VIEW)
            icons8Intent.data = Uri.parse("https://icons8.com/")
            startActivity(icons8Intent)
        }

        caiyunAbout.setOnClickListener {
            val caiyunIntent = Intent(Intent.ACTION_VIEW)
            caiyunIntent.data = Uri.parse("http://caiyunapp.com/")
            startActivity(caiyunIntent)
        }

        sunnyweatherAbout.setOnClickListener {
            val sunnyweatherIntent = Intent(Intent.ACTION_VIEW)
            sunnyweatherIntent.data = Uri.parse("https://github.com/guolindev/SunnyWeather")
            startActivity(sunnyweatherIntent)
        }

        retrofitAbout.setOnClickListener {
            val retrofitIntent = Intent(Intent.ACTION_VIEW)
            retrofitIntent.data = Uri.parse("https://github.com/square/retrofit")
            startActivity(retrofitIntent)
        }

        mPAndroidChartAbout.setOnClickListener {
            val mpAndroidChartIntent = Intent(Intent.ACTION_VIEW)
            mpAndroidChartIntent.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart")
            startActivity(mpAndroidChartIntent)
        }

        weatherAnimationAbout.setOnClickListener {
            val weatherAnimationIntent = Intent(Intent.ACTION_VIEW)
            weatherAnimationIntent.data = Uri.parse("https://github.com/Rainvvy/Weather_Bg")
            startActivity(weatherAnimationIntent)
        }

//        brvahAbout.setOnClickListener {
//            val brvahIntent = Intent(Intent.ACTION_VIEW)
//            brvahIntent.data = Uri.parse("https://github.com/PhilJay/MPAndroidChart")
//            startActivity(brvahIntent)
//        }

        privacyAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("隐私政策")
                .setMessage("1. 当您使用晴雨时需要提供网络权限，用于将您输入的城市信息使用彩云天气的" +
                        "第三方接口搜索天气。晴雨会将上一次搜索的记录保存在本地。通过清除软件数据，可" +
                        "以将已保存的数据清除。晴雨不需要其他任何权限。\n"+
                        "2. 如果您对您的隐私有任何疑问或者需要解释的，请通过产品中的反馈方式与开发者" +
                        "取得联系。 如您不同意本协议或其中的任何条款的，您应停止使用晴雨。")
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton("更多"){ _, _ -> run {
                        val morePrivacyIntent = Intent(Intent.ACTION_VIEW)
                        morePrivacyIntent.data = Uri.parse("https://bluebonnet27.gitee.io/userAssignment.html")
                        startActivity(morePrivacyIntent)
                    }
                }.show()
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
        //dark theme
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
                blogIntent.data = Uri.parse("https://blog.bluebonnet27.xyz")
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

    private fun getRandomForEgg(activity: Activity){
        when((0..2).random()){
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

    private fun openAliPay(activity: Activity){
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
                Snackbar.make(parentLiner,"啊哦，似乎出错了",Snackbar.LENGTH_SHORT).show()
            }
        }else{
            Snackbar.make(parentLiner,"您似乎没有安装支付宝",Snackbar.LENGTH_SHORT).show()
        }
    }

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

    private fun isDarkTheme(context: Context):Boolean{
        val flag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }
}