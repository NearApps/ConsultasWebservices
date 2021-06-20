package com.near.consultadnis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.near.consultadnis.database.Datos;
import com.near.consultadnis.database.entidades.Personas;
import com.near.consultadnis.utils.Utils;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Principal extends AppCompatActivity {

    private Datos sqlite;

    private EditText txt_search;
    private Button btn_search;
    private LinearLayout ly_result;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        sqlite = Datos.DB( Principal.this );

        txt_search = (EditText) findViewById(R.id.txt_search);
        btn_search = (Button) findViewById(R.id.btn_search);
        ly_result = (LinearLayout) findViewById(R.id.ly_result);

        progressDialog = new ProgressDialog(Principal.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Consultando DNI");
        progressDialog.setMessage("Esperando respuesta..");

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ly_result.removeAllViews();

                if (!TextUtils.isEmpty( txt_search.getText().toString() )){
                    if( sqlite.tb_personas().find( txt_search.getText().toString() ) == null ){
                        if ( NetworkUtils.isConnected() ){
                            if ( txt_search.getText().toString().length()==8  ){

                                progressDialog.show();
                                v.setEnabled( false );

                                try{
                                    AsyncHttpClient cliente = new AsyncHttpClient();
                                    cliente.get( (Utils.var.WS + txt_search.getText().toString()), null, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            if ( statusCode == 200 ){
                                                try {
                                                    LogUtils.d( (new String( responseBody )) );
                                                    if ( !TextUtils.isEmpty( (new String( responseBody )) ) ){

                                                        Personas.tb persona = processPerson( (new String( responseBody )) );
                                                        if ( persona != null ){
                                                            //Utils.function.showToastSuccess( "" );
                                                            if ( sqlite.tb_personas().find( persona.getDni() ) == null ){
                                                                sqlite.tb_personas().insert( persona );
                                                            }else{
                                                                sqlite.tb_personas().update( persona );
                                                            }

                                                            print_person( persona.getDni() );

                                                        }else{
                                                            Utils.function.showToastError( "No se hubo resutados" );
                                                        }

                                                    }
                                                }catch (Exception ex){
                                                    ex.printStackTrace();
                                                }finally {
                                                    if (progressDialog.isShowing()){
                                                        progressDialog.cancel();
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            error.printStackTrace();
                                            if (progressDialog.isShowing()){
                                                progressDialog.cancel();
                                            }
                                            Utils.function.showToastError( "No hemos encontrado DNI" );
                                        }

                                        @Override
                                        public void onFinish() {
                                            super.onFinish();
                                        }
                                    });
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }finally {
                                    v.setEnabled(true);
                                }

                            }else{
                                Utils.function.showToastError( "DNI no válido." );
                            }
                        }else{
                            Utils.function.showToastError( "NO esta conectado a internet." );
                        }
                    }else{
                        print_person( txt_search.getText().toString() );
                    }
                }else{
                    Utils.function.showToastError( "Escriba un DNI." );
                }
            }
        });


    }


    private Personas.tb processPerson( final String response ) {

        try{
            JSONObject jsonObject = new JSONObject( response );

            if ( jsonObject.getString("success").equals("true")  ){
                JSONObject jsonPersona = new JSONObject( jsonObject.getString("result") );

                Personas.tb persona = new Personas.tb(
                        jsonPersona.getString("num_doc"),
                        jsonPersona.getString("verificacion"),
                        jsonPersona.getString("nombres"),
                        jsonPersona.getString("apellido_paterno"),
                        jsonPersona.getString("apellido_materno"),
                        jsonPersona.getString("fecha_nacimiento"),
                        ( jsonPersona.has("edad") ? jsonPersona.getString("edad") : "" ),
                        jsonPersona.getString("sexo"),
                        jsonPersona.getString("ubi_dir_dist"),
                        jsonPersona.getString("ubi_dir_prov"),
                        jsonPersona.getString("ubi_dir_depa")
                );
                return persona;
            }else{
                return null;
            }

        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }


    private void print_person( final String dni ){
        try{
            final Personas.tb persona = sqlite.tb_personas().find( dni );
            if ( persona!=null ){
                ly_result.post(new Runnable() {
                    @Override
                    public void run() {
                        ly_result.addView( build_item( "DNI", (persona.getDni() + "-" + persona.getCod_verificcion()) ) );
                        ly_result.addView( build_item( "Nombre", persona.getNombre() ) );
                        ly_result.addView( build_item( "Apellido Paterno", persona.getAp_paterno() ) );
                        ly_result.addView( build_item( "Apellido Materno", persona.getAp_materno()) );
                        ly_result.addView( build_item( "Fecha Nac", persona.getNacimiento() ) );
                        ly_result.addView( build_item( "Edad", persona.getEdad() ) );
                        ly_result.addView( build_item( "sexo", persona.getSexo() ) );
                        ly_result.addView( build_item( "distrito", persona.getUbi_dist() ) );
                        ly_result.addView( build_item( "Provincia", persona.getUbi_prov() ) );
                        ly_result.addView( build_item( "departamento", persona.getUbi_depa() ) );
                    }
                });
            }
        }catch (Exception ex){
            ex.printStackTrace();
            ly_result.removeAllViews();
        }
    }
    private LinearLayout build_item( final String titulo, final String result ){
        LinearLayout ly_item_result = new LinearLayout( Principal.this );
        try{
            ly_item_result = (LinearLayout) View.inflate( Principal.this, R.layout._item_result, null );

            final TextView txt_titulo = (TextView) ly_item_result.findViewById(R.id.txt_titulo);
            txt_titulo.setText( titulo.toUpperCase() );

            ((TextView) ly_item_result.findViewById(R.id.txt_result)).setText(result);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return ly_item_result;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}