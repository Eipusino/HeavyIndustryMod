package heavyindustry.util.aspect;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The section manager stores all sections and their trigger controllers, serving as the entry point for adding/
 * removing sections. It is typically used to create a common singleton,
 * Or the default singleton obtained from the static factory<strong>{@code AspectManager.getDefault()}</strong>.
 * <p>Between the section managers, the saved sections and trigger controllers are relatively independent. If you are unsure when to create a new manager, you should use the default singleton instead of creating a new section manager.
 * Custom trigger controllers can be added through method {@code void addTriggerControl(BaseTriggerControl<?> control)}.
 * <p>Regarding the declaration of triggering the controller, see: {@link BaseTriggerControl}
 *
 * @since 1.0.8
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class AspectManager {
	private static final AspectManager defaultInstance = new AspectManager();

	private final HashMap<Class<?>, BaseTriggerControl> controls = new HashMap<>();
	private final HashSet<AbstractAspect<?, ?>> aspects = new HashSet<>();

	/** Construct a section manager without carrying any trigger controllers. */
	public AspectManager() {}

	/** Get the default singleton for the section manager. */
	public static AspectManager getDefault() {
		return defaultInstance;
	}

	/**
	 * Add a trigger controller, if the type of trigger controller already exists, it will overwrite the original
	 * controller.
	 * <p>Regarding the trigger controller, see {@link BaseTriggerControl}.
	 *
	 * @param control The trigger controller to be added
	 */
	public void addTriggerControl(BaseTriggerControl<?> control) {
		controls.put(control.getClass(), control);
	}

	/**
	 * Add a section to the section manager and assign its trigger entry.
	 * <p>If the section already exists, no action will be taken.
	 */
	public <T extends AbstractAspect<?, ?>> T addAspect(T aspect) {
		if (aspects.add(aspect)) {
			aspect.apply = entry -> controls.get(entry.controlType).apply(entry);
			aspect.remove = entry -> controls.get(entry.controlType).remove(entry);
			for (BaseTriggerEntry entry : aspect.triggers) {
				aspect.apply(entry);
			}
		}
		return aspect;
	}

	/**
	 * Remove a section and cancel the trigger entry it created.
	 * <p>If the section is not within the section, no action will be taken.
	 */
	public void removeAspect(AbstractAspect<?, ?> aspect) {
		if (aspects.remove(aspect)) {
			for (BaseTriggerEntry entry : aspect.triggers) {
				aspect.remove(entry);
			}
		}
	}
}
