package net.hova_it.barared.brio.apis.layouts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogActionListener;
import net.hova_it.barared.brio.apis.utils.MediaUtils;

/**
 * Created by Herman Peralta on 10/03/2016.
 * Clase que muestra el timeout dialog
 */
public class TimeoutDialogFragment extends DialogFragment implements View.OnClickListener {

    private final static String ARG_STRING_MSG = "ARG_STRING_MSG";
    private final static String ARG_INT_TIMEOUT = "ARG_INT_TIMEOUT";

    private int
                resid_string_msg = -1,
                timeout;

    private boolean continuar = true;

    private String title;

    private View
                rootView,
                loading;

    private TextView
                tvTitle;

    private TextView tvMsg;

    private DialogActionListener clickListener;


    public static TimeoutDialogFragment newInstance(String title, int seconds) {
        Bundle args = new Bundle();
        args.putString(ARG_STRING_MSG, title);
        args.putInt(ARG_INT_TIMEOUT, seconds);

        TimeoutDialogFragment fgmt = new TimeoutDialogFragment();
        fgmt.setArguments(args);

        return fgmt;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        recoverArgs();

        rootView = getActivity().getLayoutInflater().inflate(R.layout.brio_dialog_timeout, null);

        configUI();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(rootView);
        setCancelable(false);

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MediaUtils.setDialogTransparentBackground(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        timeHandler = new Handler();
        timeHandler.postDelayed(timeCounter, 1000);
    }

    /**
     * Obtiene la vista del dialogo
     */
    private void configUI() {
        loading = rootView.findViewById(R.id.timeout_loading);
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        tvMsg = (TextView) loading.findViewById(R.id.subtitle);
        rootView.findViewById(R.id.btnCancel).setOnClickListener(this);
    }

    /**
     * Se le pasan las cadenas de texto para el titulo y de cuanto tiempo sera el timeout
     */
    private void recoverArgs() {
        title = getArguments().getString(ARG_STRING_MSG);
        timeout = getArguments().getInt(ARG_INT_TIMEOUT, 5);
    }

    public void setListener(DialogActionListener listener) {
        this.clickListener = listener;
    }

    private Handler timeHandler;
    private Runnable timeCounter = new Runnable() {
        @Override
        public void run() {
            if(continuar) {
                if(timeout>0) {
                    tvMsg.setText("" + timeout);

                    timeout--;
                    timeHandler.postDelayed(this, 1000);
                } else {
                    clickListener.onAcceptResult(null);

                    getDialog().dismiss();
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                continuar = false;

                clickListener.onCancelResult(null);

                getDialog().dismiss();

                break;
        }
    }
}
