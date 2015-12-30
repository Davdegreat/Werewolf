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

import java.util.List;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_players_overview)
public class PlayersOverviewActivity extends AbstractActivity {

	@InjectView(R.id.players_list) private ListView playersListView;

	@Inject private GameManager gameManager;

	public PlayersOverviewActivity() {
		super(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<Player> players = gameManager.loadPlayers();
		PlayersAdapter adapter = new PlayersAdapter(this, players);
		playersListView.setAdapter(adapter);
	}


	private class PlayersAdapter extends ArrayAdapter<Player> {

		private final List<Player> players;

		public PlayersAdapter(Context context, List<Player> players) {
			super(context, R.layout.list_item_player);
			this.players = players;
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
					startActivity(showRoleIntent);
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
