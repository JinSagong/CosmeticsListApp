package com.jin.hwahae

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.*
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.NumberFormat

class Connect(
    conn_request: Request,
    conn_list: ArrayList<Any>,
    conn_gridAdapter: GridAdapter?,
    conn_activity: Activity?
) : AsyncTask<Void, Void, Void?>() {
    private val request = conn_request
    private val list = conn_list
    private val gridAdapter = conn_gridAdapter
    private val activityRef = conn_activity?.let { WeakReference(it as DetailActivity) }

    override fun doInBackground(vararg params: Void?): Void? {
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Networking", "Failure [$request]")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string().toString()
                val gson = GsonBuilder().create()
                val jsonObject =
                    JsonParser().parse(body).asJsonObject

                with(jsonObject) {
                    if (get("statusCode").asInt != 200) {
                        // Issues to be addressed
                        // 1. No search result - code: 404
                        // 2. No page to load anymore - code: 404
                        // 3. Problems with server - code: 500
                        publishProgress()
                        Log.d("RequestResult", "${get("body")}")
                    } else {
                        if (gridAdapter != null) {
                            // Get item list
                            get("body").asJsonArray.forEach {
                                val v = gson.fromJson(
                                    it,
                                    API.Items::class.java
                                )
                                list.add(v)
                            }
                        } else {
                            // Get item details
                            val v = gson.fromJson(
                                get("body"),
                                API.Details::class.java
                            )
                            list.add(v)
                        }
                        publishProgress()
                    }
                }
            }
        })

        return null
    }

    override fun onProgressUpdate(vararg values: Void?) {
        // Getting item list if adapter is not null ,otherwise getting item details
        gridAdapter?.notifyDataSetChanged() ?: showDetails()
        IndexActivity.loading = true
    }

    private fun showDetails() {
        val detailActivity = activityRef?.get()
        if (detailActivity != null) {
            detailActivity.titleDetailTextView?.text = (list[0] as API.Details).title
            detailActivity.priceDetailTextView?.text =
                (NumberFormat.getNumberInstance().format((list[0] as API.Details).price.toInt())
                        + detailActivity.getString(R.string.won))
            detailActivity.descriptionDetailTextView?.text =
                (list[0] as API.Details).description.replace("\\n", "\n")
            Glide.with(detailActivity).load((list[0] as API.Details).full_size_image)
                .into(detailActivity.itemDetailImageView)
        }
    }
}