package com.jin.hwahae

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import org.jetbrains.anko.toast

class DetailActivity : AppCompatActivity() {
    private var isOpen = false
    private val aniStart by lazy {
        AnimationUtils.loadAnimation(
            applicationContext, R.anim.enter_up_slide
        )
    }
    private val aniEnd by lazy {
        AnimationUtils.loadAnimation(applicationContext, R.anim.exit_down_slide)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Delete animation of activity frame and do animation of child views
        overridePendingTransition(0, 0)

        setAnimations()
        itemDetailScrollView.startAnimation(aniStart)
        isOpen = true

        val id = intent.getIntExtra("id", 0)
        val api = API(this)
        api.getDetails(id)

        // When press cancelButton
        cancelButton.setOnClickListener {
            closeActivity()
        }

        // When press purchaseButton
        purchaseButton.setOnClickListener {
            closeActivity()
            toast(getString(R.string.purchase))
        }
    }

    private fun closeActivity() {
        isOpen = false
        purchaseButton.isClickable = false
        cancelButton.isClickable = false
        itemDetailScrollView.startAnimation(
            aniEnd
        )
    }

    override fun onBackPressed() {
        if (isOpen) {
            closeActivity()
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun setAnimations() {
        // When open the activity
        val aniUpButton = AnimationUtils.loadAnimation(
            applicationContext, R.anim.button_up_slide
        )

        aniStart.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                purchaseButton.visibility = View.VISIBLE
                purchaseButton.startAnimation(aniUpButton)
            }

            override fun onAnimationStart(animation: Animation?) {}
        })

        aniUpButton.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                cancelButton.isClickable = true
                purchaseButton.isClickable = true
            }

            override fun onAnimationStart(animation: Animation?) {}
        })

        // When close the activity
        val aniDownButton = AnimationUtils.loadAnimation(
            applicationContext, R.anim.button_down_slide
        )

        aniEnd.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                purchaseButton.startAnimation(aniDownButton)
            }

            override fun onAnimationStart(animation: Animation?) {}
        })

        aniDownButton.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                purchaseButton.visibility = View.INVISIBLE
                finish()
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
    }

}
