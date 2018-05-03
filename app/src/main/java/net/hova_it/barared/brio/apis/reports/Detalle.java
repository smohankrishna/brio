package net.hova_it.barared.brio.apis.reports;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.ItemsTicket;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.models.entities.TransaccionTarjeta;
import net.hova_it.barared.brio.apis.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Tarea asincrona encargada del render de los datos a mostrar en cada lista del menu
 * "Detalles Estado de Cuenta"
 * Created by Mauricio Cerón on 06/05/2016.
 */
public class Detalle extends AsyncTask< Void , Integer , List<Object> > {

    final RelativeLayout loading;
    final TextView textSinInformacion;
    Context context;
    ModelManager modelManager;
    private DetallesListener detallesListener;
    private String TAG = "Detalle Async";
    Long fechaInicio, fechaFin;
    RecyclerView RecyclerViewPos , RecyclerViewServicio, RecyclerViewTAE,
                 RecyclerViewInternet, RecyclerViewTarjeta,RecyclerViewBanco,
                RecyclerViewSeguros, RecyclerViewWestern;
    TextView showDetallesVentas, showDetallesServicios, showDetallesTae,
             showDetallesInternet, showDetallesTarjeta,showDetallesBanco,
             showDetallesSeguros, showDetallesWestern;
    //Lista de transacciones en servidor
    List<GetTransactions.ServerResponse> syncResponse = new ArrayList<>();


    public Detalle (List<GetTransactions.ServerResponse> syncResponse,RelativeLayout loading,  TextView textSinInformacion,Context context, ModelManager modelManager,
                    Long fechaInicio,Long fechaFin,RecyclerView RecyclerViewPos ,RecyclerView RecyclerViewServicio,
                    RecyclerView RecyclerViewTAE,RecyclerView RecyclerViewInternet,RecyclerView RecyclerViewTarjeta,
                    RecyclerView RecyclerViewBanco, RecyclerView RecyclerViewSeguros, RecyclerView RecyclerViewWestern,
                    TextView showDetallesVentas,TextView showDetallesServicios, TextView showDetallesTae,
                    TextView showDetallesInternet,TextView showDetallesTarjeta,TextView showDetallesBanco,
                    TextView showDetallesSeguros, TextView showDetallesWestern ) {
        this.syncResponse = syncResponse;
        this.loading = loading;
        this.textSinInformacion = textSinInformacion;
        this.context = context;
        this.modelManager = modelManager;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.RecyclerViewPos = RecyclerViewPos;
        this.RecyclerViewServicio = RecyclerViewServicio;
        this.RecyclerViewTAE = RecyclerViewTAE;
        this.RecyclerViewInternet = RecyclerViewInternet;
        this.RecyclerViewTarjeta = RecyclerViewTarjeta;
        this.RecyclerViewBanco = RecyclerViewBanco;
        this.RecyclerViewSeguros = RecyclerViewSeguros;
        this.RecyclerViewWestern = RecyclerViewWestern;
        this.showDetallesVentas = showDetallesVentas;
        this.showDetallesServicios = showDetallesServicios;
        this.showDetallesTae = showDetallesTae;
        this.showDetallesInternet = showDetallesInternet;
        this.showDetallesTarjeta = showDetallesTarjeta;
        this.showDetallesBanco = showDetallesBanco;
        this.showDetallesSeguros = showDetallesSeguros;
        this.showDetallesWestern = showDetallesWestern;

        Log.e(TAG,"Tamaño de lista de transacciones en server = " + this.syncResponse.size() );
    }

