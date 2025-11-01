package guice.learningtest;

import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.junit.Test;

import com.google.inject.Module;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class GuiceProviderLearningTest {

	/*
	 * If we need to control the creation of a dependency of a type T, instead of
	 * injecting an instance of type T, we can inject a
	 * com.google.inject.Provider<T> and use its method get to create an instance of
	 * the dependency
	 */
	private static interface IMyService {
	}

	private static class MyService implements IMyService {
	}

	private static class MyClientWithInjectedProvider {
		@Inject
		Provider<IMyService> serviceProvider;

		IMyService getService() {
			return serviceProvider.get();
		}
	}

	@Test
	public void injectProviderExample() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {

				// if we can't use notation @Inject for example on a third part library we can
				// use
				// bind(Type.class).toProvider(.../* provider for Type */...)
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClientWithInjectedProvider client = injector.getInstance(MyClientWithInjectedProvider.class);
		assertThat(client.getService()).isNotNull();
	}

	// this can be useful for reading a file
	private static class MyFileWrapper {
		@Inject
		File file;
	}

	@Test
	public void providerBinding() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				// provider accepts a functional interface so we can use a lambda
				bind(File.class).toProvider(() -> new File("src/test/resources/afile.txt"));
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper fileWrapper = injector.getInstance(MyFileWrapper.class);
		assertThat(fileWrapper.file.exists()).isTrue();
	}
}
