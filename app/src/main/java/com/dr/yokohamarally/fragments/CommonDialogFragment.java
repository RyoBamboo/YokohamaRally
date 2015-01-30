package com.dr.yokohamarally.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dr.yokohamarally.listener.DialogListener;

/**
 * AlertDialogを汎用的に使えるようにする為のクラス
 */

public class CommonDialogFragment extends DialogFragment {

    private DialogListener listener = null;

    public static CommonDialogFragment newInstance(String title, String message) {
        return setCommonDialogFragment(title, message);
    }

    private static CommonDialogFragment setCommonDialogFragment(String title, String message) {
        CommonDialogFragment frag = new CommonDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("message", message);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        String title = bundle.getString("title");
        String message = bundle.getString("message");

        // ダイアログの表示
        return new AlertDialog.Builder(getActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int witch){
                        listener.onPositiveClick(getDialogTag());
                    }
                }
            )
            .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int witch) {
                            listener.onNegativeClick(getDialogTag());
                        }
                    }
            )
            .create();
    }

    public String getDialogTag() {
        return getTag() != null ? getTag() : "";
    }


    /**
     * DialogListener
     */
    public void setDialogListener(DialogListener listener) {
        this.listener = listener;
    }

    public void removeDialogListener() {
        this.listener = null;
    }
}
