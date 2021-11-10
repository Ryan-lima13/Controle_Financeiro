package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rlds.controlefinanceiro.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adapter.Movimentacao_adapter;
import config.ConfiguracaoFirebase;
import help.Base64Custom;
import model.Movimentacao;
import model.Usuario;

public class PrincipalActivity2 extends AppCompatActivity {
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getAuthFirebase();
    private TextView saldo, saudacao;
    private DatabaseReference firebaseref = ConfiguracaoFirebase.getDatabaseFirebase();
    private DatabaseReference usuarioRef;
    private double despesaTotal = 0.00;
    private double receitaTotal = 0.00;
    private double resumoUsuario = 0.00;
    private ValueEventListener valueEventListenerUsuario;
    private RecyclerView recyclerView;
    private Movimentacao_adapter movimentacao_adapter;
    private List<Movimentacao>listaMovimentacao = new ArrayList<>();
    private DatabaseReference movimentacaoRef ;
    private  ValueEventListener valueEventListenerMovimentacao;
    private Movimentacao movimentacao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal2);
        saldo = findViewById(R.id.textSaldoTotal);
        saudacao = findViewById(R.id.textSaudacao);
        recyclerView = findViewById(R.id.recyclerViewLista);

        // configurar Adapter
        movimentacao_adapter = new Movimentacao_adapter(listaMovimentacao, this);

        // Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(movimentacao_adapter);
        swipe();

    }

    public  void atualizarSaldo(){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
        usuarioRef = firebaseref.child("usuarios")
                .child(idUsuario);
        if (movimentacao.getTipo().equals("r")){
            receitaTotal = receitaTotal -movimentacao.getValor();
            usuarioRef.child("receitaTotal").setValue(receitaTotal);
        }
        if (movimentacao.getTipo().equals("d")){
            despesaTotal = despesaTotal - movimentacao.getValor();
            usuarioRef.child("despesaTotal").setValue(despesaTotal);

        }

    }
    public void swipe(){
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return  makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirMovimentacao(viewHolder);
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }
    public  void excluirMovimentacao(RecyclerView.ViewHolder viewHolder){
        AlertDialog.Builder mensagem = new AlertDialog.Builder(this);
        mensagem.setTitle("Excluir Movimentação");
        mensagem.setMessage("Você tem certeza que deseja realmente excluir essa movimentação? ");
        mensagem.setCancelable(false);
        mensagem.setIcon(R.drawable.ic_baseline_delete_forever_24);
        mensagem.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                movimentacao = listaMovimentacao.get(position);

                String email = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64(email);

                movimentacaoRef= firebaseref.child("movimentacao")
                        .child(idUsuario);
                movimentacaoRef.child(movimentacao.getKey()).removeValue();
                movimentacao_adapter.notifyItemRemoved(position);
                atualizarSaldo();




            }
        });
        mensagem.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(
                        PrincipalActivity2.this,
                        "cancelado",Toast.LENGTH_SHORT
                ).show();
                movimentacao_adapter.notifyDataSetChanged();

            }
        });
        mensagem.create();
        mensagem.show();


    }



    public  void recuperarResumo(){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);
         usuarioRef = firebaseref.child("usuarios")
                .child(idUsuario);
        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                 despesaTotal = usuario.getDespesaTotal();
                 receitaTotal = usuario.getReceitaTotal();
                 resumoUsuario = receitaTotal - despesaTotal;

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String resultadoFormatatdo = decimalFormat.format(resumoUsuario);
                 saudacao.setText("Olá, " + usuario.getNome());
                 saldo.setText("R$ " + resultadoFormatatdo);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public  void adicionar_receita(View view){
        Intent intent = new Intent(PrincipalActivity2.this, ReceitaActivity2.class);
        startActivity(intent);
    }
    public  void adicionar_despesa(View view){
        Intent intent = new Intent(PrincipalActivity2.this, DespesaActivity2.class);
        startActivity(intent);
    }
    public void sair(View view){

        autenticacao.signOut();
        finish();

    }
    public  void recuperarMovimentacao(){
        String email = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(email);

        movimentacaoRef= firebaseref.child("movimentacao")
                .child(idUsuario);

        valueEventListenerMovimentacao = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMovimentacao.clear();
                for (DataSnapshot dados: snapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    movimentacao.setKey(dados.getKey());
                    listaMovimentacao.add(movimentacao);


                }
                movimentacao_adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarResumo();
        recuperarMovimentacao();

    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerUsuario);
        movimentacaoRef.removeEventListener(valueEventListenerMovimentacao);

    }

}