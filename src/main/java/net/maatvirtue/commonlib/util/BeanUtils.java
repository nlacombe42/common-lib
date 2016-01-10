package net.maatvirtue.commonlib.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class BeanUtils
{
	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	/**
	 * Deep merges bean properties in a cascading style.
	 *
	 * <p>
	 * 		The beans in the <code>beans</code> list must be ordered from the
	 * 		child-most bean to the parent-most bean.
	 * </p>
	 * <p>
	 *     	The resulting bean properties will be the same value as the highest indexed bean
	 * 		that has a non null property. The only exception being List type properties, where
	 * 		the resulting bean will contains all of the values in all of the lists corresponding to the
	 * 		same property on the non null beans provided.
	 * </p>
	 *
	 * <p>
	 * 		This bean merge method does a deep merge, meaning that it will apply the previously
	 * 		described action for selecting a property's value on each <em>terminal type</em> property.
	 * 		A <em>terminal type</em> is a type that is either a primary type (Integer, int, Double, double, String, etc),
	 * 		a Collection or a non-bean type.
	 * </p>
	 */
	public static <BeanType> BeanType cascadingMerge(List<BeanType> beans, Class<BeanType> beanClass)
	{
		BeanType result;

		try
		{
			result = beanClass.newInstance();
		}
		catch(Exception exception)
		{
			throw new RuntimeException("Error instanciating type "+beanClass.getCanonicalName());
		}

		cascadingMerge(result, beans, beanClass);

		return result;
	}

	private static <BeanType> void cascadingMerge(BeanType result, List<BeanType> beans, Class<BeanType> beanClass)
	{
		for(PropertyDescriptor property: getProperties(beanClass))
		{
			if(isPropertyReadableAndWritable(property))
			{
				Class<?> propertyClass = property.getPropertyType();
				Method propertyGetter = property.getReadMethod();
				Method propertySetter = property.getWriteMethod();

				processProperty(result, beans, property.getName(), propertyClass, propertyGetter, propertySetter);
			}
		}
	}

	private static <BeanType, PropertyType, ElementType> void processProperty(BeanType result, List<BeanType> beans,
					String propertyName, Class<PropertyType> propertyClass, Method propertyGetter, Method propertySetter)
	{
		List<PropertyType> propertyBeansValues = getPropertyValuesFromBeans(beans, propertyGetter, propertyClass);
		PropertyType value;

		if(!isTerminalTypeProperty(propertyClass))
			value = cascadingMerge(propertyBeansValues, propertyClass);
		else if(Collection.class.isAssignableFrom(propertyClass))
		{
			List<? extends Collection<ElementType>> beansCollectionValues = (List<? extends Collection<ElementType>>) propertyBeansValues;
			Class<? extends Collection<ElementType>> collectionPropertyClass = (Class<? extends Collection<ElementType>>) propertyClass;
			Class<? extends Collection<ElementType>> collectionImplementation = getCollectionImplementation(collectionPropertyClass);

			try
			{
				value=(PropertyType)mergeCollections(beansCollectionValues, collectionImplementation);
			}
			catch(Exception exception)
			{
				logger.error("Error merging collections", exception);
				return;
			}
		}
		else
			value = getLastNonNull(propertyBeansValues);

		try
		{
			propertySetter.invoke(result, value);
		}
		catch(InvocationTargetException | IllegalAccessException exception)
		{
			String message = "";

			message += "Error setting property "+propertyName;
			message += " of "+result.getClass().getCanonicalName();
			message += " with value \""+value+"\"";

			if(value!=null)
				message += " of type "+value.getClass().getCanonicalName();

			throw new RuntimeException(message);
		}
	}

	private static <InterfaceType extends Collection> Class<? extends InterfaceType>
		getCollectionImplementation(Class<InterfaceType> collectionInterfaceClass)
	{
		if(List.class.isAssignableFrom(collectionInterfaceClass))
			return (Class<? extends InterfaceType>)LinkedList.class;
		else if(Set.class.isAssignableFrom(collectionInterfaceClass))
			return (Class<? extends InterfaceType>)HashSet.class;
		else
			return null;
	}

	private static <T> Collection<T> mergeCollections(List<? extends Collection<T>> collections, Class<? extends Collection<T>> resultCollectionClass)
	{
		if(allElementsEquals(collections, null))
			return null;

		Collection<T> result;

		try
		{
			result = resultCollectionClass.newInstance();
		}
		catch(InstantiationException | IllegalAccessException exception)
		{
			throw new RuntimeException("Error while instanciating Collection of type: "+resultCollectionClass.getCanonicalName(), exception);
		}

		for(Collection<T> collection: collections)
			if(collection!=null)
				result.addAll(collection);

		return result;
	}

	private static <T> boolean allElementsEquals(Collection<T> collection, T value)
	{
		if(collection==null)
			throw new IllegalArgumentException("collection cannot be null");

		for(T element: collection)
			if((value==null && element!=null) || (value!=null && !value.equals(element)))
				return false;

		return true;
	}

	private static <BeanType, PropertyType> List<PropertyType> getPropertyValuesFromBeans(List<BeanType> beans, Method getter, Class<PropertyType> propertyClass)
	{
		try
		{
			List<PropertyType> propertyValues=new LinkedList<>();

			for(BeanType bean : beans)
				if(bean==null)
					propertyValues.add(null);
				else
					propertyValues.add((PropertyType)getter.invoke(bean));

			return propertyValues;
		}
		catch(InvocationTargetException | IllegalAccessException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public static boolean isPropertyReadableAndWritable(PropertyDescriptor property)
	{
		return property.getReadMethod()!=null && property.getWriteMethod()!=null;
	}

	public static PropertyDescriptor[] getProperties(Class<?> clazz)
	{
		try
		{
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);

			return beanInfo.getPropertyDescriptors();
		}
		catch(IntrospectionException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public static boolean isTerminalTypeProperty(Class<?> clazz)
	{
		return isPrimaryType(clazz) || Collection.class.isAssignableFrom(clazz) || !isBeanType(clazz);
	}

	public static boolean isBeanType(Class<?> clazz)
	{
		return Arrays.asList(clazz.getInterfaces()).contains(Serializable.class) && hasNoArgumentConstructor(clazz) && !hasPublicField(clazz);
	}

	public static boolean hasPublicField(Class<?> clazz)
	{
		Field[] allFields = clazz.getDeclaredFields();

		for(Field field: allFields)
			return Modifier.isPublic(field.getModifiers());

		return false;
	}

	public static boolean hasNoArgumentConstructor(Class<?> clazz)
	{
		try
		{
			clazz.getConstructor();
			return true;
		}
		catch(NoSuchMethodException exception)
		{
			return false;
		}
	}

	/**
	 * Returns true if the provided class is a primitive type or
	 * a primitive wrapper type, or a String or a subclass of Number or Number class.
	 */
	public static boolean isPrimaryType(Class<?> clazz)
	{
		return clazz.isPrimitive() || isWrapperType(clazz) ||
						String.class.equals(clazz) || Number.class.isAssignableFrom(clazz);
	}

	/**
	 * Returns true if the provided class is a primitive wrapper class.
	 */
	public static boolean isWrapperType(Class<?> clazz)
	{
		return WRAPPER_TYPES.contains(clazz);
	}

	private static <T> T getLastNonNull(List<T> list)
	{
		T element;

		for(int i=list.size()-1; i>=0; i--)
		{
			element = list.get(i);

			if(element!=null)
				return element;
		}

		return null;
	}

	private static Set<Class<?>> getWrapperTypes()
	{
		Set<Class<?>> ret = new HashSet<>();

		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		ret.add(Void.class);

		return ret;
	}
}
