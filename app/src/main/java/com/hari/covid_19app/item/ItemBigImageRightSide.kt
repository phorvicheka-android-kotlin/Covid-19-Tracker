package com.hari.covid_19app.item

import androidx.annotation.StringRes
import com.hari.covid_19app.R
import com.hari.covid_19app.databinding.ItemBigImageRightSideBinding
import com.hari.covid_19app.databinding.ItemHeaderBinding
import com.xwray.groupie.databinding.BindableItem

class ItemBigImageRightSide() : BindableItem<ItemBigImageRightSideBinding>() {
    override fun getLayout() = R.layout.item_big_image_right_side

    override fun bind(viewBinding: ItemBigImageRightSideBinding, position: Int) {
        with(viewBinding) {
        }
    }
}