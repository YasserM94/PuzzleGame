package com.bau.puzzlegame.helper

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


class CustomAlertDailog {
    var instance: CustomAlertDailog

    init {
        instance = this
    }

    //custom dialog with two buttons
    fun showElegantDialogWithTwoButtons(
        context: Activity,
        title: String,
        message: String,
        OkListener: DialogInterface.OnClickListener
        ,
        CancelListener: DialogInterface.OnClickListener
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            OkListener
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            CancelListener
        }


        builder.show()
    }

    //custom dialog with one buttons
    fun showElegantDialogWithOneButton(
        context: Activity,
        title: String,
        message: String,
        OkListener: DialogInterface.OnClickListener
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setNeutralButton(android.R.string.ok) { _, _ ->
            OkListener
        }
        builder.show()
    }

    //custom dialog with one button without listener
    fun showElegantDialogWithOneButtonWithoutListner(
        context: Activity,
        title: String,
        message: String
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setNeutralButton(android.R.string.ok) { dialog, which ->

        }
        builder.show()
    }
}