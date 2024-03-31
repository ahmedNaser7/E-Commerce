package com.example.ecommerce.ui.common.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.ecommerce.R


class ProgressDialog {

   companion object {
       fun CraeteProgressDialog(
           context: Context,
       ): Dialog {
           val dialog = Dialog(context)
           val inflate = LayoutInflater.from(context).inflate(R.layout.progress_bar,null)
           dialog.setContentView(inflate)
           dialog.setCancelable(false)
           dialog.window?.setBackgroundDrawable(
               ColorDrawable(Color.WHITE)
           )
          return dialog
       }
   }
}