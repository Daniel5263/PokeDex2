package com.example.pokedex2;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;




public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "lThAsyIlczF0OQlNphrPWPZ99Z5uOXch";
    private DatabaseReference databaseReference;

    EditText enterNameEditText;
    Button searchButton;
    Button clearButton;
    Button clearDbButton;
    ListView pokeListView;
    ImageView imageView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworking.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        enterNameEditText = findViewById(R.id.enter_name);
        searchButton = findViewById(R.id.search_button);
        clearButton = findViewById(R.id.clear_button);
        clearDbButton = findViewById(R.id.clearDB_button);
        pokeListView = findViewById(R.id.poke_list);
        imageView = findViewById(R.id.poke_image);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        pokeListView.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pokemon = enterNameEditText.getText().toString();

                makeRequest(pokemon);

                enterNameEditText.setText("");
            }
        });

        pokeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedPokemon = (String) parent.getItemAtPosition(position);
                makeRequest(selectedPokemon);
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPokemonProfile();
            }
        });

        clearDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDatabase();
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

                if (adapter.getPosition(data.getName()) == -1) {
                    adapter.add(data.getName());
                    adapter.notifyDataSetChanged();
                    databaseReference = FirebaseDatabase.getInstance().getReference("pokemon");
                    storePokemonInFirebase(data);
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getApplicationContext(), "Enter a real Pokemon please", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storePokemonInFirebase(Tickers pokemon) {
        String pokemonName = pokemon.getName();

        databaseReference.orderByChild("name").equalTo(pokemonName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(getApplicationContext(), pokemonName + " already stored in database", Toast.LENGTH_SHORT).show();
                } else {
                    String key = databaseReference.push().getKey();
                    databaseReference.child(key).setValue(pokemon);
                    Toast.makeText(getApplicationContext(), pokemonName + " added!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error checking duplicate", databaseError.toException());
            }
        });
    }

    private void clearPokemonProfile() {
        enterNameEditText.setText("");
        imageView.setImageResource(android.R.color.transparent);

        ((TextView) findViewById(R.id.poke_name)).setText("");
        ((TextView) findViewById(R.id.number_view)).setText("");
        ((TextView) findViewById(R.id.weight_view)).setText("");
        ((TextView) findViewById(R.id.height_view)).setText("");
        ((TextView) findViewById(R.id.xp_view)).setText("");
        ((TextView) findViewById(R.id.move_view)).setText("");
        ((TextView) findViewById(R.id.ability_view)).setText("");
    }

    private void clearDatabase() {
        DatabaseReference pokemonReference = FirebaseDatabase.getInstance().getReference("pokemon");

        pokemonReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
                Toast.makeText(getApplicationContext(), "All Pokemon deleted from the database", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error clearing database", databaseError.toException());
            }
        });
    }
}

