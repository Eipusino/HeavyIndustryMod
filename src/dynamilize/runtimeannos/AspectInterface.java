package dynamilize.runtimeannos;

import dynamilize.*;

import java.lang.annotation.*;

/**
 * This annotation is used to declare an interface as a sliced interface type. When constructing a dynamic instance in {@link DynamicMaker},
 * the sliced interface passed in needs to carry this annotation.
 * <p>When an interface carries this annotation, it should meet the following specifications:
 * <ul>
 *   <li><strong>There must be no non-abstract default methods in the interface</strong></li>
 *   <li><strong>All interfaces extended by the interface must also be sectional interfaces</strong></li>
 *   <li><strong>There must also be no non-abstract methods in the sliced interface extended by the interface</strong></li>
 * </ul>
 *
 * All slicing interfaces extended by slicing interfaces iteratively add all methods as slicing behaviors.
 * In the {@link DynamicMaker#newInstance(Class, Class[], Class[], DynamicClass, Object...)} method, multiple slicing interfaces can also be passed in,
 * All facet interfaces are combined to form the facet applied on this dynamic instance.
 *
 * <p>The behavior in the section is used to locate the delegated method in the object's methods when creating an enhanced type of dynamic instance, that is, to limit the methods that the dynamic process will affect. From a case study:
 * <pre>{@code
 * //A sectional interface:
 * @AspectInterface
 * public interface Sample {
 *     void put(Object key, Object value);
 *     Object get(Object key);
 *     Object remove(Object key);
 * }
 *
 * //Now, using this slicing interface, create a dynamic HashMap:
 * DynamicMaker maker = DynamicFactory.getDefault();
 * DynamicObject<HashMap<String, String>> map = maker.newInstance(HashMap.class, new Class[]{Sample.class}, DynamicClass.get("Temp"));
 *
 * map.setFunction("put", (self, su, arg) -> {
 *     System.out.println("putting:" + arg.get(0) + "-" + arg.get(1));
 *     su.invokeFunc("put", arg);
 * }, Object.class, Object.class);//The put method exists in the section declaration, and this behavior can be monitored
 *
 * map.setFunction("clear", (self, su, arg) -> {
 *     System.out.println("clearing");
 *     su.invokeFunc("clear", arg);
 * });//Clear is not declared in the section, and the behavior declared here only adds a function called clear to the dynamic object, but does not monitor the clear method itself.
 * }</pre>
 *
 * You can also choose not to provide a slicing interface. When creating a dynamic instance, if the slicing interface table is set to null, the dynamic object will be <strong>fully delegated</strong>,
 * <br>This dynamic instance will enable dynamization for all <strong>non-private, non-final, and non-static</strong> methods, including those inherited.
 * <br><strong>be careful!</strong>Full delegation will create enhanced bytecode for all processed methods, and the cost of this process may be very expensive,
 * When you can determine the scope of the section, try to use the section to limit the scope of the commission as much as possible, rather than widely using full commission.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AspectInterface {}
