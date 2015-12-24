package org.faudroids.werwolf.ui;

import android.os.Bundle;
import android.widget.TextView;

import org.faudroids.werwolf.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity {

	@InjectView(R.id.txt_hello) private TextView helloView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helloView.setText("hello world");
	}
}
