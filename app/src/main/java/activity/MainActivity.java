package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.rlds.controlefinanceiro.R;

import config.ConfiguracaoFirebase;
import model.Usuario;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText editEmail, editSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
    }

    public  void logarUsuario(Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getAuthFirebase();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();

                }else {
                    String execao = "";
                    try {
                        throw  task.getException();

                    }catch (FirebaseAuthInvalidCredentialsException e){
                        execao = "Email ou senha não corresponde ao usuário!";

                    }catch (FirebaseAuthInvalidUserException e){
                        execao = "Usuário não cadastrado!";

                    }catch (Exception e){
                        execao = "Erro ao fazer login!" + e.getMessage();
                        e.printStackTrace();


                    }
                    Toast.makeText(
                            MainActivity.this,
                            execao, Toast.LENGTH_SHORT
                    ).show();


                }
            }
        });
    }

    public  void validar_login(View view){
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        if (!email.isEmpty()){
            if (!senha.isEmpty()){
                Usuario usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setSenha(senha);
                logarUsuario(usuario);

            }else {
                Toast.makeText(
                        MainActivity.this,
                        "Preencha a senha",
                        Toast.LENGTH_SHORT
                ).show();

            }

        }else {
            Toast.makeText(
                    MainActivity.this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT
            ).show();

        }
    }

    public  void cadastre_se(View view){
        Intent intent = new Intent(MainActivity.this, CadastroActivity2.class);
        startActivity(intent);
    }
    public void abrirTelaPrincipal(){
        Intent intent = new Intent(MainActivity.this, PrincipalActivity2.class);
        startActivity(intent);
    }

    public void usuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getAuthFirebase();
        if (autenticacao.getCurrentUser()!= null){
            abrirTelaPrincipal();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        usuarioLogado();
    }
}