package net.hova_it.barared.brio.apis;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.hova_it.barared.brio.BrioBaseActivity;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.DialogListener;

import brio.sdk.logger.log.BrioLog;
import lat.brio.core.BrioGlobales;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBase extends Fragment {
    
    
    public FragmentBase () {
        // Required empty public constructor
    }
    
    
    private BrioBaseActivity activityBase;
    private Fragment fragment;
    private LayoutInflater inflater;
    private View view = null;
    private Handler handler = null;
    public String TAG = FragmentBase.class.getSimpleName();
    private Context context;
    public BrioLog log;
    public Fragment getFragment() {
        return fragment;
    }
    
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
    
    public Handler getHandler() {
        return handler;
    }
    
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    
    public FragmentBase getSelf() {
        return this;
    }
    
    public BrioBaseActivity getActivityBase() {
        BrioBaseActivity activity;
        if (activityBase == null) {
            activity = (BrioBaseActivity) getActivity();
        } else {
            activity = activityBase;
        }
        return activity;
    }
    
    public void setActivityBase(BrioBaseActivity activityBase) {
        this.activityBase = activityBase;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        handler = new Handler();
        inflater = LayoutInflater.from(getContext());
        if (getFragment() != null)
            TAG = getFragment().getClass().getSimpleName();
        else if (this.getClass() != null)
            TAG = this.getClass().getSimpleName();
        log = new BrioLog(BrioGlobales.ArchivoLog);
    }
    
    public View getViewB () {
        if (view != null)
            return view;
        throw new RuntimeException("\n\n\t\tDebes inicializar el View con setView(v) en el fragmento\n" +
                "\n");
    }
    
    public void setView(View view) {
        this.view = view;
    }
    
  /*  public EditText getCustomEditText(int widgetId) {
        return ((CustomEditText) this.getViewB().findViewById(widgetId)).getEditText();
    }
    
    public CustomEditText getCustomEdit(int widgetId) {
        return ((CustomEditText) this.getViewB().findViewById(widgetId));
    }*/
    
    public EditText getEditText(int widgetId) {
        return (EditText) this.getViewB ().findViewById(widgetId);
    }
    
   /* public CustomSpinnerBase<DLCodigoDescripcion> getCustomStpinner(int widgetId) {
        return ((CustomSpinner) getViewB().findViewById(widgetId)).getSpinner();
    }
    
    public CustomCalendario getCustomCalendario(int widgetId) {
        return (CustomCalendario) getViewB().findViewById(widgetId);
    }*/
    
    public Button getButton(int widgetId) {
        return (Button) this.getViewB ().findViewById(widgetId);
    }
    
    public TextView getTextView(int widgetId) {
        return (TextView) this.getViewB ().findViewById(widgetId);
    }
    
    public LinearLayout getLinearLayout(int widgetId) {
        return (LinearLayout) this.getViewB ().findViewById(widgetId);
    }
    
    public RecyclerView getRecyclerView(int widgetId) {
        return (RecyclerView) this.getViewB ().findViewById(widgetId);
    }
    
    public void setLayoutManager(RecyclerView rv) {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager (getActivity());
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(new DefaultItemAnimator ());
    }
    
    
    public RelativeLayout getRelativeLayout(int widgetId) {
        return (RelativeLayout) this.getViewB ().findViewById(widgetId);
    }
    
    public SwipeRefreshLayout getSwipeRefreshLayout(int widgetId) {
        return (SwipeRefreshLayout) this.getViewB ().findViewById(widgetId);
    }
    
    public Spinner getSpinner(int widgetId) {
        return (Spinner) this.getViewB ().findViewById(widgetId);
    }
    
    public ProgressBar getProgressBar(int widgetId) {
        return (ProgressBar) this.getViewB ().findViewById(widgetId);
    }
    
    public ImageView getImageView(int widgetId) {
        return (ImageView) this.getViewB ().findViewById(widgetId);
    }
    
    public View getView (int widgetId) {
        return this.getViewB ().findViewById(widgetId);
    }
    
    
    public void showToast(String msj) {
        Toast.makeText(getActivity(), msj, Toast.LENGTH_SHORT).show();
    }
    
    public void showAlert(String title, String msj,DialogListener listener){
//        String title = "Envío de ticket";
//        String msj = "El correo capturado es inválido.\nPresione Cancelar para  verificar el correo o Continuar para continuar sin  enviar.";
        BrioConfirmDialog bcd = new BrioConfirmDialog ((AppCompatActivity) getActivity (), title, msj, null, listener);
        bcd.show ();
    }
    
    
    
}
