package java8.lambda;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import model.Person;


public class LambdaAndStreams
{
	public static void main(final String... args) throws InterruptedException
	{
		// collectFileContentsToSet();
		primitiveArrayToStream();
		stringArrayToStream();
		mapAndFlatMap();
	}


	private static void primitiveArrayToStream()
	{
		final int[] array = { 1, 2, 3, 4, 5 };

		final IntStream intStream = Arrays.stream(array);

		intStream.forEach(System.out::println);
	}


	private static void stringArrayToStream()
	{
		final String[] array = { "a", "b", "c", "d", "e" };

		final Stream<String> arrayStream = Arrays.stream(array);

		arrayStream.forEach(System.out::println);
	}


	private static void collectFileContentsToSet()
	{
		final List<Person> persons = new ArrayList<>();

		try (
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								LambdaAndStreams.class.getResourceAsStream("Persons.txt")));

				Stream<String> stream = reader.lines();)
		{

			stream.map(line -> {
				final String[] s = line.split(" ");
				final Person customer = new Person(s[0].trim(), Integer.parseInt(s[1]));
				persons.add(customer);
				return customer;
			}).forEach(System.out::println);

			// 1 - filter customers by min age of all who are greater than 20
			final Optional<Person> opt =
					persons.stream()
							.filter(person -> person.getAge() >= 20)
							.min(Comparator.comparing(Person::getAge));

			System.out.println("Youngest in set > 20 :" + opt);

			// 2 - filter the list to find the oldest person
			final Optional<Person> opt2 =
					persons.stream()
							.max(Comparator.comparing(Person::getAge));

			System.out.println("Oldest in set :" + opt2);

			// 3 - Collect the list into a Hashmap grouped by age
			final Map<Integer, List<Person>> groupByAge = persons.stream()
					.collect(
							Collectors.groupingBy(
									Person::getAge));

			System.out.println("Group Customers by age:" + groupByAge);

			// 4 - Post processing via downstream collector of above grouping
			final Map<Integer, Long> countByAge = persons.stream()
					.collect(
							Collectors.groupingBy(
									Person::getAge,
									Collectors.counting()));

			System.out.println("Count of Customers grouped by age into list:" + countByAge);

			// 4 - Post processing via downstream collector of above grouping collecting only name
			final Map<Integer, List<String>> groupByAgeCollectByName = persons.stream()
					.collect(
							Collectors.groupingBy(
									Person::getAge,
									Collectors.mapping(
											Person::getName,
											Collectors.toList())));

			System.out.println("Count of Customers grouped by age into TreeSet:" + groupByAgeCollectByName);

			// 5 - Post processing via downstream collector of above grouping collecting only name
			final Map<Integer, Set<String>> groupByAgeCollectNameToSet = persons.stream()
					.collect(
							Collectors.groupingBy(
									Person::getAge,
									Collectors.mapping(
											Person::getName,
											Collectors.toCollection(TreeSet::new))));

			System.out.println("Count of Customers grouped by age collected by name into set (ordered):"
					+ groupByAgeCollectNameToSet);
		}
		catch (final IOException ioe)
		{
			ioe.printStackTrace();
		}
	}


	private static void collect()
	{
		final List<Person> customers = getCustomers();

		final String customerString = customers.stream()
				.filter(customer -> customer.getAge() < 30)
				.map(Person::getName)
				.collect(
						Collectors.joining(","));

		System.out.println(customerString);
	}


	private static void mapFilterReduce()
	{
		final List<Person> customers = getCustomers();

		final Optional<Integer> minAge = customers.stream()
				.map(customer -> customer.getAge()) // Stream<Integer> - new stream with ages of customers
				.filter(age -> age < 20) // Stream<Integer> - new stream with filter applied
				.min(Comparator.naturalOrder());

		if (minAge.isPresent())
		{
			System.out.println("Age: " + minAge.get());
		}
	}


	private static void mapAndFlatMap()
	{
		final List<Integer> list1 = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
		final List<Integer> list2 = Arrays.asList(1, 2, 3);
		final List<Integer> list3 = Arrays.asList(5, 6, 7);
		final List<List<Integer>> listOfLists = Arrays.asList(list1, list2, list3);

		final Function<List<?>, Integer> map = List::size;

		final Function<List<Integer>, Stream<Integer>> flatMap = list -> list.stream();

		// The map command runs a function against each element and returns
		// a stream containing all of the outputs of the function calls
		// Its size is always the same as the original list
		System.out.println(
				"The map function returns a new stream which can be processed to obtain the output of the function supplied to it");
		listOfLists.stream()
				.map(map)
				.forEach(System.out::println);

		// The flatMap command runs a function against each element that returns a new stream
		// flatMap then flattens each stream into a new stream and closes all old streams
		// The resulting stream contains the contents of all of the streams
		System.out.println(
				"The flatmap function returns a flattened stream which can be processed to obtain the output of the function applied to it");
		listOfLists.stream()
				.flatMap(flatMap)

				.forEach(System.out::println);
	}


	private static void peekExampleWithTerminalOperation()
	{
		final Consumer<Person> consumer = System.out::println;
		final List<Person> customers = getCustomers();
		final List<Person> filteredResult = new ArrayList<>();

		customers.stream().peek(consumer)
				.filter(c -> c.getAge() < 30)
				.forEach(filteredResult::add); // A terminal operation will actually cause the stream to be processed

		System.out.println("\n\nList Contains");
		for (final Person c : filteredResult)
		{
			System.out.println(c);
		}
	}


	private static void peekExample()
	{
		final Consumer<Person> consumer = System.out::println;
		final List<Person> customers = getCustomers();
		final List<Person> filteredResult = new ArrayList<>();

		// This does not do anything! Streams do not hold any data! The list will be empty
		// and no data is processed!
		// Any calls that return a stream (intermediary operations )are only declarations and are lazy invocations
		// If a method does not return it stream then it will process the stream
		customers.stream()
				.peek(consumer)
				.filter(c -> c.getAge() < 30)
				.peek(filteredResult::add);

		for (final Person c : filteredResult)
		{
			System.out.println(c);
		}
	}


	private static void predicateLambda()
	{
		final List<String> strings = Arrays.asList("Heee", "Hello World!!!!!!", "Hello", "He hahahah he eheehehhe");
		final List<String> filteredResult = new ArrayList<>();

		final Predicate<String> p1 = s -> s.length() < 20;
		final Predicate<String> p2 = s -> s.length() > 10;
		final Predicate<String> p3 = s -> s.startsWith("He");
		final Predicate<String> p4 = p1.and(p2).and(p3);

		final Consumer<String> c1 = System.out::println;
		final Consumer<String> c2 = filteredResult::add;

		strings.stream().filter(p4).forEach(c1.andThen(c2));

		for (final String s : filteredResult)
		{
			System.out.println(s);
		}

	}


	private static void chainConsumers()
	{
		final List<String> strings = Arrays.asList("Heee", "Hello World!!!!!!", "Hello", "He hahahah he eheehehhe");
		final List<String> filteredResult = new ArrayList<>();

		final Predicate<String> p1 = s -> s.length() < 20;
		final Predicate<String> p2 = s -> s.length() < 10;
		final Predicate<String> p3 = s -> s.startsWith("He");
		p1.and(p2).and(p3);

		final Consumer<String> c1 = System.out::println;
		final Consumer<String> c2 = filteredResult::add;

		strings.forEach(c1.andThen(c2));


	}


	private static void methodReference()
	{
		final Consumer<Person> consumer = System.out::println;
		final List<Person> customers = getCustomers();

		customers.forEach(consumer);
	}


	private static void comparatorLambda()
	{
		final List<String> listOfStrings = Arrays.asList("***", "*******", "***************", "****", "*");

		final Comparator<String> comparator = (o1, o2) -> Integer.compare(o1.length(), o2.length());

		Collections.sort(listOfStrings, comparator);

		for (final String s : listOfStrings)
		{
			System.out.println(s);
		}
	}


	private static void fileFilterLambda()
	{
		final File directory =
				new File("D:/STS/workspace/DevelopingOnAws/src/main/java/uk/co/developingonaws/messagingwithsqsandsns");
		final FileFilter fileFilter2 = file -> file.getName().endsWith(".java");

		final File[] files = directory.listFiles(fileFilter2);

		for (final File file : files)
		{
			System.out.println(file.getName());
		}
	}


	private static void runnableLambda() throws InterruptedException
	{
		System.out.println("Hello world from Main Thread[" + Thread.currentThread().getName() + "]");

		final Runnable runnable = () -> {
			for (int i = 0; i < 10; i++)
			{
				System.out.println("Hello world from Thread[" + Thread.currentThread().getName() + "]");
			}
		};

		final Thread t = new Thread(runnable);
		t.start();

		// Make main thread wait for this thread to finish execution
		t.join();

		System.out.println("Thread[" + Thread.currentThread().getName() + "] waited");
	}


	private static List<Person> getCustomers()
	{
		final Person one = new Person("John", 67);
		final Person two = new Person("Dave", 34);
		final Person three = new Person("Sara", 19);
		final Person four = new Person("Katie", 27);

		return Arrays.asList(one, two, three, four);
	}
}
