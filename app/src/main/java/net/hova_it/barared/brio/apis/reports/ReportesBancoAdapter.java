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
import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioConfirmDialog;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioWebViewDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter encargado de mostrar la informacion de los movimientos Bancarios.
 * Created by Mauricio Cerón on 03/06/2016.
 */
public class ReportesBancoAdapter extends RecyclerView.Adapter<ReportesBancoAdapter.BancoViewHolder>{
    private List<Ticket> tickets;
    private List<Double> comisiones;
    private List<Double> cargos;
    private Context context;
    private FragmentManager fragmentManager;
    private BrioConfirmDialog dialogConfirmacion;
    private int layout;
    private String mensaje;
    private final Object me;
    private ModelManager modelManager;
    private BrioWebViewDialog webViewDialog;


    public ReportesBancoAdapter(Context context, List<Ticket> tickets, List<Double> comisiones, List<Double> cargos){
        this.context = context;
        this.comisiones = comisiones;
        this.cargos = cargos;
        this.tickets = tickets;//new ArrayList<>();
        this.me = this;
        modelManager = new ModelManager(context);
        Log.w("ReporteBancoAdapter", "entre");


    }
    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }


    /**
     * Inflado de MenuBrioHolder contenedor de informacion que se mostrara en cada fila.
     * @param parent Fragmen padre.
     * @param viewType
     * @return
     */
    @Override
    public BancoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vItem = LayoutInflater.from(context).inflate(R.layout.report_banco_row, parent, false);
        return new BancoViewHolder(vItem);
    }


    /**
     * Ingreso de informacion y calculo de comisiones dependiendo del tipo de movimiento Bancario.
     * @param holder Contenedor de informacion.
     * @param position Posicion del elemento en la lista.
     */

    @Override
    public void onBindViewHolder(BancoViewHolder holder, int position) {
        Log.w("onBindViewHolder","entre");


        Ticket reporteHoleder = tickets.get(position);
        List<ItemsTicket> itemsTicket = modelManager.itemsTicket.getByIdTicket(reporteHoleder.getIdTicket());
        String tipo="";
        for (int i=0 ; i<itemsTicket.size();i++){
         if(!(itemsTicket.get(i).getCodigoBarras()).equals("")){
             Log.w("Adapter Banco","tipo de ticket= "+tipo);
             tipo = itemsTicket.get(i).getCodigoBarras().substring(0,4);
             Log.w("Adapter Banco","tipo de ticket despues del subString= "+tipo);
             break;
         }
        }

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

        int idTipoTicket = reporteHoleder.getIdTipoTicket();
        String tipoTicket = modelManager.tipoTicket.getByIdTipoTicket(idTipoTicket).getDescripcion();
        String fecha = Utils.getBrioDate(reporteHoleder.getTimestamp());

        holder.ticketP = reporteHoleder;
        holder.posicion=position;
        holder.ticket.setText(reporteHoleder.getIdTicket()+"");
        holder.tipo.setText(tipo);
        holder.fecha.setText(fecha);
        holder.total.setText(String.format("%.2f", reporteHoleder.getImporteNeto()));

        /*switch (tipo){

            case "VENT":
            case "REPO":
                holder.comision.setText("15.00");//todo: validar comision si se coloca 30.00 o 15.00 en comision
                //holder.ganancia.setText("10.50");
                break;

            case "RECA":
                holder.comision.setText("7.00");
                //holder.ganancia.setText("4.90");
                break;

            default:
                holder.comision.setText("-");
                holder.ganancia.setText("-");
                holder.tipo.setText("-");
                break;

        }*/
    }

    @Override
    public int getItemCount() {
        return tickets== null ? 0 :tickets.size();
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
    public class BancoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView ticket;
        public TextView tipo;
        public TextView fecha;
        public TextView total;
        public TextView comision;
        public TextView ganancia;
        public LinearLayout rowReporte;
        public int posicion;
        public Ticket ticketP;


        public BancoViewHolder(View vitem) {
            super(vitem);
            rowReporte = (LinearLayout)vitem.findViewById(R.id.row_banco);
            rowReporte.setOnClickListener(this);
            ticket=(TextView)vitem.findViewById(R.id.reporte_banco_ticket);
            tipo=(TextView)vitem.findViewById(R.id.reporte_banco_tipo);
            fecha=(TextView)vitem.findViewById(R.id.reporte_banco_fecha);
            total=(TextView)vitem.findViewById(R.id.reporte_banco_total);
            comision=(TextView)vitem.findViewById(R.id.reporte_banco_comision);
            ganancia=(TextView)vitem.findViewById(R.id.reporte_banco_ganancia);
        }

        @Override
        public void onClick(View v) {
            if(Utils.shouldPerformClick()) {

                switch (v.getId()) {
                    case R.id.row_banco:
                        String superTicketHTML;
                        Log.w("Reportes", "Banco adapter id ticket=" + ticketP.getIdTicket());
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


