package org.faudroids.werewolf.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.faudroids.werewolf.R;
import org.faudroids.werewolf.core.GameManager;
import org.faudroids.werewolf.core.Player;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_players_overview)
public class PlayersOverviewActivity extends AbstractActivity {

	private static final int REQUEST_SHOW_PLAYER = 42;

	@InjectView(R.id.players_list) private ListView playersListView;
	private PlayersAdapter playersAdapter;

	@Inject private GameManager gameManager;

	public PlayersOverviewActivity() {
		super(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		playersAdapter = new PlayersAdapter(this);
		playersListView.setAdapter(playersAdapter);
		playersAdapter.setPlayers(gameManager.loadPlayers());

		if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.all_players);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_SHOW_PLAYER:
				playersAdapter.setPlayers(gameManager.loadPlayers());
				return;
		}
	}

	private class PlayersAdapter extends ArrayAdapter<Player> {

		private final List<Player> players = new ArrayList<>();

		public PlayersAdapter(Context context) {
			super(context, R.layout.list_item_player);
		}

		public void setPlayers(List<Player> players) {
			this.players.clear();
			this.players.addAll(players);
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Player player = getItem(position);
			View view = getLayoutInflater().inflate(R.layout.list_item_player, parent, false);
			((TextView) view.findViewById(R.id.player_name_txt)).setText(player.getName());

			// show player role on click
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent showRoleIntent = new Intent(PlayersOverviewActivity.this, ShowRolesActivity.class);
					showRoleIntent.putExtra(ShowRolesActivity.EXTRA_PLAYER, player);
					startActivityForResult(showRoleIntent, REQUEST_SHOW_PLAYER);
				}
			});

			return view;
		}

		@Override
		public int getCount() {
			return players.size();
		}

		@Override
		public Player getItem(int position) {
			return players.get(position);
		}
	}

}
