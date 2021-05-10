package com.example.neopop

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.TriangleEdgeTreatment

class FRButton : FrameLayout, OnTouchListener {
    private val button = AppCompatButton(context)
    var isShadowColorDefined = false

    //Custom values
    private var isShadowEnabled = true
    private var mButtonColor = 0
    private var mShadowColor = 0
    private var mShadowHeight = 0
    private var mCornerRadius = 0

    //Native values
    private var mPaddingLeft = 0
    private var mPaddingRight = 0
    private var mPaddingTop = 0
    private var mPaddingBottom = 0

    //Background drawable
    private var pressedDrawable: Drawable? = null
    private var unpressedDrawable: Drawable? = null

    constructor(context: Context?) : super(context!!) {
        init()
        button.setOnTouchListener(this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
        parseAttrs(context, attrs)
        button.setOnTouchListener(this)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
        parseAttrs(context, attrs)
        button.setOnTouchListener(this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        //Update background color
        refresh()
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                updateBackground(pressedDrawable)
                button.setPadding(
                    mPaddingLeft,
                    mPaddingTop + mShadowHeight,
                    mPaddingRight,
                    mPaddingBottom
                )
            }
            MotionEvent.ACTION_MOVE -> {
                val r = Rect()
                view.getLocalVisibleRect(r)
                if (!r.contains(motionEvent.x.toInt(), motionEvent.y.toInt() + 3 * mShadowHeight) &&
                    !r.contains(motionEvent.x.toInt(), motionEvent.y.toInt() - 3 * mShadowHeight)
                ) {
                    updateBackground(unpressedDrawable)
                    button.setPadding(
                        mPaddingLeft,
                        mPaddingTop + mShadowHeight,
                        mPaddingRight,
                        mPaddingBottom + mShadowHeight
                    )
                }
            }
            MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                updateBackground(unpressedDrawable)
                button.setPadding(
                    mPaddingLeft,
                    mPaddingTop + mShadowHeight,
                    mPaddingRight,
                    mPaddingBottom + mShadowHeight
                )
            }
        }
        return false
    }

    private fun init() {
        //Init default values
        isShadowEnabled = true
        val resources = resources ?: return
        mButtonColor = resources.getColor(R.color.fbutton_default_color)
        mShadowColor = resources.getColor(R.color.fbutton_default_shadow_color)
        mShadowHeight = resources.getDimensionPixelSize(R.dimen.fbutton_default_shadow_height)
        mCornerRadius = resources.getDimensionPixelSize(R.dimen.fbutton_default_conner_radius)
    }

    private fun parseAttrs(context: Context, attrs: AttributeSet?) {
        //Load from custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FButton) ?: return
        for (i in 0 until typedArray.indexCount) {
            val attr = typedArray.getIndex(i)
            if (attr == R.styleable.FButton_shadowEnabled) {
                isShadowEnabled = typedArray.getBoolean(attr, true) //Default is true
            } else if (attr == R.styleable.FButton_buttonColor) {
                mButtonColor = typedArray.getColor(attr, R.color.fbutton_default_color)
            } else if (attr == R.styleable.FButton_shadowColor) {
                mShadowColor = typedArray.getColor(attr, R.color.fbutton_default_shadow_color)
                isShadowColorDefined = true
            } else if (attr == R.styleable.FButton_shadowHeight) {
                mShadowHeight =
                    typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_shadow_height)
            } else if (attr == R.styleable.FButton_cornerRadius) {
                mCornerRadius =
                    typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_conner_radius)
            }
        }
        typedArray.recycle()

        //Get paddingLeft, paddingRight
        val attrsArray = intArrayOf(
            android.R.attr.paddingLeft,  // 0
            android.R.attr.paddingRight
        )
        val ta = context.obtainStyledAttributes(attrs, attrsArray) ?: return
        mPaddingLeft = ta.getDimensionPixelSize(0, 0)
        mPaddingRight = ta.getDimensionPixelSize(1, 0)
        ta.recycle()

        //Get paddingTop, paddingBottom
        val attrsArray2 = intArrayOf(
            android.R.attr.paddingTop,  // 0
            android.R.attr.paddingBottom
        )
        val ta1 = context.obtainStyledAttributes(attrs, attrsArray2) ?: return
        mPaddingTop = ta1.getDimensionPixelSize(0, 0)
        mPaddingBottom = ta1.getDimensionPixelSize(1,0)
        ta1.recycle()
    }

    fun refresh() {
//        button = new AppCompatButton(getContext());
        button.textSize = 14f
        button.text = "text"
        button.gravity = Gravity.CENTER
        button.setTextColor(Color.WHITE)
        //        button.setPadding(10,10,10,0);
        val lp2 = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.CENTER
        )
        button.layoutParams = lp2
        this.addView(button)
        val alpha = Color.alpha(mButtonColor)
        val hsv = FloatArray(3)
        Color.colorToHSV(mButtonColor, hsv)
        hsv[2] *= 0.8f // value component
        //if shadow color was not defined, generate shadow color = 80% brightness
        if (!isShadowColorDefined) {
            mShadowColor = Color.HSVToColor(alpha, hsv)
        }
        //Create pressed background and unpressed background drawables
        if (this.isEnabled) {
            if (isShadowEnabled) {
                pressedDrawable = createDrawable(mCornerRadius, mButtonColor, mShadowColor, true)
                unpressedDrawable = createDrawable(mCornerRadius, mButtonColor, mShadowColor, false)
            } else {
                mShadowHeight = 0
                pressedDrawable =
                    createDrawable(mCornerRadius, mShadowColor, Color.TRANSPARENT, true)
                unpressedDrawable =
                    createDrawable(mCornerRadius, mButtonColor, Color.TRANSPARENT, false)
            }
        } else {
            Color.colorToHSV(mButtonColor, hsv)
            hsv[1] *= 0.25f // saturation component
            mShadowColor = Color.HSVToColor(alpha, hsv)
            val disabledColor = mShadowColor
            // Disabled button does not have shadow
            pressedDrawable = createDrawable(mCornerRadius, disabledColor, Color.TRANSPARENT, true)
            unpressedDrawable =
                createDrawable(mCornerRadius, disabledColor, Color.TRANSPARENT, false)
        }
        updateBackground(unpressedDrawable)
        //Set padding
        setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom)
        this.background = createDrawableBg(
            mCornerRadius,
            context.resources.getColor(R.color.teal_200),
            mShadowColor,
            false
        )

