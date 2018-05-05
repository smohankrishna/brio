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
public class ReportesGananciaAdapter extends RecyclerView.Adapter<ReportesGananciaAdapter.GananciasViewHolder>{
    private List<Reporte> reportes;
    private Context context;
    private FragmentManager fragmentManager;
    private ReportesFragment reportesFragment;
    private BrioConfirmDialog dialogConfirmacion;
    private int layout;
    private String mensaje;
    private final Object me;
    private ReportesDAO reportesDAO;



    public ReportesGananciaAdapter(Context context, List<Reporte> reportes){
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

    /**
     * Inflado de MenuBrioHolder contenedor de informacion que se mostrara en cada fila.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public GananciasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(context).inflate(R.layout.report_ganancias_row, parent, false);
        return new GananciasViewHolder(vItem);
    }

    /**
     * Ingreso de informacion correspondiente a las Ganancias.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(GananciasViewHolder holder, int position) {
        Log.w("onBindViewHolder","entre");

        Reporte reporteHoleder = reportes.get(position);
        holder.reporte = reporteHoleder;
        holder.posicion=position;
        holder.descArticulo.setText(reporteHoleder.getDescripcionItem());
        holder.vendidos.setText(String.format("%.2f", reporteHoleder.getVendidos()));
        holder.precioCompra.setText(String.format("%.2f", reporteHoleder.getPrecioCompra()));
        holder.precioVenta.setText(String.format("%.2f", reporteHoleder.getPrecioBase()));
        holder.ganancia.setText(String.format("%.2f", reporteHoleder.getGanancia()));

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
    public class GananciasViewHolder extends RecyclerView.ViewHolder {

        public TextView descArticulo;
        public TextView vendidos;
        public TextView precioCompra;
        public TextView precioVenta;
        public TextView ganancia;
        public LinearLayout rowReporte;
        public int posicion;
        public Reporte reporte;


        public GananciasViewHolder(View vitem) {
            super(vitem);
            descArticulo=(TextView)vitem.findViewById(R.id.reporte_ganancia_producto);
            vendidos=(TextView)vitem.findViewById(R.id.reporte_vendidos);
            precioCompra=(TextView)vitem.findViewById(R.id.reporte_precio_compra);
            precioVenta=(TextView)vitem.findViewById(R.id.reporte_precio_venta);
            ganancia=(TextView)vitem.findViewById(R.id.reporte_ganancia);

        }

    }
}
