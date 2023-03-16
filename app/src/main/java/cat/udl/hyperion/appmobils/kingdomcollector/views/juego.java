package cat.udl.hyperion.appmobils.kingdomcollector.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import cat.udl.hyperion.appmobils.kingdomcollector.Models.Player.Player;
import cat.udl.hyperion.appmobils.kingdomcollector.R;

public class juego extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (savedInstanceState == null) {
            BoardView boardView = BoardView.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, boardView);
            transaction.commit();
            PlayerView playerFragment = new PlayerView();
            fragmentTransaction.replace(R.id.player_fragment_container, playerFragment);
            fragmentTransaction.commit();
        }
    }
}
