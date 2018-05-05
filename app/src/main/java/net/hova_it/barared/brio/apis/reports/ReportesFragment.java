package net.hova_it.barared.brio.apis.reports;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.layouts.menus.api.OptionMenuFragment;
import net.hova_it.barared.brio.apis.models.ModelManager;
import net.hova_it.barared.brio.apis.models.entities.Reporte;
import net.hova_it.barared.brio.apis.models.entities.Ticket;
import net.hova_it.barared.brio.apis.models.entities.TransaccionTarjeta;
import net.hova_it.barared.brio.apis.utils.FragmentListButtonListener;
import net.hova_it.barared.brio.apis.utils.Utils;
import net.hova_it.barared.brio.apis.utils.dialogs.BrioAlertDialog;
import net.hova_it.barared.brio.apis.views.BrioButton2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lat.brio.core.BrioGlobales;

/**
 * Fragmen principal, encargado de mostrar la informacion de "Reporte de ventas" y "Detalles estado de cuenta"
 * Created by Mauricio Cerón on 12/04/2016.
 */
public class ReportesFragment extends OptionMenuFragment implements View.OnClickListener{

    public final static String KEY_LOG = ReportesFragment.class.getSimpleName();

    private Spinner selecReporte;
    private BrioButton2 reporteBtn;
    private EditText fechaInicio;
    private EditText fechaFin;
    private TextView showDetallesVentas, showDetallesServicios, showDetallesTae, showDetallesInternet, showDetallesTarjeta,
            showDetallesBanco, showDetallesSeguros, showDetallesWestern;
    //private LinearLayout layoutDetallesVentas;
    private TextView textSinArticulos,textSinInformacion , textSinInternet;
    private RecyclerView mRecyclerView,RecyclerViewPos , RecyclerViewServicio, RecyclerViewTAE,
            RecyclerViewInternet, RecyclerViewTarjeta,RecyclerViewBanco, RecyclerViewSeguros, RecyclerViewWestern;
    private LinearLayout mRecyclerViewDetallesPos, mRecyclerViewDetallesServicios, mRecyclerViewDetallesTAE
            , mRecyclerViewDetallesInternet, mRecyclerViewDetallesTarjeta,mRecyclerViewDetallesBanco,
            mRecyclerViewDetallesSeguros, mRecyclerViewDetallesWestern;



    private ReportesAdapter mAdapter;
    private ReportesGananciaAdapter mGananciaAdapter;
    private ReportesClosedCashAdapter mClosedCashAdapter;
    private ReportesCajaAdapter mCaja;




    private ModelManager modelManager;
    private List<Reporte> reportList;
    private List<Ticket> ticketsCaja,ticketsServiciosAutorizados,tVentas,tServicios,tTae,tInternet,tBanco, tSeguros, tWestern;
    private List<TransaccionTarjeta> tTransaccionesTarjeta;
    private View listReport;
    private LinearLayout listResults,columnasVendidos,columnasGanancias,columnasCaja
            ,columnasDetallesVentas,columnasDetallesServicios,columnasDetallesBanco
            ,columnasDetallesSeguros, columnasDetallesWestern,columnas_closedcash;
    private LinearLayout listaDetalles,selectTypeReport;
    private Button articulo;
    private RelativeLayout detallesVentas, detallesServicios, detallesTae, detallesInternet, detallesTarjeta,detallesBanco, detallesSeguros, detallesWestern;
    private boolean bVentas=false,bServicios=false,bTae=false,bInternet=false, bTarjeta=false, bBanco=false, bSeguros= false, bWestern=false;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private RelativeLayout loading;

    private  int report = -1;

    private Detalle getDetalle;
    private List<Object> objectListasDetalles = new ArrayList<>();

    private DateFormat formatterToShow = new SimpleDateFormat("dd-MM-yyyy");
    private BrioAlertDialog errorFecha, errorTipoReporte;




    //Report specific
    private LinearLayout report_specific;
    private ReportSpecificAdapter reportSpecificAdapter;



    public  ReportesFragment(){}

    public static ReportesFragment getInstance(int report){
        ReportesFragment reportesFragment = new ReportesFragment();
        reportesFragment.report = report;

        return reportesFragment;
    }


