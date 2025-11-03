package guice.learningtest;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;

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

	// Windows doesn’t care about case when looking up files.!!!!!!!!!!!
	@Test
	public void providerBinding() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				// provider accepts a functional interface so we can use a lambda
				bind(File.class).toProvider(() -> new File("src/test/resources/aFile.txt"));
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper fileWrapper = injector.getInstance(MyFileWrapper.class);
		assertThat(fileWrapper.file.exists()).isTrue();
	}

	/*
	 * In case we need multiple bindings for the same type we can use a built-in
	 * Guice binding annotation like @Named
	 */
	private static class MyFileWrapper2 {
		File file;

		@Inject
		public MyFileWrapper2(@Named("PATH") String path, @Named("NAME") String name) {
			file = new File(path, name);
		}
	}

	@Test
	public void bindingAnnotations() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				// declared with annotedWith is not that fine as we can misspell them
				// the next test will try to solve this problem
				bind(String.class).annotatedWith(Names.named("PATH")).toInstance("src/test/resources");
				bind(String.class).annotatedWith(Names.named("NAME")).toInstance("aFile.txt");
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper2 fileWrapper = injector.getInstance(MyFileWrapper2.class);
		assertThat(fileWrapper.file.exists()).isTrue();
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	private static @interface FilePath {
	}

	@BindingAnnotation
	@Target({ FIELD, PARAMETER, METHOD })
	@Retention(RUNTIME)
	private static @interface FileName {
	}

	private static class MyFileWrapper3 {
		File file;

		@Inject
		public MyFileWrapper3(@FilePath String path, @FileName String name) {
			file = new File(path, name);
		}
	}

	@Test
	public void customBindingAnnotations() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(String.class).annotatedWith(FilePath.class).toInstance("src/test/resources");
				bind(String.class).annotatedWith(FileName.class).toInstance("aFile.txt");
			}
		};
		Injector injector = Guice.createInjector(module);
		MyFileWrapper3 fileWrapper = injector.getInstance(MyFileWrapper3.class);
		assertThat(fileWrapper.file.exists()).isTrue();
	}

	private static class MyClient {
		MyService service;
		
		@Inject
		public MyClient(MyService service) {
			this.service = service;
		}
	}

	
	// custom bindings override
	// use Modules.override(new DefaultModule()).with(new CustomModule())
	@Test
	public void modulesOverride() {
		Module defaultModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(defaultModule);
		MyClient client1 = injector.getInstance(MyClient.class);
		MyClient client2 = injector.getInstance(MyClient.class);
		// not singleton
		assertThat(client1.service).isNotEqualTo(client2.service);
		Module customModule = new AbstractModule() {
			@Override
			protected void configure() {
				bind(MyService.class).in(Singleton.class);
			}
		};
		injector = Guice.createInjector(Modules.override(defaultModule).with(customModule)); // override module
		client1 = injector.getInstance(MyClient.class);
		client2 = injector.getInstance(MyClient.class);
		assertThat(client1.service).isNotNull();
		// now it is singleton
		assertThat(client1.service).isEqualTo(client2.service);
	}

}
