package com.example.coffeeapp.managers

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val KEY_THEME = "app_theme"
    private const val PREF_NAME = "coffee_app_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NICKNAME = "user_nickname"
    private const val KEY_USER_BIRTHDAY_MILLIS = "user_birthday_millis"
    private const val DEFAULT_NAME = "Кофейный гурман"
    private const val KEY_USER_NAME = "user_name"
    private const val PREFS_NAME = "user_session"
    private const val KEY_USER_ID = "user_id"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginSession(context: Context, email: String) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserEmail(context: Context): String {
        return getPreferences(context).getString(KEY_USER_EMAIL, "unknown@coffee.com") ?: "unknown@coffee.com"
    }
    fun saveTheme(context: Context, themeName: String) {
        getPreferences(context).edit().putString(KEY_THEME, themeName).apply()
    }

    fun getAppTheme(context: Context): String {
        return getPreferences(context).getString(KEY_THEME, "coffee") ?: "coffee"
    }

    // 🔥 НОВЫЕ МЕТОДЫ ДЛЯ СОХРАНЕНИЯ ПРОФИЛЯ
    fun saveNickname(context: Context, nickname: String) {
        getPreferences(context).edit().putString(KEY_USER_NICKNAME, nickname).apply()
    }

    fun getNickname(context: Context): String {
        return getPreferences(context).getString(KEY_USER_NICKNAME, "Кофейный Гурман") ?: "Кофейный Гурман"
    }

    fun saveBirthdayMillis(context: Context, millis: Long) {
        getPreferences(context).edit().putLong(KEY_USER_BIRTHDAY_MILLIS, millis).apply()
    }

    fun getBirthdayMillis(context: Context): Long {
        return getPreferences(context).getLong(KEY_USER_BIRTHDAY_MILLIS, 0L)
    }

    fun clearSession(context: Context) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.putString(KEY_USER_EMAIL, null)
        editor.putString(KEY_USER_NICKNAME, null)
        editor.putLong(KEY_USER_BIRTHDAY_MILLIS, 0L)
        editor.putInt(KEY_USER_ID, -1) // ← Очищаем ID при выходе!
        editor.apply()
    }

    fun getUserName(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_NAME, DEFAULT_NAME) ?: DEFAULT_NAME
    }

    fun saveUserName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }
    fun saveUserId(context: Context, userId: Int) {
        getPreferences(context).edit().putInt(KEY_USER_ID, userId).apply()
    }
    fun getUserId(context: Context): Int {
        // Если ID почему-то не сохранился, вернем -1 как знак ошибки
        return getPreferences(context).getInt(KEY_USER_ID, -1)
    }

}