package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import config.ConfiguracaoFirebase;
import help.Base64Custom;
import model.Movimentacao;
import model.Usuario;

public class ReceitaActivity2 extends AppCompatActivity {
    private TextInputEditText editData, editCategoria, editDescricao;
    private EditText editValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseref = ConfiguracaoFirebase.getDatabaseFirebase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getAuthFirebase();
    private double receitaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita2);
        editCategoria = findViewById(R.id.editCategoria);
        editData = findViewById(R.id.editData);
        editDescricao = findViewById(R.id.editDescricao);
        editValor = findViewById(R.id.editValor);
    }
    public  boolean validar_receita(){
        String data = editData.getText().toString();
        String categoria = editCategoria.getText().toString();
        String descricao = editDescricao.getText().toString();
        String valor = editValor.getText().toString();
        if(!data.isEmpty()){
            if (!categoria.isEmpty()){
                if (!descricao.isEmpty()){
                    if (!valor.isEmpty()){
                        return  true;

                    }else {
                        Toast.makeText(
                                ReceitaActivity2.this,
                                "Preencha o valor!",
                                Toast.LENGTH_SHORT
                        ).show();
                        return false;

                    }

                }else {
                    Toast.makeText(
                            ReceitaActivity2.this,
                            "Preencha a descrição!",
                            Toast.LENGTH_SHORT
                    ).show();
                    return false;

                }

            }else {
                Toast.makeText(
                        ReceitaActivity2.this,
                        "Preencha a categoria!",
                        Toast.LENGTH_SHORT
                ).show();
                return false;

            }

        }else {
            Toast.makeText(
                    ReceitaActivity2.this,
                    "Preencha a data!",
                    Toast.LENGTH_SHORT
            ).show();
            return  false;

        }

    }
    public void salvar_receita(View view){
        if (validar_receita()){
            movimentacao = new Movimentacao();
            double valorRecuperado = Double.parseDouble(editValor.getText().toString());
            movimentacao.setValor(valorRecuperado);
            movimentacao.setCategoria(editCategoria.getText().toString());
            movimentacao.setDescricao(editDescricao.getText().toString());
            movimentacao.setData(editData.getText().toString());
            movimentacao.setTipo("r");
            double despesaAtualizada = receitaTotal + valorRecuperado;
            atualizarReceita(despesaAtualizada);

            movimentacao.salvar();
            finish();

        }
    }
    public  void recuprarReceitaTotal(){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
        DatabaseReference usuarioRef = firebaseref.child("usuarios")
                .child(idUsuario);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                receitaTotal = usuario.getReceitaTotal();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public  void atualizarReceita(Double despesa){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
        DatabaseReference usuarioRef = firebaseref.child("usuarios")
                .child(idUsuario);
        usuarioRef.child("receitaTotal").setValue(despesa);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuprarReceitaTotal();

    }
}