package guice.learningtest;

import static org.junit.Assert.*;

import org.junit.Test;
import com.google.inject.Module;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class GuiceFieldAndMethodInjectionLearningTest {

	/*
	 * We will use this test class for learning inject fields
	 */
	private static interface IMyService {
	}

	private static class MyService implements IMyService {
	}

	private static class MyClientWithInjectedField {
		@Inject
		IMyService service;
	}

	private static class MyClientWithInjectedMethod {
		IMyService service;

		/*
		 * use @Inject(optional=true). This allows you to use a dependency when it
		 * exists and to fall back to a default otherwise.
		 */
		@Inject(optional = true)
		public void init(IMyService service) {
			this.service = service;
		}
	}

	@Test
	public void fieldAndMethodInjection() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClientWithInjectedField client1 = injector.getInstance(MyClientWithInjectedField.class);
		MyClientWithInjectedMethod client2 = injector.getInstance(MyClientWithInjectedMethod.class);
		assertNotNull(client1.service);
		assertNotNull(client2.service);
	}

	// let's do the same but with another syntax
	@Test
	public void injectMembers() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClientWithInjectedField client1 = new MyClientWithInjectedField();
		injector.injectMembers(client1);
		MyClientWithInjectedMethod client2 = new MyClientWithInjectedMethod();
		injector.injectMembers(client2);
		assertNotNull(client1.service);
		assertNotNull(client2.service);
	}

	/*
	 * In general, constructor injection is to be preferred, since it allows you to
	 * declare final fields. Moreover, it allows you to create instances directly,
	 * without Guice, and this might be very useful in testing, using mocking, for
	 * example. However, constructor injection cannot be optional. Method injection
	 * is useful when you need to create an instance manually, not with Guice. It is
	 * also useful for optional or changeable dependencies. Field injection has the
	 * most compact syntax, but you will be able to create instances only with Guice
	 * (or with reflection)
	 */
}
