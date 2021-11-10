package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rlds.controlefinanceiro.R;

import java.util.Locale;

import config.ConfiguracaoFirebase;
import help.Base64Custom;
import model.Movimentacao;
import model.Usuario;

public class DespesaActivity2 extends AppCompatActivity {
    private TextInputEditText txtData, txtCategoria, txtDescricao;
    private EditText txtValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseref = ConfiguracaoFirebase.getDatabaseFirebase();
    private  FirebaseAuth autenticacao = ConfiguracaoFirebase.getAuthFirebase();
    private double despesaTotal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa2);
        txtCategoria = findViewById(R.id.txtCategoria);
        txtData = findViewById(R.id.txtData);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtValor  = findViewById(R.id.textValor);

    }
    public boolean validarDespesa(){
        String data = txtData.getText().toString();
        String categoria = txtCategoria.getText().toString();
        String descricao = txtDescricao.getText().toString();
        String valor = txtValor.getText().toString();
        if(!data.isEmpty()){
            if (!categoria.isEmpty()){
                if (!descricao.isEmpty()){
                    if (!valor.isEmpty()){
                        return  true;

                    }else {
                        Toast.makeText(
                                DespesaActivity2.this,
                                "Preencha o valor!",
                                Toast.LENGTH_SHORT
                        ).show();
                        return false;

                    }

                }else {
                    Toast.makeText(
                            DespesaActivity2.this,
                            "Preencha a descrição!",
                            Toast.LENGTH_SHORT
                    ).show();
                    return false;

                }

            }else {
                Toast.makeText(
                        DespesaActivity2.this,
                        "Preencha a categoria!",
                        Toast.LENGTH_SHORT
                ).show();
                return false;

            }

        }else {
            Toast.makeText(
                    DespesaActivity2.this,
                    "Preencha a data!",
                    Toast.LENGTH_SHORT
            ).show();
            return  false;

        }




    }

    public  void salvar_despesa(View view){

        if(validarDespesa()){
            movimentacao = new Movimentacao();
            double valorRecuperado = Double.parseDouble(txtValor.getText().toString());
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(txtCategoria.getText().toString());
            movimentacao.setDescricao(txtDescricao.getText().toString());
            movimentacao.setData(txtData.getText().toString());
            movimentacao.setTipo("d");
            double despesaAtualizada = despesaTotal + valorRecuperado;
            atualizarDespesa(despesaAtualizada);

            movimentacao.salvar();
            finish();

        }





    }
    public  void recuprarDespesaTotal(){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
        DatabaseReference usuarioRef = firebaseref.child("usuarios")
                .child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuprarDespesaTotal();
    }
    public  void atualizarDespesa(Double despesa){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
        DatabaseReference usuarioRef = firebaseref.child("usuarios")
                .child(idUsuario);
        usuarioRef.child("despesaTotal").setValue(despesa);
    }
}