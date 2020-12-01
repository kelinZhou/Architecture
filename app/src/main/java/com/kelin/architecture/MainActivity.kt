package com.kelin.architecture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kelin.architecture.core.proxy.API
import com.kelin.architecture.core.proxy.ProxyFactory
import com.kelin.architecture.util.ToastUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ProxyFactory.createProxy { API.AUTH.logout() }
            .onSuccess { ToastUtil.showShortToast("成功") }
            .onFailed { ToastUtil.showShortToast("失败") }
            .request()
    }
}