    private List<Object> ticketComparator(List<Ticket> ticketsLocales,  List<GetTransactions.ServerResponse> transactionServer, int tipo){
        Log.e(TAG, "Entrando a ticketComparator" + ticketsLocales.size() + "\nDatos en server = " + transactionServer.size());

        List<Object> results = new ArrayList<>();
        List<ItemsTicket> autorizaciones = new ArrayList<>();
        String idsTickets = "";
        List<Ticket> ticketsLocalesOK = new ArrayList<>();
        List<Double> comisiones = new ArrayList<>();
        List<Double> cargos = new ArrayList<>();

        List<Integer> ticketeEncontrados = new ArrayList<>();//validacion

        try {

            for (int i = 0; ticketsLocales.size() > i; i++) {
                int id;
                id = ticketsLocales.get(i).getIdTicket();
                if (i == 0) {
                    idsTickets = String.valueOf(id);
                } else {
                    idsTickets = idsTickets + ", " + String.valueOf(id);
                }

            }

            Log.e(TAG, "Tickets a buscar = " + idsTickets);
            autorizaciones = modelManager.itemsTicket.getAutorizationByIdTicket(idsTickets);

            for (int j = 0; autorizaciones.size() > j; j++) {

                String numAut;

                if(autorizaciones.get(j).getDescripcion().contains("(") && autorizaciones.get(j).getDescripcion().contains(")")){
                    Log.w(TAG," autorizacion (): " + autorizaciones.get(j).getDescripcion());
                    numAut = autorizaciones.get(j).getDescripcion().replaceAll(".*\\(","").replaceAll("\\).*","");
                }else{
                    Log.w(TAG," autorizacion sin(): " + autorizaciones.get(j).getDescripcion());
                    String autTemp[] = autorizaciones.get(j).getDescripcion().split(" ");
                    numAut = autTemp[1] + " ";
                }

                Log.w(TAG," numAut : " + numAut);


                //String mydata = "some string with 'the data i want' inside";
                /*Pattern pattern = Pattern.compile("'(\\(.*\\))'");
                Matcher matcher = pattern.matcher(numAut);
                if (matcher.find()) {
                    Log.e(TAG, "Cadena extraida " + matcher.group(1));
                    numAut = matcher.group(1);
                }*/
                /*numAut = numAut.replace("(","").replace(")","");replace("Autorización (", "").replace(")", "")
                        .replace("<br />","").replace("Mensaje: Recarga Exitosa","");*/
                Log.e(TAG, "Ticket " + autorizaciones.get(j).getIdTicket() + " Autorizacion: " + numAut);
                Log.e(TAG, "Tamaño arreglo server= " + transactionServer.size() );
                for (int k = 0; transactionServer.size() > k; k++) {
                    boolean encontrado = false;
                    Log.e(TAG,"Tipo en funcion= "+ tipo + " En operacion server " + transactionServer.get(k).getTipo() );
                    if (tipo == transactionServer.get(k).getTipo()){
                        Log.e(TAG,"Supereee el if de tipo de transaccion");
                        Log.e(TAG,"Autorizaciones en Server= " + transactionServer.get(k).getAuthorization() + " Autorizacion local: " + numAut);

                            Log.e(TAG,"Transaccion bancaria= " + numAut.equals(String.valueOf(transactionServer.get(k).getTransaction())));

                        if ((numAut.equals(transactionServer.get(k).getAuthorization())) || (tipo == 7 && numAut.equals(String.valueOf(transactionServer.get(k).getTransaction())))) {
                            encontrado = true;
                            ticketsLocalesOK.add(ticketsLocales.get(j));
                            comisiones.add(transactionServer.get(k).getCommision());

                            Double car = 0d;
                            Log.e(TAG,"FinalCommType  del server = " + transactionServer.get(k).getFinalCommType() );
                            if(transactionServer.get(k).getFinalCommType() == 2){
                                car = transactionServer.get(k).getAmount() * ((transactionServer.get(k).getFinalComm()) / 100);
                                Log.e(TAG, "FinalCommType = " + 2 + " comision a repartir calculada = " + car);
                                //cargos.add(car);
                            }
                            if(transactionServer.get(k).getFinalCommType() == 3){
                                car = transactionServer.get(k).getFinalComm();
                                Log.e(TAG, "FinalCommType = " + 3 + " comision a repartir calculada = " + car);
                                //cargos.add(car);
                            }
                            Log.e(TAG, "FinalCommType = " + transactionServer.get(k).getFinalCommType() + " comision a repartir calculada = " + car);
                            cargos.add(car);

                            if(encontrado){
                                Log.e(TAG, "ticket " + autorizaciones.get(j).getIdTicket() + " encontrado!!!" );
                                ticketeEncontrados.add(autorizaciones.get(j).getIdTicket());
                            }

                            break;
                        }
                    }
                }
            }
            Log.e(TAG, "Datos en ticketsLocalesOK = " + ticketsLocalesOK.size() + "\nDatos en comisiones = " + comisiones.size());
            for (int l = 0; ticketsLocalesOK.size() > l; l++) {
                Log.e(TAG, "Ticket " + l + " con id " + ticketsLocalesOK.get(l).getIdTicket() + " le corresponde la comision de " + comisiones.get(l));
            }
        }catch (Exception e){
            Log.e(TAG,"Error en ticketComparator: " + e);
        }


        Log.e(TAG,"Tickets encontrado: ");
        for(int l=0 ; ticketeEncontrados.size() > l ; l++){
            Log.e(TAG, ticketsLocalesOK.get(l)+", ");
        }

        results.add(ticketsLocalesOK);
        results.add(comisiones);
        results.add(cargos);


        return results;
    }


