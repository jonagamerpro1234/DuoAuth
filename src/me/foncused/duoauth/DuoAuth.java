package me.foncused.duoauth;

import co.aikar.taskchain.TaskChain;
import me.foncused.duoauth.command.AuthCommand;
import me.foncused.duoauth.database.AuthDatabase;
import me.foncused.duoauth.enumerable.DatabaseOption;
import me.foncused.duoauth.event.auth.Auth;
import me.foncused.duoauth.event.player.AsyncPlayerPreLogin;
import me.foncused.duoauth.event.player.PlayerJoin;
import me.foncused.duoauth.event.player.PlayerLogin;
import me.foncused.duoauth.event.player.PlayerQuit;
import me.foncused.duoauth.lib.aikar.TaskChainManager;
import me.foncused.duoauth.runnable.AuthRunnable;
import me.foncused.duoauth.utility.AuthUtilities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DuoAuth extends JavaPlugin {

	private Map<String, Boolean> players = Collections.synchronizedMap(new HashMap<>());
	private FileConfiguration config;
	private AuthDatabase db;

	@Override
	public void onEnable() {
		this.initialize();
		this.register();
	}

	// Config, Database, and Libraries
	private void initialize() {
		this.registerConfig();
		this.registerDatabase();
		this.registerLibraries();
	}

	private void registerConfig() {
		this.saveDefaultConfig();
		this.config = this.getConfig();
	}

	private void registerDatabase() {
		final String database = this.config.getString("database");
		DatabaseOption option;
		try {
			option = DatabaseOption.valueOf(database.toUpperCase());
		} catch(final IllegalArgumentException e) {
			AuthUtilities.consoleWarning("Database option set to " + database + " is not safe, reverting...");
			option = DatabaseOption.JSON;
		}
		AuthUtilities.console("Database option set to " + option.toString());
		this.db = new AuthDatabase(this, this.players, option);
	}

	private void registerLibraries() {
		new TaskChainManager(this);
	}

	// Utilities, Runnables, Events, and Commands
	private void register() {
		// Utilities
		int costFactor = this.config.getInt("cost-factor");
		if(costFactor < 12) {
			AuthUtilities.consoleWarning("Bcrypt cost factor set to " + costFactor + " is too low, reverting to minimum...");
			costFactor = 12;
		} else if(costFactor > 30) {
			AuthUtilities.consoleWarning("Bycrypt cost factor set to " + costFactor + " is too high, reverting to maximum...");
			costFactor = 30;
		}
		AuthUtilities.console("Bcrypt cost factor set to " + costFactor);
		new AuthUtilities(costFactor);
		// Runnables
		int deauthTimeout = this.config.getInt("deauth.timeout");
		if(deauthTimeout <= 0) {
			AuthUtilities.consoleWarning("Deauth timeout set to " + deauthTimeout + " hours is not safe, reverting to default...");
			deauthTimeout = 48;
		}
		AuthUtilities.console("Deauth timeout set to " + deauthTimeout + " hours");
		int deauthTimeoutCheckInterval = this.config.getInt("deauth.timeout-check-interval");
		if(deauthTimeoutCheckInterval <= 0) {
			AuthUtilities.consoleWarning("Deauth timeout check interval set to " + deauthTimeoutCheckInterval + " minutes is not safe, reverting to default...");
			deauthTimeoutCheckInterval = 5;
		}
		AuthUtilities.console("Deauth timeout check interval set to " + deauthTimeoutCheckInterval + " minutes");
		final boolean timeoutOnline = this.config.getBoolean("deauth.timeout-online");
		AuthUtilities.console(timeoutOnline ? "Deauth timeout online mode activated" : "Deauth timeout online mode deactivated");
		new AuthRunnable(
				this.players,
				this,
				this.db,
				deauthTimeout,
				deauthTimeoutCheckInterval,
				timeoutOnline
		).runTimeoutTask();
		// Events
		final PluginManager pm = Bukkit.getPluginManager();
		final PlayerLogin pl = new PlayerLogin(true);
		pm.registerEvents(pl, this);
		int commandAttempts = this.config.getInt("command.attempts");
		if(commandAttempts <= 0) {
			AuthUtilities.consoleWarning("Maximum authentication attempts set to " + commandAttempts + " is not safe, reverting to default...");
			commandAttempts = 5;
		}
		AuthUtilities.console("Maximum authentication attempts set to " + commandAttempts);
		final boolean deauthAddressChanges = this.config.getBoolean("deauth.ip-changes");
		AuthUtilities.console(deauthAddressChanges ? "Deauth timeout IP address change mode activated" : "Deauth timeout IP address change mode deactivated");
		final AsyncPlayerPreLogin appl = new AsyncPlayerPreLogin(this.db, commandAttempts, deauthAddressChanges);
		pm.registerEvents(appl, this);
		pm.registerEvents(new Auth(this.players), this);
		final TaskChain chain = TaskChainManager.newChain();
		chain
				.sync(() -> {
					chain.setTaskData("password", this.config.getString("password.default"));
					String pin = this.config.getString("pin.default");
					if(!(pin.matches("^[0-9]+$"))) {
						AuthUtilities.consoleWarning("Default PIN set to " + pin + " is not numeric, reverting to default...");
						pin = "1234";
					}
					chain.setTaskData("pin", pin);
				})
				.async(() -> {
					final String pwhash = AuthUtilities.getSecureBCryptHash((String) chain.getTaskData("password"));
					AuthUtilities.console("Default password hash for 'duoauth.enforced' is " + pwhash);
					chain.setTaskData("pwhash", pwhash);
					final String pinhash = AuthUtilities.getSecureBCryptHash((String) chain.getTaskData("pin"));
					AuthUtilities.console("Default PIN hash for 'duoauth.enforced' is " + pinhash);
					chain.setTaskData("pinhash", pinhash);
				})
				.sync(() -> {
					pm.registerEvents(
							new PlayerJoin(
									this.players,
									this.db,
									(String) chain.getTaskData("pwhash"),
									(String) chain.getTaskData("pinhash")
							),
							this
					);
					pm.registerEvents(new PlayerQuit(this.players), this);
					// Commands
					int commandCooldown = this.config.getInt("command.cooldown");
					if(commandCooldown <= 0) {
						AuthUtilities.consoleWarning("Command cooldown time set to " + commandCooldown + " seconds is not safe, reverting to default...");
						commandCooldown = 20;
					}
					AuthUtilities.console("Command cooldown time set to " + commandCooldown + " seconds");
					int passwordMinLength = this.config.getInt("password.min-length");
					if(passwordMinLength <= 0) {
						AuthUtilities.consoleWarning("Minimum password length set to " + passwordMinLength + " is not safe, reverting to default...");
						passwordMinLength = 8;
					}
					AuthUtilities.console("Minimum password length set to " + passwordMinLength);
					final boolean passwordBothCases = this.config.getBoolean("password.both-cases");
					AuthUtilities.console(passwordBothCases ? "Both cases required" : "Both cases not required");
					final boolean passwordNumbers = this.config.getBoolean("password.numbers");
					AuthUtilities.console(passwordNumbers ? "Numbers required" : "Numbers not required");
					final boolean passwordSpecialChars = this.config.getBoolean("password.special-chars");
					AuthUtilities.console(passwordSpecialChars ? "Special characters required" : "Special characters not required");
					int pinMinLength = this.config.getInt("pin.min-length");
					if(pinMinLength <= 0) {
						AuthUtilities.consoleWarning("Minimum PIN length set to " + pinMinLength + " is not safe, reverting to default...");
						pinMinLength = 4;
					}
					AuthUtilities.console("Minimum PIN length set to " + pinMinLength);
					this.getCommand("auth").setExecutor(
							new AuthCommand(
									this,
									this.players,
									this.db,
									commandCooldown,
									appl.getCommandAttempts(),
									passwordMinLength,
									passwordBothCases,
									passwordNumbers,
									passwordSpecialChars,
									pinMinLength
							)
					);
					pl.setLoading(false);
				})
				.execute();
	}

}