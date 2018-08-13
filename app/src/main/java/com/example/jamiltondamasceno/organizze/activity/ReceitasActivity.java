package com.example.jamiltondamasceno.organizze.activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jamiltondamasceno.organizze.R;
import com.example.jamiltondamasceno.organizze.config.ConfiguracaoFirebase;
import com.example.jamiltondamasceno.organizze.helper.Base64Custom;
import com.example.jamiltondamasceno.organizze.helper.DateCustom;
import com.example.jamiltondamasceno.organizze.model.Movimentacao;
import com.example.jamiltondamasceno.organizze.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ReceitasActivity extends AppCompatActivity {

    private TextInputEditText campoData, campoCategoria, campoDescricao;
    private EditText campoValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double receitaTotal; // já vem sendo acumulada
    private DatePickerDialog.OnDateSetListener dateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        campoData = findViewById(R.id.editData);
        campoCategoria = findViewById(R.id.editCategoria);
        campoDescricao = findViewById(R.id.editDescricao);
        campoValor = findViewById(R.id.editValor);

        //preenche com a data atual
        campoData.setText(DateCustom.dataAtual());
        campoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> data = DateCustom.partesData(DateCustom.dataAtual());
                DatePickerDialog dialog = new DatePickerDialog(ReceitasActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateDialog,
                        Integer.valueOf(data.get(2)),
                        Integer.valueOf(data.get(1)),
                        Integer.valueOf(data.get(0)));

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
                String diaEditado;
                if (dia < 10) {
                    diaEditado = "0" + dia;
                } else {
                    diaEditado = String.valueOf(dia);
                }
                String mesEditado;
                if (mes < 10) {
                    mesEditado = "0" + mes;
                } else {
                    mesEditado = String.valueOf(mes);
                }
                String data = diaEditado + "/" + mesEditado + "/" + ano;
                campoData.setText(data);
            }
        };
        recuperarReceitaTotal();
    }

    public void salvarReceita(View view) {
        if (validarCamposReceita()) {
            movimentacao = new Movimentacao();
            String data = campoData.getText().toString();
            Double valorRecuperado = Double.parseDouble(campoValor.getText().toString());
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(campoCategoria.getText().toString());
            movimentacao.setDescricao(campoDescricao.getText().toString());
            movimentacao.setData(data);
            movimentacao.setTipo("r");

            Double receitaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(receitaAtualizada);

            movimentacao.salvar(data);
            finish();
        }

    }

    public boolean validarCamposReceita() {

        String textoValor = campoValor.getText().toString();
        String textoData = campoData.getText().toString();
        String textoCategoria = campoCategoria.getText().toString();
        String textoDescricao = campoDescricao.getText().toString();

        if (!textoValor.isEmpty()) {
            if (!textoData.isEmpty()) {
                if (!textoCategoria.isEmpty()) {
                    if (!textoDescricao.isEmpty()) {

                        return true;

                    } else {
                        Toast.makeText(ReceitasActivity.this,
                                "A descricao não foi preenchida!",
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                } else {
                    Toast.makeText(ReceitasActivity.this,
                            "A categoria não foi preenchida!",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                Toast.makeText(ReceitasActivity.this,
                        "A data não foi preenchida!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Toast.makeText(ReceitasActivity.this,
                    "Valor não foi preenchido!",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void recuperarReceitaTotal() {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void atualizarReceita(Double receitaAtualizada) {
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.child("receitaTotal").setValue(receitaAtualizada);
    }

//    public void atualizaReceitaMensal(Double valor, String data) {
//        String mes = data.substring(3, 5);
//        String ano = data.substring(6, 10);
//        String mesAno = mes + ano;
//        String emailUsuario = autenticacao.getCurrentUser().getEmail();
//        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
//        DatabaseReference saldoMensalRef = firebaseRef.child("movimentacao").child(idUsuario).child(mesAno);
//        saldoMensalRef.child("receitaMensal").setValue(valor);
//    }
}
