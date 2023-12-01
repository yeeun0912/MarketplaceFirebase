package com.example.marketplacefirebase

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 만약 사용자가 로그인되어 있다면 로그아웃 처리
        FirebaseAuth.getInstance().signOut()
    }
}