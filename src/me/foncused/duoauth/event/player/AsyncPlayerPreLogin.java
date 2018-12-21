package me.foncused.duoauth.event.player;

import me.foncused.duoauth.database.AuthDatabase;
import me.foncused.duoauth.enumerable.AuthMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER;

public class AsyncPlayerPreLogin implements Listener {

	private AuthDatabase db;
	private int commandAttempts;
	private boolean deauthAddressChanges;

	public AsyncPlayerPreLogin(final AuthDatabase db, final int commandAttempts, final boolean deauthAddressChanges) {
		this.db = db;
		this.commandAttempts = commandAttempts;
		this.deauthAddressChanges = deauthAddressChanges;
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
		final String uuid = event.getUniqueId().toString();
		if(this.db.contains(uuid)) {
			if(this.db.readAttempts(uuid) >= this.commandAttempts) {
				event.disallow(KICK_OTHER, AuthMessage.LOCKED.toString());
				return;
			}
			final String ip = event.getAddress().getHostAddress();
			if(deauthAddressChanges && (!(this.db.readAddress(uuid).equals(ip)))) {
				this.db.writeAuthed(uuid, false);
			}
		}
	}

	public int getCommandAttempts() {
		return this.commandAttempts;
	}

}