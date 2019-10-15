package com.example.snapkit.thumbnailgallery

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapkit.R
import com.example.snapkit.databinding.ItemThumbnailViewBinding
import com.example.snapkit.domain.ImageFile

class ThumbnailGalleryAdapter(private val onClickListener: OnClickThumbnailListener) :
    ListAdapter<ImageFile, ThumbnailGalleryAdapter.ThumbnailViewHolder>(DiffImageFileCallBack) {

    // Check if the user has enabled long press to remove images.
    private var _longPressDeleteEnabled: Boolean = false
    val longPressDeleteEnabled: Boolean
        get() = _longPressDeleteEnabled
    // Store the selected items into a list.
    var selectedItems = mutableSetOf<Pair<ImageFile, Int>>()

    class ThumbnailViewHolder(var thumbnail: ItemThumbnailViewBinding) : RecyclerView.ViewHolder(thumbnail.root) {
        // TODO: Provide image URI info, this is for later use.
        fun bind() {
            thumbnail.executePendingBindings()
        }
    }

    companion object DiffImageFileCallBack : DiffUtil.ItemCallback<ImageFile>() {

        override fun areItemsTheSame(oldItem: ImageFile, newItem: ImageFile): Boolean {
            return oldItem.filePath == newItem.filePath
        }

        override fun areContentsTheSame(oldItem: ImageFile, newItem: ImageFile): Boolean {

            return oldItem.filePath == newItem.filePath
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ThumbnailViewHolder(ItemThumbnailViewBinding.inflate(layoutInflater))
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val imageView = holder.thumbnail.thumbnailView
        val imageCard = holder.thumbnail.imageCard
        val imageFile = getItem(position)
        val filePath = getItem(position).filePath
        // Call the OnClickThumbnailListener in the onClick method of the ImageView.
        imageView.apply {
            // Set transition name for the shared element system.
            ViewCompat.setTransitionName(imageView, filePath)

            setOnClickListener {
                if (_longPressDeleteEnabled) {
                    toggleHighlightSelection(imageCard, imageFile, holder.adapterPosition)
                } else {
                    onClickListener.onClick(holder.adapterPosition, imageFile, imageView)
                }
            }
            setOnLongClickListener {
                addSelectionHighlight(imageCard)
                enableLongPressDeletion()
                // Add the data pertaining to the view to the selected items.
                selectedItems.add(Pair(imageFile, holder.adapterPosition))
                onClickListener.onLongClick(holder.adapterPosition, imageFile, imageView)
                true
            }
        }
        // Check to see if the imageFrame SHOULD be highlighted after the ViewHolder has been re-used.
        when (selectedItems.contains(Pair(imageFile, holder.adapterPosition))) {
            true -> addSelectionHighlight(imageCard)
            else -> removeSelectionHighlight(imageCard)
        }
        // Remove any previous data.
        Glide.with(imageView.context)
            .clear(imageView)
        // Load image into glide.
        Glide.with(imageView.context)
            .load(filePath)
            .centerCrop()
            .into(imageView)
    }

    /**
     * Toggles highlight selection on the FrameLayout.
     *
     * @param imageFrame the FrameLayout of the ImageView
     * @param imageData the image data.
     * @param position the position of the imageFrame in the recycler view
     */
    private fun toggleHighlightSelection(imageFrame: CardView, imageData: ImageFile, position: Int) {
        // If delete selection is enabled then highlight the imageView.
        if (_longPressDeleteEnabled) {
            val itemData = Pair(imageData, position)
            if (imageFrame.foreground == null) {
                addSelectionHighlight(imageFrame)
                selectedItems.add(itemData)
            } else {
                removeSelectionHighlight(imageFrame)
                selectedItems.remove(itemData)
                // Disable the long press selection once the number of items selected is zero.
                if (selectedItems.size == 0) {
                    disableLongPressDeletion()
                    onClickListener.onSelectedItemsEmpty()
                }
            }
        } else {
            removeSelectionHighlight(imageFrame)
        }
    }

    /**
     * Displays a white transparent highlight over the View.
     * NOTE: The View must be a FrameLayout.
     *
     * @param view the FrameLayout to highlight.
     */
    private fun addSelectionHighlight(view: CardView) {
        view.foreground = ContextCompat.getDrawable(view.context, R.drawable.dark_transparent_tint)
        // Animate the scale of the selected view
        ValueAnimator.ofFloat(1f, 0.90f).apply {
            duration = 150
            addUpdateListener { animator ->
                val animatedScaleValue = animator.animatedValue as Float
                view.scaleX = animatedScaleValue
                view.scaleY = animatedScaleValue
            }
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    /**
     * Remove the white highlight from the ImageView.
     * NOTE: The View must be a FrameLayout.
     *
     * @param view the FrameLayout to strip the highlight from.
     */
    private fun removeSelectionHighlight(view: FrameLayout) {
        view.foreground = null
        ValueAnimator.ofFloat(.90f, 1f).apply {
            duration = 100
            addUpdateListener { animator ->
                val animatedScaleValue = animator.animatedValue as Float
                view.scaleX = animatedScaleValue
                view.scaleY = animatedScaleValue
            }
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    /**
     * Enable the long press selection mode.
     */
    private fun enableLongPressDeletion() {
        _longPressDeleteEnabled = true
    }

    /**
     * Disable the long press selection mode on the adapter and clear the selectedItems set.
     */
    fun disableLongPressDeletion() {
        _longPressDeleteEnabled = false
        // Keep a copy of the old list.
        val tempItems = selectedItems.toSet()
        selectedItems.clear()

        // Rebind the items in this list to refresh the views, in OnBindViewHolder.
        for (items in tempItems) {
            notifyItemChanged(items.second)
        }
    }
}

/**
 * An OnClickListener class for the ThumbnailGalleryAdapter class.
 */
interface OnClickThumbnailListener {

    /**
     * An onClick listener event for the ViewHolder.
     *
     * @param position the position of the view holder in the adapter.
     * @param imageFile the ImageFile object that's relative to the position in the adapter.
     * @param view the View that was clicked on.
     */
    fun onClick(position: Int, imageFile: ImageFile, view: View)

    /**
     * An onClick listener event for the ViewHolder.
     *
     * @param position the position of the view holder in the adapter.
     * @param imageFile the ImageFile object that's relative to the position in the adapter.
     * @param view the View that was long pressed.
     */
    fun onLongClick(position: Int, imageFile: ImageFile, view: View)

    /**
     * An event listener for when the selectedItems is empty.
     */
    fun onSelectedItemsEmpty()
}
