package io.macgyver.plugin.github;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.kohsuke.github.GHRepository;

import com.google.common.base.Preconditions;

public class GitHubUtil {

	/**
	 * github-api does not provide a way to set the default branch until 1.69.  
	 * GHRepository.edit() can do it, but is private.  This is a simple workaround
	 * that uses reflection to work around the private limitation.
	 * 
	 * @param repo
	 * @param branchName
	 * @throws IOException
	 */
	public static void setDefaultBranch(GHRepository repo, String branchName)
			throws IOException {
		Preconditions.checkNotNull(repo);
		Preconditions.checkNotNull(branchName);

		try {
			Method editMethod = repo.getClass().getDeclaredMethod("edit",
					String.class, String.class);
			editMethod.setAccessible(true);

			editMethod.invoke(repo, "default_branch", branchName);
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			throw new IOException(e);
		}

	}
}
