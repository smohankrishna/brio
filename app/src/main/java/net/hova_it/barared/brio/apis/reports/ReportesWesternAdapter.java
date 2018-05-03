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
 * Created by Edith Maldonado on 26/09/2016.
 */
public class ReportesWesternAdapter extends RecyclerView.Adapter<ReportesWesternAdapter.WesternViewHolder>{
        private List<Ticket> tickets; //// TODO: 26/09/2016
        private List<Double> comisiones;
        private List<Double> cargos;
        private Context context;
        private FragmentManager fragmentManager;
//    private ReportesFragment reportesFragment;
        private BrioConfirmDialog dialogConfirmacion;
        private int layout;
        private String mensaje;
        private final Object me;
        private ModelManager modelManager;



        public ReportesWesternAdapter(Context context, List<Ticket> tickets, List<Double> comisiones, List<Double> cargos){
            this.context = context;
            this.tickets = tickets;//new ArrayList<>(); //TODO
            this.comisiones = comisiones;
            this.cargos = cargos;
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
    public WesternViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(context).inflate(R.layout.report_western_row, parent, false);
        return new WesternViewHolder(vItem);
    }

    /**
     * Inflado de MenuBrioHolder contenedor de informacion que se mostrara en cada fila.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(WesternViewHolder holder, int position) {
        Log.w("onBindViewHolder","entre");

        //Reparacion de comision
        String tempComi[] = String.valueOf(comisiones.get(position)).split("\\.");
        String comiParse = String.valueOf(comisiones.get(position));
        if(tempComi.length == 2){
            if(tempComi[1].length()> 2){
                tempComi[1] = tempComi[1].substring(0,2);
                comiParse = tempComi[0] + "." + tempComi[1];
                Log.w("ReporteAdapter", "Reparando comision. original=" + comisiones.get(position) +" reparada=" + comiParse);
            }
        }
        //Reparacion de cargo
        String tempCar[] = String.valueOf(cargos.get(position)).split("\\.");
        String carParse = String.valueOf(cargos.get(position));
        if(tempCar.length == 2){
            if(tempCar[1].length()> 2){
                tempCar[1] = tempCar[1].substring(0,2);
                carParse = tempCar[0] + "." + tempCar[1];
                Log.w("ReporteAdapter", "Reparando cargo. original=" + cargos.get(position) +" reparada=" + carParse);
            }
        }

        holder.ganancia.setText(comiParse);
        holder.comision.setText(carParse);

        Ticket reporteHoleder = tickets.get(position);

        int idTipoTicket = reporteHoleder.getIdTipoTicket();
        String tipoTicket = modelManager.tipoTicket.getByIdTipoTicket(idTipoTicket).getDescripcion();
        String fecha = Utils.getBrioDate(reporteHoleder.getTimestamp());
        holder.ticketP = reporteHoleder;
        holder.posicion=position;
        holder.ticket.setText(reporteHoleder.getIdTicket()+"");
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
    public class WesternViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView ticket;
        public TextView fecha;
        public TextView total;
        public TextView comision;
        public TextView ganancia;
        public LinearLayout rowReporte;
        public int posicion;
        public Ticket ticketP;
        public BrioWebViewDialog webViewDialog;


        public WesternViewHolder(View vitem) {
            super(vitem);
            rowReporte=(LinearLayout)vitem.findViewById(R.id.row_western);
            rowReporte.setOnClickListener(this);
            ticket=(TextView)vitem.findViewById(R.id.reporte_ticket);
            fecha=(TextView)vitem.findViewById(R.id.reporte_fecha);
            total=(TextView)vitem.findViewById(R.id.reporte_total);
            comision=(TextView)vitem.findViewById(R.id.reporte_comision);
            ganancia=(TextView)vitem.findViewById(R.id.reporte_ganancia);
        }

        @Override
        public void onClick(View v) {
            if(Utils.shouldPerformClick()) {
                switch (v.getId()) {
                    case R.id.row_western:
                        String superTicketHTML;
                        Log.w("Reportes", "Western adapter id ticket=" + ticketP.getIdTicket());
                        superTicketHTML = HTMLTicketPatcher.superTicketToHTML(ticketP.getIdTicket(), context);
                        webViewDialog = new BrioWebViewDialog((BrioActivityMain) context, superTicketHTML);
                        Log.w("Reportes", "Seguros enviando a imprimir=" + ticketP.getIdTicket());
                        webViewDialog.setIdTicket(ticketP.getIdTicket());
                        webViewDialog.show();

                        break;
                }
            }

        }
    }
}

