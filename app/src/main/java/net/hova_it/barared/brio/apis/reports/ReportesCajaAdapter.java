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
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioWebViewDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter encargado de mostrar la informacion de los movimientos en Caja.
 * Created by Mauricio Cerón on 17/05/2016.
 */
public class ReportesCajaAdapter extends RecyclerView.Adapter<ReportesCajaAdapter.CajaViewHolder>{
    private List<Ticket> tickets;
    private Context context;
    private FragmentManager fragmentManager;
    private BrioConfirmDialog dialogConfirmacion;
    private int layout;
    private String mensaje;
    private final Object me;
    private ModelManager modelManager;



    public ReportesCajaAdapter(Context context, List<Ticket> tickets){
        this.context = context;
        this.tickets = tickets;//new ArrayList<>();
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

    /**
     * Inflado de MenuBrioHolder contenedor de informacion que se mostrara en cada fila.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public CajaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(context).inflate(R.layout.report_ventas_row, parent, false);
        return new CajaViewHolder(vItem);
    }

    /**
     * Ingreso de informacion y tipo de movimiento en Caja.
     * @param holder Contenedor de informacion.
     * @param position Posicion del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(CajaViewHolder holder, int position) {
        Log.w("onBindViewHolder","entre");


        Ticket reporteHoleder = tickets.get(position);

        int idTipoTicket = reporteHoleder.getIdTipoTicket();
        String tipoTicket = "";
        switch (idTipoTicket) {

            case 2:
                tipoTicket ="Entrada";
                break;

            case 3:
                tipoTicket ="Salida";
                break;

        }

        String fecha = Utils.getBrioDate(reporteHoleder.getTimestamp());
        holder.ticketP = reporteHoleder;
        holder.posicion=position;
        holder.ticket.setText(tipoTicket);
        holder.fecha.setText(fecha);
        holder.total.setText(String.format("%.2f",reporteHoleder.getImporteNeto()));

    }

    @Override
    public int getItemCount() {
        return tickets == null ? 0 :tickets.size();
    }


    public void setData(List<Ticket> data) {
        if (data != null) {
            tickets = new ArrayList<>();
            tickets.addAll(data);
            Log.w("ReporteAdapter","setData null");
        }
        Log.w("ReporteAdapter","setData not null");
        this.notifyDataSetChanged();
    }

    /**
     *  Ingreso de informacion a mostrar en cada cardView de la lista.
     *  Asignacion de listener encargado de mostrar la informacion de cada ticket.
     */
    public class CajaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView ticket;
        public TextView fecha;
        public TextView total;
        public LinearLayout rowReporte;
        public int posicion;
        public Ticket ticketP;
        public BrioWebViewDialog webViewDialog;


        public CajaViewHolder(View vitem) {
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

