package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.rlds.controlefinanceiro.R;

import config.ConfiguracaoFirebase;
import help.Base64Custom;
import model.Usuario;

public class CadastroActivity2 extends AppCompatActivity {
    private TextInputEditText txtNome, txtEmail, txtSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro2);
        txtSenha = findViewById(R.id.txtSenha);
        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);
    }

    public  void salvarUsuario(Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getAuthFirebase();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(idUsuario);
                    usuario.salvar();

                    Toast.makeText(
                            CadastroActivity2.this,
                            "Usuário cadastrado com sucesso!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                }else {
                    String execao = "";
                    try {
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e){
                        execao = "Digite uma senha mais forte!";

                    }catch (FirebaseAuthInvalidCredentialsException e){
                        execao = "Digite um email válido!";


                    }catch (FirebaseAuthUserCollisionException e){
                        execao = "Usuário já cadastrado!";

                    }catch (Exception e){
                        execao = "Erro ao cadastrar usuário! " + e.getMessage();
                        e.printStackTrace();


                    }
                    Toast.makeText(
                            CadastroActivity2.this,
                            execao, Toast.LENGTH_SHORT

                    ).show();
                }
            }
        });

    }

    public  void validar_usuario(View view){
        String nome = txtNome.getText().toString();
        String email = txtEmail.getText().toString();
        String senha = txtSenha.getText().toString();

        if (!nome.isEmpty()){
            if (!email.isEmpty()){
                if (!senha.isEmpty()){
                    Usuario usuario = new Usuario();
                    usuario.setSenha(senha);
                    usuario.setEmail(email);
                    usuario.setNome(nome);
                    salvarUsuario(usuario);

                }else {
                    Toast.makeText(
                            CadastroActivity2.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT
                    ).show();

                }

            }else {
                Toast.makeText(
                        CadastroActivity2.this,
                        "Preencha o email!",
                        Toast.LENGTH_SHORT
                ).show();

            }

        }else {
            Toast.makeText(
                    CadastroActivity2.this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT
            ).show();

        }
    }
}