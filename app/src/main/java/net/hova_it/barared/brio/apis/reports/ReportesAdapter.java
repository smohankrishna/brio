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
 * Adapter encargado de mostrar la informacion de las listas "Reportes de Ventas".
 * Created by Mauricio Cerón on 12/04/2016.
 */
public class ReportesAdapter extends RecyclerView.Adapter<ReportesAdapter.ReportesViewHolder> {
    private List<Reporte> reportes;
    private Context context;
    private FragmentManager fragmentManager;
    private ReportesFragment reportesFragment;
    private BrioConfirmDialog dialogConfirmacion;
    private int layout;
    private String mensaje;
    private final Object me;
    private ReportesDAO reportesDAO;

    public ReportesAdapter(Context context, List<Reporte> reportes){
        this.context = context;
        this.reportes = reportes;//new ArrayList<>();
        this.me = this;
        reportesDAO = ((BrioActivityMain)context).modelManager.reporte;
        Log.w("ReporteAdapter", "entre");


    }
    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }


    @Override
    public ReportesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(context).inflate(R.layout.report_vendidos_row, parent, false);
        return new ReportesViewHolder(vItem);
    }


    @Override
    public void onBindViewHolder(ReportesViewHolder holder, int position) {
        Log.w("onBindViewHolder","entre");
        Reporte reporteHoleder = reportes.get(position);
        holder.reporte = reporteHoleder;
        holder.posicion=position;
        holder.descArticulo.setText(reporteHoleder.getDescripcionItem());
        holder.cantidad.setText(String.format("%.2f", reporteHoleder.getVendidos()));


    }

    /**
     * Regresa el tamaño de la lista.
     * @return
     */
    @Override
    public int getItemCount() {
        int size;
        try {//Fixme
            size = reportes.size();
        }catch(Exception e){
            size = 0;
        }
        return size;

    }

    /**
     * Ingreso de lista de pojos al array
     * @param data
     */
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
     * Ingreso de informacion a mostrar en cada cardView de la lista
     */
    public class ReportesViewHolder extends RecyclerView.ViewHolder {

        public TextView descArticulo;
        public TextView cantidad;
        public LinearLayout rowReporte;
        public int posicion;
        public Reporte reporte;


        public ReportesViewHolder(View vitem) {
            super(vitem);
            descArticulo=(TextView)vitem.findViewById(R.id.reporte_producto);
            cantidad=(TextView)vitem.findViewById(R.id.reporte_cantidad);

        }

    }

    /*public class GananciasViewHolder extends RecyclerView.ViewHolder {

        public TextView descArticulo;
        public TextView precioCompra;
        public TextView precioVenta;
        public TextView precioGanancia;
        public LinearLayout rowReporte;
        public int posicion;
        public Reporte reporte;


        public GananciasViewHolder(View vitem) {
            super(vitem);
            descArticulo=(TextView)vitem.findViewById(R.id.reporte_ganancia_producto);
            precioCompra=(TextView)vitem.findViewById(R.id.reporte_precio_compra);
            precioVenta=(TextView)vitem.findViewById(R.id.reporte_precio_venta);
            precioGanancia=(TextView)vitem.findViewById(R.id.reporte_ganancia);

        }

    }*/


}