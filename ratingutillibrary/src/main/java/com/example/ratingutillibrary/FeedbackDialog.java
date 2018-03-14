package com.example.ratingutillibrary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FeedbackDialog extends DialogFragment {
    private static final String TAG = FeedbackDialog.class.getSimpleName();

    private static final String EXTRA_EMAIL = "email";
    private static final String EXTRA_SUBJECT = "subject";
    private static final String EXTRA_APP_NAME = "app-name";
    private static final String EXTRA_DIALOG_TITLE_COLOR = "dialog-title-color";
    private static final String EXTRA_DIALOG_COLOR = "dialog-color";
    private static final String EXTRA_TEXT_COLOR = "text-color";
    private static final String EXTRA_HEADER_TEXT_COLOR = "header-text-color";
    private static final String EXTRA_LOGO = "icon";
    private static final String EXTRA_RATE_BUTTON_TEXT_COLOR = "button-text-color";
    private static final String EXTRA_RATE_BUTTON_BG_COLOR = "button-bg-color";
    private static final String EXTRA_TITLE_DIVIDER = "color-title-divider";
    private static final String EXTRA_RATING_BAR = "get-rating";
    private static final String EXTRA_ON_ACTION_LISTENER = "on-action-listener";

    // Views
    private View confirmDialogTitleView;
    private View confirmDialogView;
    private Button send;
    private EditText mailText;

    public static FeedbackDialog newInstance(String email,
                                             String subject,
                                             String appName,
                                             int titleBackgroundColor,
                                             int dialogColor,
                                             int headerTextColor,
                                             int textColor,
                                             int logoResId,
                                             int lineDividerColor,
                                             int rateButtonTextColor,
                                             int rateButtonBackgroundColor,
                                             float getRatingBar,
                                             OnRatingListener onRatingListener) {
        FeedbackDialog feedbackDialog = new FeedbackDialog();
        Bundle args = new Bundle();
        args.putString(EXTRA_EMAIL, email);
        args.putString(EXTRA_SUBJECT, subject);
        args.putString(EXTRA_APP_NAME, appName);
        args.putInt(EXTRA_DIALOG_TITLE_COLOR, titleBackgroundColor);
        args.putInt(EXTRA_DIALOG_COLOR, dialogColor);
        args.putInt(EXTRA_HEADER_TEXT_COLOR, headerTextColor);
        args.putInt(EXTRA_TEXT_COLOR, textColor);
        args.putInt(EXTRA_LOGO, logoResId);
        args.putInt(EXTRA_RATE_BUTTON_TEXT_COLOR, rateButtonTextColor);
        args.putInt(EXTRA_RATE_BUTTON_BG_COLOR, rateButtonBackgroundColor);
        args.putInt(EXTRA_TITLE_DIVIDER, lineDividerColor);
        args.putFloat(EXTRA_RATING_BAR, getRatingBar);
        args.putParcelable(EXTRA_ON_ACTION_LISTENER, onRatingListener);

        feedbackDialog.setArguments(args);
        return feedbackDialog;

    }

    public FeedbackDialog() {
        // Empty constructor, required for pause/resume
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        initializeUiFieldsDialogGoToMail();
        Log.d(TAG, "All components were initialized successfully");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailBuilder.from(getActivity())
                        .to(getArguments().getString(EXTRA_EMAIL))
                        .subject(getArguments().getString(EXTRA_SUBJECT))
                        .body(mailText.getText().toString())
                        .start();
            }
        });

        return builder.setCustomTitle(confirmDialogTitleView).setView(confirmDialogView).create();
    }

    private void initializeUiFieldsDialogGoToMail() {
        confirmDialogTitleView = View.inflate(getActivity(), R.layout.rating_feedback_dialog_title, null);
        confirmDialogView = View.inflate(getActivity(), R.layout.rating_feedback_send_email, null);

        confirmDialogTitleView.setBackgroundColor(getArguments().getInt(EXTRA_DIALOG_TITLE_COLOR));
        confirmDialogView.setBackgroundColor(getArguments().getInt(EXTRA_DIALOG_COLOR));

        ((TextView) confirmDialogTitleView.findViewById(R.id.confirmDialogTitle)).setTextColor(getArguments().getInt(EXTRA_HEADER_TEXT_COLOR));
        mailText = ((EditText) confirmDialogView.findViewById(R.id.editTextMailText));
        mailText.setTextColor(getArguments().getInt(EXTRA_TEXT_COLOR));

        send = (Button) confirmDialogView.findViewById(R.id.buttonSend);
        send.setTextColor(getArguments().getInt(EXTRA_RATE_BUTTON_TEXT_COLOR));
        send.setBackgroundColor(getArguments().getInt(EXTRA_RATE_BUTTON_BG_COLOR));
    }


    @Override
    public void onStart() {
        super.onStart();
        final int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        final View titleDivider = getDialog().findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(getArguments().getInt(EXTRA_TITLE_DIVIDER));
        }
    }

    private boolean isPackageInstalled(String packageName) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
