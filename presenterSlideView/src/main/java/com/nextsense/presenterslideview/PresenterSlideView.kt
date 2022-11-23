package com.nextsense.presenterslideview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.concurrent.schedule

@SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
class PresenterSlideView(private val parent: Context, private val attrs: AttributeSet?, private val defStyle: Int): FrameLayout(parent, attrs) {
    companion object {
        private const val MIN_AUTO_SCROLL_DELAY = 1000
        private const val DEFAULT_VISIBILITY = VISIBLE
    }

    private lateinit var recyclerView: RecyclerView
    private var adapter: Adapter<*>? = null

    private var dotRecycler: RecyclerView? = null
    private var dotAdapter: DotList? = null

    private var scrollTimer: Timer? = null

    private var orientation = 0
    private var loop = true
    private var autoScroll = true
    private var animationDelay = 3000
    private var dotVisibility = true
    private var dotSize = dpToPx(12f).toInt()
    private var dotMargin = dpToPx(3f).toInt()
    private var dotSelectedDrawable = defaultDot(Color.BLUE)
    private var dotUnSelectedDrawable = defaultDot(Color.GRAY)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    init {
        setupAttributes()
        setupDotIndicator()
        setupRecycler()
        setupAutoScroll(0)
    }

    private fun setupAttributes() {
        val attrSet = parent.obtainStyledAttributes(attrs, R.styleable.PresenterSlideView, defStyle, 0)
        orientation = attrSet.getInt(R.styleable.PresenterSlideView_orientation, orientation)
        loop = attrSet.getBoolean(R.styleable.PresenterSlideView_loopItems, loop)
        autoScroll = attrSet.getBoolean(R.styleable.PresenterSlideView_autoScroll, autoScroll)
        animationDelay = attrSet.getInt(R.styleable.PresenterSlideView_autoScrollDelay, animationDelay)
        dotVisibility = attrSet.getInt(R.styleable.PresenterSlideView_dotVisibility, DEFAULT_VISIBILITY) == VISIBLE
        dotSize = attrSet.getDimensionPixelSize(R.styleable.PresenterSlideView_dotSize, dotSize)
        dotMargin = attrSet.getDimensionPixelSize(R.styleable.PresenterSlideView_dotMargin, dotMargin)
        dotSelectedDrawable = attrSet.getDrawable(R.styleable.PresenterSlideView_dotSelected) ?: dotSelectedDrawable
        dotUnSelectedDrawable = attrSet.getDrawable(R.styleable.PresenterSlideView_dotUnselected) ?: dotUnSelectedDrawable
        attrSet.recycle()
    }

    private fun setupDotIndicator() {
        if(dotVisibility) {
            dotRecycler = RecyclerView(parent)
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            params.bottomMargin = dpToPx(16f).toInt()
            params.gravity = Gravity.CENTER or Gravity.BOTTOM
            dotRecycler?.layoutParams = params
            dotAdapter = DotList(dotSize, dotMargin, dotSelectedDrawable, dotUnSelectedDrawable) { deltaMove ->
                if(orientation == RecyclerView.HORIZONTAL) {
                    recyclerView.smoothScrollBy(deltaMove * width, 0)
                } else {
                    recyclerView.smoothScrollBy(0, deltaMove * height)
                }
                setupAutoScroll(1000)
            }

            val layoutManager = LinearLayoutManager(parent, RecyclerView.HORIZONTAL, false)
            dotRecycler?.layoutManager = layoutManager
            dotRecycler?.adapter = dotAdapter
            dotRecycler?.elevation = dpToPx(16f)
            addView(dotRecycler)
        }
    }

