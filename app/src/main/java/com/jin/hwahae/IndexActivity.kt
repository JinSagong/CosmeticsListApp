package com.jin.hwahae

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.content_index.*
import org.jetbrains.anko.toast

class IndexActivity : AppCompatActivity() {
    private var toastBack: Toast? = null
    private var lastTimeBackPressed = 0L

    private var type = "oily"
    private var page = 1
    private val api by lazy {
        API(recyclerView)
    }
    private var query = ""
    private var isStart = false

    private val gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

    companion object {
        var loading = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)

        // Set searchView textListener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Hide keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                page = 1
                query = newText!!
                createGridView()
                return true
            }
        })

        // Set spinner itemListener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Boolean isStart can prevent to call these lines when this app is started
                if (isStart) {
                    val typeTo = when (parent?.getItemAtPosition(position).toString()) {
                        getString(R.string.type2) -> "dry"
                        getString(R.string.type3) -> "sensitive"
                        else -> "oily"
                    }
                    // If existed skin type is selected, re-load is not conducted
                    type = if (type != typeTo) typeTo else type
                    page = 1
                    createGridView()
                } else {
                    isStart = true
                }
            }
        }

        // Set recyclerView scrollListener
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //Check for scroll down
                if (dy > 0) {
                    if (loading) {
                        val currentItem = gridLayoutManager.findLastCompletelyVisibleItemPosition()
                        val totalItems = gridLayoutManager.itemCount
                        Log.d(
                            "ForChecking",
                            "[currentItem: $currentItem / totalItems: $totalItems]"
                        )
                        // Load next page when 15th item of 20 items appear on the screen
                        if (totalItems <= currentItem + 5) {
                            loading = false
                            page++
                            // Continuous scrolling
                            createGridView()
                        }
                    }
                }
            }
        })

        // Create initial view
        createGridView()
    }

    private fun createGridView() {
        Log.d("ForChecking", "[type: $type / query: $query / page: $page]")

        if (query == "") {
            api.getList(type, page)
        } else {
            api.getSearchResult(type, query, page)
        }
    }

    override fun onBackPressed() {
        if (gridLayoutManager.findFirstCompletelyVisibleItemPosition() <= 0) {
            toastBack?.cancel()
            toastBack = toast(getString(R.string.back_pressed))
            if (System.currentTimeMillis() - lastTimeBackPressed < 1500) {
                toastBack?.cancel()
                super.onBackPressed()
            }
            lastTimeBackPressed = System.currentTimeMillis()
        } else {
            // If back is pressed, scroll view up to top of screen
            recyclerView.smoothScrollToPosition(0)
        }
    }
}