    /**
     * Inflado de elementos del Layout.
     * @param inflater Layout a inflar
     * @param container Contenedor donde se mostrare el Layout.
     * @param savedInstanceState Variable para guardar el estado del fragment.
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.reports_fragments, container, false);
        listReport = inflater.inflate(R.layout.reports_tab_fragment, container, false);
        articulo = (Button) rootView.findViewById(R.id.btnProducto);
        loading = (RelativeLayout)rootView.findViewById(R.id.loading_reports);
        columnasVendidos = (LinearLayout) rootView.findViewById(R.id.columnas_ventas);
        columnasGanancias = (LinearLayout) rootView.findViewById(R.id.columnas_vendidos);
        columnas_closedcash = (LinearLayout) rootView.findViewById(R.id.columnas_closedcash);
        columnasCaja = (LinearLayout) rootView.findViewById(R.id.columnas_caja);
        columnasDetallesVentas = (LinearLayout) rootView.findViewById(R.id.columnas_detalles_ventas);
        columnasDetallesServicios = (LinearLayout) rootView.findViewById(R.id.columnas_detalles_servicios);
        columnasDetallesBanco = (LinearLayout) rootView.findViewById(R.id.columnas_detalles_banco);
        columnasDetallesSeguros = (LinearLayout)rootView.findViewById(R.id.columnas_detalles_seguros);
        columnasDetallesWestern = (LinearLayout)rootView.findViewById(R.id.columnas_detalles_western);
        ///DETALLES DE VENTAS
        listaDetalles = (LinearLayout) rootView.findViewById(R.id.layout_detalle_estado_de_cuenta);
        detallesVentas = (RelativeLayout)rootView.findViewById(R.id.mostrar_ventas);
        detallesVentas.setOnClickListener(this);
        detallesInternet = (RelativeLayout)rootView.findViewById(R.id.mostrar_internet);
        detallesInternet.setOnClickListener(this);
        detallesServicios = (RelativeLayout)rootView.findViewById(R.id.mostrar_servicios);
        detallesServicios.setOnClickListener(this);
        detallesTae = (RelativeLayout)rootView.findViewById(R.id.mostrar_tae);
        detallesTae.setOnClickListener(this);
        detallesTarjeta = (RelativeLayout)rootView.findViewById(R.id.mostrar_transacciones_tarjeta);
        detallesTarjeta.setOnClickListener(this);
        detallesBanco = (RelativeLayout)rootView.findViewById(R.id.mostrar_banco);
        detallesBanco.setOnClickListener(this);
        detallesSeguros = (RelativeLayout)rootView.findViewById(R.id.mostrar_seguros);
        detallesSeguros.setOnClickListener(this);
        detallesWestern = (RelativeLayout)rootView.findViewById(R.id.mostrar_western);
        detallesWestern.setOnClickListener(this);
        ///DETALLES TRANSACCIONES Y TOTAL //////////////////////
        showDetallesVentas = (TextView) rootView.findViewById(R.id.parent_list_item_detalles_ventas_results);
        showDetallesServicios = (TextView) rootView.findViewById(R.id.parent_list_item_detalles_servicios_results);
        showDetallesTae = (TextView) rootView.findViewById(R.id.parent_list_item_detalles_tae_results);
        showDetallesInternet = (TextView) rootView.findViewById(R.id.parent_list_item_detalles_internet_results);
        showDetallesTarjeta = (TextView) rootView.findViewById(R.id.parent_list_item_detalles_tarjeta_results);
        showDetallesBanco = (TextView) rootView.findViewById(R.id.parent_list_item_detalles_banco_results);
        showDetallesSeguros = (TextView) rootView.findViewById(R.id.parent_list_item_detalles_seguros_results);
        showDetallesWestern = (TextView) rootView.findViewById(R.id.parent_list_item_detalles_western_results);
        ////////////////////////////
        // layoutDetallesVentas  = (LinearLayout) rootView.findViewById(R.id.layout_detalles);
        selecReporte = (Spinner) rootView.findViewById(R.id.spin_report);
        reporteBtn = (BrioButton2) rootView.findViewById(R.id.btn_report);
        reporteBtn.setOnClickListener(this);
        /*fechaInicio = (TextView) rootView.findViewById(R.id.fecha_inicial);
        fechaInicio.setOnClickListener(this);
        fechaFin = (TextView) rootView.findViewById(R.id.fecha_fin);
        fechaFin.setOnClickListener(this);*/
        textSinArticulos = (TextView)rootView.findViewById(R.id.textSinArticulosReporte);
        textSinInformacion = (TextView)rootView.findViewById(R.id.textSinInformacionReporte);
        listResults = (LinearLayout)rootView.findViewById(R.id.layout_vendidos);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recicler_view_report);
        //////////////Reporte sin internet////////////////////////////////////////////////////////////////
        textSinInternet = (TextView)rootView.findViewById(R.id.textSinInternet);
        //Listas de detalles de estado de cuenta/////////////////////////////////////////////////////////
        mRecyclerViewDetallesPos = (LinearLayout) rootView.findViewById(R.id.list_detalles_pos);
        RecyclerViewPos = (RecyclerView) rootView.findViewById(R.id.recycler_view_detalles_pos);
        mRecyclerViewDetallesServicios = (LinearLayout) rootView.findViewById(R.id.list_detalles_servicios);
        RecyclerViewServicio = (RecyclerView) rootView.findViewById(R.id.recycler_view_detalles_servicios);
        mRecyclerViewDetallesTAE = (LinearLayout) rootView.findViewById(R.id.list_detalles_tae);
        RecyclerViewTAE = (RecyclerView) rootView.findViewById(R.id.recycler_view_detalles_tae);
        mRecyclerViewDetallesInternet = (LinearLayout) rootView.findViewById(R.id.list_detalles_internet);
        RecyclerViewInternet = (RecyclerView) rootView.findViewById(R.id.recycler_view_detalles_internet);
        mRecyclerViewDetallesTarjeta = (LinearLayout) rootView.findViewById(R.id.list_detalles_tarjeta);
        RecyclerViewTarjeta = (RecyclerView) rootView.findViewById(R.id.recycler_view_detalles_tarjeta);
        mRecyclerViewDetallesBanco = (LinearLayout) rootView.findViewById(R.id.list_detalles_banco);
        RecyclerViewBanco = (RecyclerView) rootView.findViewById(R.id.recycler_view_detalles_banco);
        mRecyclerViewDetallesSeguros = (LinearLayout) rootView.findViewById(R.id.list_detalles_seguros);
        RecyclerViewSeguros = (RecyclerView) rootView.findViewById(R.id.recycler_view_detalles_seguros);
        mRecyclerViewDetallesWestern = (LinearLayout) rootView.findViewById(R.id.list_detalles_western);
        RecyclerViewWestern = (RecyclerView) rootView.findViewById(R.id.recycler_view_detalles_western);

        //report specific view
        report_specific = (LinearLayout) rootView.findViewById(R.id.report_specific_include);



        /////////////////////////////////////////////////////////////////////////////////////////////////
        selectTypeReport = (LinearLayout) rootView.findViewById(R.id.select_type_report);
        fechaInicio = (EditText) rootView.findViewById(R.id.fecha_inicial);
        fechaInicio.setInputType(InputType.TYPE_NULL);
        fechaInicio.requestFocus();


        fechaFin = (EditText) rootView.findViewById(R.id.fecha_fin);
        fechaFin.setInputType(InputType.TYPE_NULL);

        setDateTimeField();
        configureUI(rootView);

        return rootView;
    }

    /**
     * Funcion encargada de obtener la informacion capturada por el usuario en los campos
     * "Fecha Inicial" y "Fecha Final" en los menus "Reporte de ventas" y "Detalles estado de cuenta"
     */
    private void setDateTimeField() {
        fechaInicio.setOnClickListener(this);
        fechaFin.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(getContext(), /*R.style.DialogTheme,*/new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fechaInicio.setText(formatterToShow.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(getContext(),/*R.style.DialogTheme,*/ new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fechaFin.setText(formatterToShow.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void configureUI(View root) {

        fillSpinner();

        // DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Date now = new Date();
        String today = formatterToShow.format(now);

        try {
            Log.w("configureUI", "try=" + today + " " + Utils.getBrioDate());

            fechaInicio.setText(today);
            fechaFin.setText(today);
            Log.w("configureUI", "fecha inicio=" + fechaInicio);
            Log.w("configureUI", "fecha fecha="+fechaFin);

        }catch (Exception e){Log.w("configureUI","catch");}

        //mRecyclerView = (RecyclerView) reporte.findViewById(R.id.recicler_view_report);
        //recycleView reportes
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.GONE);
        //recycleView detalles
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager llm3 = new LinearLayoutManager(getActivity());
        llm3.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager llm4 = new LinearLayoutManager(getActivity());
        llm4.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager llm5 = new LinearLayoutManager(getActivity());
        llm5.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager llm6 = new LinearLayoutManager(getActivity());
        llm6.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager llm7 = new LinearLayoutManager(getActivity());
        llm6.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager llm8 = new LinearLayoutManager(getActivity());
        llm8.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager llm9 = new LinearLayoutManager(getActivity());
        llm9.setOrientation(LinearLayoutManager.VERTICAL);

        RecyclerViewPos.setLayoutManager(llm2);
        RecyclerViewPos.setHasFixedSize(true);

        RecyclerViewServicio.setLayoutManager(llm3);
        RecyclerViewServicio.setHasFixedSize(true);

        RecyclerViewTAE.setLayoutManager(llm4);
        RecyclerViewTAE.setHasFixedSize(true);

        RecyclerViewInternet.setLayoutManager(llm5);
        RecyclerViewInternet.setHasFixedSize(true);

        RecyclerViewTarjeta.setLayoutManager(llm6);
        RecyclerViewTarjeta.setHasFixedSize(true);

        RecyclerViewBanco.setLayoutManager(llm7);
        RecyclerViewBanco.setHasFixedSize(true);

        RecyclerViewSeguros.setLayoutManager(llm8);
        RecyclerViewSeguros.setHasFixedSize(true);

        RecyclerViewWestern.setLayoutManager(llm9);
        RecyclerViewWestern.setHasFixedSize(true);

        textSinArticulos.setVisibility(View.VISIBLE);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        //getLoaderManager().initLoader(0, null, this);
    }

    private void fillSpinner(){

        List<String> spinnerReportsType = new ArrayList<>();

        spinnerReportsType.add(Utils.getString(R.string.report_default, getContext()));
        spinnerReportsType.add(Utils.getString(R.string.report_10_most_sold, getContext()));
        spinnerReportsType.add(Utils.getString(R.string.report_10_less_sold, getContext()));
        spinnerReportsType.add(Utils.getString(R.string.report_earning, getContext()));
        //spinnerReportsType.add(Utils.getString(R.string.report_sales, getContext()));
        spinnerReportsType.add(Utils.getString(R.string.report_drawer, getContext()));
        //TODO TESYS21 agregar Reporte de cierre de caja
        spinnerReportsType.add(Utils.getString(R.string.report_cash_closing, getContext()));
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        spinnerReportsType
                );

        selecReporte.setAdapter(spinnerAdapter);
        if(report > 0){
            selecReporte.setSelection(report);
            selectTypeReport.setVisibility(View.GONE);
        }else {
            selecReporte.setSelection(0);
            selectTypeReport.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Manejo de listener de UI para el ingreso de informacion requerida para la generacion de reportes.
     */
    private void generateReport(){
        modelManager = new ModelManager(getContext());

        String fechaInicioTemp = fechaInicio.getText().toString() + " 00:00:00";
        String fechaFinTemp = fechaFin.getText().toString() + " 23:59:59";
        Long tFechaInicio = Utils.dateToTimeatamp(fechaInicioTemp);
        Log.w("Finicio","timestamp "+tFechaInicio);
        Long tFechaFin = Utils.dateToTimeatamp(fechaFinTemp);
        Log.w("Ffin","timestamp "+tFechaFin);
        int tipoReporte = selecReporte.getSelectedItemPosition();

        errorFecha = new BrioAlertDialog((AppCompatActivity) getActivity(), Utils.getString(R.string.report_error_title, getContext()),
                Utils.getString(R.string.report_error_date, getContext()));

        errorTipoReporte = new BrioAlertDialog((AppCompatActivity) getActivity(), Utils.getString(R.string.report_error_title, getContext()),
                Utils.getString(R.string.report_error_type_report, getContext()));

        if(report != -1){
            tipoReporte = report;
        }

        if(tipoReporte > 0) {
            if (tFechaFin > tFechaInicio) {
                ((BrioActivityMain)getActivity()).disableMenus();
                ((BrioActivityMain)getActivity()).disableBackNavigation();
                reporteBtn.setEnabled(false);
                fechaInicio.setEnabled(false);
                fechaFin.setEnabled(false);

                loading.setVisibility(View.VISIBLE);
                columnas_closedcash.setVisibility(View.GONE);
                switch (tipoReporte) {

                    case 1:// los 10 mas vendidos
                        reportList = modelManager.reporte.getReport(tFechaInicio, tFechaFin, tipoReporte);
                        mAdapter = new ReportesAdapter(getActivity(), reportList);
                        mRecyclerView.setAdapter(mAdapter);
                        textSinArticulos.setVisibility(View.GONE);
                        columnasVendidos.setVisibility(View.VISIBLE);
                        columnasGanancias.setVisibility(View.GONE);
                        columnasCaja.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        listResults.setVisibility(View.VISIBLE);
                        listaDetalles.setVisibility(View.GONE);
                        loading.setVisibility(View.GONE);

                        ((BrioActivityMain)getActivity()).enableMenus();
                        ((BrioActivityMain)getActivity()).enableBackNavigation();
                        reporteBtn.setEnabled(true);
                        fechaInicio.setEnabled(true);
                        fechaFin.setEnabled(true);
                        break;

                    case 2:// los 10 menos vendidos
                        reportList = modelManager.reporte.getReport(tFechaInicio, tFechaFin, tipoReporte);
                        mAdapter = new ReportesAdapter(getActivity(), reportList);
                        mRecyclerView.setAdapter(mAdapter);
                        textSinArticulos.setVisibility(View.GONE);
                        columnasVendidos.setVisibility(View.VISIBLE);
                        columnasCaja.setVisibility(View.GONE);
                        columnasGanancias.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        listResults.setVisibility(View.VISIBLE);
                        listaDetalles.setVisibility(View.GONE);
                        loading.setVisibility(View.GONE);


                        ((BrioActivityMain)getActivity()).enableMenus();
                        ((BrioActivityMain)getActivity()).enableBackNavigation();
                        reporteBtn.setEnabled(true);
                        fechaInicio.setEnabled(true);
                        fechaFin.setEnabled(true);
                        break;

                    case 3://Ganancias
                        reportList = modelManager.reporte.getReport(tFechaInicio, tFechaFin, tipoReporte);
                        mGananciaAdapter = new ReportesGananciaAdapter(getActivity(), reportList);
                        mRecyclerView.setAdapter(mGananciaAdapter);
                        textSinArticulos.setVisibility(View.GONE);
                        columnasVendidos.setVisibility(View.GONE);
                        columnasCaja.setVisibility(View.GONE);
                        columnasGanancias.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        listResults.setVisibility(View.VISIBLE);
                        listaDetalles.setVisibility(View.GONE);
                        loading.setVisibility(View.GONE);


                        ((BrioActivityMain)getActivity()).enableMenus();
                        ((BrioActivityMain)getActivity()).enableBackNavigation();
                        reporteBtn.setEnabled(true);
                        fechaInicio.setEnabled(true);
                        fechaFin.setEnabled(true);
                        break;

                    case 5://ClosedCash
                        reportList = modelManager.reporte.getReport(tFechaInicio, tFechaFin, tipoReporte);
                        mClosedCashAdapter = new ReportesClosedCashAdapter(getActivity(), reportList,new CustomReportOnClickListner(){
                            @Override
                            public void onReportRowClick(View specfic_report_item, int position) {

                                loading.setVisibility(View.VISIBLE);

                                //Same reporte list
                                //report_specific

                                ((TextView)report_specific.findViewById(R.id.report_specfic_concepto_name)).setText("Ayyapa");


                                loading.setVisibility(View.GONE);
                                report_specific.setVisibility(View.VISIBLE);



                                //reportList


                                //Need to load specfic_report fragment, on clicking on the row of the reports


                            }

                        });



                        mRecyclerView.setAdapter(mClosedCashAdapter);
                        textSinArticulos.setVisibility(View.GONE);
                        columnasVendidos.setVisibility(View.GONE);
                        columnasCaja.setVisibility(View.GONE);
                        columnas_closedcash.setVisibility(View.VISIBLE);
                        columnasGanancias.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        listResults.setVisibility(View.VISIBLE);
                        listaDetalles.setVisibility(View.GONE);
                        loading.setVisibility(View.GONE);


                        ((BrioActivityMain)getActivity()).enableMenus();
                        ((BrioActivityMain)getActivity()).enableBackNavigation();
                        reporteBtn.setEnabled(true);
                        fechaInicio.setEnabled(true);
                        fechaFin.setEnabled(true);
                        break;

                    case 4://Entradas y salidas caja
                        ticketsCaja = modelManager.tickets.getTicketsEntradaSalida(tFechaInicio, tFechaFin);
                        mCaja = new ReportesCajaAdapter(getActivity(), ticketsCaja);
                        mRecyclerView.setAdapter(mCaja
                        );
                        textSinArticulos.setVisibility(View.GONE);
                        columnasVendidos.setVisibility(View.GONE);
                        columnasGanancias.setVisibility(View.GONE);
                        columnasCaja.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        listResults.setVisibility(View.VISIBLE);
                        listaDetalles.setVisibility(View.GONE);
                        loading.setVisibility(View.GONE);


                        ((BrioActivityMain)getActivity()).enableMenus();
                        ((BrioActivityMain)getActivity()).enableBackNavigation();
                        reporteBtn.setEnabled(true);
                        fechaInicio.setEnabled(true);
                        fechaFin.setEnabled(true);

                        break;

                    case 6: // Este es el tipo Detalle estado de cuenta - Comentado por: Manuel Delgadillo 25/02/2017

                        loading.setVisibility(View.VISIBLE);
                        Log.w("Reporte", "antes de la lista pintando");

                        // Ocultar la vista de texto sin articulos - Comentado por: Manuel Delgadillo 25/02/2017
                        textSinArticulos.setVisibility(View.GONE);
                        // Ocultar la vista de columnas de "Venta de productos" - Comentado por: Manuel Delgadillo 25/02/2017
                        columnasVendidos.setVisibility(View.GONE);

                        columnasCaja.setVisibility(View.GONE);
                        columnasGanancias.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.GONE);
                        listResults.setVisibility(View.VISIBLE);
                        //textSinInformacion.setVisibility(View.VISIBLE);
                        //mRecyclerViewDetallesPos.setVisibility(View.VISIBLE);


                        try {

                            long bench = System.currentTimeMillis();
                            Log.w("Reporte", "antes de la lista try");
                            //tickets = modelManager.tickets.getPosByFechaTicket(tFechaInicio, tFechaFin);

                            long bench2 = System.currentTimeMillis()-bench;
                            Log.w("Reporte","bench(modelManager.tickets.getPosByFechaTicket(tFechaInicio, tFechaFin)): "+bench2);
                            //ticketsServiciosAutorizados = modelManager.tickets.getAutorizadosByRangoFechaTicket(tFechaInicio, tFechaFin);

                            bench = System.currentTimeMillis();
                            bench2 = System.currentTimeMillis()-bench;
                            Log.w("Reporte","bench(modelManager.tickets.getAutorizadosByRangoFechaTicket(tFechaInicio, tFechaFin)): "+bench2);
                            //Log.w("Reporte","Lista tickets size "+tickets.size());

                            /////////////////Async Get Transactions//////////////////////////////
                            GetTransactions serverService = new GetTransactions(getActivity());

                            String idComercio = modelManager.settings.getByNombre("ID_COMERCIO").getValor();
                            final long fInicio = tFechaInicio;
                            final long fFin = tFechaFin;
                            String url = "http://" + BrioGlobales.URL_ZERO_BRIO+ "/session/api/tickets/estado?idComercio=" + idComercio + "&fechaInicio=" + (fInicio * 1000) + "&fechaFin=" + (fFin * 1000);

                            Log.w("Reporte", url);

                            serverService.request(url, new GetTransactions.ServerServiceListener() {

                                @Override
                                public void onServerResponse(List<GetTransactions.ServerResponse> syncResponse) {

                                    getDetalle = new Detalle(syncResponse,loading, textSinInformacion, getActivity(), modelManager,
                                            fInicio, fFin, RecyclerViewPos, RecyclerViewServicio, RecyclerViewTAE,
                                            RecyclerViewInternet, RecyclerViewTarjeta, RecyclerViewBanco, RecyclerViewSeguros, RecyclerViewWestern, showDetallesVentas, showDetallesServicios,
                                            showDetallesTae, showDetallesInternet, showDetallesTarjeta, showDetallesBanco, showDetallesSeguros, showDetallesWestern);
                                    getDetalle.execute();
                                    getDetalle.setDetallesListener(new Detalle.DetallesListener() {
                                        @Override
                                        public void onFinshed(List<Object> listas) {
                                            objectListasDetalles = listas;
                                            tVentas = objectListasDetalles.get(0) == null ? new ArrayList<Ticket>() : (List<Ticket>) objectListasDetalles.get(0);
                                            //Log.w("Reportes","tVentas="+tVentas.size());
                                            tServicios = objectListasDetalles.get(1) == null ? new ArrayList<Ticket>() : (List<Ticket>) objectListasDetalles.get(1);
                                            //Log.w("Reportes","tServicios="+tServicios.size());
                                            tTae = objectListasDetalles.get(2) == null ? new ArrayList<Ticket>() : (List<Ticket>) objectListasDetalles.get(2);
                                            //Log.w("Reportes","tTae="+tTae.size());
                                            tInternet = objectListasDetalles.get(3) == null ? new ArrayList<Ticket>() : (List<Ticket>) objectListasDetalles.get(3);
                                            //Log.w("Reportes","tInternet="+tInternet.size());
                                            tTransaccionesTarjeta = objectListasDetalles.get(4) == null ? new ArrayList<TransaccionTarjeta>() : (List<TransaccionTarjeta>) objectListasDetalles.get(4);
                                            //Log.w("Reportes","tTransaccionesTarjeta="+tTransaccionesTarjeta.size());
                                            tBanco = objectListasDetalles.get(5) == null ? new ArrayList<Ticket>() : (List<Ticket>) objectListasDetalles.get(5);

                                            tSeguros = objectListasDetalles.get(6) == null ? new ArrayList<Ticket>() : (List<Ticket>) objectListasDetalles.get(6);

                                            tWestern = objectListasDetalles.get(7) == null ? new ArrayList<Ticket>() : (List<Ticket>) objectListasDetalles.get(7);


                                            loading.setVisibility(View.GONE);
                                            listaDetalles.setVisibility(View.VISIBLE);

                                            ((BrioActivityMain) getActivity()).enableMenus();
                                            ((BrioActivityMain) getActivity()).enableBackNavigation();
                                            reporteBtn.setEnabled(true);
                                            fechaInicio.setEnabled(true);
                                            fechaFin.setEnabled(true);
                                        }
                                    });
                                }

                                @Override
                                public void onErrorResponse(String syncResponse) {
                                    Log.w("Reporte", "Error en la consulta del servicio!!!!");
                                    Log.w("Reporte", "onErrorResponse");
                                    //listResults.setVisibility(View.VISIBLE);
                                    //detallesVentas.setVisibility(View.VISIBLE);
                                    detallesServicios.setVisibility(View.GONE);
                                    detallesTae.setVisibility(View.GONE);
                                    detallesInternet.setVisibility(View.GONE);
                                    detallesTarjeta.setVisibility(View.GONE);
                                    detallesBanco.setVisibility(View.GONE);
                                    detallesSeguros.setVisibility(View.GONE);
                                    detallesWestern.setVisibility(View.GONE);
                                    mRecyclerViewDetallesPos.setVisibility(View.GONE);
                                    textSinInternet.setVisibility(View.VISIBLE);
                                    loading.setVisibility(View.GONE);


                                    ((BrioActivityMain)getActivity()).enableMenus();
                                    ((BrioActivityMain)getActivity()).enableBackNavigation();
                                    reporteBtn.setEnabled(true);
                                    fechaInicio.setEnabled(true);
                                    fechaFin.setEnabled(true);

                                }
                            });
                            ////////////////////////////////////////////////////////////////////
                            //aqui estaba Async Detalles
                            ////////////////////////////////////////////////////////////////////
                        }catch (Exception e){

                            Log.w("Reporte", "antes de la lista catch!!!!");
                            Log.w("Reporte", "la lista esta vacia ocurrio un problema");
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.GONE);


                            ((BrioActivityMain)getActivity()).enableMenus();
                            ((BrioActivityMain)getActivity()).enableBackNavigation();
                            reporteBtn.setEnabled(true);
                            fechaInicio.setEnabled(true);
                            fechaFin.setEnabled(true);
                        }

                        break;
                }



            } else {
                errorFecha.show();
            }
        }else {
            errorTipoReporte.show();
        }
    }

    /**
     * Limpieza de UI.
     */
    private void clean(){

        textSinInternet.setVisibility(View.GONE);//View agregado para el caso cuando no halla internet
        mRecyclerViewDetallesPos.setVisibility(View.GONE);
        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
        columnasDetallesVentas.setVisibility(View.GONE);
        columnasDetallesServicios.setVisibility(View.GONE);
        columnasDetallesBanco.setVisibility(View.GONE);
        columnasDetallesSeguros.setVisibility(View.GONE);
        columnasDetallesWestern.setVisibility(View.GONE);
        detallesVentas.setVisibility(View.VISIBLE);
        detallesServicios.setVisibility(View.VISIBLE);
        detallesTae.setVisibility(View.VISIBLE);
        detallesInternet.setVisibility(View.VISIBLE);
        detallesTarjeta.setVisibility(View.VISIBLE);
        detallesBanco.setVisibility(View.VISIBLE);
        detallesSeguros.setVisibility(View.VISIBLE);
        detallesWestern.setVisibility(View.VISIBLE);
        listaDetalles.setVisibility(View.GONE);
        bVentas=false;
        bServicios=false;
        bTae=false;
        bInternet=false;
        bTarjeta=false;
        bBanco=false;
        bSeguros=false;
        bWestern= false;
    }

    /*public void setPaymentControllerAdapterListener(FragmentListButtonListener listener) {
        this.mListener = listener;

        //todo: poner listener en el adapter aqui

        //Log.e("PAGES", "adapter null? " + (mAdapter==null));
        //Log.e("PAGES", "listener null? " + (mListener==null));
        //mAdapter.setFragmentListButtonListener(listener);
    }


    public Loader<List<Reporte>> onCreateLoader(int id, Bundle args) {
        //mPDialog = ProgressDialog.show(getActivity(), "", "Cargando, espere un momento", true);

        Log.w("onCreateLoader", "entre al loader");
        return new ReportesLoader(getActivity(), modelManager.reporte, id);
    }



    public void onLoadFinished(Loader<List<Reporte>> loader, List<Reporte> data) {

        Log.w("onCreateLoader", "entre al loader");

        if(data != null) {
            mAdapter.setData(data);
            reportList = data;
            textSinArticulos.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }else{
            textSinArticulos.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        /*
        // The list should now be shown.
        if (isResumed()) {
            mRecyclerViewsetListShown(true);
        } else {
            setListShownNoAnimation(true);
        }



    }

    public void onLoaderReset(Loader<List<ViewInventario>> loader) {
        mAdapter.setData(null);
    }*/


    @Override
    protected View getRootView() {
        return rootView;
    }

    @Override
    protected void beforeRemove() {

    }

    /**
     * Manejo de listener de elementos de captura y despliegue de informacion dentro de los menus
     * "Reporte de ventas" y "Detalles estado de cuenta"
     * @param v View seleccionado.
     */
    @Override
    public void onClick(View v) {
        boolean n=Utils.shouldPerformClick();
        //Log.w("")
        if(true) {

            switch (v.getId()) {
                case R.id.fecha_inicial:

                    fromDatePickerDialog.show();

                    break;
                case R.id.fecha_fin:


                    toDatePickerDialog.show();

                    break;

                case R.id.btn_report:
                    try {
                        clean();
                        generateReport();
                    } catch (Exception e) {
                        //clean();
                        //textSinInformacion.setVisibility(View.VISIBLE);
                    }

                    break;

                case R.id.mostrar_ventas:

                    bVentas = !bVentas;

                    if (bVentas) {
                        if (tVentas.size() > 0) {
                        /*mVentasAdapter = new ReportesVentasAdapter(getActivity(), tVentas);
                        mRecyclerViewDetallesPos.setAdapter(mVentasAdapter);*/
                            detallesVentas.setVisibility(View.VISIBLE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            columnasDetallesVentas.setVisibility(View.VISIBLE);
                            textSinInformacion.setVisibility(View.GONE);
                        } else {
                            detallesVentas.setVisibility(View.VISIBLE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.VISIBLE);
                        }
                    } else {
                        detallesVentas.setVisibility(View.VISIBLE);
                        detallesServicios.setVisibility(View.VISIBLE);
                        detallesTae.setVisibility(View.VISIBLE);
                        detallesInternet.setVisibility(View.VISIBLE);
                        detallesTarjeta.setVisibility(View.VISIBLE);
                        detallesBanco.setVisibility(View.VISIBLE);
                        detallesSeguros.setVisibility(View.VISIBLE);
                        detallesWestern.setVisibility(View.VISIBLE);
                        mRecyclerViewDetallesPos.setVisibility(View.GONE);
                        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                        columnasDetallesVentas.setVisibility(View.GONE);
                        textSinInformacion.setVisibility(View.GONE);
                    }

                    break;

                case R.id.mostrar_servicios:

                    bServicios = !bServicios;

                    if (bServicios) {
                        if (tServicios.size() > 0) {
                        /*mServiciosAdapter = new ReportesServiciosAdapter(getActivity(), tServicios, 1);
                        mRecyclerViewDetallesPos.setAdapter(mServiciosAdapter);*/
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.VISIBLE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            columnasDetallesServicios.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesServicios.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);

                            textSinInformacion.setVisibility(View.GONE);
                        } else {
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.VISIBLE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.VISIBLE);
                        }
                    } else {
                        detallesVentas.setVisibility(View.VISIBLE);
                        detallesServicios.setVisibility(View.VISIBLE);
                        detallesTae.setVisibility(View.VISIBLE);
                        detallesInternet.setVisibility(View.VISIBLE);
                        detallesTarjeta.setVisibility(View.VISIBLE);
                        detallesBanco.setVisibility(View.VISIBLE);
                        detallesSeguros.setVisibility(View.VISIBLE);
                        detallesWestern.setVisibility(View.VISIBLE);
                        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                        columnasDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesPos.setVisibility(View.GONE);
                        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                        textSinInformacion.setVisibility(View.GONE);
                    }

                    break;

                case R.id.mostrar_tae:

                    bTae = !bTae;

                    if (bTae) {
                        if (tTae.size() > 0) {
                    /*mServiciosAdapter = new ReportesServiciosAdapter(getActivity(),tTae,2);
                    mRecyclerViewDetallesPos.setAdapter(mServiciosAdapter);*/
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.VISIBLE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            columnasDetallesServicios.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesTAE.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.GONE);
                        } else {
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.VISIBLE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.VISIBLE);
                        }
                    } else {
                        detallesVentas.setVisibility(View.VISIBLE);
                        detallesServicios.setVisibility(View.VISIBLE);
                        detallesTae.setVisibility(View.VISIBLE);
                        detallesInternet.setVisibility(View.VISIBLE);
                        detallesTarjeta.setVisibility(View.VISIBLE);
                        detallesBanco.setVisibility(View.VISIBLE);
                        detallesSeguros.setVisibility(View.VISIBLE);
                        detallesWestern.setVisibility(View.VISIBLE);
                        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                        columnasDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesPos.setVisibility(View.GONE);
                        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                        textSinInformacion.setVisibility(View.GONE);
                    }

                    break;

                case R.id.mostrar_internet:

                    bInternet = !bInternet;

                    if (bInternet) {
                        if (tInternet.size() > 0) {
                    /*mServiciosAdapter = new ReportesServiciosAdapter(getActivity(),tInternet,3);
                    mRecyclerViewDetallesPos.setAdapter(mServiciosAdapter);*/
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.VISIBLE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            columnasDetallesServicios.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesInternet.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.GONE);
                        } else {
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.VISIBLE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.VISIBLE);
                        }
                    } else {
                        detallesVentas.setVisibility(View.VISIBLE);
                        detallesServicios.setVisibility(View.VISIBLE);
                        detallesTae.setVisibility(View.VISIBLE);
                        detallesInternet.setVisibility(View.VISIBLE);
                        detallesTarjeta.setVisibility(View.VISIBLE);
                        detallesBanco.setVisibility(View.VISIBLE);
                        detallesSeguros.setVisibility(View.VISIBLE);
                        detallesWestern.setVisibility(View.VISIBLE);
                        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                        columnasDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesPos.setVisibility(View.GONE);
                        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                        textSinInformacion.setVisibility(View.GONE);
                    }

                    break;

                case R.id.mostrar_transacciones_tarjeta:

                    bTarjeta = !bTarjeta;

                    if (bTarjeta) {
                        if (tTransaccionesTarjeta.size() > 0) {
                        /*mTarjetaAdapter = new ReportesTransaccionesTarjetaAdapter(getActivity(), tTransaccionesTarjeta);
                        mRecyclerViewDetallesPos.setAdapter(mTarjetaAdapter);*/
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.VISIBLE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            columnasDetallesVentas.setVisibility(View.VISIBLE);
                            textSinInformacion.setVisibility(View.GONE);
                        } else {
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.VISIBLE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.VISIBLE);
                        }
                    } else {
                        detallesVentas.setVisibility(View.VISIBLE);
                        detallesServicios.setVisibility(View.VISIBLE);
                        detallesTae.setVisibility(View.VISIBLE);
                        detallesInternet.setVisibility(View.VISIBLE);
                        detallesTarjeta.setVisibility(View.VISIBLE);
                        detallesBanco.setVisibility(View.VISIBLE);
                        detallesSeguros.setVisibility(View.VISIBLE);
                        detallesWestern.setVisibility(View.VISIBLE);
                        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                        mRecyclerViewDetallesPos.setVisibility(View.GONE);
                        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                        columnasDetallesVentas.setVisibility(View.GONE);
                        textSinInformacion.setVisibility(View.GONE);
                    }

                    break;

                case R.id.mostrar_banco:

                    bBanco = !bBanco;

                    if (bBanco) {
                        if (tBanco.size() > 0) {
                        /*mTarjetaAdapter = new ReportesTransaccionesTarjetaAdapter(getActivity(), tTransaccionesTarjeta);
                        mRecyclerViewDetallesPos.setAdapter(mTarjetaAdapter);*/
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.VISIBLE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            columnasDetallesBanco.setVisibility(View.VISIBLE);
                            textSinInformacion.setVisibility(View.GONE);
                        } else {
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.VISIBLE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.VISIBLE);
                        }
                    } else {
                        detallesVentas.setVisibility(View.VISIBLE);
                        detallesServicios.setVisibility(View.VISIBLE);
                        detallesTae.setVisibility(View.VISIBLE);
                        detallesInternet.setVisibility(View.VISIBLE);
                        detallesTarjeta.setVisibility(View.VISIBLE);
                        detallesBanco.setVisibility(View.VISIBLE);
                        detallesSeguros.setVisibility(View.VISIBLE);
                        detallesWestern.setVisibility(View.VISIBLE);
                        mRecyclerViewDetallesPos.setVisibility(View.GONE);
                        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                        columnasDetallesBanco.setVisibility(View.GONE);
                        textSinInformacion.setVisibility(View.GONE);
                    }

                    break;

                case R.id.mostrar_seguros:

                    bSeguros = !bSeguros;

                    if (bSeguros) {
                        if (tSeguros.size() > 0) {
                        /*mSegurosAdapter = new ReportesSegurosAdapter(getActivity(), tSeguros);
                        mRecyclerViewDetallesSeguros.setAdapter(mSegurosAdapter);*/
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.VISIBLE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            columnasDetallesServicios.setVisibility(View.VISIBLE);
                            textSinInformacion.setVisibility(View.GONE);
                        } else {
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.VISIBLE);
                            detallesWestern.setVisibility(View.GONE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);////////
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                            textSinInformacion.setVisibility(View.VISIBLE);
                        }
                    } else {
                        detallesVentas.setVisibility(View.VISIBLE);
                        detallesServicios.setVisibility(View.VISIBLE);
                        detallesTae.setVisibility(View.VISIBLE);
                        detallesInternet.setVisibility(View.VISIBLE);
                        detallesTarjeta.setVisibility(View.VISIBLE);
                        detallesBanco.setVisibility(View.VISIBLE);
                        detallesSeguros.setVisibility(View.VISIBLE);
                        detallesWestern.setVisibility(View.VISIBLE);
                        mRecyclerViewDetallesPos.setVisibility(View.GONE);
                        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                        columnasDetallesSeguros.setVisibility(View.GONE);
                        textSinInformacion.setVisibility(View.GONE);
                    }

                    break;

                case R.id.mostrar_western:

                    bWestern = !bWestern;

                    if (bWestern) {
                        if (tWestern.size() > 0) {
                        /*mSegurosAdapter = new ReportesSegurosAdapter(getActivity(), tSeguros);
                        mRecyclerViewDetallesSeguros.setAdapter(mSegurosAdapter);*/
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.VISIBLE);
                            columnasDetallesServicios.setVisibility(View.VISIBLE);
                            textSinInformacion.setVisibility(View.GONE);
                        } else {
                            detallesVentas.setVisibility(View.GONE);
                            detallesServicios.setVisibility(View.GONE);
                            detallesTae.setVisibility(View.GONE);
                            detallesInternet.setVisibility(View.GONE);
                            detallesTarjeta.setVisibility(View.GONE);
                            detallesBanco.setVisibility(View.GONE);
                            detallesSeguros.setVisibility(View.GONE);
                            detallesWestern.setVisibility(View.VISIBLE);
                            mRecyclerViewDetallesPos.setVisibility(View.GONE);
                            mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                            mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                            mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                            mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                            mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                            mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                            mRecyclerViewDetallesWestern.setVisibility(View.GONE);/////////
                            textSinInformacion.setVisibility(View.VISIBLE);
                        }
                    } else {
                        detallesVentas.setVisibility(View.VISIBLE);
                        detallesServicios.setVisibility(View.VISIBLE);
                        detallesTae.setVisibility(View.VISIBLE);
                        detallesInternet.setVisibility(View.VISIBLE);
                        detallesTarjeta.setVisibility(View.VISIBLE);
                        detallesBanco.setVisibility(View.VISIBLE);
                        detallesSeguros.setVisibility(View.VISIBLE);
                        detallesWestern.setVisibility(View.VISIBLE);
                        mRecyclerViewDetallesPos.setVisibility(View.GONE);
                        mRecyclerViewDetallesServicios.setVisibility(View.GONE);
                        mRecyclerViewDetallesTAE.setVisibility(View.GONE);
                        mRecyclerViewDetallesInternet.setVisibility(View.GONE);
                        mRecyclerViewDetallesTarjeta.setVisibility(View.GONE);
                        mRecyclerViewDetallesBanco.setVisibility(View.GONE);
                        mRecyclerViewDetallesSeguros.setVisibility(View.GONE);
                        mRecyclerViewDetallesWestern.setVisibility(View.GONE);
                        columnasDetallesSeguros.setVisibility(View.GONE);
                        textSinInformacion.setVisibility(View.GONE);
                    }

                    break;



            }
        }
    }


}