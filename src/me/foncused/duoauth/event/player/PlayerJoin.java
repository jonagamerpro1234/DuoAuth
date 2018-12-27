package me.foncused.duoauth.event.player;

import me.foncused.duoauth.config.ConfigManager;
import me.foncused.duoauth.database.AuthDatabase;
import me.foncused.duoauth.lib.aikar.TaskChainManager;
import me.foncused.duoauth.utility.AuthUtilities;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerJoin implements Listener {

	private final Map<UUID, Boolean> players;
	private final ConfigManager cm;
	private final AuthDatabase db;

	public PlayerJoin(final Map<UUID, Boolean> players, final ConfigManager cm, final AuthDatabase db) {
		this.players = players;
		this.cm = cm;
		this.db = db;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		TaskChainManager.newChain()
				.asyncFirst(() -> this.db.contains(uuid))
				.syncLast(contained -> {
					if(contained) {
						TaskChainManager.newChain()
								.asyncFirst(() -> this.db.readAuthed(uuid))
								.syncLast(authed -> this.players.put(uuid, authed))
								.execute();
					} else if(player.hasPermission("duoauth.enforced")) {
						this.players.put(uuid, false);
						final String ip = AuthUtilities.getPlayerAddress(player);
						TaskChainManager.newChain()
								.asyncFirst(() -> this.db.write(
										uuid,
										this.cm.getPasswordDefault(),
										this.cm.getPinDefault(),
										0,
										ip
								))
								.syncLast(written -> {
									AuthUtilities.alertOne(player, ChatColor.RED + "The server administrator has required you to set up authentication. Please enter the command '/auth <password> <pin>' using the credentials given to you, and then use '/auth reset' to set your own credentials. Thank you!");
									final String name = player.getName();
									final String u = uuid.toString();
									AuthUtilities.notify(
											written ?
													"User " + u + "(" + name + ") has 'duoauth.enforced' and setup of default authentication was successful" :
													"User " + u + "(" + name + ") has 'duoauth.enforced' but setup of default authentication has failed"
									);
								})
								.execute();
					} else {
						this.players.put(uuid, true);
					}
				})
				.execute();
	}

}
