package java8.dates;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;
import java.util.stream.Stream;

import model.Person;


public class Dates
{
	public static void main(final String... args)
	{
		populateDob();

		temporalAdjuster();

		getTimeZones();

		final ZonedDateTime nextMeeting = zonedDateTime();

		formatZonedDateTime(nextMeeting);

		guess();
	}


	private static void guess()
	{
		final int x = 11 & 9;
		System.out.println(x);

		final int y = x ^ 3; // XOR
		System.out.println(y);

		System.out.println(y | 12);	// BWO
	}


	private static void formatZonedDateTime(final ZonedDateTime nextMeeting)
	{
		System.out.println(DateTimeFormatter.ISO_LOCAL_DATE.format(nextMeeting));

		System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(nextMeeting));

		System.out.println(DateTimeFormatter.RFC_1123_DATE_TIME.format(nextMeeting));
	}


	private static ZonedDateTime zonedDateTime()
	{
		final ZonedDateTime currentMeeting =
				ZonedDateTime.of(
						LocalDate.of(2018, Month.OCTOBER, 30),
						LocalTime.of(13, 00),
						ZoneId.of("Europe/London"));

		final ZonedDateTime nextMeeting = currentMeeting.plus(Period.ofMonths(1));

		System.out.println("Next Meeting is " + nextMeeting.toString());

		final ZonedDateTime nextMeetingUsa = nextMeeting.withZoneSameInstant(ZoneId.of("US/Central"));

		System.out.println("Next Meeting USA time is " + nextMeetingUsa.toString());

		return nextMeeting;
	}


	private static void getTimeZones()
	{
		final Set<String> allZoneIds = ZoneId.getAvailableZoneIds();

		for (final String s : allZoneIds)
		{
			System.out.println(s);
		}
	}


	private static void temporalAdjuster()
	{
		// Get the date of the next Sunday from now
		final LocalDate now = LocalDate.now();

		final LocalDate nextSunday = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));

		System.out.println("The date of next Sunday is " + nextSunday.toString());
	}


	private static void populateDob()
	{
		try (
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								Dates.class.getResourceAsStream("Persons.txt")));

				Stream<String> stream = reader.lines();)
		{
			stream.map(
					line -> {
						final String[] s = line.split(" ");
						final String name = s[0].trim();
						final int year = Integer.parseInt(s[1]);
						final Month month = Month.of(Integer.parseInt(s[2]));
						final int day = Integer.parseInt(s[3]);
						final LocalDate dob = LocalDate.of(year, month, day);
						final int age = Period.between(dob, LocalDate.now()).getYears();
						final Person person = new Person(name, age, dob);
						return person;
					})
					.forEach(System.out::println);

		}
		catch (final IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
