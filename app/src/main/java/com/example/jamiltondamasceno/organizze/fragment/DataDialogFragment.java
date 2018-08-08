package com.example.jamiltondamasceno.organizze.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jamiltondamasceno.organizze.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;

public class DataDialogFragment extends DialogFragment {

    private MaterialCalendarView calendarView;
    private String dataSelecionada;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_dialog_fragment, container, false);
        calendarView = view.findViewById(R.id.calendarViewSelecionaData);
        configuraCalendario();
        return view;
    }

    public void configuraCalendario() {
        CharSequence meses[] = {"janeiro", "fevereiro", "março", "abril", "maio", "junho",
                "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
        calendarView.setTitleMonths(meses);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView materialCalendarView, @NonNull CalendarDay calendarDay, boolean b) {
                dataSelecionada = calendarDay.getDay() + "/" + (calendarDay.getMonth() + 1) + "/" + calendarDay.getYear();
                System.out.println("Data selecionada = " + dataSelecionada);
            }
        });

    }

    public String retornaDataFormatada() {
        return this.dataSelecionada;
    }
}
