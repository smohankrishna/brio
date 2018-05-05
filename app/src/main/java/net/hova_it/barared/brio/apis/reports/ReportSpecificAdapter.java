package net.hova_it.barared.brio.apis.reports;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.hova_it.barared.brio.BrioActivityMain;
import net.hova_it.barared.brio.R;
import net.hova_it.barared.brio.apis.models.daos.ReportesDAO;
import net.hova_it.barared.brio.apis.models.entities.Reporte;

import java.util.ArrayList;
import java.util.List;

public class ReportSpecificAdapter extends RecyclerView.Adapter<ReportSpecificAdapter.SpecificReportViewholder> {

    private List<Reporte> specific_reportes;
    private Context context;
    private ReportesDAO reportesDAO;
    private FragmentManager fragmentManager;
    private int layout;


    private CustomSpecificBackButtonListener customSpecificBackButtonListener;

    public ReportSpecificAdapter(Context context, List<Reporte> specific_reportes,CustomSpecificBackButtonListener customSpecificBackButtonListener)
    {
        this.context = context;
        this.specific_reportes = specific_reportes;
        this.reportesDAO =  ((BrioActivityMain)context).modelManager.reporte;
        this.customSpecificBackButtonListener = customSpecificBackButtonListener;
        Log.w("ReportSpecificAdapter", "Entry");

    }


    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager = fragmentManager;
    }

    public void setLayout(int layout){
        this.layout = layout;
    }

    @Override
    public SpecificReportViewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View specific_report = LayoutInflater.from(context).inflate(R.layout.report_specfic_entry_row,parent,false);

        SpecificReportViewholder specificReportViewholder=new SpecificReportViewholder(specific_report);

        return specificReportViewholder;
    }

    @Override
    public void onBindViewHolder(SpecificReportViewholder holder, int position) {
        Log.w("SpecificAdapter","SpecificReportViewholder Setting data to row ");

        Reporte reporteHoleder = specific_reportes.get(position);
        holder.reporte = reporteHoleder;
        holder.posicion=position;
        holder.hora.setText(reporteHoleder.getHora());
        holder.cantidad.setText(reporteHoleder.getCantidad());
        holder.descripcion.setText(reporteHoleder.getDescripcionItem());

        holder.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customSpecificBackButtonListener.onBackClickFromSpecificReport();
            }
        });

    }


    public void setData(List<Reporte> data) {
        if (data != null) {
            specific_reportes = new ArrayList<>();
            specific_reportes.addAll(data);
            Log.w("ReporteSpecificAdapter","setData null");
        }
        Log.w("ReporteSpecificAdapter","setData not null");
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return specific_reportes==null ? 0:specific_reportes.size();
    }

    public class SpecificReportViewholder extends RecyclerView.ViewHolder {

        private TextView hora,cantidad,descripcion;
        private Button backButton;
        public int posicion;
        public Reporte reporte;


        public SpecificReportViewholder(View itemView) {
            super(itemView);
            hora = (TextView) itemView.findViewById(R.id.reporte_specific_hora);
            cantidad = (TextView) itemView.findViewById(R.id.reporte_specific_cantidad);
            descripcion = (TextView) itemView.findViewById(R.id.reporte_specific_descripcion);
            backButton = (Button) itemView.findViewById(R.id.report_specfic_backButton);



        }
    }
}
