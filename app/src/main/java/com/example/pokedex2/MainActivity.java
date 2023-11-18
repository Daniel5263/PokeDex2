package com.example.pokedex2;



import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;

import com.androidnetworking.interfaces.ParsedRequestListener;
import com.squareup.picasso.Picasso;




public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "lThAsyIlczF0OQlNphrPWPZ99Z5uOXch";

    EditText enterNameEditText;
    Button searchButton;
    ListView pokeListView;
    ImageView imageView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworking.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        enterNameEditText = findViewById(R.id.enter_name);
        searchButton = findViewById(R.id.search_button);
        pokeListView = findViewById(R.id.poke_list);
        imageView = findViewById(R.id.poke_image);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        pokeListView.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pokemon = enterNameEditText.getText().toString();

                makeRequest(pokemon);



                adapter.add(pokemon);
                adapter.notifyDataSetChanged();

                enterNameEditText.setText("");
            }
        });
    }

    private void makeRequest(String name) {
        ANRequest req = AndroidNetworking.get("https://pokeapi.co/api/v2/pokemon/{name}/")
                .addPathParameter("name", name)
                .addQueryParameter("apikey", API_KEY)
                .setPriority(Priority.LOW)
                .build();

        req.getAsObject(Tickers.class, new ParsedRequestListener<Tickers>() {
            @Override
            public void onResponse(Tickers data) {
                    String TAG = "POKEMON";
                    String randomMove = data.getMoveName();
                    String randomAbility = data.getAbilityName();
                    String imageURL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + data.getId() + ".png";

                    Picasso.get().load(imageURL).into(imageView);

                    Log.i(TAG, "name : " + data.getName());
                    Log.i(TAG, "id : " + data.getId());
                    Log.i(TAG, "weight : " + data.getWeight());
                    Log.i(TAG, "height : " + data.getHeight());
                    Log.i(TAG, "base_experience : " + data.getBaseXP());
                    Log.i(TAG, "Random Move: " + randomMove);
                    Log.i(TAG, "Random Ability: " + randomAbility);

                    ((TextView) findViewById(R.id.poke_name)).setText(data.getName());
                    ((TextView) findViewById(R.id.number_view)).setText(String.valueOf(data.getId()));
                    ((TextView) findViewById(R.id.weight_view)).setText(String.valueOf(data.getWeight()));
                    ((TextView) findViewById(R.id.height_view)).setText(String.valueOf(data.getHeight()));
                    ((TextView) findViewById(R.id.xp_view)).setText(String.valueOf(data.getBaseXP()));
                    ((TextView) findViewById(R.id.move_view)).setText(randomMove);
                    ((TextView) findViewById(R.id.ability_view)).setText(randomAbility);


                    Toast.makeText(getApplicationContext(), data.getName() + " added!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getApplicationContext(), "Error on getting data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

