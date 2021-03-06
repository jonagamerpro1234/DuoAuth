package me.foncused.duoauth.spigot.config;

public class LangManager {

	private final String allowAdminSuccess;
	private final String authenticating;
	private final String authenticatingFailed;
	private final String authenticatingSuccess;
	private final String authInProgress;
	private final String authInProgressAdmin;
	private final String bug;
	private final String codeInvalid;
	private final String deauthAdminSuccess;
	private final String deauthFailed;
	private final String deauthSuccess;
	private final String enforced;
	private final String generate;
	private final String generating;
	private final String kicked;
	private final String loading;
	private final String locked;
	private final String mustWait;
	private final String noConsole;
	private final String noGenerate;
	private final String noPermission;
	private final String playerNotAuthed;
	private final String playerNotDb;
	private final String pleaseSaveQr;
	private final String prefixAlert;
	private final String prefixNotify;
	private final String resetAdminSuccess;
	private final String resetFailed;
	private final String resetSuccess;
	private final String sessionExpired;
	private final String settingUp;
	private final String settingUpFailed;
	private final String settingUpSuccess;

	public LangManager(
		final String allowAdminSuccess,
		final String authenticating,
		final String authenticatingFailed,
		final String authenticatingSuccess,
		final String authInProgress,
		final String authInProgressAdmin,
		final String bug,
		final String codeInvalid,
		final String deauthAdminSuccess,
		final String deauthFailed,
		final String deauthSuccess,
		final String enforced,
		final String generate,
		final String generating,
		final String kicked,
		final String loading,
		final String locked,
		final String mustWait,
		final String noConsole,
		final String noGenerate,
		final String noPermission,
		final String playerNotAuthed,
		final String playerNotDb,
		final String pleaseSaveQr,
		final String prefixAlert,
		final String prefixNotify,
		final String resetAdminSuccess,
		final String resetFailed,
		final String resetSuccess,
		final String sessionExpired,
		final String settingUp,
		final String settingUpFailed,
		final String settingUpSuccess
	) {
		this.allowAdminSuccess = allowAdminSuccess;
		this.authenticating = authenticating;
		this.authenticatingFailed = authenticatingFailed;
		this.authenticatingSuccess = authenticatingSuccess;
		this.authInProgress = authInProgress;
		this.authInProgressAdmin = authInProgressAdmin;
		this.bug = bug;
		this.codeInvalid = codeInvalid;
		this.deauthAdminSuccess = deauthAdminSuccess;
		this.deauthFailed = deauthFailed;
		this.deauthSuccess = deauthSuccess;
		this.enforced = enforced;
		this.generate = generate;
		this.generating = generating;
		this.kicked = kicked;
		this.loading = loading;
		this.locked = locked;
		this.mustWait = mustWait;
		this.noConsole = noConsole;
		this.noGenerate = noGenerate;
		this.noPermission = noPermission;
		this.playerNotAuthed = playerNotAuthed;
		this.playerNotDb = playerNotDb;
		this.pleaseSaveQr = pleaseSaveQr;
		this.prefixAlert = prefixAlert;
		this.prefixNotify = prefixNotify;
		this.resetAdminSuccess = resetAdminSuccess;
		this.resetFailed = resetFailed;
		this.resetSuccess = resetSuccess;
		this.sessionExpired = sessionExpired;
		this.settingUp = settingUp;
		this.settingUpFailed = settingUpFailed;
		this.settingUpSuccess = settingUpSuccess;
	}

	public synchronized String getAllowAdminSuccess() {
		return this.allowAdminSuccess;
	}

	public synchronized String getAuthenticating() {
		return this.authenticating;
	}

	public synchronized String getAuthenticatingFailed() {
		return this.authenticatingFailed;
	}

	public synchronized String getAuthenticatingSuccess() {
		return this.authenticatingSuccess;
	}

	public synchronized String getAuthInProgress() {
		return this.authInProgress;
	}

	public synchronized String getAuthInProgressAdmin() {
		return this.authInProgressAdmin;
	}

	public synchronized String getBug() {
		return this.bug;
	}

	public synchronized String getCodeInvalid() {
		return this.codeInvalid;
	}

	public synchronized String getDeauthAdminSuccess() {
		return this.deauthAdminSuccess;
	}

	public synchronized String getDeauthFailed() {
		return this.deauthFailed;
	}

	public synchronized String getDeauthSuccess() {
		return this.deauthSuccess;
	}

	public synchronized String getEnforced() {
		return this.enforced;
	}

	public synchronized String getGenerate() {
		return this.generate;
	}

	public synchronized String getGenerating() {
		return this.generating;
	}

	public synchronized String getKicked() {
		return this.kicked;
	}

	public synchronized String getLoading() {
		return this.loading;
	}

	public synchronized String getLocked() {
		return this.locked;
	}

	public synchronized String getMustWait() {
		return this.mustWait;
	}

	public synchronized String getNoConsole() {
		return this.noConsole;
	}

	public synchronized String getNoGenerate() {
		return this.noGenerate;
	}

	public synchronized String getNoPermission() {
		return this.noPermission;
	}

	public synchronized String getPlayerNotAuthed() {
		return this.playerNotAuthed;
	}

	public synchronized String getPlayerNotDb() {
		return this.playerNotDb;
	}

	public synchronized String getPleaseSaveQr() {
		return this.pleaseSaveQr;
	}

	public synchronized String getPrefixAlert() {
		return this.prefixAlert;
	}

	public synchronized String getPrefixNotify() {
		return this.prefixNotify;
	}

	public synchronized String getResetAdminSuccess() {
		return this.resetAdminSuccess;
	}

	public synchronized String getResetFailed() {
		return this.resetFailed;
	}

	public synchronized String getResetSuccess() {
		return this.resetSuccess;
	}

	public synchronized String getSessionExpired() {
		return this.sessionExpired;
	}

	public synchronized String getSettingUp() {
		return this.settingUp;
	}

	public synchronized String getSettingUpFailed() {
		return this.settingUpFailed;
	}

	public synchronized String getSettingUpSuccess() {
		return this.settingUpSuccess;
	}

}