    @Override
    protected void onPreExecute() {
        /*loading.setVisibility(View.VISIBLE);*/
    }

    /**
     * Extraccion de informacion de la DB y llenado de listas.
     * @param params
     * @return
     */
    @Override
    protected List<Object> doInBackground(Void ... params) {
        Log.w(TAG, "doInBackground");
        List<Ticket> tVentas,tServicios,tTae,tInternet,tBanco, tSeguros, tWestern;
        List<Double> cServicios,cTae,cInternet,cBanco,cSeguros,cWestern;
        List<Double> carServicios,carTae,carInternet,carBanco,carSeguros,carWestern;
        List<TransaccionTarjeta> tTransaccionesTarjeta;
        List<Object> listas= new ArrayList<Object>();

        final ReportesVentasAdapter mVentasAdapter;
        final ReportesServiciosAdapter mServiciosAdapter,mTaeAdapter,mInternetAdapter;
        final ReportesTransaccionesTarjetaAdapter mTarjetaAdapter;
        final ReportesBancoAdapter mBancoAdapter;
        final ReportesSegurosAdapter mSegurosAdapter;
        final ReportesWesternAdapter mWesternAdapter;

        tVentas = new ArrayList<>();
        tServicios = new ArrayList<>();
        cServicios = new ArrayList<>();
        carServicios = new ArrayList<>();
        tTae = new ArrayList<>();
        cTae = new ArrayList<>();
        carTae = new ArrayList<>();
        tInternet = new ArrayList<>();
        cInternet = new ArrayList<>();
        carInternet = new ArrayList<>();
        tTransaccionesTarjeta = new ArrayList<>();
        tBanco = new ArrayList<>();
        cBanco = new ArrayList<>();
        carBanco = new ArrayList<>();
        tSeguros = new ArrayList<>();
        cSeguros = new ArrayList<>();
        carSeguros = new ArrayList<>();
        tWestern = new ArrayList<>();
        cWestern = new ArrayList<>();
        carWestern = new ArrayList<>();
        Log.w(TAG, "antes de la lista try");

        try {
            tVentas = modelManager.tickets.getVentasPosByFechaTicket(fechaInicio,fechaFin);
            tServicios = modelManager.tickets.getAutorizadosByRangoFechaTicket(fechaInicio, fechaFin, "4");
            tTae = modelManager.tickets.getAutorizadosByRangoFechaTicket(fechaInicio,fechaFin,"5");
            tInternet = modelManager.tickets.getAutorizadosByRangoFechaTicket(fechaInicio,fechaFin,"6");
            tBanco = modelManager.tickets.getAutorizadosByRangoFechaTicket(fechaInicio,fechaFin," 7, 8");
            tSeguros = modelManager.tickets.getAutorizadosByRangoFechaTicket(fechaInicio, fechaFin, "11");
            tWestern = modelManager.tickets.getAutorizadosByRangoFechaTicket(fechaInicio, fechaFin, "10");
            tTransaccionesTarjeta = modelManager.transaccionesTarjeta.getForRange(fechaInicio,fechaFin);

        }catch (Exception e){Log.w("Async","Problema al extraer tickets de la BD ");}

        int transccionesV=0,transccionesS=0,transccionesTae=0,transccionesI=0,transccionesT=0,transccionesB=0, transccionesSe=0, transccionesW=0 ;
        double totalV=0.0,totalS=0.0,totalTae=0.0,totalI=0.0,totalT=0.0,totalB=0.0, totalSe=0.0, totalW=0.0;

        ///////////////////////////////////////////////////
        //Comparacion tickets locales vs registros server//
        ///////////////////////////////////////////////////
        Log.w("Async","Entrando a ticketComparator");
        /*
            Log.w("Async","Entrando a ticketComparator con " + tServicios.size() + " Servicios");
            List<Object> lServicios = (ticketComparator(tServicios, syncResponse));
            tServicios = (List<Ticket>)lServicios.get(0);
            cServicios = (List<Double>)lServicios.get(1);


        Log.w("Async","Entrando a ticketComparator con " + tTae.size() + " TAE");
        List<Object> lTae = (ticketComparator(tTae, syncResponse));
        tTae = (List<Ticket>)lTae.get(0);
        cTae = (List<Double>)lTae.get(1);
        Log.w("Async","Entrando a ticketComparator con " + tInternet.size() + " Internet");
        List<Object> lInternet = (ticketComparator(tInternet, syncResponse));
        tInternet = (List<Ticket>)lInternet.get(0);
        cInternet = (List<Double>)lInternet.get(1);*/

        try{
            Log.w("Async","tVentas size="+tVentas.size());
            transccionesV=tVentas.size();
            for(int i=0; i<transccionesV; i++){
                totalV+= tVentas.get(i).getImporteNeto();
            }

        }catch (Exception e){
            Log.w("Async","catch ventas :(\n" + e);
            transccionesV=0;
            totalV=0.0;
        }

        try{
            Log.w("Async","tServicios size="+tServicios.size());
            List<Object> lServicios = (ticketComparator(tServicios, syncResponse, 2));//Servicios en server = 2
            tServicios = (List<Ticket>)lServicios.get(0);
            cServicios = (List<Double>)lServicios.get(1);
            carServicios = (List<Double>)lServicios.get(2);
            transccionesS=tServicios.size();
            for(int i=0; i<transccionesS; i++){
                totalS+= tServicios.get(i).getImporteNeto();
            }

        }catch (Exception e){
            Log.w("Async","catch servicios :(\n" + e);
            transccionesS=0;
            totalS=0.0;
        }

        try{
            Log.w("Async","tTae size="+tTae.size());
            List<Object> lTae = (ticketComparator(tTae, syncResponse, 1));//TAE en server = 1
            tTae = (List<Ticket>)lTae.get(0);
            cTae = (List<Double>)lTae.get(1);
            carTae = (List<Double>)lTae.get(2);
            transccionesTae=tTae.size();
            for(int i=0; i<transccionesTae; i++){
                totalTae+= tTae.get(i).getImporteNeto();
            }

        }catch (Exception e){
            Log.w("Async","catch TAE :(\n" + e);
            transccionesTae=0;
            totalTae=0.0;
        }

        try{
            Log.w("Async","tInternet size="+tInternet.size());
            List<Object> lInternet = (ticketComparator(tInternet, syncResponse, 6));//Internet en server = 6
            tInternet = (List<Ticket>)lInternet.get(0);
            cInternet = (List<Double>)lInternet.get(1);
            carInternet = (List<Double>)lInternet.get(2);
            transccionesI=tInternet.size();
            for(int i=0; i<transccionesI; i++){
                totalI+= tInternet.get(i).getImporteNeto();
            }

        }catch (Exception e){
            Log.w("Async","catch Internet :(\n" + e);
            transccionesI=0;
            totalI=0.0;
        }
        try{
            Log.w("Async","tTarjeta size="+tTransaccionesTarjeta.size());
            transccionesT=tTransaccionesTarjeta.size();
            for(int i=0; i<transccionesT; i++){
                totalT+= tTransaccionesTarjeta.get(i).getMonto();
            }

        }catch (Exception e){
            Log.w("Async","catch Pago tarjetas :(\n" + e);
            transccionesT=0;
            totalT=0.0;
        }

        try{
            Log.w("Async","tBanco size="+tBanco.size());
            List<Object> lBanco = (ticketComparator(tBanco, syncResponse, 7));//Banco en server = 7
            tBanco = (List<Ticket>)lBanco.get(0);
            cBanco = (List<Double>)lBanco.get(1);
            carBanco = (List<Double>)lBanco.get(2);
            transccionesB=tBanco.size();
            for(int i=0; i<transccionesB; i++){
                totalB+= tBanco.get(i).getImporteNeto();
            }

        }catch (Exception e){
            Log.w("Async","catch Banco :(\n" + e);
            transccionesB=0;
            totalB=0.0;
        }

        try{
            Log.w("Async","tSeguros size="+tSeguros.size());
            List<Object> lSeguros = (ticketComparator(tSeguros, syncResponse, 13));//Seguros en server = 13
            tSeguros = (List<Ticket>)lSeguros.get(0);
            cSeguros = (List<Double>)lSeguros.get(1);
            carSeguros = (List<Double>)lSeguros.get(2);
            transccionesSe=tSeguros.size();
            for(int i=0; i<transccionesSe; i++){
                totalSe+= tSeguros.get(i).getImporteNeto();
            }

        }catch (Exception e){
            Log.w("Async","catch Seguros :(\n" + e);
            transccionesSe=0;
            totalSe=0.0;
        }


        try{
            Log.w("Async","tWestern size="+tWestern.size());
            List<Object> lWestern = (ticketComparator(tWestern, syncResponse, 12));//Western en server = 12
            tWestern = (List<Ticket>)lWestern.get(0);
            cWestern = (List<Double>)lWestern.get(1);
            carWestern = (List<Double>)lWestern.get(2);
            transccionesW=tWestern.size();
            for(int i=0; i<transccionesW; i++){
                totalW+= tWestern.get(i).getImporteNeto();
            }

        }catch (Exception e){
            Log.w("Async","catch Western :(\n" + e);
            transccionesW=0;
            totalW=0.0;
        }

        listas.add(tVentas);
        listas.add(tServicios);
        listas.add(tTae);
        listas.add(tInternet);
        listas.add(tTransaccionesTarjeta);
        listas.add(tBanco);
        listas.add(tSeguros);
        listas.add(tWestern);

        final String msnVentas =
        String.format("%s %-8d\t%s%-10.2f",Utils.getString(R.string.report_show_detalle_transacciones,context),transccionesV
                ,Utils.getString(R.string.report_show_detalle_total, context),totalV);
        final String msnServicios =
                String.format("%s %-8d\t%s%-10.2f",Utils.getString(R.string.report_show_detalle_transacciones,context),transccionesS
                ,Utils.getString(R.string.report_show_detalle_total,context),totalS);
        final String msnTAE =
                String.format("%s %-8d\t%s%-10.2f",Utils.getString(R.string.report_show_detalle_transacciones,context),transccionesTae
                ,Utils.getString(R.string.report_show_detalle_total,context),totalTae);
        final String msnInternet =
                String.format("%s %-8d\t%s%-10.2f",Utils.getString(R.string.report_show_detalle_transacciones,context),transccionesI
                ,Utils.getString(R.string.report_show_detalle_total,context),totalI);
        final String msnTarjeta =
                String.format("%s %-8d\t%s%-10.2f",Utils.getString(R.string.report_show_detalle_transacciones,context),transccionesT
                ,Utils.getString(R.string.report_show_detalle_total,context),totalT);
        final String msnBanco =
                String.format("%s %-8d\t%s%-10.2f",Utils.getString(R.string.report_show_detalle_transacciones,context),transccionesB
                        ,Utils.getString(R.string.report_show_detalle_total,context),totalB);
        final String msnSeguros =
                String.format("%s %-8d\t%s%-10.2f",Utils.getString(R.string.report_show_detalle_transacciones,context),transccionesSe
                        ,Utils.getString(R.string.report_show_detalle_total,context),totalSe);
        final String msnWestern =
                String.format("%s %-8d\t%s%-10.2f",Utils.getString(R.string.report_show_detalle_transacciones,context),transccionesW
                        ,Utils.getString(R.string.report_show_detalle_total,context),totalW);

        mVentasAdapter = new ReportesVentasAdapter(context, tVentas);
        mServiciosAdapter = new ReportesServiciosAdapter(context, tServicios, cServicios, carServicios,1);
        mTaeAdapter = new ReportesServiciosAdapter(context, tTae, cTae, carTae,2);
        mInternetAdapter = new ReportesServiciosAdapter(context, tInternet, cInternet, carInternet,3);
        mTarjetaAdapter = new ReportesTransaccionesTarjetaAdapter(context, tTransaccionesTarjeta);
        mBancoAdapter = new ReportesBancoAdapter(context, tBanco, cBanco, carBanco);
        mSegurosAdapter = new ReportesSegurosAdapter(context, tSeguros, cSeguros, carSeguros);
        mWesternAdapter = new ReportesWesternAdapter(context, tWestern, cWestern, carWestern);
        ((AppCompatActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDetallesVentas.setText(msnVentas);
                showDetallesServicios.setText(msnServicios);
                showDetallesTae.setText(msnTAE);
                showDetallesInternet.setText(msnInternet);
                showDetallesTarjeta.setText(msnTarjeta);
                showDetallesBanco.setText(msnBanco);
                showDetallesSeguros.setText(msnSeguros);
                showDetallesWestern.setText(msnWestern);

                RecyclerViewPos.setAdapter(mVentasAdapter);
                RecyclerViewServicio.setAdapter(mServiciosAdapter);
                RecyclerViewTAE.setAdapter(mTaeAdapter);
                RecyclerViewInternet.setAdapter(mInternetAdapter);
                RecyclerViewTarjeta.setAdapter(mTarjetaAdapter);
                RecyclerViewBanco.setAdapter(mBancoAdapter);
                RecyclerViewSeguros.setAdapter(mSegurosAdapter);
                RecyclerViewWestern.setAdapter(mWesternAdapter);

            }
        });

        return listas;
    }

    @Override
    protected void onPostExecute(List<Object> listas) {
        //loading.setVisibility(View.GONE);
        detallesListener.onFinshed(listas);

    }


    public void setDetallesListener(DetallesListener detallesListener) {
        this.detallesListener = detallesListener;
    }

    public interface DetallesListener {
        void onFinshed( List<Object> listas );

    }
}