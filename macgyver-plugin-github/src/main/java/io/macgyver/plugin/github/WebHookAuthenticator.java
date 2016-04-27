package io.macgyver.plugin.github;

import java.util.Optional;

public interface WebHookAuthenticator {

	public Optional<Boolean> authenticate(GitHubWebHookMessage message );
}
