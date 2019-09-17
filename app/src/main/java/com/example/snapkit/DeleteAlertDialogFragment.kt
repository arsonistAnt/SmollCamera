package com.example.snapkit

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

/**
 * A custom dialog which purpose is to re-confirm to the user an image wants to be deleted.
 *
 * *Note: As of now this dialog fragment can only be hosted by other Fragments that implements DeleteAlertDialogListener.
 *        Will add ability to host from activity if the case requires it.
 */
class DeleteAlertDialogFragment : DialogFragment() {
    internal lateinit var listener: DeleteAlertDialogListener
    lateinit var alertDialog: AlertDialog

    // Listener for the host that's hosting this dialog.
    interface DeleteAlertDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as DeleteAlertDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(("$context must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.delete_alert_dialog_layout, null))
                .setNegativeButton(
                    R.string.delete_alert_negative
                ) { _, _ ->
                    // Pass click event to host.
                    listener.onDialogNegativeClick(this)
                }
                .setPositiveButton(
                    R.string.delete_alert_positive
                ) { _, _ ->
                    // Pass click event to host.
                    listener.onDialogPositiveClick(this)
                }
            alertDialog = builder.create()
            alertDialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mDialog = dialog as AlertDialog
        // Modify alert dialog layout
        mDialog.apply {
            window?.setBackgroundDrawableResource(R.drawable.delete_alert_dialog_drawable)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        modifyButtonParams()
    }

    /**
     * Change the layout and spacing of the negative and positive buttons.
     */
    private fun modifyButtonParams() {
        val mDialog = dialog as AlertDialog
        mDialog.apply {
            // Retrieve buttons and layout parameters
            val posButton = getButton(AlertDialog.BUTTON_POSITIVE)
            val negButton = getButton(AlertDialog.BUTTON_NEGATIVE)
            val layoutParams = posButton.layoutParams as LinearLayout.LayoutParams
            val notQuiteBlackColor = ContextCompat.getColor(requireContext(), R.color.not_quite_black)
            val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)
            // Layout adjustments
            layoutParams.weight = 20.0f
            posButton.layoutParams = layoutParams
            posButton.setBackgroundColor(notQuiteBlackColor)
            posButton.setTextColor(whiteColor)
            negButton.layoutParams = layoutParams
            negButton.setBackgroundColor(notQuiteBlackColor)
            negButton.setTextColor(whiteColor)
            // Change the window size of the alert dialog fragment
            val width = resources.displayMetrics.widthPixels * .80
            window?.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}