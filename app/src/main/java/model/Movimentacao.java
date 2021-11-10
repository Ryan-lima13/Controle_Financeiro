package model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import config.ConfiguracaoFirebase;
import help.Base64Custom;

public class Movimentacao {
    private String data;
    private String categoria;
    private String descricao;
    private double valor;
    private  String tipo;
    private String key;

    public Movimentacao() {
    }

    public  void salvar(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getAuthFirebase();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference database = ConfiguracaoFirebase.getDatabaseFirebase();
        database.child("movimentacao")
                .child(idUsuario)
                .push()
                .setValue(this);

    }

    public String getData() {
        return data;
    }



    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
