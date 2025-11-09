package guice.learningtest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;

public class GuiceLearningTest {
	private static class MyService {
	}

	private static class MyClient {
		MyService service;

		// we want to inject this using guice
		/*
		 * Guice will be able to create an instance of MyClient and to figure out how to
		 * inject into its constructor annotated with @Inject an instance of MyService.
		 * If MyService had further dependencies, Guice would first resolve those
		 * dependencies, before passing the resulting object into MyClient’s constructor
		 */
		@Inject
		public MyClient(MyService service) {
			this.service = service;
		}
	}

	@Test
	public void canInstantiateConcreteClassesWithoutConfiguration() {
		// Without any further configuration,
		// Guice can instantiate a MyService (and inject it into a MyClient)

		// this is simple because we used concrete methods, for abstract we need to do
		// something more
		AbstractModule module = new AbstractModule() {
		};
		Injector injector = Guice.createInjector(module); // createInjector needs an AbstractModule

		// By default, Guice returns a new instance each time it supplies a value.
		MyClient client = injector.getInstance(MyClient.class);
		assertNotNull(client.service);
	}

	// TEST ON ABSTRACT TYPES
	private static interface IMyService {
	}

	private static class MyService2 implements IMyService {
	}

	private static class MyGenericClient {
		IMyService service;

		@Inject
		public MyGenericClient(IMyService service) {
			this.service = service;
		}
	}

	/*
	 * | Binding Type | Behavior | Result | |
	 * ----------------------------------------------- |
	 * ---------------------------------------------------------- |
	 * ------------------------ | | `bind(A.class).to(B.class)` | Creates a **new
	 * instance** of `B` each time, unless scoped | Different instances | |
	 * `bind(A.class).toInstance(new B())` | Uses a **single provided instance** |
	 * Same instance every time | | `bind(A.class).to(B.class).in(Singleton.class)`
	 * | Guice creates **one shared instance** of `B` | Same instance every time |
	 */

	/*
	 * In our module, we must implement the method configure and specify the
	 * bindings: a binding is meant to bind an abstract type to a concrete type. In
	 * the method configure bindings are specified using the fluent API of Guice,
	 */
	@Test
	public void injectAbstractType() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				/*
				 * Guice knows that every time someone asks for an IMyService, it should create
				 * a new instance of MyService2, unless you specify a scope (like @Singleton or
				 * .in(Singleton.class)).
				 */
				bind(IMyService.class).to(MyService2.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyGenericClient client = injector.getInstance(MyGenericClient.class);
		assertNotNull(client.service);
	}

	@Test
	public void bindToInstance() {
		Module module = new AbstractModule() {
			/*
			 * Here we're not telling Guice how to create an instance, we're giving it one
			 * specific instance. That means Guice will use that same single object every
			 * time an IMyService is injected.
			 */
			@Override
			protected void configure() {
				/*
				 * with .toInstance(new MyService2()), you own the instance. (you can manage it
				 * call methods and so on) With .in(Singleton.class), Guice owns the instance.
				 * With toInstance you bind a type on a specific instance of that type
				 */
				bind(IMyService.class).toInstance(new MyService2());
			}
		};
		Injector injector = Guice.createInjector(module);
		MyGenericClient client1 = injector.getInstance(MyGenericClient.class);
		MyGenericClient client2 = injector.getInstance(MyGenericClient.class);
		assertNotNull(client1.service);
		assertSame(client1.service, client2.service);
	}

	/*
	 * WARNING! singleton in Guice means the same instance per injector.
	 */

	@Test
	public void bindToSingleton() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyService.class).to(MyService2.class);
				bind(MyService.class).in(Singleton.class);
			}
		};
		Injector injector = Guice.createInjector(module);
		MyClient client1 = injector.getInstance(MyClient.class);
		MyClient client2 = injector.getInstance(MyClient.class);
		assertNotNull(client1.service);

		// this is true because getInstance returns always the same instance of the same
		// injector since the singleton is used
		assertSame(client1.service, client2.service);
	}

	@Test
	public void singletonPerInjector() {
		/*
		 * IMPORTANT! This tests succeeds because singleton scope in Guice is per
		 * Injector, not global or static.
		 */
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				/*
				 * Apply this to implementation classes when you want only one instance (per
				 * Injector) to be reused for all injections for that binding.
				 */
				bind(IMyService.class).to(MyService2.class).in(Singleton.class);
			}
		};
		assertNotSame(Guice.createInjector(module).getInstance(MyClient.class).service,
				Guice.createInjector(module).getInstance(MyClient.class).service);
	}
}
