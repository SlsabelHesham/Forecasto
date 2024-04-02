package com.example.weatherforecast.favouritePlaces.view

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.favouritePlaces.viewModel.FavouritePlacesViewModel

@Suppress("DEPRECATION")
class SwipeToDeleteCallback(private val adapter: FavouritePlacesAdapter, private val favouritePlacesViewModel: FavouritePlacesViewModel) : ItemTouchHelper.SimpleCallback(
    0, /*ItemTouchHelper.LEFT or */ItemTouchHelper.RIGHT
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val currentPlace = adapter.currentList[position]

        AlertDialog.Builder(adapter.context)
            .setTitle(R.string.deleteLocation)
            .setMessage(R.string.deleteLocationMSG)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                favouritePlacesViewModel.deletePlace(currentPlace)
            }
            .setNegativeButton(android.R.string.no) { _, _ ->
                adapter.notifyItemChanged(position)
            }
            .setIcon(R.drawable.image1)
            .show()
    }
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val background = ColorDrawable(ContextCompat.getColor(adapter.context, R.color.main_blue))

        if (dX > 0) {
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
        } else {
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }

        background.draw(c)

        val icon = ContextCompat.getDrawable(adapter.context, R.drawable.delete)
        val iconSize = (itemView.height * 0.4).toInt()
        val resizedIcon = Bitmap.createScaledBitmap(
            (icon as BitmapDrawable).bitmap,
            iconSize,
            iconSize,
            false
        )
        val drawable = BitmapDrawable(adapter.context.resources, resizedIcon)
        val iconMargin = (itemView.height - iconSize) / 1.5
        val iconTop = itemView.top + (itemView.height - iconSize) / 1.5
        val iconBottom = iconTop + iconSize

        if (dX > 0) {
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + iconMargin + iconSize
            drawable.setBounds(iconLeft.toInt(), iconTop.toInt(), iconRight.toInt(), iconBottom.toInt())
        } else {
            val iconLeft = itemView.right - iconMargin - iconSize
            val iconRight = itemView.right - iconMargin
            drawable.setBounds(iconLeft.toInt(), iconTop.toInt(), iconRight.toInt(), iconBottom.toInt())
        }
        drawable.draw(c)
    }
}