package guice.learningtest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class GuiceAssistedInjectLearningTest {

	/*
	 * There might be cases when we want a few dependencies to be injected by Guice
	 * and other dependencies to be provided directly when creating an instance
	 */

	// view
	private static interface IMyView {
	}

	private static class MyView implements IMyView {
	}

	private static interface IMyRepository {
	}

	private static class MyRepository implements IMyRepository {
	}

	private static interface IMyController {
	}

	private static class MyController implements IMyController {
		IMyView view;
		IMyRepository repository;

		@Inject
		public MyController(@Assisted IMyView view, // from the instance's creator
				IMyRepository repository // from the Injector
		) {
			this.view = view;
			this.repository = repository;
		}
	}

	/*
	 * The Guice AssistedInject mechanism automatically maps the factory’s create
	 * method’s parameters to the corresponding @Assisted parameters in the
	 * implementation class’ constructor. Other constructor arguments will be
	 * injected as usual. It is then enough to use the Guice API using
	 * FactoryModuleBuilder to have Guice implement a factory implementation
	 * automatically So IMyView view of MyControllerFactory is binded to @Assisted
	 * IMyView view MyController class
	 */

	// factory method to create controller instances
	private static interface MyControllerFactory {
		IMyController create(IMyView view);
	}

	@Test
	public void assistedInject() {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				bind(IMyRepository.class).to(MyRepository.class); // this is the injector that will inject the second
				// parameter of MyController class
				install(new FactoryModuleBuilder().implement(IMyController.class, MyController.class) // returns both
						// IMyController
						// and MyControl
						.build(MyControllerFactory.class));
			}
		};
		Injector injector = Guice.createInjector(module);
		MyControllerFactory controllerFactory = injector.getInstance(MyControllerFactory.class);
		MyController controller = (MyController) controllerFactory.create(new MyView()); // casting needed as it returns
		// IMyController

		assertThat(controller.view).isNotNull();
		assertThat(controller.repository).isNotNull();
	}
}
