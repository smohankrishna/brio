package net.hova_it.barared.brio.apis.reports;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.daos.ReportesDAO;
import net.hova_it.barared.brio.apis.models.entities.Reporte;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter encargado de mostrar la informacion de las ganacias.
 * Created by Mauricio Cerón on 20/04/2016.
 */
public class ReportesClosedCashAdapter extends RecyclerView.Adapter<ReportesClosedCashAdapter.ClosedCashViewHolder>{
    private List<Reporte> reportes;
    private Context context;
    private FragmentManager fragmentManager;
    private ReportesFragment reportesFragment;
    private BrioConfirmDialog dialogConfirmacion;
    private int layout;
    private String mensaje;
    private final Object me;
    private ReportesDAO reportesDAO;

    private  CustomReportOnClickListner customReportOnClickListner;



    public ReportesClosedCashAdapter(Context context, List<Reporte> reportes,CustomReportOnClickListner customReportOnClickListner){
        this.context = context;
        this.reportes = reportes;//new ArrayList<>();
        this.me = this;
        this.reportesDAO = ((BrioActivityMain)context).modelManager.reporte;
        this.customReportOnClickListner = customReportOnClickListner;
        Log.w("ReporteAdapter", "entre");


    }
    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    /**
     * Inflado de MenuBrioHolder contenedor de informacion que se mostrara en cada fila.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ClosedCashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(context).inflate(R.layout.report_closedcash_row, parent, false);
        final ClosedCashViewHolder mViewHolder = new ClosedCashViewHolder(vItem);
        //setting customreport onclick listner
        vItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customReportOnClickListner.onReportRowClick(vItem,mViewHolder.getLayoutPosition());
            }
        });

        return mViewHolder;
    }

    /**
     * Ingreso de informacion correspondiente a las ClosedCash.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ClosedCashViewHolder holder, int position) {
        Log.w("onBindViewHolder","entre");

        Reporte reporteHoleder = reportes.get(position);
        holder.reporte = reporteHoleder;
        holder.posicion=position;
        holder.reporte_concepto.setText(reporteHoleder.getConcepto());
        holder.reporte_cantidad.setText(reporteHoleder.getCantidad());
        holder.reporte_hora.setText(reporteHoleder.getHora());

    }

    @Override
    public int getItemCount() {
        return reportes == null ? 0 : reportes.size();
    }


    public void setData(List<Reporte> data) {
        if (data != null) {
            reportes = new ArrayList<>();
            reportes.addAll(data);
            Log.w("ReporteAdapter","setData null");
        }
        Log.w("ReporteAdapter","setData not null");
        this.notifyDataSetChanged();
    }

    /**
     *  Ingreso de informacion a mostrar en cada cardView de la lista.
     */
    public class ClosedCashViewHolder extends RecyclerView.ViewHolder {

        public TextView reporte_concepto;
        public TextView reporte_cantidad;
        public TextView reporte_hora;
        public LinearLayout rowReporte;
        public int posicion;
        public Reporte reporte;


        public ClosedCashViewHolder(View vitem) {
            super(vitem);
            reporte_concepto=(TextView)vitem.findViewById(R.id.reporte_concepto);
            reporte_cantidad=(TextView)vitem.findViewById(R.id.reporte_cantidad);
            reporte_hora=(TextView)vitem.findViewById(R.id.reporte_hora);

        }

    }
}