    private fun setupRecycler() {
        recyclerView = RecyclerView(parent)
        recyclerView.layoutParams = RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        recyclerView.layoutManager = if(orientation == RecyclerView.HORIZONTAL) {
            LinearLayoutManager(parent, RecyclerView.HORIZONTAL, false)
        } else {
            LinearLayoutManager(parent, RecyclerView.VERTICAL, false)
        }

        recyclerView.setOnTouchListener { _, event ->
            if(event?.action == MotionEvent.ACTION_DOWN || event?.action == MotionEvent.ACTION_MOVE) {
                scrollTimer?.cancel()
                scrollTimer?.purge()
                scrollTimer = null
            } else if(event?.action == MotionEvent.ACTION_UP) {
                setupAutoScroll(0)
            }

            return@setOnTouchListener false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val index = if(orientation == RecyclerView.HORIZONTAL) {
                    val x = recyclerView.computeHorizontalScrollOffset() + (width / 2)
                    (x / width) % (adapter?.uniqueCount ?: 1)
                } else {
                    val y = recyclerView.computeVerticalScrollOffset() + (height / 2)
                    (y / height) % (adapter?.uniqueCount ?: 1)
                }

                dotAdapter?.setItems(adapter?.uniqueCount ?: 1, index)
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        val snapper = PagerSnapHelper()
        snapper.attachToRecyclerView(recyclerView)
        addView(recyclerView)
    }

    private fun setupAutoScroll(delayOffset: Int) {
        scrollTimer?.cancel()
        scrollTimer?.purge()
        scrollTimer = null
        if(autoScroll) {
            val delay = animationDelay.coerceAtLeast(MIN_AUTO_SCROLL_DELAY).toLong()
            scrollTimer = Timer()
            scrollTimer?.schedule(delay + delayOffset, delay) {
                runOnUi {
                    if(orientation == RecyclerView.HORIZONTAL) {
                        recyclerView.smoothScrollBy(width, 0)
                    } else {
                        recyclerView.smoothScrollBy(0, height)
                    }
                }
            }
        }
    }

    fun setAdapter(adapter: Adapter<*>?) {
        this.adapter = adapter
        this.adapter?.setLoop(loop)
        recyclerView.adapter = adapter
    }

    private fun runOnUi(call: () -> Unit) {
        val mainLooper = Looper.getMainLooper()
        Handler(mainLooper).post {
            call()
        }
    }

    private fun defaultDot(@ColorInt colorInt: Int): Drawable {
        val shape = ShapeDrawable(OvalShape())
        shape.paint.color = colorInt
        return shape
    }

    private fun dpToPx(dp: Float): Float {
        return dp * (parent.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private class DotHolder<T : ImageView>(val dot: T) : RecyclerView.ViewHolder(dot)
    private class DotList(private val viewSize: Int,
                          private val viewMargin: Int,
                          private val selectedDrawable: Drawable,
                          private val unselectedDrawable: Drawable,
                          private val onSelected: (Int) -> Unit): RecyclerView.Adapter<DotHolder<ImageView>>() {

        private var dots = 0
        private var selectedDot: Int? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DotHolder<ImageView> {
            val context = parent.context
            val view = ImageView(context)
            val params = LayoutParams(viewSize, viewSize)
            params.leftMargin = viewMargin
            params.rightMargin = viewMargin
            view.layoutParams = params
            return DotHolder(view)
        }

        override fun onBindViewHolder(holder: DotHolder<ImageView>, position: Int) {
            holder.dot.background = if (position == selectedDot) {
                selectedDrawable
            } else {
                unselectedDrawable
            }

            holder.dot.setOnClickListener {
                val result = selectedDot?.let { currentDot -> position - currentDot} ?: 1
                onSelected(result)
            }
        }

        override fun getItemCount(): Int {
            return dots
        }

        fun setItems(dots: Int, selected: Int) {
            this.dots = dots
            this.selectedDot = selected
            notifyDataSetChanged()
        }
    }

    abstract class Adapter<T : RecyclerView.ViewHolder>: RecyclerView.Adapter<T>() {
        lateinit var parent: ViewGroup
        lateinit var context: Context
        private var loop = true
        var uniqueCount = 0
        private var onItemsChanged: (() -> Unit)? = null

        final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
            this.parent = parent
            this.context = parent.context
            val inflater = LayoutInflater.from(context)
            return onCreateView(inflater, parent)
        }

        final override fun onBindViewHolder(holder: T, position: Int) {
            val fixedPosition = constraintPosition(position)
            onBindView(holder, fixedPosition)
        }

        final override fun getItemCount(): Int {
            val newCount = getCount()
            if(newCount != uniqueCount) {
                onItemsChanged?.invoke()
            }

            uniqueCount = newCount
            return if(uniqueCount > 0 && loop) Int.MAX_VALUE else uniqueCount
        }

        fun setLoop(loop: Boolean) {
            this.loop = loop
            notifyDataSetChanged()
        }

        private fun constraintPosition(position: Int): Int {
            return if(uniqueCount > 0 && loop) {
                position % uniqueCount
            } else {
                position
            }
        }

        abstract fun onCreateView(inflater: LayoutInflater, parent: ViewGroup) : T

        abstract fun onBindView(holder: T, position: Int)

        abstract fun getCount(): Int
    }
}
