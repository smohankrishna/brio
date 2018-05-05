package net.hova_it.barared.brio.apis.reports;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.mail.HTMLTicketPatcher;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.TransaccionTarjeta;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioWebViewDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter encargado de mostrar la informacion de los Servicios.
 * Created by Mauricio Cerón on 03/05/2016.
 */
public class ReportesTransaccionesTarjetaAdapter extends RecyclerView.Adapter<ReportesTransaccionesTarjetaAdapter.TarjetaViewHolder>{
    private List<TransaccionTarjeta> transaccionTarjetas;
    private Context context;
    private FragmentManager fragmentManager;
    //    private ReportesFragment reportesFragment;
    private BrioConfirmDialog dialogConfirmacion;
    private int layout;
    private String mensaje;
    private final Object me;
    private ModelManager modelManager;



    public ReportesTransaccionesTarjetaAdapter(Context context, List<TransaccionTarjeta> tickets){
        this.context = context;
        this.transaccionTarjetas = tickets;//new ArrayList<>();

        this.me = this;
        modelManager = new ModelManager(context);
        Log.w("ReporteAdapter", "entre");


    }
    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }


    @Override
    public TarjetaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(context).inflate(R.layout.report_ventas_row, parent, false);
        return new TarjetaViewHolder(vItem);
    }


    /**
     * Inflado de MenuBrioHolder contenedor de informacion que se mostrara en cada fila.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(TarjetaViewHolder holder, int position) {
        Log.w("onBindViewHolder", "entre");


        TransaccionTarjeta reporteHoleder = transaccionTarjetas.get(position);

        String fecha = Utils.getBrioDate(reporteHoleder.getTimestamp());
        holder.ticketP = reporteHoleder;
        holder.posicion=position;
        holder.ticket.setText(reporteHoleder.getIdTicket()+"");
        holder.fecha.setText(fecha);
        holder.total.setText(String.format("%.2f", reporteHoleder.getMonto()));

    }

    @Override
    public int getItemCount() {
        return transaccionTarjetas == null ? 0 : transaccionTarjetas.size();
    }


    public void setData(List<TransaccionTarjeta> data) {
        if (data != null) {
            transaccionTarjetas = new ArrayList<>();
            transaccionTarjetas.addAll(data);
            Log.w("ReporteAdapter","setData null");
        }
        Log.w("ReporteAdapter","setData not null");
        this.notifyDataSetChanged();
    }

    /**
     *  Ingreso de informacion a mostrar en cada cardView de la lista.
     *  Asignacion de listener encargado de mostrar la informacion de cada ticket.
     */
    public class TarjetaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView ticket;
        public TextView fecha;
        public TextView total;
        public LinearLayout rowReporte;
        public int posicion;
        public TransaccionTarjeta ticketP;
        public BrioWebViewDialog webViewDialog;


        public TarjetaViewHolder(View vitem) {
            super(vitem);
            rowReporte=(LinearLayout)vitem.findViewById(R.id.row_ventas);
            rowReporte.setOnClickListener(this);
            ticket=(TextView)vitem.findViewById(R.id.reporte_ticket);
            fecha=(TextView)vitem.findViewById(R.id.reporte_fecha);
            total=(TextView)vitem.findViewById(R.id.reporte_total);
        }

        @Override
        public void onClick(View v) {
            if(Utils.shouldPerformClick()) {
                switch (v.getId()) {
                    case R.id.row_ventas:
                        String superTicketHTML;
                        Log.w("Reportes", "Ventas adapter id ticket=" + ticketP.getIdTicket());
                        superTicketHTML = HTMLTicketPatcher.superTicketToHTML(ticketP.getIdTicket(), context);
                        webViewDialog = new BrioWebViewDialog((BrioActivityMain) context, superTicketHTML);
                        Log.w("Reportes", "Ventas enviando a imprimir=" + ticketP.getIdTicket());
                        webViewDialog.setIdTicket(ticketP.getIdTicket());
                        webViewDialog.show();

                        break;
                }
            }

        }
    }
}

