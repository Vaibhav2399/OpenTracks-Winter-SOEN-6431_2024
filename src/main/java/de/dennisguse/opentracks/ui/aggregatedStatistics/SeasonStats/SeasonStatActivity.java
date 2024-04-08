package de.dennisguse.opentracks.ui.aggregatedStatistics.SeasonStats;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.databinding.ActivitySeasonStatBinding;

public class SeasonStatActivity extends AppCompatActivity {

    ActivitySeasonStatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeasonStatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String[] seasonNames = {"2020 - 2021 Season Stats", "2021 - 2022 Season Stats", "2022 - 2023 Season Stats", "2023 - 2024 Season Stats"};
        Random random = new Random();

        String[] Days = new String[4];
        int[] VerticalM = new int[4];
        int[] Runs = new int[4];
        double[] DistanceKM = new double[4];
        int[] Resort = new int[4];
        String[] Active = new String[4];
        double[] MaxSpeed = new double[4];
        int[] MaxAltitude = new int[4];
        int[] TallestRun = new int[4];
        double[] LongestRun = new double[4];

        ArrayList<DummySeason> seasonArrayList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Days[i] = Integer.toString(random.nextInt(1001));
            VerticalM[i] = random.nextInt(1000);
            Runs[i] = random.nextInt(20);
            DistanceKM[i] = random.nextDouble() * 20;
            Resort[i] = random.nextInt(10) + 1;
            int hours = random.nextInt(24);
            int minutes = random.nextInt(60);
            int seconds = random.nextInt(60);
            String activeTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            Active[i] = activeTime;
            MaxSpeed[i] = random.nextDouble() * 100;
            TallestRun[i] = (int) (random.nextDouble() * 100);
            MaxAltitude[i] = (int) (random.nextDouble() * 5000);
            LongestRun[i] = random.nextDouble() * 10;

            seasonArrayList.add(new DummySeason(seasonNames[i], Integer.parseInt(Days[i]), Runs[i], Resort[i], VerticalM[i], DistanceKM[i], Active[i], MaxSpeed[i], MaxAltitude[i], TallestRun[i], LongestRun[i]));
        }

        SeasonListAdapter listAdapter = new SeasonListAdapter(SeasonStatActivity.this, seasonArrayList);
        binding.seasonListView.setAdapter(listAdapter);
        binding.seasonListView.setClickable(true);
        binding.seasonListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SeasonStatActivity.this, RunAndChairStatActivity.class);
                intent.putExtra("seasonName",seasonArrayList.get(position).seasonName);
                intent.putExtra("days",seasonArrayList.get(position).days);
                intent.putExtra("runs",seasonArrayList.get(position).runs);
                intent.putExtra("resort",seasonArrayList.get(position).resort);
                intent.putExtra("vertical_m",seasonArrayList.get(position).vertical_m);
                intent.putExtra("distance",seasonArrayList.get(position).distance);
                intent.putExtra("active",seasonArrayList.get(position).active);
                intent.putExtra("max_Speed",seasonArrayList.get(position).max_Speed);
                intent.putExtra("max_alt",seasonArrayList.get(position).max_alt);
                intent.putExtra("tallestRun",seasonArrayList.get(position).tallestRun);
                intent.putExtra("longestRun",seasonArrayList.get(position).longestRun);
                startActivity(intent);
            }
        });
    }
}

//This class is each item in the list class which in our case is seasons. this is a dummy class just to have items You should use seasons in the code not ListItem class.
class DummySeason {
    String seasonName;
    int days;
    int runs;
    int resort;
    int vertical_m;
    double distance;
    String active;
    double max_Speed;
    int max_alt;
    int tallestRun;
    double longestRun;

    public DummySeason(String seasonName, int days, int runs, int resort, int vertical_m, double distance, String active, double max_Speed, int max_alt, int tallestRun, double longestRun) {
        this.seasonName = seasonName;
        this.days = days;
        this.runs = runs;
        this.resort = resort;
        this.vertical_m = vertical_m;
        this.distance = distance;
        this.active = active;
        this.max_Speed = max_Speed;
        this.max_alt = max_alt;
        this.tallestRun = tallestRun;
        this.longestRun = longestRun;
    }
}