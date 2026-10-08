package com.yuanbao.timetable

import android.content.Context
import android.webkit.JavascriptInterface
import org.json.JSONObject

/**
 * 原生能力桥 —— 为「将来」的 GPS 动态提醒与路线导航预留
 *
 * 当前版本：所有方法返回占位结果，不申请任何权限、不触发任何弹窗，
 *          对现有课表功能零影响。
 *
 * 将来接入 GPS 时，只需在本类补充实现，并在 MainActivity 中
 * 增加权限申请逻辑；网页侧调用方式保持不变：
 *
 *   AppBridge.getLocation()        ->  JSON {lat, lon, acc, ts} 或 {error:...}
 *   AppBridge.scheduleReminder(...) ->  JSON {ok:true}
 *   AppBridge.navigateTo(building) ->  唤起导航
 *
 * 这样网页端（本周课表.html）无需改动即可获得原生能力。
 */
class AppBridge(private val context: Context) {

    /** 预留：获取当前位置。当前未实现，返回 not-implemented */
    @JavascriptInterface
    fun getLocation(): String {
        return JSONObject().apply {
            put("ok", false)
            put("error", "not-implemented")
        }.toString()
    }

    /** 预留：注册一次课前提醒（1h / 30min / 10min 三档距离阈值） */
    @JavascriptInterface
    fun scheduleReminder(payload: String): String {
        return JSONObject().apply {
            put("ok", false)
            put("error", "not-implemented")
        }.toString()
    }

    /** 预留：取消提醒 */
    @JavascriptInterface
    fun cancelReminder(id: String): String {
        return JSONObject().apply {
            put("ok", false)
            put("error", "not-implemented")
        }.toString()
    }

    /** 预留：路线导航（自建路网，不依赖高德/百度） */
    @JavascriptInterface
    fun navigateTo(building: String): String {
        return JSONObject().apply {
            put("ok", false)
            put("error", "not-implemented")
        }.toString()
    }

    /** 教学楼经纬度表：将来由你实测后填入，供距离判断与路线长度计算使用
     *  格式示例："文贤楼" -> doubleArrayOf(lat, lon)
     *  入口点与路线折线可另行扩展 */
    @JavascriptInterface
    fun getBuildingCoords(): String = "{}"
}
