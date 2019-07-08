package me.foncused.duoauth.event.player;

import co.aikar.taskchain.TaskChain;
import me.foncused.duoauth.DuoAuth;
import me.foncused.duoauth.cache.AuthCache;
import me.foncused.duoauth.config.ConfigManager;
import me.foncused.duoauth.config.LangManager;
import me.foncused.duoauth.database.AuthDatabase;
import me.foncused.duoauth.enumerable.DatabaseProperty;
import me.foncused.duoauth.lib.aikar.TaskChainManager;
import me.foncused.duoauth.util.AuthUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class PlayerJoin implements Listener {

	private final DuoAuth plugin;
	private final ConfigManager cm;
	private final LangManager lm;
	private final AuthDatabase db;

	public PlayerJoin(final DuoAuth plugin) {
		this.plugin = plugin;
		this.cm = this.plugin.getConfigManager();
		this.lm = this.plugin.getLangManager();
		this.db = this.plugin.getDatabase();
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final String name = player.getName();
		TaskChainManager.newChain()
				.asyncFirst(() -> this.db.contains(uuid))
				.syncLast(contained -> {
					if(contained) {
						final TaskChain chain = TaskChainManager.newChain();
						chain
								.async(() -> {
									chain.setTaskData("password", db.readProperty(uuid, DatabaseProperty.PASSWORD).getAsString());
									chain.setTaskData("pin", db.readProperty(uuid, DatabaseProperty.PIN).getAsString());
									chain.setTaskData("authed", db.readProperty(uuid, DatabaseProperty.AUTHED).getAsBoolean());
									chain.setTaskData("attempts", db.readProperty(uuid, DatabaseProperty.ATTEMPTS).getAsInt());
									try {
										chain.setTaskData("ip", InetAddress.getByName(db.readProperty(uuid, DatabaseProperty.IP).getAsString()));
									} catch(final UnknownHostException e) {
										e.printStackTrace();
									}
								})
								.sync(() -> {
									final AuthCache cache =
											new AuthCache(
													(String) chain.getTaskData("password"),
													(String) chain.getTaskData("pin"),
													(boolean) chain.getTaskData("authed"),
													(int) chain.getTaskData("attempts"),
													(InetAddress) chain.getTaskData("ip")
									);
									this.log(name, cache);
									this.plugin.setAuthCache(uuid, cache);
								})
								.execute();
					} else if(player.hasPermission("duoauth.enforced")) {
						final InetAddress ip = AuthUtil.getPlayerAddress(player);
						final String password = this.cm.getPasswordDefault();
						final String pin = this.cm.getPinDefault();
						TaskChainManager.newChain()
								.asyncFirst(() -> this.db.write(
										uuid,
										password,
										pin,
										false,
										0,
										ip
								))
								.syncLast(written -> {
									final AuthCache cache = new AuthCache(
											password,
											pin,
											false,
											0,
											ip
									);
									this.log(name, cache);
									this.plugin.setAuthCache(uuid, cache);
									AuthUtil.alertOne(player, this.lm.getEnforced());
									final String u = uuid.toString();
									AuthUtil.notify(
											written
													? "User " + u + " (" + name + ") has 'duoauth.enforced' and setup of default authentication was successful"
													: "User " + u + " (" + name + ") has 'duoauth.enforced' but setup of default authentication has failed"
									);
								})
								.execute();
					}
				})
				.execute();
	}

	private void log(final String name, final AuthCache cache) {
		AuthUtil.console(
				ChatColor.GOLD + "Player: " + ChatColor.GRAY + name + ChatColor.GOLD + ", " +
						"Authed: " + ChatColor.GRAY + cache.isAuthed() + ChatColor.GOLD + ", " +
						"Attempts: " + ChatColor.GRAY + cache.getAttempts() + ChatColor.GOLD + ", " +
						"IP: " + ChatColor.GRAY + cache.getIp().getHostAddress()
		);
	}

}