package com.smv.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.smv.app.interfaces.IUser;
import com.smv.app.models.User;


public class MainActivity extends AppCompatActivity {

    ArrayList<String> titles = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    private ListView list;
    private EditText doc;
    private EditText name;
    private EditText lastName;
    private Button button;
    private ProgressBar spinner;

    private String IP = "http://192.168.0.112:8080/siso/user/";

    int duration = Toast.LENGTH_LONG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_layaut);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        button = (Button) findViewById(R.id.guardar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                captureUser();
            }
        });


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        list = findViewById(R.id.list);
        list.setAdapter(arrayAdapter);
        this.getUsers();
    }


    private void captureUser() {
        String doc;
        String name;
        String lastName;
        try {
            this.doc = findViewById(R.id.txtDoc);
            this.name = findViewById(R.id.txtNombre);
            this.lastName = findViewById(R.id.txtApellido);

            doc = this.doc.getText().toString();
            name = this.name.getText().toString();
            lastName = this.lastName.getText().toString();

            if ((doc.equals("")) || (name.equals("")) || (lastName.equals(""))) {

                Toast.makeText(getApplicationContext(),R.string.no_data_complete, duration).show();

            } else {
                User usuario = new User(doc,name,lastName);
                System.out.println(usuario.getNombre());
                this.sendUser(usuario);
                this.doc.setText("");
                this.name.setText("");
                this.lastName.setText("");
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.error_raro, duration).show();
        }


    }

    private void sendUser(User user) {
        spinner.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.IP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IUser service = retrofit.create(IUser.class);
        Call<User> send = service.createUser(user);

        send.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                spinner.setVisibility(View.INVISIBLE);
                getUsers();
                Toast.makeText(getApplicationContext(),R.string.exito_http, duration).show();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                spinner.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), R.string.error_http , duration).show();

            }
        });
    }

    private void getUsers() {
        this.titles.clear();
        spinner.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.IP)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IUser service = retrofit.create(IUser.class);

        Call<List<User>> users = service.listUsers();

        users.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                spinner.setVisibility(View.INVISIBLE);
                for (User user : response.body()) {
                    titles.add(user.getNombre() + " " + user.getApellido());
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                spinner.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), R.string.error_http, duration).show();
            }
        });
    }


}