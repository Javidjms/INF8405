package com.example.jms.touristapp.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.jms.touristapp.R;

/**
 * This class shows the About Dialog Box
 */
public class AboutDialog {


    public AboutDialog(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.about_title)
                .setMessage(String.format(activity.getString(R.string.about_message), activity.getString(R.string.project_author_1), activity.getString(R.string.project_author_2), activity.getString(R.string.project_author_3)))

        .setCancelable(true)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

}
