package com.example.snapkit.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.snapkit.R
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


/**
 * Returns a AlertDialog object with the PositiveButton onClick() already preset to the openSettings() function.
 *
 * @param context the context
 * @return the AlertDialog object.
 */
fun getAlertDialog(context: Context): AlertDialog = AlertDialog.Builder(context)
    .setCancelable(false) // Prevent back button from dismissing the dialog.
    .setPositiveButton(R.string.permissions_dialog_positive_button) { _, _ ->
        openSettings(context as Activity)
    }
    .create()

/**
 * Check if the app has access to the proper permissions.
 *
 * @param context the context
 * @param perms a vararg of permissions to be checked
 * @return true or false if all permissions are granted
 */
fun hasPermissions(context: Context, vararg perms: String): Boolean = perms.all {
    ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
}

/**
 * Explicit intent into the app settings.
 *
 * @param activity the activity
 */
fun openSettings(activity: Activity) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.parse("package:${activity.packageName}")
    )
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    activity.startActivity(intent)
}

/**
 * Use the Dexter library to request multiple permissions. If the user denies all permissions then the application itself
 * will go into the background. If the user permanently denies a permission then a AlertDialog will be shown.
 *
 * @param activity the activity
 * @param permDeniedDialog the AlertDialog that will be shown when the user has permanently denied a permission
 * @param perms the list of permissions to request from the user
 */
fun requestForPermissions(activity: Activity, permDeniedDialog: AlertDialog, vararg perms: String) {
    Dexter.withActivity(activity)
        .withPermissions(*perms)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    when {
                        it.isAnyPermissionPermanentlyDenied -> permDeniedDialog.show()
                        it.deniedPermissionResponses.size > 0 -> openHome(activity)
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                token?.continuePermissionRequest()
            }
        })
        .withErrorListener { error -> Log.e("Dexter", "There was an error: $error") }
        .check()
}

/**
 * Explicit intent to the home menu on the mobile phone.
 *
 * @param activity the activity
 */
fun openHome(activity: Activity) {
    val startMain = Intent(Intent.ACTION_MAIN)
    startMain.addCategory(Intent.CATEGORY_HOME)
    startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    activity.startActivity(startMain)
}

