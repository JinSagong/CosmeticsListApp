package com.jin.hwahae

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*

class API {
    private val baseURL =
        "https://6uqljnm1pb.execute-api.ap-northeast-2.amazonaws.com/prod/products"
    private val contType = "application/json"

    private val view: RecyclerView?
    private var list: ArrayList<Any>
    private var gridAdapter: GridAdapter?
    private val activity: Activity?

    // For creating item list
    constructor(rView: RecyclerView) {
        view = rView
        list = arrayListOf()
        gridAdapter = GridAdapter(list)
        view.adapter = gridAdapter
        activity = null
    }

    // For getting item details
    constructor(a: Activity) {
        view = null
        list = arrayListOf()
        gridAdapter = null
        activity = a
    }

    private fun connect(resource: String) {
        val request =
            Request.Builder().url(baseURL + resource).header("Content-Type", contType).build()

        // Do asyncTask work
        Connect(request, list, gridAdapter, activity).execute()
    }

    fun getList(skinType: String, page: Int) {
        if (page == 1) {
            list.clear()
            gridAdapter?.notifyDataSetChanged()
        }
        connect("?skin_type=$skinType&page=$page")
    }

    fun getSearchResult(skinType: String, search: String, page: Int) {
        if (page == 1) {
            list.clear()
            gridAdapter?.notifyDataSetChanged()
        }
        connect("?skin_type=$skinType&search=$search&page=$page")
    }

    fun getDetails(id: Int) {
        connect("/$id")
    }

    data class Items(
        val id: Int,
        val thumbnail_image: String,
        val title: String,
        val price: String,
        val oily_score: Int,
        val dry_score: Int,
        val sensitive_score: Int
    )

    data class Details(
        val id: Int,
        val full_size_image: String,
        val title: String,
        val description: String,
        val price: String,
        val oily_score: Int,
        val dry_score: Int,
        val sensitive_score: Int
    )
}