package net.hova_it.barared.brio.apis.session;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioDialogVersionBeta;
import net.hova_it.barared.brio.apis.utils.preferencias.Preferencias;
import net.hova_it.barared.brio.apis.views.BrioButton2;

import java.util.HashMap;
import java.util.Map;

import lat.brio.core.BrioGlobales;

/**
 * Fragment principal para el acceso a la aplicacion (Login)
 * Created by Alejandro Gomez on 21/12/2015.
 * Updated by Herman Peralta on 04/078/2016.
 */
public class LoginFormFragment extends Fragment implements View.OnClickListener {
    private View view;
    private LoginFormManager loginFormManager;
    private LoginFormFragmentListener loginFormFragmentListener;
    
    public static LoginFormFragment newInstance() {
        Bundle args = new Bundle();
        
        LoginFormFragment loginFormFragment = new LoginFormFragment();
        loginFormFragment.setArguments(args);
        
        return loginFormFragment;
    }
    
    /**
     * Inflar Layout, componentes y asignacion de listeners
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.session_login_fragment, container, false);
        this.view = view;
        
        if (loginFormManager != null) {
            loginFormFragmentListener = loginFormManager;
            
            Button buttonLogin = (Button) view.findViewById(R.id.buttonLogin);
            Button buttonReset = (Button) view.findViewById(R.id.buttonReset);
            Button buttonBack = (Button) view.findViewById(R.id.buttonBack);
            
            TextView textChangeAdminPass = (TextView) view.findViewById(R.id.textChangeAdminPass);
            BrioButton2 btnAdminPassBack = (BrioButton2) view.findViewById(R.id.btnAdminPassBack);
            BrioButton2 btnAdminPassAccept = (BrioButton2) view.findViewById(R.id.btnAdminPassAccept);
            TextView textForgetLink = (TextView) view.findViewById(R.id.textForgetLink);
            
            ImageButton img_rollback = (ImageButton) view.findViewById(R.id.btn_rollback_app);
            
            buttonLogin.setOnClickListener(this);
            buttonReset.setOnClickListener(this);
            buttonBack.setOnClickListener(this);
            textChangeAdminPass.setOnClickListener(this);
            btnAdminPassBack.setOnClickListener(this);
            btnAdminPassAccept.setOnClickListener(this);
            textForgetLink.setOnClickListener(this);
            img_rollback.setOnClickListener(this);
            
            loginFormFragmentListener.onFragmentCreated();
    
//            TODO quitar esta linea, es solo para que siempre aparezca el mensaje que es beta
            img_rollback.setVisibility(View.GONE);
            if (BrioGlobales.VERSION_BETA) {
                img_rollback.setVisibility(View.VISIBLE);
                if (Preferencias.i.getShowDialogBeta()) {
                    
                    BrioDialogVersionBeta alert = new BrioDialogVersionBeta(null);
                    alert.setCancelable(false);
                    alert.show(getFragmentManager(), BrioDialogVersionBeta.class.getSimpleName());
                    
                    //alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
            }
        }
        ((BrioActivityMain) getActivity ()).setDispatchKey(false);
        
        return view;
    }
    
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                loginFormFragmentListener.onButtonLoginClick();
                break;
            case R.id.buttonReset:
                loginFormFragmentListener.onButtonResetClick();
                break;
            case R.id.buttonBack:
                loginFormFragmentListener.onButtonBackClick();
                break;
            case R.id.textChangeAdminPass:
                loginFormFragmentListener.onBtnAdminPassGoClick();
                break;
            case R.id.btnAdminPassBack:
                loginFormFragmentListener.onBtnAdminPassBackClick();
                break;
            case R.id.btnAdminPassAccept:
                loginFormFragmentListener.onBtnAdminPassAcceptClick();
                break;
            case R.id.textForgetLink:
                loginFormFragmentListener.onButtonForgetClick();
                break;
            case R.id.btn_rollback_app:
                loginFormFragmentListener.onBtnRollbacVersion();
                break;
        }
        
    }
    
    /**
     * Mapeo de de informacion de login con los componentes graficos del Layout
     *
     * @return
     */
    public Map<String, Object> getFields() {
        // Form Fields
        EditText editLoginUsuario = (EditText) view.findViewById(R.id.editLoginUsuario);
        EditText editLoginPassword = (EditText) view.findViewById(R.id.editLoginPassword);
        TextView textLoginError = (TextView) view.findViewById(R.id.textLoginError);
        TextView textVersion = (TextView) view.findViewById(R.id.textVersion);
        
        TextView textPreguntas = (TextView) view.findViewById(R.id.tv_Preguntas);
        EditText editResetRespuesta = (EditText) view.findViewById(R.id.editResetRespuesta);
        EditText editResetPassword = (EditText) view.findViewById(R.id.editResetPassword);
        TextView textResetResult = (TextView) view.findViewById(R.id.textResetResult);
        
        LinearLayout loginLayout = (LinearLayout) view.findViewById(R.id.loginLayout);
        LinearLayout resetLayout = (LinearLayout) view.findViewById(R.id.resetLayout);
        
        View panelChangeAdminPass = view.findViewById(R.id.panelChangeAdminPass);
        EditText editPass1 = (EditText) view.findViewById(R.id.editPass1);
        EditText editPass2 = (EditText) view.findViewById(R.id.editPass2);
        TextView textChangeAdminPass = (TextView) view.findViewById(R.id.textChangeAdminPass);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.btn_rollback_app);
        
        Map<String, Object> fields = new HashMap<>();
        fields.put("editLoginUsuario", editLoginUsuario);
        fields.put("editLoginPassword", editLoginPassword);
        fields.put("textLoginError", textLoginError);
        fields.put("textVersion", textVersion);
        
        fields.put("textPreguntas", textPreguntas);
        fields.put("editResetRespuesta", editResetRespuesta);
        fields.put("editResetPassword", editResetPassword);
        fields.put("textResetResult", textResetResult);
        
        fields.put("loginLayout", loginLayout);
        fields.put("resetLayout", resetLayout);
        
        fields.put("panelChangeAdminPass", panelChangeAdminPass);
        fields.put("editPass1", editPass1);
        fields.put("editPass2", editPass2);
        fields.put("textChangeAdminPass", textChangeAdminPass);
        
        fields.put("btn_rollback_app", imageButton);
        
        return fields;
    }
    
    public void setLoginFormManager(LoginFormManager loginFormManager) {
        this.loginFormManager = loginFormManager;
    }
    
    
    public interface LoginFormFragmentListener {
        void onFragmentCreated();
        
        void onButtonLoginClick();
        
        void onButtonResetClick();
        
        void onButtonUpdateClick();
        
        void onButtonBackClick();
        
        void onButtonForgetClick();
        
        void onBtnAdminPassBackClick();
        
        void onBtnAdminPassGoClick();
        
        void onBtnAdminPassAcceptClick();
        
        void onBtnRollbacVersion();
    }
    
}