//        drawTriangle(this.getBottom(),this.getBottom()+mShadowHeight,getBottom()-mShadowHeight, 0 );
    }

    private fun updateBackground(background: Drawable?) {
        if (background == null) return
        //Set button background
        if (Build.VERSION.SDK_INT >= 16) {
            button.background = background
        } else {
            button.setBackgroundDrawable(background)
        }
    }

    private fun createDrawable(
        radius: Int,
        topColor: Int,
        bottomColor: Int,
        isPressed: Boolean
    ): LayerDrawable {
        val outerRadius = floatArrayOf(
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat()
        )
        //        int rightColor = getContext().getResources().getColor(R.color.purple_200);
//        topColor = getContext().getResources().getColor(R.color.teal_200);
        //Top
        val topRoundRect = RoundRectShape(outerRadius, null, null)
        val topShapeDrawable = ShapeDrawable(topRoundRect)
        topShapeDrawable.paint.color = topColor
        //Bottom
        val roundRectShape = RoundRectShape(outerRadius, null, null)
        val bottomShapeDrawable = ShapeDrawable(roundRectShape)
        bottomShapeDrawable.paint.color = bottomColor
        val cornerSize = context.resources.getDimension(R.dimen.fbutton_default_padding_left)
        val shapeDrawable = MaterialShapeDrawable(
            ShapeAppearanceModel.builder()
                .setAllCornerSizes(cornerSize)
                .setTopRightCorner(CutCornerTreatment())
                .setBottomLeftCorner(CutCornerTreatment())
                .setTopLeftCorner(CutCornerTreatment())
                .setBottomRightCornerSize(0f)
                .build()
        )
        shapeDrawable.fillColor =
            ColorStateList.valueOf(context.getColor(R.color.fbutton_default_shadow_color))
        //        RoundRectShape rightRectShape = new RoundRectShape(outerRadius, null, null);
//        ShapeDrawable rightShapeDrawable = new ShapeDrawable(rightRectShape);
//        rightShapeDrawable.getPaint().setColor(rightColor);
        //Create array
        val drawArray = arrayOf<Drawable>(bottomShapeDrawable, topShapeDrawable)
        val layerDrawable = LayerDrawable(drawArray)

        //Set shadow height
        if (isShadowEnabled && isPressed) {
            //unpressed drawable
            layerDrawable.setLayerInset(0, 0, 0, 0, 0) /*index, left, top, right, bottom*/
            layerDrawable.setLayerInset(
                1,
                mShadowHeight,
                mShadowHeight,
                0,
                0
            ) /*index, left, top, right, bottom*/
        } else {
            //pressed drawable
            layerDrawable.setLayerInset(0, 0, 0, 0, 0) /*index, left, top, right, bottom*/
            layerDrawable.setLayerInset(
                1,
                0,
                0,
                0,
                mShadowHeight
            ) /*index, left, top, right, bottom*/
        }
        //        layerDrawable.addLayer(buttonLayers);
//        layerDrawable.setLayerInset(1, mShadowHeight, 0, 0,0 );  /*index, left, top, right, bottom*/
        return layerDrawable
    }

    private fun createDrawableBg(
        radius: Int,
        topColor: Int,
        bottomColor: Int,
        isPressed: Boolean
    ): LayerDrawable {
        val outerRadius = floatArrayOf(
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            radius.toFloat()
        )
        //        int rightColor = getContext().getResources().getColor(R.color.purple_200);
//        topColor = getContext().getResources().getColor(R.color.teal_200);
        //Top
        val topRoundRect = RoundRectShape(outerRadius, null, null)
        val topShapeDrawable = ShapeDrawable(topRoundRect)
        topShapeDrawable.paint.color = topColor
        //Bottom
        val roundRectShape = RoundRectShape(outerRadius, null, null)
        val bottomShapeDrawable = ShapeDrawable(roundRectShape)
        bottomShapeDrawable.paint.color = bottomColor
        //        RoundRectShape rightRectShape = new RoundRectShape(outerRadius, null, null);
//        ShapeDrawable rightShapeDrawable = new ShapeDrawable(rightRectShape);
//        rightShapeDrawable.getPaint().setColor(rightColor);
        val cornerSize = context.resources.getDimension(R.dimen.fbutton_default_padding_left)
        val shapeDrawable = MaterialShapeDrawable(
            ShapeAppearanceModel.builder()
                .setAllCornerSizes(cornerSize)
                .setTopRightCorner(CutCornerTreatment())
                .setBottomLeftCorner(CutCornerTreatment())
                .setTopLeftCorner(CutCornerTreatment())
                .setBottomRightCornerSize(0f)
                .build()
        )
        shapeDrawable.fillColor =
            ColorStateList.valueOf(context.getColor(R.color.fbutton_default_shadow_color))
        //        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);


        //Create array
//        Drawable buttonLayers = getContext().getResources().getDrawable(R.drawable.bottom);
//        Drawable buttonLayers1 = getContext().getResources().getDrawable(R.drawable.op);
//        Drawable[] drawArray = {buttonLayers,buttonLayers1,bottomShapeDrawable, topShapeDrawable};
        val drawArray = arrayOf(shapeDrawable, topShapeDrawable)
        val layerDrawable = LayerDrawable(drawArray)

        //Set shadow height
        if (isShadowEnabled && isPressed) {
            //unpressed drawable
//            layerDrawable.setLayerInset(1, 0, 0, 0, 0);  /*index, left, top, right, bottom*/
            layerDrawable.setLayerInset(
                1,
                0,
                mShadowHeight,
                mShadowHeight,
                0
            ) /*index, left, top, right, bottom*/
        } else {
            //pressed drawable
            layerDrawable.setLayerInset(
                1,
                0,
                0,
                mShadowHeight,
                mShadowHeight
            ) /*index, left, top, right, bottom*/
            //            layerDrawable.setLayerInset(3, 0, 0, mShadowHeight, mShadowHeight);  /*index, left, top, right, bottom*/
        }
        //        layerDrawable.setLayerInset(1, mShadowHeight, 0, 0,0 );  /*index, left, top, right, bottom*/
        layerDrawable.draw(Canvas())
        return layerDrawable
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        refresh()
    }

    //Getter
    fun isShadowEnabled(): Boolean {
        return isShadowEnabled
    }

    //Setter
    fun setShadowEnabled(isShadowEnabled: Boolean) {
        this.isShadowEnabled = isShadowEnabled
        shadowHeight = 0
        refresh()
    }

    var buttonColor: Int
        get() = mButtonColor
        set(buttonColor) {
            mButtonColor = buttonColor
            refresh()
        }
    var shadowColor: Int
        get() = mShadowColor
        set(shadowColor) {
            mShadowColor = shadowColor
            isShadowColorDefined = true
            refresh()
        }
    var shadowHeight: Int
        get() = mShadowHeight
        set(shadowHeight) {
            mShadowHeight = shadowHeight
            refresh()
        }
    var cornerRadius: Int
        get() = mCornerRadius
        set(cornerRadius) {
            mCornerRadius = cornerRadius
            refresh()
        }
}