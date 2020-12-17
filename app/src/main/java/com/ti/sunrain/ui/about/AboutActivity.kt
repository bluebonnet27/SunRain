package com.ti.sunrain.ui.about

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.ti.sunrain.BuildConfig
import com.ti.sunrain.R
import com.ti.sunrain.SunRainApplication
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.item_about_header.*
import java.net.URISyntaxException

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(aboutToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24dp)
        }

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
                Snackbar.make(parentLiner,"很抱歉，在您的手机上没有找到能发送电子邮件的应用",Snackbar.LENGTH_SHORT).show()
            }
        }

        githubIssuesAbout.setOnClickListener {
            val githubIntent = Intent(Intent.ACTION_VIEW)
            githubIntent.data = Uri.parse("https://github.com/bluebonnet27/SunRain/issues")
            startActivity(githubIntent)
        }

        alipayAbout.setOnClickListener {
            openAliPay(this)
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    private fun getRandomForEgg(activity: Activity){
        when((0..99).random()){
            0 -> AlertDialog.Builder(activity)
                .setMessage("我是一份涩图")
                .setPositiveButton(activity.getString(R.string.ok),null)
                .show()
            else -> Toast.makeText(activity, "别点了，没有彩蛋的", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAliPay(activity: Activity){
        val url = "intent://platformapi/startapp?saId=10000007&" +
                "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Ffkx11810qxcirwaicgedwc6%3F_s" +
                "%3Dweb-other&_t=1472443966571#Intent;" +
                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
        Toast.makeText(SunRainApplication.context, "感谢您的捐赠！", Toast.LENGTH_SHORT).show()
        if(hasInstalledAlipayClient()){
            try {
                val intent = Intent.parseUri(url,Intent.URI_INTENT_SCHEME)
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
        val ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone"
        val packageManager = SunRainApplication.context.packageManager
        return try {
            val info = packageManager.getPackageInfo(ALIPAY_PACKAGE_NAME,0)
            info != null
        }catch (e:PackageManager.NameNotFoundException){
            e.printStackTrace()
            false
        }
    }
}