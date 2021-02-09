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
import kotlinx.android.synthetic.main.item_about_header.*
import java.net.URISyntaxException

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
            updateIcon.setImageResource(R.drawable.baseline_update_white_24dp)
            nowVersionIcon.setImageResource(R.drawable.baseline_info_white_24dp)

            wechatIcon.setImageResource(R.drawable.ic_wechat_white)
            alipayIcon.setImageResource(R.drawable.ic_alipay_white)
        }

        setSupportActionBar(aboutToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }
        aboutToolbar.overflowIcon = ContextCompat.getDrawable(this,R.drawable.baseline_more_vert_white_24dp)

        iconImageAbout.setOnClickListener {
            getRandomForEgg(this)
        }

        versionTitleAbout.text = "V" + BuildConfig.VERSION_NAME

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

        updateAbout.setOnClickListener {
            Toast.makeText(this, "密码是 c7t9", Toast.LENGTH_SHORT).show()

            val updateIntent = Intent(Intent.ACTION_VIEW)
            updateIntent.data = Uri.parse("https://wwa.lanzous.com/b0dwdaush")
            startActivity(updateIntent)
        }

        nowVersionAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(BuildConfig.VERSION_NAME)
                .setMessage("1. 更改进度条颜色为主题色\n2. 修改图表使之更好看")
                .show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()

            R.id.coolapkContact -> {
                val coolapkIntent = Intent(Intent.ACTION_VIEW)
                coolapkIntent.data = Uri.parse("https://www.coolapk.com/u/1756645?from=qr")
                startActivity(coolapkIntent)
            }
        }
        return true
    }

    private fun getRandomForEgg(activity: Activity){
        when((0..99).random()){
            0 -> AlertDialog.Builder(activity)
                .setMessage(resources.getString(R.string.sex_pic))
                .setPositiveButton(activity.getString(R.string.ok),null)
                .show()
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